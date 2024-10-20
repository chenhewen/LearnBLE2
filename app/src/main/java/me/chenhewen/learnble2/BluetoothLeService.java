package me.chenhewen.learnble2;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import me.chenhewen.learnble2.data.GattCharacteristicItem;
import me.chenhewen.learnble2.data.GattDescriptorItem;
import me.chenhewen.learnble2.data.GattServiceItem;
import me.chenhewen.learnble2.event.GattStateEvent;
import me.chenhewen.learnble2.event.ServiceDiscoveredEvent;

public class BluetoothLeService extends Service {

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";

    private Binder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
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

    private Map<String, BluetoothGatt> bluetoothGattMap = new HashMap<>();

    @SuppressLint("MissingPermission")
    public boolean connect(final String address) {
//        BluetoothGatt bluetoothGatt = bluetoothGattMap.get(address)
        if (bluetoothAdapter == null || address == null) {
            System.out.println("chw: BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        try {
            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            // connect to the GATT server on the device
            BluetoothGatt bluetoothGatt = device.connectGatt(this, false, new MyBluetoothGattCallback(address));
            bluetoothGattMap.put(address, bluetoothGatt);
            return true;
        } catch (IllegalArgumentException exception) {
            System.out.println("chw: Device not found with provided address.  Unable to connect");
            return false;
        }
        // connect to the GATT server on the device
    }

    @SuppressLint("MissingPermission")
    public void disconnect(String address) {
        BluetoothGatt bluetoothGatt = bluetoothGattMap.get(address);
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
    }

    @SuppressLint("MissingPermission")
    public void discoverServices(String address) {
        BluetoothGatt bluetoothGatt = bluetoothGattMap.get(address);
        if (bluetoothGatt != null) {
            bluetoothGatt.discoverServices();
        }
    }

    public List<String> fetchAllServiceUuids(String address) {
        BluetoothGatt bluetoothGatt = bluetoothGattMap.get(address);
        if (bluetoothGatt == null) {
            return new ArrayList<>();
        }

        List<BluetoothGattService> services = bluetoothGatt.getServices();
        return services.stream()
                .map(service -> service.getUuid().toString())
                .collect(Collectors.toList());

    }

    public List<String> fetchAllCharacteristicUuids(String address, String serviceUuid) {
        BluetoothGatt bluetoothGatt = bluetoothGattMap.get(address);
        if (bluetoothGatt == null) {
            return new ArrayList<>();
        }

        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUuid));
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        return characteristics.stream()
                .map(characteristic -> characteristic.getUuid().toString())
                .collect(Collectors.toList());

    }

    public List<GattServiceItem> fetchAllServiceItems(String address) {
        BluetoothGatt bluetoothGatt = bluetoothGattMap.get(address);
        if (bluetoothGatt == null) {
            return new ArrayList<>();
        }

        List<BluetoothGattService> services = bluetoothGatt.getServices();
        System.out.println("chw: services.size " + services.size());
        List<GattServiceItem> serviceItems = new ArrayList<>();
        for (BluetoothGattService service : services) {
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

            List<GattCharacteristicItem> characteristicItems = new ArrayList<>();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();

                List<GattDescriptorItem> descriptorItems = new ArrayList<>();
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    UUID descriptorUuid = descriptor.getUuid();
                    byte[] descriptorValueBytes = descriptor.getValue();
                    String descriptorValueString = descriptorValueBytes == null ? "" : new String(descriptorValueBytes);
                    GattDescriptorItem gattDescriptorItem = new GattDescriptorItem(descriptorUuid, descriptorValueString, descriptorValueBytes);
                    descriptorItems.add(gattDescriptorItem);
                }

                UUID characteristicUuid = characteristic.getUuid();
                int characteristicProperties = characteristic.getProperties();
                GattCharacteristicItem gattCharacteristicItem = new GattCharacteristicItem(characteristicUuid, characteristicProperties, descriptorItems);
                characteristicItems.add(gattCharacteristicItem);
            }

            UUID serviceUuid = service.getUuid();
            GattServiceItem serviceItem = new GattServiceItem(serviceUuid, characteristicItems);
            serviceItems.add(serviceItem);
        }

        return serviceItems;
    }

    @SuppressLint("MissingPermission")
    public void send(String address, String serviceUuid, String characteristicUuid, final byte[] msgInBytes) {
        if (msgInBytes == null) {
            return;
        }

        BluetoothGatt bluetoothGatt = bluetoothGattMap.get(address);
        if (bluetoothGatt == null) {
            return;
        }

        List<BluetoothGattService> services = bluetoothGatt.getServices();
        System.out.println("chw:" + DataHub.deviceAddress + " services size  " + services.size());
        for (BluetoothGattService service : services) {
            System.out.println("chw: " + service.getUuid());
        }

        String serviceId = serviceUuid;
        UUID serviceUUID = UUID.fromString(serviceId); // 替换为你的服务UUID
        BluetoothGattService service = bluetoothGatt.getService(serviceUUID);

        if (service == null) {
            System.out.println("chw: service not found");
            return;
        }

        String characteristicId = characteristicUuid;
        UUID characteristicUUID = UUID.fromString(characteristicId); // 替换为你的特征值UUID
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);

        if (characteristic == null) {
            System.out.println("chw: characteristic not found");
            return;
        }

        byte[] dataToWrite = msgInBytes; // 替换为你的要写入的数据
        characteristic.setValue(dataToWrite);

        boolean success = bluetoothGatt.writeCharacteristic(characteristic);
        if (success) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            System.out.println("chw: write success");
        } else {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            System.out.println("chw: write failed");
        }
    }

    private class MyBluetoothGattCallback extends BluetoothGattCallback {

        public MyBluetoothGattCallback(String address) {
            this.address = address;
        }

        private String address;

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                System.out.println("chw: gattUpdateReceiver connected");
                // Attempts to discover services after successful connection.
                BluetoothGatt bluetoothGatt = bluetoothGattMap.get(address);
                if (bluetoothGatt != null) {
                    bluetoothGatt.discoverServices();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                System.out.println("chw: gattUpdateReceiver disconnected");
            }

            EventBus.getDefault().post(new GattStateEvent(address, status, newState));
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            List<GattServiceItem> serviceItems = fetchAllServiceItems(address);
            BLEApplication.getBluetoothDealer().gettServiceItemsMap.put(address, serviceItems);

            EventBus.getDefault().post(new ServiceDiscoveredEvent(address, serviceItems));
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    @SuppressLint("MissingPermission")
    private void close() {
        for (BluetoothGatt bluetoothGatt : bluetoothGattMap.values()) {
            if (bluetoothGatt != null) {
                bluetoothGatt.close();
                bluetoothGatt = null;
            }
        }
    }
}