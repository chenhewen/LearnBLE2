package me.chenhewen.learnble2;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        connectButton = findViewById(R.id.connect_button);
//        connectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DeviceControlActivity.start(MainActivity.this);
//            }
//        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (checkPermission()) {
            scanLeDevice();
        }
    }

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private Handler handler = new Handler();
    private static int REQUEST_CODE = 10;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @SuppressLint("MissingPermission")
    private void makeSelfDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); // 300秒可见时间
        startActivity(discoverableIntent);

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

//                    android.Manifest.permission.BLUETOOTH_ADVERTISE
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
            System.out.println("chw: startScan");
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            System.out.println("chw: stopScan");
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

//    private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();

    // Device scan callback.
    @SuppressLint("MissingPermission")
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
//                    leDeviceListAdapter.addDevice(result.getDevice());
//                    leDeviceListAdapter.notifyDataSetChanged();
                    String deviceAddress = result.getDevice().getAddress();
                    String name = result.getDevice().getName();
                    System.out.println("chw: ScanCallback result: " + name + " "+ deviceAddress);

                    if ("CC2340TR2.4-GC".equals(name)) {
                        DataHub.deviceAddress = deviceAddress;
                    }
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
}