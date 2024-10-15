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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chenhewen.learn.TabFragmentItem;
import me.chenhewen.learn.TabFragmentManager;
import me.chenhewen.learn.TabLayoutFragmentActivity;
import me.chenhewen.learnble2.dealer.BluetoothDealer;

public class DashBoardActivity extends AppCompatActivity {

    private static int REQUEST_CODE = 10;

    private BluetoothDealer bluetoothDealer;
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

        listenBluetoothStateChange();
        checkPermission();

        bluetoothDealer = BLEApplication.getBluetoothDealer();

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayout.Tab scannerTab = tabLayout.getTabAt(0);
        TabLayout.Tab device1Tab = tabLayout.getTabAt(1);
        View fragmentAnchorView = findViewById(R.id.fragment_anchor);

        TabFragmentItem tabFragmentItem1 = new TabFragmentItem(scannerTab, new ScannerFragment(), false, true);
        TabFragmentItem tabFragmentItem2 = new TabFragmentItem(device1Tab, new DeviceFragment(), false, false);
        List<TabFragmentItem> initialTabFragments = new ArrayList<>(Arrays.asList(tabFragmentItem1, tabFragmentItem2));
        TabFragmentManager tabFragmentManager = new TabFragmentManager(tabLayout, fragmentAnchorView, getApplicationContext(), getSupportFragmentManager());
        tabFragmentManager.init(initialTabFragments);

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

    private void listenBluetoothStateChange() {
        // 初始化广播接收器
        BluetoothStateReceiver bluetoothStateReceiver = new BluetoothStateReceiver();
        // 定义 IntentFilter 以接收蓝牙状态的广播
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter); // 注册广播接收器
    }

    private boolean checkPermission() {
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

    private class BluetoothStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        // 蓝牙关闭
                        inlineNotification.setVisibility(View.VISIBLE);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        // 蓝牙开启
                        inlineNotification.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}