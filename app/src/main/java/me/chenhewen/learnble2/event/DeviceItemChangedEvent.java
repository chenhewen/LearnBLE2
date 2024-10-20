package me.chenhewen.learnble2.event;

import me.chenhewen.learnble2.model.DeviceItem;

public class DeviceItemChangedEvent {
    public DeviceItem deviceItem;
    public Operation operation;

    public DeviceItemChangedEvent(DeviceItem deviceItem, Operation operation) {
        this.deviceItem = deviceItem;
        this.operation = operation;
    }

    public enum Operation {
        ADD,
        REMOVE
    }
}
