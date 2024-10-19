package me.chenhewen.learnble2;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import me.chenhewen.learnble2.dealer.BluetoothDealer;
import me.chenhewen.learnble2.model.ActionItem;
import me.chenhewen.learnble2.model.DeviceItem;

public class BottomSheet {

    public BottomSheet(Context context, BluetoothLeService bluetoothService, BluetoothDealer bluetoothDealer, DeviceItem deviceItem) {
        this.context = context;
        this.bluetoothService = bluetoothService;
        this.bluetoothDealer = bluetoothDealer;
        this.deviceItem = deviceItem;
    }

    private Context context;
    private BluetoothLeService bluetoothService;
    private BluetoothDealer bluetoothDealer;
    private DeviceItem deviceItem;

    private static class ValueWrapper<T> {
        T value;
        public ValueWrapper(T value) {
            this.value = value;
        }
    }


    public void openSheet(final ActionItem actionItem) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View sheetContentView = LayoutInflater.from(context).inflate(R.layout.device_add_sheet, null);
        bottomSheetDialog.setContentView(sheetContentView);
        bottomSheetDialog.show();

        // 数据
        String serviceUuid = actionItem == null ? Const.PREFER_SERVICE_UUID : actionItem.serviceUuid;
        String characteristicUuid = actionItem == null ? Const.PREFER_CHARACTERISTIC_UUID : actionItem.characteristicUuid;
        String title = actionItem == null ? "" : actionItem.title;
        ActionItem.SendDataType sendDataType = actionItem == null ? ActionItem.SendDataType.STRING : actionItem.sendDataType;
        String sendMessage = "";
        if (actionItem != null) {
            if (actionItem.sendDataType == ActionItem.SendDataType.STRING) {
                sendMessage = actionItem.sendString;
            } else if (actionItem.sendDataType == ActionItem.SendDataType.HEX) {
                sendMessage = ActionItem.convertByteArrayToHexString(actionItem.sendHex);
            }
        }

        ValueWrapper<String> serviceUuidWrapper = new ValueWrapper<>(serviceUuid);
        ValueWrapper<String> characteristicUuidWrapper = new ValueWrapper<>(characteristicUuid);
        ValueWrapper<ActionItem.SendDataType> sendDataTypeWrapper = new ValueWrapper<>(sendDataType);

        List<String> serviceUuids = bluetoothDealer.getAllServiceUuids(deviceItem.address);
        List<String> characteristicUuids = bluetoothDealer.getAllCharacteristicUuids(deviceItem.address, serviceUuid);


        // service UUID
        ArrayAdapter<String> serviceUuidAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, serviceUuids);
        AutoCompleteTextView serviceUuidSelectionView = sheetContentView.findViewById(R.id.service_uuid_selection_view);
        serviceUuidSelectionView.setAdapter(serviceUuidAdapter);
        serviceUuidSelectionView.setText(serviceUuidWrapper.value, false);

        // characteristic UUID
        ArrayAdapter<String> characteristicUuidAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, characteristicUuids);
        AutoCompleteTextView characteristicUuidSelectionView = sheetContentView.findViewById(R.id.characteristic_uuid_selection_view);
        characteristicUuidSelectionView.setAdapter(characteristicUuidAdapter);
        characteristicUuidSelectionView.setText(characteristicUuidWrapper.value, false);

        // Title
        TextInputEditText titleView = sheetContentView.findViewById(R.id.title_view);
        titleView.setText(title);

        // Data type
        ActionItem.SendDataType[] dataTypeItems = ActionItem.SendDataType.values();
        ArrayAdapter<ActionItem.SendDataType> dataTypeAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, dataTypeItems);
        AutoCompleteTextView dataTypeSelectionView = sheetContentView.findViewById(R.id.data_type_selection);
        dataTypeSelectionView.setAdapter(dataTypeAdapter);
        dataTypeSelectionView.setText(sendDataTypeWrapper.value.getRawValue(), false);

        // Send message
        TextInputEditText msgView = sheetContentView.findViewById(R.id.msg_view);
        msgView.setText(sendMessage);

        // Actions
        View sendButton = sheetContentView.findViewById(R.id.send_button);
        View saveButton = sheetContentView.findViewById(R.id.save_button);

        // 交互
        serviceUuidSelectionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                serviceUuidWrapper.value = serviceUuids.get(position);
                List<String> characteristicUuids = bluetoothDealer.getAllCharacteristicUuids(deviceItem.address, serviceUuidWrapper.value);
                ArrayAdapter<String> newAdapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_dropdown_item_1line, characteristicUuids);
                characteristicUuidSelectionView.setAdapter(newAdapter);
                characteristicUuidWrapper.value = characteristicUuids.isEmpty() ? "" : characteristicUuids.get(0);
                characteristicUuidSelectionView.setText(characteristicUuidWrapper.value, false);
            }
        });

        InputFilter hexInputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String hexChars = "0123456789ABCDEFabcdef";
                for (int i = start; i < end; i++) {
                    if (!hexChars.contains(String.valueOf(source.charAt(i)))) {
                        return "";  // 如果字符不合法，阻止输入
                    }
                }
                return null;  // 返回 null 表示输入合法
            }
        };

        dataTypeSelectionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendDataTypeWrapper.value = dataTypeItems[position];
                if (sendDataTypeWrapper.value.equals(ActionItem.SendDataType.STRING)) {
                    msgView.setFilters(new InputFilter[0]);
                } else if (sendDataTypeWrapper.value.equals(ActionItem.SendDataType.HEX)) {
                    msgView.setFilters(new InputFilter[] { hexInputFilter });
                }
                msgView.setText("");
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleView.getText().toString();
                String msg = msgView.getText().toString();
                byte[] sendMsg = new byte[0];
                if (sendDataTypeWrapper.value == ActionItem.SendDataType.STRING) {
                    sendMsg = msg.getBytes();
                } else if (sendDataTypeWrapper.value == ActionItem.SendDataType.HEX) {
                    sendMsg = ActionItem.convertHexStringToByteArray(msg);
                }


                if (bluetoothService != null) {
                    bluetoothService.send(
                            deviceItem.address,
                            serviceUuidWrapper.value,
                            characteristicUuidWrapper.value,
                            sendMsg);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleView.getText().toString();
                String msg = msgView.getText().toString();

                ActionItem tempActionItem = null;
                if (sendDataTypeWrapper.value == ActionItem.SendDataType.STRING) {
                    tempActionItem = new ActionItem(serviceUuidWrapper.value, characteristicUuidWrapper.value, title, sendDataTypeWrapper.value, msg, null);
                } else if (sendDataTypeWrapper.value == ActionItem.SendDataType.HEX) {
                    byte[] msgInHex = ActionItem.convertHexStringToByteArray(msg);
                    tempActionItem = new ActionItem(serviceUuidWrapper.value, characteristicUuidWrapper.value, title, sendDataTypeWrapper.value, null, msgInHex);
                }

                if (actionItem != null) {
                    bluetoothDealer.removeActionItem(deviceItem, actionItem);
                    bluetoothDealer.addActionItem(deviceItem, tempActionItem);
                } else {
                    bluetoothDealer.addActionItem(deviceItem, tempActionItem);
                }

                bottomSheetDialog.dismiss();
            }
        });
    }
}
