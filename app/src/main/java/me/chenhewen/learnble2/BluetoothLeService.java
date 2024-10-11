package me.chenhewen.learnble2;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;

    private int connectionState;

    private Binder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    private BluetoothAdapter bluetoothAdapter;

    public boolean initialize() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            System.out.println("chw: Unable to obtain a BluetoothAdapter");
            return false;
        }
        return true;
    }

    private BluetoothGatt bluetoothGatt;


    @SuppressLint("MissingPermission")
    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            System.out.println("chw: BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        try {
            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            // connect to the GATT server on the device
            bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
            return true;
        } catch (IllegalArgumentException exception) {
            System.out.println("chw: Device not found with provided address.  Unable to connect");
            return false;
        }
        // connect to the GATT server on the device
    }

    @SuppressLint("MissingPermission")
    public void send(final String message) {
        if (message.isEmpty()) {
            return;
        }

        List<BluetoothGattService> services = bluetoothGatt.getServices();
        System.out.println("chw:" + DataHub.deviceAddress + " services size  " + services.size());
        for (BluetoothGattService service : services) {
            System.out.println("chw: " + service.getUuid());
        }

        String serviceId = "0000ff10-0000-1000-8000-00805f9b34fb";
        UUID serviceUUID = UUID.fromString(serviceId); // 替换为你的服务UUID
        BluetoothGattService service = bluetoothGatt.getService(serviceUUID);

        if (service == null) {
            System.out.println("chw: service not found");
            return;
        }

        String characteristicId = "0000ff11-0000-1000-8000-00805f9b34fb";
        UUID characteristicUUID = UUID.fromString(characteristicId); // 替换为你的特征值UUID
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);

        if (characteristic == null) {
            System.out.println("chw: characteristic not found");
            return;
        }

        byte[] dataToWrite = new byte[]{0x01, 0x02}; // 替换为你的要写入的数据
        characteristic.setValue(dataToWrite);

        boolean success = bluetoothGatt.writeCharacteristic(characteristic);
        if (success) {
            System.out.println("chw: write success");
        } else {
            System.out.println("chw: write failed");
        }
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                System.out.println("chw: gattUpdateReceiver connected");
                connectionState = STATE_CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);

                // Attempts to discover services after successful connection.
                bluetoothGatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                System.out.println("chw: gattUpdateReceiver disconnected");
                connectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    @SuppressLint("MissingPermission")
    private void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }
}