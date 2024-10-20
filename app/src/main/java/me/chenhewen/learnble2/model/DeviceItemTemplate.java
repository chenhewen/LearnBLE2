package me.chenhewen.learnble2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DeviceItemTemplate {

    public DeviceItemTemplate(int targetDeviceNo, String templateName, List<ActionItem> actionItems) {
        this.targetDeviceNo = targetDeviceNo;
        this.templateName = templateName;
        this.actionItems = actionItems;
    }

    public String id = UUID.randomUUID().toString();
    public int targetDeviceNo;
    public String templateName;
    public List<ActionItem> actionItems = new ArrayList<>();

    public static DeviceItemTemplate templateOnOFF = new DeviceItemTemplate(
            0,
            "ON & OFF",
            new ArrayList<>(Arrays.asList(
                    new ActionItem("0000ff10-0000-1000-8000-00805f9b34fb",
                            "0000ff11-0000-1000-8000-00805f9b34fb",
                            "ON",
                            ActionItem.SendDataType.STRING,
                            "on",
                            null),
                    new ActionItem("0000ff10-0000-1000-8000-00805f9b34fb",
                            "0000ff11-0000-1000-8000-00805f9b34fb",
                            "OFF",
                            ActionItem.SendDataType.STRING,
                            "off",
                            null)
            ))
    );

}
