package me.chenhewen.learnble2;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;

import com.tencent.bugly.crashreport.CrashReport;

import me.chenhewen.learnble2.dealer.BluetoothDealer;
import me.chenhewen.learnble2.dealer.SettingDealer;

public class BLEApplication extends Application {

    private static BluetoothDealer bluetoothDealer;
    private static SettingDealer settingDealer;

    @Override
    public void onCreate() {
        super.onCreate();

        listenBluetoothStateChange();

        BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothDealer = new BluetoothDealer(this, bluetoothManager);
        settingDealer = new SettingDealer(this);

        CrashReport.initCrashReport(getApplicationContext());
    }

    public static BluetoothDealer getBluetoothDealer() {
        return bluetoothDealer;
    }

    public static SettingDealer getSettingDealer() {
        return settingDealer;
    }

    private void listenBluetoothStateChange() {
        // 初始化广播接收器
        BluetoothStateReceiver bluetoothStateReceiver = new BluetoothStateReceiver();
        // 定义 IntentFilter 以接收蓝牙状态的广播
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter); // 注册广播接收器
    }

}
