package me.chenhewen.learnble2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScannerFragment extends Fragment {

    private List<DeviceItem> mockItems = new ArrayList<>(Arrays.asList(
            new DeviceItem("AAAAA", "1.A.B.C.D.E.F"),
            new DeviceItem("BBBBB", "2.A.B.C.D.E.F")
    ));
    private List<DeviceItem> deviceItems = mockItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_scanner, container, false);
        RecyclerView recyclerView = contentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyRecyclerAdapter());
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
            holder.addressView.setText(deviceItem.macAddress);
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

    public static class DeviceItem {
        public DeviceItem(String name, String macAddress) {
            this.name = name;
            this.macAddress = macAddress;
        }

        public String name;
        public String macAddress;
    }

}