package me.chenhewen.learnble2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DeviceItem implements Serializable {

    public String id = UUID.randomUUID().toString();
    public String name;
    public String address;
    public List<ActionItem> actionItems = new ArrayList<>();

    public DeviceItem(String name, String address, List<ActionItem> actionItems) {
        this.name = name;
        this.address = address;
        this.actionItems = actionItems;
    }

    public DeviceItem(ScanItem scanItem) {
        this.name = scanItem.name;
        this.address = scanItem.address;
        // TODO:
        this.actionItems = DeviceItemTemplate.templateOnOFF.actionItems;
    }

    public static List<DeviceItem> mockItems = new ArrayList<>(Arrays.asList(
            new DeviceItem("MockDevice-1", "mac.1.1.1.1", ActionItem.mockItems)
//            new DeviceItem("MockDevice-2", "mac.2.2.2.2", ActionItem.mockItems)
    ));


}
