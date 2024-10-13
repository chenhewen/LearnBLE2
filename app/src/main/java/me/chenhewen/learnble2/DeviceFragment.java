package me.chenhewen.learnble2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceFragment extends Fragment {

    private List<InfoItem> mockItems = new ArrayList<>(Arrays.asList(
        new InfoItem("Hello"),
        new InfoItem("World")
    ));
    private List<InfoItem> infoItems = mockItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_device, container, false);
        RecyclerView recyclerView = contentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new DeviceFragment.MyRecyclerAdapter());
        return contentView;
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<DeviceFragment.MyViewHolder> {

        @NonNull
        @Override
        public DeviceFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_recycler_view_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceFragment.MyViewHolder holder, int position) {
            InfoItem infoItem = infoItems.get(position);
            holder.dataStringView.setText(infoItem.sendString);
            holder.dataHexView.setText(infoItem.getSendHexString());
            holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 创建 PopupMenu
                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.send_item_popup_menu, popupMenu.getMenu());

                    // 处理菜单点击事件
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.action_send) {
                                // TODO:
                            } else if (item.getItemId() == R.id.action_edit) {
                                // TODO:
                            } else if (item.getItemId() == R.id.action_delete) {
                                // TODO:
                            }

                            return false;
                        }
                    });

                    // 显示 PopupMenu
                    popupMenu.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return infoItems.size();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dataStringView;
        TextView dataHexView;
        ImageButton moreButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dataStringView = itemView.findViewById(R.id.item_send_string);
            dataHexView = itemView.findViewById(R.id.item_send_hex);
            moreButton = itemView.findViewById(R.id.item_more_button);
        }
    }

    public static class InfoItem {
        public InfoItem(String sendString) {
            this.sendString = sendString;
        }

        public String sendString;

        public String getSendHexString() {
            StringBuilder hexString = new StringBuilder("0x: ");

            for (int i = 0; i < sendString.length(); i++) {
                // 获取字符的16进制表示
                String hex = Integer.toHexString(sendString.charAt(i));
                // 保证每个字符占两个字节，不足的补0
                if (hex.length() < 2) {
                    hexString.append("0");
                }
                hexString.append(hex);

                // 如果不是最后一个字符，添加逗号
                if (i < sendString.length() - 1) {
                    hexString.append(", ");
                }
            }

            return hexString.toString();
        }
    }
}