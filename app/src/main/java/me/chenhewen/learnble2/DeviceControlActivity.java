package me.chenhewen.learnble2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import me.chenhewen.learnble2.model.ScanItem;

public class DeviceControlActivity extends AppCompatActivity {

    // 常量
    private static int REQUEST_CODE = 10;
    private static final long SCAN_PERIOD = 10000; // Stops scanning after 10 seconds.
    private String deviceAddress = "";

    // 服务
    private BluetoothLeService bluetoothService;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    // 视图
    private Button sendButton;
    private Spinner spinner;


    private boolean scanning;
    private Handler handler = new Handler();
    private List<ScanItem> scanItems = new ArrayList();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothService = ((BluetoothLeService.LocalBinder) service).getService();
            if (bluetoothService != null) {
                if (!bluetoothService.initialize()) {
                    System.out.println("chw: Unable to initialize Bluetooth");
                    finish();
                }
                // perform device connection
                bluetoothService.connect(deviceAddress);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };
    private MySpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_layout);

        sendButton = findViewById(R.id.send_button);
        spinner = findViewById(R.id.spinner);

        spinnerAdapter = new MySpinnerAdapter();
        spinner.setAdapter(spinnerAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothService == null) {
                    return;
                }

//                bluetoothService.send("whatever");
            }
        });

        bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (checkPermission()) {
            scanLeDevice();
        }

//        deviceAddress = DataHub.deviceAddress;

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("chw no permission and shoot a request");
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

        return true;
    }

    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    scanning = false;
                    System.out.println("chw: postDelayed stopScan");
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            scanItems.clear();
            System.out.println("chw: startScan");
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            System.out.println("chw: stopScan");
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    // Device scan callback.
    @SuppressLint("MissingPermission")
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    String deviceAddress = result.getDevice().getAddress();
                    String name = result.getDevice().getName();
                    System.out.println("chw: ScanCallback result: " + name + " "+ deviceAddress);

//                    if ("CC2340TR2.4-GC".equals(name)) {
//                        DataHub.deviceAddress = deviceAddress;
//                    }

                    ScanItem scanItem = new ScanItem(name, deviceAddress, 0);
                    scanItems.add(scanItem);
                    spinnerAdapter.notifyDataSetChanged();
                }
            };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，可以继续操作
                System.out.println("chw: onRequestPermissionsResult grant");
                scanLeDevice();
            } else {
                // 用户拒绝了权限，处理拒绝情况
                System.out.println("chw: onRequestPermissionsResult reject");
            }
        }
    }

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                connected = true;
//                updateConnectionState(R.string.connected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

//                connected = false;
//                updateConnectionState(R.string.disconnected);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bluetoothService != null) {
            final boolean result = bluetoothService.connect(deviceAddress);
            System.out.println("chw: Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }
    
    
    public static void start(Context context) {
        Intent intent = new Intent(context, DeviceControlActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public class MySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        @Override
        public int getCount() {
            return scanItems.size();
        }

        @Override
        public Object getItem(int position) {
            return scanItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(DeviceControlActivity.this);
                convertView = inflater.inflate(R.layout.device_item, parent, false);
            }

            bindView(position, convertView, parent, scanItems.get(position));

            return convertView;
        }

        private void bindView(int position, View convertView, ViewGroup parent, ScanItem scanItem) {
            // 由于列表数量不会很多，我们直接简写
            TextView nameView = convertView.findViewById(R.id.device_name);
            TextView addressView = convertView.findViewById(R.id.device_address);
            Button connectButton = convertView.findViewById(R.id.device_connect_button);

            nameView.setText(scanItem.name);
            addressView.setText(scanItem.address);
            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothService.connect(scanItem.address);
                }
            });
        }
    }
}