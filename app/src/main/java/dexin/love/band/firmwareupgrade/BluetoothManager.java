package dexin.love.band.firmwareupgrade;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import dexin.love.band.fragment.MeFragment;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.utils.ThreadPoolUtils;
import dexin.love.band.utils.ToastUtils;

/**
 * Created by wouter on 6-11-14.
 */
public abstract class BluetoothManager {
    static final String TAG = "BluetoothManager";

    public static final int END_SIGNAL = 0xfe000000;
    public static final int REBOOT_SIGNAL = 0xfd000000;

    // Input values
    int memoryType;

    // SPI
    int MISO_GPIO;  // P0_5 (0x05)
    int MOSI_GPIO;  // P0_6 (0x06)
    int CS_GPIO;    // P0_3 (0x03)
    int SCK_GPIO;   // P0_0 (0x00)

    // SUOTA
    int imageBank;


    //DeviceActivity activity;
    MeFragment scannerFragment;
    File file;
    String fileName;
    //Context context;
    FragmentActivity activity;
    BluetoothDevice device;
    HashMap errors;

    boolean lastBlock = false;
    boolean lastBlockSent = false;
    boolean preparedForLastBlock = false;
    boolean endSignalSent = false;
    boolean rebootsignalSent = false;
    boolean finished = false;
    boolean hasError = false;
    public int type;
    protected int step;
    int blockCounter = 0;
    int chunkCounter = -1;
    public Queue characteristicsQueue;

    public BluetoothManager(FragmentActivity activity) {
        //this.context = context;
        this.activity = activity;
        initErrorMap();
        characteristicsQueue = new ArrayDeque<BluetoothGattCharacteristic>();
    }

    public abstract void processStep(Intent intent);

    public boolean isFinished() {
        return finished;
    }

