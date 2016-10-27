package dexin.love.band.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class ScanService {
	private BluetoothAdapter mBluetoothAdapter;
//	 private DeviceAdapter deviceAdapter;
	 private  Context context;
	 private Handler mHandler;
	 private boolean mScanning;
	 private String Ble_Address;
	 
	 public static final int SCAN_SUCCEED = 5; 
	 public static final int SCAN_FAIL = 4; 
	
   public ScanService(Context context, Handler handler) {
	       mHandler=handler;
	       this.context=context;
	    }

	public boolean init()
	{
		
		  if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
	            Toast.makeText(this.context,"不支持蓝牙", Toast.LENGTH_SHORT).show();
	           return false;
	        }
		//return true;
		  final BluetoothManager bluetoothManager =
	                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
	        mBluetoothAdapter = bluetoothManager.getAdapter();
	        if (mBluetoothAdapter == null) {
	            Toast.makeText(this.context, "不支持蓝牙", Toast.LENGTH_SHORT).show();
	            return false;
	        }
        return true;
	}
	
	 public  void scanLeDevice(final boolean enable,String Address ) {
	   //     final Button cancelButton = (Button) findViewById(R.id.btn_cancel);
		      Ble_Address = Address;
	        if (enable) {
	            // Stops scanning after a pre-defined scan period.
	            mHandler.postDelayed(new Runnable() {
	                @Override
	                public void run() {
	                	if(mScanning)
	                	{
	                		mScanning = false;						
		                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
		                    Toast.makeText(context, "未发现蓝牙设备", Toast.LENGTH_SHORT).show();
							Log.d("ScanService","未发现蓝牙设备");
		                    mHandler.obtainMessage(SCAN_FAIL, 0, 0, null).sendToTarget();
	                	}
						
	                }
	            }, 10000);
	            mScanning = true;	          
	            mBluetoothAdapter.startLeScan(mLeScanCallback);
	           // cancelButton.setText(R.string.cancel);
	        } else {
	            mScanning = false;
	            mBluetoothAdapter.stopLeScan(mLeScanCallback);
	          //  cancelButton.setText(R.string.scan);
	        }

	    }
	 
	    private BluetoothAdapter.LeScanCallback mLeScanCallback =
	            new BluetoothAdapter.LeScanCallback() {

	        @Override
	        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
	                	
	                	((Activity) context).runOnUiThread(new Runnable() {
	                          @Override
	                          public void run() {
	                            //  addDevice(device,rssi);	                        	  
	                        	    if(device.getAddress().equals(Ble_Address))
	                        		  {
	                        	    	 mScanning = false;
	                     	             mBluetoothAdapter.stopLeScan(mLeScanCallback);
	                     	             mHandler.obtainMessage(SCAN_SUCCEED, 0, 0, Ble_Address).sendToTarget();
										  Log.d("ScanService", "连接设备");
	                        		  }
	                          }
	                      });	                   
	        }
	    };	
}
