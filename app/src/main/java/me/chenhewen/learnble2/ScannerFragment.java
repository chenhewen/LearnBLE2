package me.chenhewen.learnble2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chenhewen.learnble2.dealer.BluetoothDealer;
import me.chenhewen.learnble2.model.DeviceItem;

public class ScannerFragment extends Fragment {

    // 常量
    private static final String ARG_SAVE_STATE = "ARG_SAVE_STATE";

    // 内部状态变量
    private Handler handler = new Handler();

    // 视图
    private MyRecyclerAdapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // 数据
    private BluetoothDealer bluetoothDealer;
    private List<DeviceItem> mockItems = new ArrayList<>(Arrays.asList(
            new DeviceItem("AAAAA", "1.A.B.C.D.E.F", -10),
            new DeviceItem("BBBBB", "2.A.B.C.D.E.F", -20)
    ));
    private List<DeviceItem> deviceItems = mockItems;

//    private static final String ARG_deviceItems = "ARG_deviceItems";
//
//    public static ScannerFragment newInstance(List<DeviceItem> deviceItems) {
//        ScannerFragment fragment = new ScannerFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_deviceItems, new ArrayList<>(deviceItems));
//        fragment.setArguments(args);
//
//        return fragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        bluetoothDealer = BLEApplication.getBluetoothDealer();

        if (bluetoothDealer.deviceItems.isEmpty()) {
            bluetoothDealer.scanLeDevice();
        } else {
            deviceItems = bluetoothDealer.deviceItems;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_scanner, container, false);
        swipeRefreshLayout = contentView.findViewById(R.id.swipe_refresh_layout);

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
            DeviceItem deviceItem = deviceItems.get(position);
            holder.nameView.setText(deviceItem.name);
            holder.addressView.setText(deviceItem.address);
            holder.connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO:
                }
            });
        }

        @Override
        public int getItemCount() {
            return deviceItems.size();
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
    public void onMessageEvent(DeviceItem.MessageEvent event) {
        this.deviceItems = event.deviceItems;
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}