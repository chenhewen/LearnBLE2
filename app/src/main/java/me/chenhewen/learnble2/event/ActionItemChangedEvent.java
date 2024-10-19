package me.chenhewen.learnble2.event;

import me.chenhewen.learnble2.model.DeviceItem;

public class ActionItemChangedEvent {

    // 用来标识变化的ActionItem属于哪个DeviceItem
    public DeviceItem deviceItem;

    public ActionItemChangedEvent(DeviceItem deviceItem) {
        this.deviceItem = deviceItem;
    }
}
