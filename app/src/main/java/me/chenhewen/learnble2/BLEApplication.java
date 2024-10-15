package me.chenhewen.learnble2;

import android.app.Application;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import me.chenhewen.learnble2.dealer.BluetoothDealer;

public class BLEApplication extends Application {

    private static BluetoothDealer bluetoothDealer;

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothDealer = new BluetoothDealer(this, bluetoothManager);
    }

    public static BluetoothDealer getBluetoothDealer() {
        return bluetoothDealer;
    }
}
