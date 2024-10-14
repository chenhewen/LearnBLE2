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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chenhewen.learnble2.model.InfoItem;

public class DeviceFragment extends Fragment {

    private List<InfoItem> infoItems = InfoItem.mockItems;

    private String[] uuidMockItems = new String[] {"A-A-A-A", "B-B-B-B", "C-C-C-C", "D-D-D-D"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_device, container, false);
        RecyclerView recyclerView = contentView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new DeviceFragment.MyRecyclerAdapter());

        View fabButton = contentView.findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSheet(null);
            }
        });

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
            paddingLastItem(holder, position, infoItems.size());
            InfoItem infoItem = infoItems.get(position);
            holder.titlView.setText(infoItem.title);
            if (infoItem.sendDataType == InfoItem.SendDataType.STRING) {
                holder.dataStringView.setVisibility(View.VISIBLE);
                holder.dataStringView.setText(infoItem.sendString);
                holder.dataHexView.setText(infoItem.getDisplayHexString());
            } else if (infoItem.sendDataType == InfoItem.SendDataType.HEX) {
                holder.dataStringView.setVisibility(View.GONE);
            }
            holder.dataHexView.setText(infoItem.getDisplayHexString());

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
                                openSheet(infoItem);
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

        private void paddingLastItem(MyViewHolder holder, int position, int listSize) {
            if (position == listSize - 1){
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.bottomMargin = 500;
                holder.itemView.setLayoutParams(layoutParams);
            } else{
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.bottomMargin = 0;
                holder.itemView.setLayoutParams(layoutParams);
            }
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titlView;
        TextView dataStringView;
        TextView dataHexView;
        ImageButton moreButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titlView = itemView.findViewById(R.id.item_title);
            dataStringView = itemView.findViewById(R.id.item_send_string);
            dataHexView = itemView.findViewById(R.id.item_send_hex);
            moreButton = itemView.findViewById(R.id.item_more_button);
        }
    }

    private void openSheet(InfoItem infoItem) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View sheetContentView = LayoutInflater.from(getContext()).inflate(R.layout.device_add_sheet, null);
        bottomSheetDialog.setContentView(sheetContentView);
        bottomSheetDialog.show();

        InfoItem mockInfoItem = infoItems.get(1);
        if (infoItem == null) {
            infoItem = mockInfoItem;
        }

        // UUID
        String[] uuidItems = uuidMockItems;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, uuidItems);
        AutoCompleteTextView uuidSelectionView = sheetContentView.findViewById(R.id.uuid_selection_view);
        uuidSelectionView.setAdapter(adapter);
        uuidSelectionView.setText(infoItem.uuid, false);

        // Title
        TextInputEditText titleView = sheetContentView.findViewById(R.id.title_view);
        titleView.setText(infoItem.title);

        // Data type
        String[] dataTypeItems = InfoItem.SendDataType.getAllValues();
        ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, dataTypeItems);
        AutoCompleteTextView dataTypeSelectionView = sheetContentView.findViewById(R.id.data_type_selection);
        dataTypeSelectionView.setAdapter(dataTypeAdapter);
        dataTypeSelectionView.setText(infoItem.sendDataType.getRawValue(), false);

        // Send message
        TextInputEditText msgView = sheetContentView.findViewById(R.id.msg_view);
        if (infoItem.sendDataType == InfoItem.SendDataType.STRING) {
            msgView.setText(infoItem.sendString);
        } else if (infoItem.sendDataType == InfoItem.SendDataType.HEX) {
            msgView.setText(InfoItem.convertIntArrayToHexString(infoItem.sendHex));
        }

        // Actions
        View sendButton = sheetContentView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleView.getText().toString();
                System.out.println("title: " + title);
                bottomSheetDialog.dismiss();
            }
        });
    }
}