package me.chenhewen.learnble2;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import me.chenhewen.learnble2.event.BluetoothStateEvent;

public class BluetoothStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
//            int bluetoothState;
//            switch (state) {
//                case BluetoothAdapter.STATE_ON:
//                    // 蓝牙开启
//                    bluetoothState = BluetoothAdapter.STATE_ON;
//                    break;
//                case BluetoothAdapter.STATE_OFF:
//                    // 蓝牙关闭
//                    bluetoothState = BluetoothAdapter.STATE_OFF;
//                    break;
//                case BluetoothAdapter.STATE_TURNING_ON:
//                    bluetoothState = BluetoothAdapter.STATE_TURNING_ON;
//                    break;
//                case BluetoothAdapter.STATE_TURNING_OFF:
//                    bluetoothState = BluetoothAdapter.STATE_TURNING_OFF;
//                    break;
//            }
            EventBus.getDefault().post(new BluetoothStateEvent(state));
        }
    }
}