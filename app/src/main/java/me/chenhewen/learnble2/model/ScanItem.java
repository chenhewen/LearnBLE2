package me.chenhewen.learnble2.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class ScanItem implements Serializable {
    public ScanItem(String name, String address, int rssi) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
    }

    public String name;
    public String address;
    public int rssi;

    public static void sortByRssi(List<ScanItem> deviceList) {
        deviceList.sort(new Comparator<ScanItem>() {
            @Override
            public int compare(ScanItem d1, ScanItem d2) {
                return Integer.compare(d2.rssi, d1.rssi); // 从大到小排序
            }
        });
    }

    public static class MessageEvent {
        public MessageEvent(List<ScanItem> scanItems) {
            this.scanItems = scanItems;
        }

        public List<ScanItem> scanItems;
    }
}
