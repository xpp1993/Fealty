package dexin.love.band.firmwareupgrade;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by wouter on 6-11-14.
 */
public class BluetoothGattSingleton {
    private static BluetoothGatt gatt = null;

    public static BluetoothGatt getGatt() {
        return gatt;
    }

    public static void setGatt(BluetoothGatt newGatt) {
        gatt = newGatt;
    }
}
