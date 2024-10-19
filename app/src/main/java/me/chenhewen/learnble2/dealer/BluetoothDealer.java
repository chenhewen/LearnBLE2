package me.chenhewen.learnble2.dealer;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import me.chenhewen.learnble2.data.GattServiceItem;
import me.chenhewen.learnble2.event.ActionItemChangedEvent;
import me.chenhewen.learnble2.event.DeviceItemChangedEvent;
import me.chenhewen.learnble2.model.ActionItem;
import me.chenhewen.learnble2.model.DeviceItem;
import me.chenhewen.learnble2.model.ScanItem;

public class BluetoothDealer {
    
    public BluetoothDealer(Context context, BluetoothManager bluetoothManager) {
        this.context = context;
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    // 常量
    private static final long SCAN_PERIOD = 10000;

    // 蓝牙服务
    private Context context;
    private BluetoothAdapter bluetoothAdapter;

    // 内部状态变量
    private boolean scanning;
    private Handler handler = new Handler();

    // 数据
    public Map<String, List<GattServiceItem>> gettServiceItemsMap = new HashMap<>();
    public List<ScanItem> scanItems = new ArrayList();
    public List<DeviceItem> deviceItems = new ArrayList<>();

    public boolean isBluetoothEnable() {
        return bluetoothAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    public void enableBluetooth() {
        bluetoothAdapter.enable();
    }

    @SuppressLint("MissingPermission")
    public void scanLeDevice() {
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    System.out.println("chw: postDelayed stopScan");
                    bluetoothLeScanner.stopScan(deviceScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            scanItems.clear();
            System.out.println("chw: startScan");
            bluetoothLeScanner.startScan(deviceScanCallback);
        }
    }

    // Device scan callback.
    @SuppressLint("MissingPermission")
    private ScanCallback deviceScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    String deviceAddress = result.getDevice().getAddress();
                    String name = result.getDevice().getName();
                    name = (name == null || name.isEmpty()) ? "N/A" : name;
                    int rssi = result.getRssi();

//                    System.out.println("chw: ScanCallback result: " + name + " "+ deviceAddress);

                    ScanItem scanItem = new ScanItem(name, deviceAddress, rssi);
                    if (scanItems.stream().noneMatch(item -> item.address.equals(deviceAddress))) {
                        scanItems.add(scanItem);
                    }

                    ScanItem.sortByRssi(scanItems);
                    EventBus.getDefault().post(new ScanItem.MessageEvent(scanItems));
                }
            };


    // Device item operations

    public void addDeviceItem(DeviceItem deviceItem) {
        boolean noneExists = deviceItems.stream().noneMatch((item) -> item.address.equals(deviceItem.address));
        if (noneExists) {
            deviceItems.add(deviceItem);
            // 广播数据变化
            EventBus.getDefault().post(new DeviceItemChangedEvent(deviceItem.address));
        }
    }

    public void removeDeviceItem(DeviceItem deviceItem) {
        deviceItems.removeIf( (item)-> item.address.equals(deviceItem.address));
        // 广播数据变化
        EventBus.getDefault().post(new DeviceItemChangedEvent(deviceItem.address));
    }

    public void addActionItem(DeviceItem deviceItem, ActionItem actionItem) {
        deviceItem.actionItems.add(actionItem);
        // 广播数据变化
        EventBus.getDefault().post(new ActionItemChangedEvent(deviceItem));
    }

    public void removeActionItem(DeviceItem deviceItem, ActionItem actionItem) {
        deviceItem.actionItems.removeIf(aItem->aItem.id.equals(actionItem.id));
        // 广播数据变化
        EventBus.getDefault().post(new ActionItemChangedEvent(deviceItem));
    }


    public List<String> getAllServiceUuids(String address) {
        List<GattServiceItem> serviceItems = gettServiceItemsMap.get(address);
        if (serviceItems == null) {
            return new ArrayList<>();
        }
        return serviceItems.stream().map(item->item.uuid.toString()).collect(Collectors.toList());
    }

    public List<String> getAllCharacteristicUuids(String address, String serviceUuid) {
        List<GattServiceItem> serviceItems = gettServiceItemsMap.get(address);
        if (serviceItems == null) {
            return new ArrayList<>();
        }

        GattServiceItem serviceItem = serviceItems.stream()
                .filter(item -> item.uuid.toString().equals(serviceUuid))
                .findFirst().orElse(null);
        if (serviceItem == null) {
            return new ArrayList<>();
        }

        return serviceItem.characteristicItems.stream().map(item->item.uuid.toString()).collect(Collectors.toList());
    }
}
