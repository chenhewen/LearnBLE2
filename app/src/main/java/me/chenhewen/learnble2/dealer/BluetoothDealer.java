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
import java.util.List;

import me.chenhewen.learnble2.model.DeviceItem;

public class BluetoothDealer {
    
    public BluetoothDealer(Context context, BluetoothManager bluetoothManager) {
        this.context = context;
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    // 常量
    private static final long SCAN_PERIOD = 10000;

    // 蓝牙服务
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    // 内部状态变量
    private boolean scanning;
    private Handler handler = new Handler();

    // 数据
    public List<DeviceItem> deviceItems = new ArrayList();

    public boolean isBluetoothEnable() {
        return bluetoothAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    public void enableBluetooth() {
        bluetoothAdapter.enable();
    }

    @SuppressLint("MissingPermission")
    public void scanLeDevice() {
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
            deviceItems.clear();
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

                    DeviceItem deviceItem = new DeviceItem(name, deviceAddress, rssi);
                    if (deviceItems.stream().noneMatch(item -> item.address.equals(deviceAddress))) {
                        deviceItems.add(deviceItem);
                    }

                    DeviceItem.sortByRssi(deviceItems);
                    EventBus.getDefault().post(new DeviceItem.MessageEvent(deviceItems));
                }
            };
}