    public boolean getError() {
        return hasError;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) throws IOException {
        this.file = file;
        this.file.setType(this.type);
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public void setMemoryType(int memoryType) {
        this.memoryType = memoryType;
    }


    public void setImageBank(int imageBank) {
        this.imageBank = imageBank;
    }

    public void setMISO_GPIO(int MISO_GPIO) {
        this.MISO_GPIO = MISO_GPIO;
    }

    public void setMOSI_GPIO(int MOSI_GPIO) {
        this.MOSI_GPIO = MOSI_GPIO;
    }

    public void setCS_GPIO(int CS_GPIO) {
        this.CS_GPIO = CS_GPIO;
    }

    public void setSCK_GPIO(int SCK_GPIO) {
        this.SCK_GPIO = SCK_GPIO;
    }

    public void enableNotifications() {
        Log.d(TAG, "- enableNotifications");
        Log.d(TAG, "- Enable notifications for SPOTA_SERV_STATUS characteristic");
        // Get the service status UUID from the gatt and enable notifications
        List<BluetoothGattService> services = BluetoothGattSingleton.getGatt().getServices();
        for (BluetoothGattService service : services) {
            Log.d(TAG, "  Found service: " + service.getUuid().toString());
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                Log.d(TAG, "  Found characteristic: " + characteristic.getUuid().toString());
                if (characteristic.getUuid().equals(Statics.SPOTA_SERV_STATUS_UUID)) {
                    Log.d(TAG, "*** Found SUOTA service");
                    BluetoothGattSingleton.getGatt().setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                            Statics.SPOTA_DESCRIPTOR_UUID);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    BluetoothGattSingleton.getGatt().writeDescriptor(descriptor);
                }
            }
        }
    }

    protected abstract int getSpotaMemDev();

    public void setSpotaMemDev() {
        BluetoothGattCharacteristic characteristic = BluetoothGattSingleton.getGatt().getService(Statics.SPOTA_SERVICE_UUID)
                .getCharacteristic(Statics.SPOTA_MEM_DEV_UUID);

        int memType = this.getSpotaMemDev();
        characteristic.setValue(memType, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        BluetoothGattSingleton.getGatt().writeCharacteristic(characteristic);
        Log.d(TAG, "setSpotaMemDev: " + String.format("%#10x", memType));
        Log.d(TAG, "Set SPOTA_MEM_DEV: " + String.format("%#10x", memType));
    }

    /**
     * 0x05060300 when
     * mem_type:        "External SPI" (0x13)
     * MISO GPIO:       P0_5 (0x05)
     * MOSI GPIO:       P0_6 (0x06)
     * CS GPIO:         P0_3 (0x03)
     * SCK GPIO:        P0_0 (0x00)
     * image_bank:      "Oldest" (value: 0)
     */
    private int getMemParamsSPI() {
        return (MISO_GPIO << 24) | (MOSI_GPIO << 16) | (CS_GPIO << 8) | SCK_GPIO;
    }

    // Step 8 in documentation
    public void setSpotaGpioMap() {
        int memInfoData = 0;
        boolean valid = false;
        switch (memoryType) {
            case Statics.MEMORY_TYPE_SPI:
                memInfoData = this.getMemParamsSPI();
                valid = true;
                break;
        }
        if (valid) {
            Log.d(TAG, "setSpotaGpioMap: " + String.format("%#10x", memInfoData));
            Log.d(TAG, "Set SPOTA_GPIO_MAP: " + String.format("%#10x", memInfoData));
            BluetoothGattCharacteristic characteristic = BluetoothGattSingleton.getGatt().getService(Statics.SPOTA_SERVICE_UUID)
                    .getCharacteristic(Statics.SPOTA_GPIO_MAP_UUID);
            characteristic.setValue(memInfoData, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
            BluetoothGattSingleton.getGatt().writeCharacteristic(characteristic);
        } else {
            Log.e(TAG, "Memory type not set.");
            Log.d(TAG, "Set SPOTA_GPIO_MAP: Memory type not set.");
        }
    }

    public void setPatchLength() {
        int blocksize = file.getFileBlockSize();
//		int blocksizeLE = (blocksize & 0xFF) << 8 | ((blocksize & 0xFF00) >> 8);
        if (lastBlock) {
            blocksize = this.file.getNumberOfBytes() % file.getFileBlockSize();
            preparedForLastBlock = true;
        }
        Log.d(TAG, "setPatchLength: " + blocksize + " - " + String.format("%#4x", blocksize));
        Log.d(TAG, "Set SPOTA_PATCH_LENGTH: " + blocksize);
        BluetoothGattCharacteristic characteristic = BluetoothGattSingleton.getGatt().getService(Statics.SPOTA_SERVICE_UUID)
                .getCharacteristic(Statics.SPOTA_PATCH_LEN_UUID);
        characteristic.setValue(blocksize, BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        BluetoothGattSingleton.getGatt().writeCharacteristic(characteristic);
    }

    public float sendBlock() {
        //float progress = 0;
        final float progress = ((float) (blockCounter + 1) / (float) file.getNumberOfBlocks()) * 100;
        if (!lastBlockSent) {
            //progress = ((float) (blockCounter + 1) / (float) file.getNumberOfBlocks()) * 100;
            //activity.runOnUiThread(new Runnable() {
            ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
                @Override
                public void run() {
                    sendProgressUpdate((int) progress);
                }
            });
            //sendProgressUpdate((int) progress);
            Log.d(TAG, "Sending block " + (blockCounter + 1) + " of " + file.getNumberOfBlocks());
            byte[][] block = file.getBlock(blockCounter);

            //for (int i = 0; i < block.length; i++) {
            int i = ++chunkCounter;
            boolean lastChunk = false;
            if (chunkCounter == block.length - 1) {
                chunkCounter = -1;
                lastChunk = true;
            }
            byte[] chunk = block[i];

            int chunkNumber = (blockCounter * file.getChunksPerBlockCount()) + i + 1;
            final String message = "Sending chunk " + chunkNumber + " of " + file.getTotalChunkCount() + " (with " + chunk.length + " bytes)";
            //if (chunkNumber % 100 == 0)
//			activity.runOnUiThread(new Runnable() {
            ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, message);
                }
            });
            String systemLogMessage = "Sending block " + (blockCounter + 1) + ", chunk " + (i + 1) + ", blocksize: " + block.length + ", chunksize " + chunk.length;
            Log.d(TAG, systemLogMessage);
            BluetoothGattCharacteristic characteristic = BluetoothGattSingleton.getGatt().getService(Statics.SPOTA_SERVICE_UUID)
                    .getCharacteristic(Statics.SPOTA_PATCH_DATA_UUID);
            characteristic.setValue(chunk);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            boolean r = BluetoothGattSingleton.getGatt().writeCharacteristic(characteristic);
            Log.d(TAG, "writeCharacteristic: " + r);
            //}

            if (lastChunk) {

                // SUOTA
                if (!lastBlock) {
                    blockCounter++;
                } else {
                    lastBlockSent = true;
                }
                if (blockCounter + 1 == file.getNumberOfBlocks()) {
                    lastBlock = true;
                }
            }
        }
        return progress;
    }

    public void sendEndSignal() {
        Log.d(TAG, "sendEndSignal");
        Log.d(TAG, "send SUOTA END command");
        BluetoothGattCharacteristic characteristic = BluetoothGattSingleton.getGatt().getService(Statics.SPOTA_SERVICE_UUID)
                .getCharacteristic(Statics.SPOTA_MEM_DEV_UUID);
        characteristic.setValue(END_SIGNAL, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        BluetoothGattSingleton.getGatt().writeCharacteristic(characteristic);
        endSignalSent = true;
    }

    public void sendRebootSignal() {
        Log.d(TAG, "sendRebootSignal");
        Log.d(TAG, "send SUOTA REBOOT command");
        BluetoothGattCharacteristic characteristic = BluetoothGattSingleton.getGatt().getService(Statics.SPOTA_SERVICE_UUID)
                .getCharacteristic(Statics.SPOTA_MEM_DEV_UUID);
        characteristic.setValue(REBOOT_SIGNAL, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        BluetoothGattSingleton.getGatt().writeCharacteristic(characteristic);
        rebootsignalSent = true;
    }

    public void readNextCharacteristic() {
        if (characteristicsQueue.size() >= 1) {
            BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) characteristicsQueue.poll();
            BluetoothGattSingleton.getGatt().readCharacteristic(characteristic);
            Log.d(TAG, "readNextCharacteristic");
        }
    }

    private void sendProgressUpdate(int progress) {
//		activity.progressBar.setProgress(progress);
        //scannerFragment.progressBar.setProgress(progress);
        scannerFragment.m_progressDlg.setProgress(progress);
    }

    public void disconnect() {
        try {
            BluetoothGattSingleton.getGatt().disconnect();
            BluetoothGattSingleton.getGatt().close();
            Log.d(TAG, "Disconnect from device");
            /**
             //             * 重启手环
             //             */
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error disconnecting from device");
        }
        try {
            if (file != null) {
                file.close();
            }
        } catch (Exception e) {
        }
    }

    protected void onSuccess() {
        scannerFragment.m_progressDlg.dismiss();
        finished = true;
        Log.d(TAG, "升级完成！");
        new AlertDialog.Builder(activity)
                .setTitle("升级完成！")
                .setMessage("是否重启手环?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendRebootSignal();
                        ToastUtils.showToastInUIThread("手环正在重启，请耐心等待！");
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        activity.switchView(0);
//						scannerFragment.switchView(0);
                        //  disconnect();
                        //disconnect();
                    }
                })
                .show();
    }

    public void onError(int errorCode) {
        scannerFragment.m_progressDlg.dismiss();
        if (!hasError) {
            String error = (String) errors.get(errorCode);
            Log.d(TAG, "Error: " + error);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            dialogBuilder.setTitle("An error occurred.")
                    .setMessage(error);
            dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
//                    activity.finish();
                    //  scannerFragment.finish();
                    scannerFragment.m_progressDlg.dismiss();
                }
            });
            dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // activity.finish();
                    // scannerFragment.finish();
                    scannerFragment.m_progressDlg.dismiss();
                }
            });
            dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            dialogBuilder.show();
            disconnect();
            hasError = true;
        }
    }

    private void initErrorMap() {
        this.errors = new HashMap<Integer, String>();
        // Value zero must not be used !! Notifications are sent when status changes.
        errors.put(3, "Forced exit of SPOTA service. See Table 1");
        errors.put(4, "Patch Data CRC mismatch.");
        errors.put(5, "Received patch Length not equal to PATCH_LEN characteristic value.");
        errors.put(6, "External Memory Error. Writing to external device failed.");
        errors.put(7, "Internal Memory Error. Not enough internal memory space for patch.");
        errors.put(8, "Invalid memory device.");
        errors.put(9, "Application error.");

        // SUOTAR application specific error codes
        errors.put(11, "Invalid image bank");
        errors.put(12, "Invalid image header");
        errors.put(13, "Invalid image size");
        errors.put(14, "Invalid product header");
        errors.put(15, "Same Image Error");
        errors.put(16, " Failed to read from external memory device");
    }

    protected void goToStep(int step) {
        Intent i = new Intent();
        i.putExtra("step", step);
        processStep(i);
    }
}