package me.chenhewen.learnble2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.List;

import me.chenhewen.learnble2.model.ActionItem;
import me.chenhewen.learnble2.model.DeviceItem;

public class DeviceFragment extends Fragment {

    public static final String ARG_DEVICE_ITEM = "ARG_DEVICE_ITEM";

    private DeviceItem deviceItem;

    private String[] uuidMockItems = new String[] {"A-A-A-A", "B-B-B-B", "C-C-C-C", "D-D-D-D"};

    public static DeviceFragment newInstance(DeviceItem deviceItem) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DEVICE_ITEM, deviceItem);
        DeviceFragment fragment = new DeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        System.out.println("chw: DeviceFragment onCreate");
        if (args != null) {
            System.out.println("chw: DeviceFragment onCreate args != null");
            deviceItem = (DeviceItem) args.getSerializable(ARG_DEVICE_ITEM);
        } else if (savedInstanceState != null) {
            System.out.println("chw: DeviceFragment onCreate savedInstanceState != null");
            deviceItem = (DeviceItem) savedInstanceState.getSerializable(ARG_DEVICE_ITEM);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("chw: onSaveInstanceState");
        outState.putSerializable(ARG_DEVICE_ITEM, deviceItem);
    }

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
            paddingLastItem(holder, position, deviceItem.actionItems.size());
            ActionItem actionItem = deviceItem.actionItems.get(position);
            holder.titlView.setText(actionItem.title);
            if (actionItem.sendDataType == ActionItem.SendDataType.STRING) {
                holder.dataStringView.setVisibility(View.VISIBLE);
                holder.dataStringView.setText(actionItem.sendString);
                holder.dataHexView.setText(actionItem.getDisplayHexString());
            } else if (actionItem.sendDataType == ActionItem.SendDataType.HEX) {
                holder.dataStringView.setVisibility(View.GONE);
            }
            holder.dataHexView.setText(actionItem.getDisplayHexString());

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
                                openSheet(actionItem);
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
            return deviceItem.actionItems.size();
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

    private void openSheet(ActionItem actionItem) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View sheetContentView = LayoutInflater.from(getContext()).inflate(R.layout.device_add_sheet, null);
        bottomSheetDialog.setContentView(sheetContentView);
        bottomSheetDialog.show();

        ActionItem mockActionItem = deviceItem.actionItems.get(1);
        if (actionItem == null) {
            actionItem = mockActionItem;
        }

        // UUID
        String[] uuidItems = uuidMockItems;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, uuidItems);
        AutoCompleteTextView uuidSelectionView = sheetContentView.findViewById(R.id.uuid_selection_view);
        uuidSelectionView.setAdapter(adapter);
        uuidSelectionView.setText(actionItem.uuid, false);

        // Title
        TextInputEditText titleView = sheetContentView.findViewById(R.id.title_view);
        titleView.setText(actionItem.title);

        // Data type
        String[] dataTypeItems = ActionItem.SendDataType.getAllValues();
        ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, dataTypeItems);
        AutoCompleteTextView dataTypeSelectionView = sheetContentView.findViewById(R.id.data_type_selection);
        dataTypeSelectionView.setAdapter(dataTypeAdapter);
        dataTypeSelectionView.setText(actionItem.sendDataType.getRawValue(), false);

        // Send message
        TextInputEditText msgView = sheetContentView.findViewById(R.id.msg_view);
        if (actionItem.sendDataType == ActionItem.SendDataType.STRING) {
            msgView.setText(actionItem.sendString);
        } else if (actionItem.sendDataType == ActionItem.SendDataType.HEX) {
            msgView.setText(ActionItem.convertIntArrayToHexString(actionItem.sendHex));
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