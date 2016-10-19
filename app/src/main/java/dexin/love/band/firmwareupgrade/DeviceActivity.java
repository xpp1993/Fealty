package dexin.love.band.firmwareupgrade;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dexin.love.band.R;

public class DeviceActivity extends SuotaActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
	final static String TAG = "DeviceActivity";
	DeviceConnectTask connectTask;
	BroadcastReceiver bluetoothGattReceiver, progressUpdateReceiver, connectionStateReceiver;
	String[] viewTitles;
	Map filesMap;
	ArrayList filesList = new ArrayList<Integer>();
	LayoutInflater inflater;
	public ListView fileListView;
	ArrayAdapter<String> mArrayAdapter;

	ProgressDialog dialog;

	// Container which holds all the views
	ViewFlipper deviceContainer;
	// All layout views used in this activity
	View deviceFileListView, deviceMain, deviceParameterSettings, progressLayout;

	// Progress layout attributes
	public ProgressBar progressBar;
	public TextView progressText;
    ScrollView scroll;
	TextView logWindow;
	Button patchDevice, updateDevice;
	RadioButton memoryTypeSPI, memoryTypeI2C;
	LinearLayout imageBankContainer, patchBaseAddressContainer, blockSizeContainer;
	View parameterI2cView, parameterSpiView;
	Spinner sclGpioSpinner, sdaGpioSpinner, misoGpioSpinner, mosiGpioSpinner, csGpioSpinner, sckGpioSpinner, imageBankSpinner;
	//EditText patchBaseAddress, I2CDeviceAddress, blockSize;
	EditText I2CDeviceAddress, blockSize;
	Button sendToDeviceButton, closeButton;

	int memoryType;

	public BluetoothManager bluetoothManager;

	static DeviceActivity instance;

	Map<String, String> previousSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;
		previousSettings = (Map<String, String>) Statics.getAllPreviousInput(this);
		bluetoothManager = new SuotaManager(this);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_container);
		viewTitles = getResources().getStringArray(R.array.app_device_titles);

		this.bluetoothGattReceiver = new BluetoothGattReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				super.onReceive(context, intent);
				bluetoothManager.processStep(intent);
			}
		};

		this.progressUpdateReceiver = new BluetoothGattReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				super.onReceive(context, intent);
				int progress = intent.getIntExtra("progess", 0);
				progressBar.setProgress(progress);
			}
		};

		this.connectionStateReceiver = new BluetoothGattReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				super.onReceive(context, intent);
				int connectionState = intent.getIntExtra("state", 0);
				DeviceActivity.this.connectionStateChanged(connectionState);
			}
		};

		registerReceiver(
				this.bluetoothGattReceiver,
				new IntentFilter(Statics.BLUETOOTH_GATT_UPDATE));

		registerReceiver(
				this.progressUpdateReceiver,
				new IntentFilter(Statics.PROGRESS_UPDATE));

		registerReceiver(
				this.connectionStateReceiver,
				new IntentFilter(Statics.CONNECTION_STATE_UPDATE));

		bluetoothManager.setDevice((BluetoothDevice) getIntent().getExtras().get("device"));

		connectTask = new DeviceConnectTask(DeviceActivity.this, bluetoothManager.getDevice()) {
			@Override
			protected void onProgressUpdate(BluetoothGatt... gatt) {
				BluetoothGattSingleton.setGatt(gatt[0]);
			}
		};
		connectTask.execute();

		dialog = new ProgressDialog(this);
		dialog.setMessage("Connecting, please wait...");
		//dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //bluetoothManager.disconnect();
                finish();
            }
        });
		dialog.show();

		inflater = LayoutInflater.from(this);
		deviceContainer = (ViewFlipper) findViewById(R.id.deviceLayoutContainer);
		deviceMain = inflater.inflate(R.layout.device_main, deviceContainer, true);
		deviceFileListView = inflater.inflate(R.layout.device_file_list, deviceContainer, true);
		deviceParameterSettings = inflater.inflate(R.layout.device_parameter_settings, deviceContainer, true);
		progressLayout = inflater.inflate(R.layout.progress, deviceContainer, true);
		switchView(0);
		progressText = (TextView) progressLayout.findViewById(R.id.progress_text);
		progressBar = (ProgressBar) progressLayout.findViewById(R.id.progress_bar);
        scroll = (ScrollView) progressLayout.findViewById(R.id.logScroll);
		logWindow = (TextView) progressLayout.findViewById(R.id.logWindow);
        logWindow.setText(null, TextView.BufferType.EDITABLE);
		progressBar.setProgress(0);
		progressBar.setMax(100);
	}

	public static DeviceActivity getInstance() {
		return DeviceActivity.instance;
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "ondestroy");
		bluetoothManager.disconnect();
		try {
			unregisterReceiver(this.bluetoothGattReceiver);
			unregisterReceiver(this.progressUpdateReceiver);
			unregisterReceiver(this.connectionStateReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
        if (this.deviceContainer.getDisplayedChild() == 3) {
            if (bluetoothManager.isFinished())
                switchView(0);
            else {
                bluetoothManager.disconnect();
                finish();
            }
        }
		else if (this.deviceContainer.getDisplayedChild() >= 1) {
			switchView(this.deviceContainer.getDisplayedChild() - 1);
		} else {
			bluetoothManager.disconnect();
			super.onBackPressed();
		}
	}

	public void initMainScreen() {
		Log.d(TAG, "initMainScreen");
		initMainScreenItems();

		updateDevice = (Button) deviceMain.findViewById(R.id.updateButton);
		patchDevice = (Button) deviceMain.findViewById(R.id.patchButton);
		updateDevice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BluetoothDevice device = bluetoothManager.getDevice();
				bluetoothManager = new SuotaManager(DeviceActivity.this);
				bluetoothManager.setDevice(device);
				initFileList();
				switchView(1);
			}
		});

		if (this.dialog.isShowing()) {
			this.dialog.dismiss();
		}

	}

	private void initMainScreenItems() {
		Log.d(TAG, "initMainScreenItems");
		List<BluetoothGattService> services = BluetoothGattSingleton.getGatt().getServices();
		for (BluetoothGattService service : services) {
			List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
			for (BluetoothGattCharacteristic characteristic : characteristics) {
				if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_MANUFACTURER_NAME_STRING)) {
					bluetoothManager.characteristicsQueue.add(characteristic);
				} else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_MODEL_NUMBER_STRING)) {
					bluetoothManager.characteristicsQueue.add(characteristic);
				} else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_FIRMWARE_REVISION_STRING)) {
					bluetoothManager.characteristicsQueue.add(characteristic);
				} else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_SOFTWARE_REVISION_STRING)) {
					bluetoothManager.characteristicsQueue.add(characteristic);
				} else if (characteristic.getUuid().equals(Statics.SPOTA_MEM_INFO_UUID)) {
					BluetoothGattSingleton.setSpotaMemInfoCharacteristic(characteristic);
				}
			}
		}
		bluetoothManager.readNextCharacteristic();
	}

	private void initFileList() {
		fileListView = (ListView) deviceFileListView.findViewById(R.id.file_list);

		mArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);

		filesMap = File.list();
		Iterator it = filesMap.entrySet().iterator();
		fileListView.setAdapter(mArrayAdapter);
		fileListView.setOnItemClickListener(this);
		while (it.hasNext()) {
			Map.Entry filesMap = (Map.Entry) it.next();
			Log.d("File", filesMap.getKey().toString());
			filesList.add(filesMap.getKey());
			mArrayAdapter.add((String) filesMap.getValue());
		}
	}

	private void initParameterSettings() {
		int gpioValuesId = R.array.gpio_values;
		memoryTypeSPI = (RadioButton) deviceParameterSettings.findViewById(R.id.memoryTypeSPI);
		memoryTypeSPI.setOnClickListener(this);
		memoryTypeI2C = (RadioButton) deviceParameterSettings.findViewById(R.id.memoryTypeI2C);
		memoryTypeI2C.setOnClickListener(this);

		closeButton = (Button) deviceParameterSettings.findViewById(R.id.buttonClose);
		closeButton.setOnClickListener(this);

		// SUOTA ONLY
		imageBankContainer = (LinearLayout) deviceParameterSettings.findViewById(R.id.imageBankContainer);
		blockSizeContainer = (LinearLayout) deviceParameterSettings.findViewById(R.id.blockSizeContainer);
		blockSize = (EditText) deviceParameterSettings.findViewById(R.id.blockSize);

		// SPOTA ONLY
		patchBaseAddressContainer = (LinearLayout) deviceParameterSettings.findViewById(R.id.patchBaseAddressContainer);

		if (bluetoothManager.type == SuotaManager.TYPE) {
			patchBaseAddressContainer.setVisibility(View.GONE);
			imageBankContainer.setVisibility(View.VISIBLE);
			blockSizeContainer.setVisibility(View.VISIBLE);
			String previousText = previousSettings.get(String.valueOf(R.id.blockSize));
			if(previousText == null || previousText.equals("")) {
				previousText = Statics.DEFAULT_BLOCK_SIZE_VALUE;
			}
			blockSize.setText(previousText);
		}

		// Different views for memory types
		parameterI2cView = deviceParameterSettings.findViewById(R.id.pI2cContainer);
		parameterSpiView = deviceParameterSettings.findViewById(R.id.pSpiContainer);

		// SPOTA patch base address
//		patchBaseAddress = (EditText) deviceParameterSettings.findViewById(R.id.patchBaseAddress);
//		patchBaseAddress.setText(previousSettings.get(String.valueOf(R.id.patchBaseAddress)));

		// SUOTA image bank
		imageBankSpinner = (Spinner) deviceParameterSettings.findViewById(R.id.imageBank);
		ArrayAdapter<CharSequence> imageBankAdapter = ArrayAdapter.createFromResource(this,
				R.array.image_bank_addresses, android.R.layout.simple_spinner_item);
		imageBankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		imageBankSpinner.setAdapter(imageBankAdapter);
		imageBankSpinner.setOnItemSelectedListener(this);
		int position = imageBankAdapter.getPosition(previousSettings.get(String.valueOf(R.id.imageBank)));
		imageBankSpinner.setSelection(position);

		// I2C Device address
		I2CDeviceAddress = (EditText) deviceParameterSettings.findViewById(R.id.I2CDeviceAddress);
		I2CDeviceAddress.setText(previousSettings.get(String.valueOf(R.id.I2CDeviceAddress)));

		// Spinners for I2C
		sclGpioSpinner = (Spinner) deviceParameterSettings.findViewById(R.id.sclGpioSpinner);
		sdaGpioSpinner = (Spinner) deviceParameterSettings.findViewById(R.id.sdaGpioSpinner);

		// Spinners for SPI
		misoGpioSpinner = (Spinner) deviceParameterSettings.findViewById(R.id.misoGpioSpinner);
		mosiGpioSpinner = (Spinner) deviceParameterSettings.findViewById(R.id.mosiGpioSpinner);
		csGpioSpinner = (Spinner) deviceParameterSettings.findViewById(R.id.csGpioSpinner);
		sckGpioSpinner = (Spinner) deviceParameterSettings.findViewById(R.id.sckGpioSpinner);

		ArrayAdapter<CharSequence> gpioAdapter = ArrayAdapter.createFromResource(this,
				gpioValuesId, android.R.layout.simple_spinner_item);
		gpioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sclGpioSpinner.setAdapter(gpioAdapter);
		sclGpioSpinner.setOnItemSelectedListener(this);
		position = gpioAdapter.getPosition(previousSettings.get(String.valueOf(R.id.sclGpioSpinner)));
        if (position <= 0) {
            position = Statics.DEFAULT_SCL_GPIO_VALUE;
        }
		sclGpioSpinner.setSelection(position);

		sdaGpioSpinner.setAdapter(gpioAdapter);
		sdaGpioSpinner.setOnItemSelectedListener(this);
		position = gpioAdapter.getPosition(previousSettings.get(String.valueOf(R.id.sdaGpioSpinner)));
        if (position <= 0) {
            position = Statics.DEFAULT_SDA_GPIO_VALUE;
        }
		sdaGpioSpinner.setSelection(position);

		misoGpioSpinner.setAdapter(gpioAdapter);
		misoGpioSpinner.setOnItemSelectedListener(this);
		position = gpioAdapter.getPosition(previousSettings.get(String.valueOf(R.id.misoGpioSpinner)));
		if (position <= 0) {
			position = Statics.DEFAULT_MISO_VALUE;
		}
		Log.d("position", "MISO: " + position);
		misoGpioSpinner.setSelection(position);

		mosiGpioSpinner.setAdapter(gpioAdapter);
		mosiGpioSpinner.setOnItemSelectedListener(this);
		position = gpioAdapter.getPosition(previousSettings.get(String.valueOf(R.id.mosiGpioSpinner)));
		if (position <= 0) {
			position = Statics.DEFAULT_MOSI_VALUE;
		}
		Log.d("position", "MOSI: " + position);
		mosiGpioSpinner.setSelection(position);

		csGpioSpinner.setAdapter(gpioAdapter);
		csGpioSpinner.setOnItemSelectedListener(this);
		position = gpioAdapter.getPosition(previousSettings.get(String.valueOf(R.id.csGpioSpinner)));
		if (position <= 0) {
			position = Statics.DEFAULT_CS_VALUE;
		}
		Log.d("position", "CS: " + position);
		csGpioSpinner.setSelection(position);

		sckGpioSpinner.setAdapter(gpioAdapter);
		sckGpioSpinner.setOnItemSelectedListener(this);
		position = gpioAdapter.getPosition(previousSettings.get(String.valueOf(R.id.sckGpioSpinner)));
		sckGpioSpinner.setSelection(position);

		sendToDeviceButton = (Button) deviceParameterSettings.findViewById(R.id.sendToDeviceButton);
		sendToDeviceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startUpdate();
			}
		});

        int previousMemoryType;
            previousMemoryType = Integer.parseInt(Statics.getPreviousInput(DeviceActivity.this, Statics.MEMORY_TYPE_SUOTA_INDEX));

            String previousText = previousSettings.get(String.valueOf(R.id.I2CDeviceAddress));
            if(previousText == null || previousText.equals("")) {
                previousText = Statics.DEFAULT_I2C_DEVICE_ADDRESS;
            }
            I2CDeviceAddress.setText(previousText);

        if (previousMemoryType > 0) {
            setMemoryType(previousMemoryType);
        }
        else {
            // Set default memory type to SPI
            setMemoryType(Statics.MEMORY_TYPE_SPI);
        }
	}

	private void startUpdate() {
		Intent intent = new Intent();

		if (bluetoothManager.type == SuotaManager.TYPE) {
			Statics.setPreviousInput(this, R.id.blockSize, blockSize.getText().toString());
		}

		if (memoryType == Statics.MEMORY_TYPE_I2C) {
			int I2CDeviceAddressValue = Integer.decode(I2CDeviceAddress.getText().toString());
			Statics.setPreviousInput(this, R.id.I2CDeviceAddress, String.format("%#10x", I2CDeviceAddressValue));
			bluetoothManager.setI2CDeviceAddress(I2CDeviceAddressValue);
		}

		// Set default block size to 1 for SPOTA, this will not be used in this case
		int fileBlockSize = 1;
		if (bluetoothManager.type == SuotaManager.TYPE) {
			fileBlockSize = Integer.parseInt(blockSize.getText().toString());
		}
		bluetoothManager.getFile().setFileBlockSize(fileBlockSize);

		intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
		intent.putExtra("step", 1);
		sendBroadcast(intent);
		switchView(3);
	}

	private void setMemoryType(int memoryType) {
		this.clearMemoryTypeChecked();
		this.memoryType = memoryType;
		bluetoothManager.setMemoryType(memoryType);
		parameterI2cView.setVisibility(View.GONE);
		parameterSpiView.setVisibility(View.GONE);

            Statics.setPreviousInput(this, Statics.MEMORY_TYPE_SUOTA_INDEX, String.valueOf(memoryType));

		switch (memoryType) {
			case Statics.MEMORY_TYPE_SPI:
				parameterSpiView.setVisibility(View.VISIBLE);
				memoryTypeSPI.setChecked(true);
				break;

			case Statics.MEMORY_TYPE_I2C:
				parameterI2cView.setVisibility(View.VISIBLE);
				memoryTypeI2C.setChecked(true);
				break;
		}
	}

	public void switchView(int viewIndex) {
		this.deviceContainer.setDisplayedChild(viewIndex);
		setTitle(viewTitles[viewIndex]);
		if (viewIndex == 0) {
			setTitle(bluetoothManager.getDevice().getName());
		}
	}
	private void clearMemoryTypeChecked() {
		memoryTypeI2C.setChecked(false);
		memoryTypeSPI.setChecked(false);
	}

	public void enableCloseButton() {
		closeButton.setVisibility(View.VISIBLE);
	}

	private void connectionStateChanged(int connectionState) {
		if(connectionState == BluetoothProfile.STATE_DISCONNECTED) {
			Toast.makeText(DeviceActivity.this, this.bluetoothManager.getDevice().getName() + " disconnected.", Toast.LENGTH_LONG).show();
            if (BluetoothGattSingleton.getGatt() != null)
			    BluetoothGattSingleton.getGatt().close();
			if(!bluetoothManager.isFinished()) {
                if (!bluetoothManager.getError())
				    finish();
			}
		}
	}

	public void logMemInfoValue(int memInfoValue) {
		String message = "Patch Memory Info:\n";
		int numberOfPatches = (memInfoValue >> 16) & 0xff;
		int numberOfBytes = memInfoValue & 0xff;
		int sizeOfPatches = (int) Math.ceil((double)numberOfBytes / (double)4);
		message += "\tNumber of patches = " + numberOfPatches + "\n" +
				"\tSize of patches = " + sizeOfPatches + " words (" + numberOfBytes + " bytes)";
		this.log(message);
	}

	public void log(String message) {
		this.logWindow.getEditableText().append(message + "\n");
        this.scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		// Get the resourceID necessary for retrieving the file
		String filename = filesList.get(position).toString();
		bluetoothManager.setFileName(filename);
		Log.d(TAG, "Clicked: " + filename);
		try {
			bluetoothManager.setFile(File.getByFileName(filename));
			initParameterSettings();
			switchView(2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.memoryTypeSPI:
				setMemoryType(Statics.MEMORY_TYPE_SPI);
				break;
			case R.id.memoryTypeI2C:
				setMemoryType(Statics.MEMORY_TYPE_I2C);
				break;
			case R.id.buttonClose:
				finish();
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		String stringValue = parent.getSelectedItem().toString();
		int value = Statics.gpioStringToInt(stringValue);
		Statics.setPreviousInput(this, parent.getId(), stringValue);
		switch (parent.getId()) {
			// SPI
			case R.id.misoGpioSpinner:
				bluetoothManager.setMISO_GPIO(value);
				break;
			case R.id.mosiGpioSpinner:
				bluetoothManager.setMOSI_GPIO(value);
				break;
			case R.id.csGpioSpinner:
				bluetoothManager.setCS_GPIO(value);
				break;
			case R.id.sckGpioSpinner:
				bluetoothManager.setSCK_GPIO(value);
				break;

			// I2C
			case R.id.sclGpioSpinner:
				bluetoothManager.setSCL_GPIO(value);
				break;
			case R.id.sdaGpioSpinner:
				bluetoothManager.setSDA_GPIO(value);
				break;

			// SUOTA
			case R.id.imageBank:
				bluetoothManager.setImageBank(value);
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
}