package me.chenhewen.learnble2;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chenhewen.learn.TabFragmentManager;
import me.chenhewen.learnble2.data.GattServiceItem;
import me.chenhewen.learnble2.dealer.BluetoothDealer;
import me.chenhewen.learnble2.event.BluetoothStateEvent;
import me.chenhewen.learnble2.model.ActionItem;
import me.chenhewen.learnble2.model.DeviceItem;
import me.chenhewen.learnble2.model.DeviceItemTemplate;
import me.chenhewen.learnble2.model.ScanItem;

public class ScannerFragment extends Fragment {
    // 常量
    private static final String ARG_SAVE_STATE = "ARG_SAVE_STATE";

    // 服务
    private BluetoothLeService bluetoothService;

    // 内部状态变量
    private Handler handler = new Handler();

    // 视图
    private MyRecyclerAdapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // 数据
    private BluetoothDealer bluetoothDealer;
    private List<ScanItem> mockItems = new ArrayList<>(Arrays.asList(
            new ScanItem("AAAAA", "1.A.B.C.D.E.F", -10),
            new ScanItem("BBBBB", "2.A.B.C.D.E.F", -20)
    ));
    private List<ScanItem> scanItems = mockItems;

//    private static final String ARG_1 = "ARG_1";
//
//    public static ScannerFragment newInstance(TabFragmentManager tabFragmentManager) {
//        ScannerFragment fragment = new ScannerFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_1, tabFragmentManager);
//        fragment.setArguments(args);
//
//        return fragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        bluetoothDealer = BLEApplication.getBluetoothDealer();

        if (bluetoothDealer.scanItems.isEmpty()) {
            if (bluetoothDealer.isBluetoothEnable()) {
                bluetoothDealer.scanLeDevice();
            }
        } else {
            scanItems = bluetoothDealer.scanItems;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_scanner, container, false);
        swipeRefreshLayout = contentView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setEnabled(bluetoothDealer.isBluetoothEnable());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bluetoothDealer.scanLeDevice();
                // 设置5秒后自动停止刷新动画
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 5000); // 5秒延迟
            }
        });

        RecyclerView recyclerView = contentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new MyRecyclerAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
        return contentView;
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.scanner_recycler_view_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ScanItem scanItem = scanItems.get(position);
            holder.nameView.setText(scanItem.name);
            holder.addressView.setText(scanItem.address);
            holder.connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startBluetoothLeService(scanItem);
                }
            });
        }

        @Override
        public int getItemCount() {
            return scanItems.size();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView addressView;
        Button connectButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.item_title);
            connectButton = itemView.findViewById(R.id.item_connect_button);
            addressView = itemView.findViewById(R.id.item_address);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScanItem.MessageEvent event) {
        this.scanItems = event.scanItems;
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BluetoothStateEvent event) {
        swipeRefreshLayout.setEnabled(event.bluetoothState == BluetoothAdapter.STATE_ON);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void startBluetoothLeService(ScanItem scanItem) {
        Intent gattServiceIntent = new Intent(getContext(), BluetoothLeService.class);
        MyBLEServiceConnection serviceConnection = new MyBLEServiceConnection(scanItem);
        getContext().bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private class MyBLEServiceConnection implements ServiceConnection {

        public MyBLEServiceConnection(ScanItem scanItem) {
            this.scanItem = scanItem;
        }

        private ScanItem scanItem;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothService = ((BluetoothLeService.LocalBinder) service).getService();
            if (bluetoothService != null) {
                if (!bluetoothService.initialize()) {
                    Toast.makeText(getContext(), "Fail to connect", Toast.LENGTH_SHORT).show();
                }
                // 连接设备
                boolean success = bluetoothService.connect(scanItem.address);
                if (success) {
                    // 增加Tab
                    DeviceItem saveDeviceItemForCurrentAddress = bluetoothDealer.savedDeviceItemMap.get(scanItem.address);
                    List<ActionItem> actionItems = saveDeviceItemForCurrentAddress == null ? DeviceItemTemplate.templateOnOFF.actionItems : saveDeviceItemForCurrentAddress.actionItems;
                    bluetoothDealer.addDeviceItem(new DeviceItem(scanItem.name, scanItem.address, actionItems));
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    }
}