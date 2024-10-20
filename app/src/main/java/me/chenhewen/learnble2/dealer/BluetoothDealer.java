package me.chenhewen.learnble2.dealer;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.chenhewen.learnble2.Const;
import me.chenhewen.learnble2.FileUtils;
import me.chenhewen.learnble2.data.GattServiceItem;
import me.chenhewen.learnble2.event.ActionItemChangedEvent;
import me.chenhewen.learnble2.event.DeviceItemChangedEvent;
import me.chenhewen.learnble2.model.ActionItem;
import me.chenhewen.learnble2.model.DeviceItem;
import me.chenhewen.learnble2.model.DeviceItemTemplate;
import me.chenhewen.learnble2.model.ScanItem;

public class BluetoothDealer {
    
    public BluetoothDealer(Context context, BluetoothManager bluetoothManager) {
        this.context = context;
        bluetoothAdapter = bluetoothManager.getAdapter();
        templates.add(DeviceItemTemplate.templateOnOFF);
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
    public List<DeviceItemTemplate> templates = new ArrayList<>();

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
            EventBus.getDefault().post(new DeviceItemChangedEvent(deviceItem, DeviceItemChangedEvent.Operation.ADD));
        }
    }

    public void removeDeviceItem(DeviceItem deviceItem) {
        deviceItems.removeIf( (item)-> item.address.equals(deviceItem.address));
        // 广播数据变化
        EventBus.getDefault().post(new DeviceItemChangedEvent(deviceItem, DeviceItemChangedEvent.Operation.REMOVE));
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


    // Templates

    @SuppressLint("CheckResult")
    public void saveTemplatesAsync(Context context) {
        Observable.create(emitter -> {
            try {
                // 在这里执行发送数据的操作
                Gson gson = new Gson();
                String jsonString = gson.toJson(templates);
                FileUtils.saveStringToFile(context, Const.TEMPLATE_FILE_NAME, jsonString);

                emitter.onComplete(); // 操作完成
            } catch (Exception e) {
                emitter.onError(e); // 如果发生错误，通知错误
            }
        })
        .subscribeOn(Schedulers.io()) // 在IO线程执行
        .observeOn(AndroidSchedulers.mainThread()) // 在主线程观察
        .subscribe(
            data -> {
                // 这里处理返回的数据并更新UI
                System.out.println("获取到数据: " + data);
            },
            throwable -> {
                // 处理错误
                System.err.println("发生错误: " + throwable.getMessage());
            }
        );
    }

    @SuppressLint("CheckResult")
    public void fetchTemplatesAsync(Context context) {
        Observable<List<DeviceItemTemplate>> objectObservable = Observable.create(emitter -> {
            try {
                Gson gson = new Gson();
                String jsonString = FileUtils.readStringFromFile(context, Const.TEMPLATE_FILE_NAME);
                Type deviceListType = new TypeToken<List<DeviceItemTemplate>>() {
                }.getType();
                List<DeviceItemTemplate> list = gson.fromJson(jsonString, deviceListType);

                emitter.onNext(list);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
        objectObservable.subscribeOn(Schedulers.io());
        objectObservable.observeOn(AndroidSchedulers.mainThread());
        objectObservable.subscribe(
            (List<DeviceItemTemplate> data) -> {
                // 这里处理返回的数据并更新UI
                Gson gson = new Gson();
                String jsonString = gson.toJson(data);
                System.out.println("获取到数据: " + jsonString);
            },
            throwable -> {
                // 处理错误
                System.err.println("发生错误: " + throwable.getMessage());
            });

    }
}
