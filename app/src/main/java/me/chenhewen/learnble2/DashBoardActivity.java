package me.chenhewen.learnble2;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chenhewen.learn.TabFragmentItem;
import me.chenhewen.learn.TabFragmentManager;
import me.chenhewen.learn.TabLayoutFragmentActivity;
import me.chenhewen.learnble2.dealer.BluetoothDealer;
import me.chenhewen.learnble2.event.BluetoothStateEvent;
import me.chenhewen.learnble2.model.DeviceItem;

public class DashBoardActivity extends AppCompatActivity {

    private static int REQUEST_CODE = 10;

    private BluetoothDealer bluetoothDealer;
    public TabFragmentManager tabFragmentManager;

    View inlineNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestPermission();
        EventBus.getDefault().register(this);

        bluetoothDealer = BLEApplication.getBluetoothDealer();

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        View fragmentAnchorView = findViewById(R.id.fragment_anchor);

        tabFragmentManager = new TabFragmentManager(tabLayout, fragmentAnchorView, getApplicationContext(), getSupportFragmentManager());
        tabFragmentManager.addTab("Scanner", new ScannerFragment(), false);
        for (DeviceItem deviceItem : DeviceItem.mockItems) {
            tabFragmentManager.addTab(deviceItem.name, DeviceFragment.newInstance(deviceItem), true);
        }

        inlineNotification = findViewById(R.id.inline_notification);
        View enableBluetoothButton = findViewById(R.id.inline_notification_enable_button);
        inlineNotification.setVisibility(bluetoothDealer.isBluetoothEnable() ? View.GONE : View.VISIBLE);
        enableBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothDealer.enableBluetooth();
            }
        });
    }

    private boolean requestPermission() {
//        System.out.println("chw no permission and shoot a request");
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
        }, REQUEST_CODE);

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，可以继续操作
//                System.out.println("chw: onRequestPermissionsResult grant");
            } else {
                // 用户拒绝了权限，处理拒绝情况
//                System.out.println("chw: onRequestPermissionsResult reject");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BluetoothStateEvent event) {
        switch (event.bluetoothState) {
            case BluetoothAdapter.STATE_ON:
                inlineNotification.setVisibility(View.GONE);
                break;
            case BluetoothAdapter.STATE_OFF:
                inlineNotification.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}