package me.chenhewen.learnble2.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceItem implements Serializable {
    public DeviceItem(String name, String address, int rssi) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
    }

    public String name;
    public String address;
    public int rssi;

    public static void sortByRssi(List<DeviceItem> deviceList) {
        deviceList.sort(new Comparator<DeviceItem>() {
            @Override
            public int compare(DeviceItem d1, DeviceItem d2) {
                return Integer.compare(d2.rssi, d1.rssi); // 从大到小排序
            }
        });
    }

    public static class MessageEvent {
        public MessageEvent(List<DeviceItem> deviceItems) {
            this.deviceItems = deviceItems;
        }

        public List<DeviceItem> deviceItems;
    }
}
