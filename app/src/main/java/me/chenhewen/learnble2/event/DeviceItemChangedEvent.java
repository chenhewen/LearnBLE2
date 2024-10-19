package me.chenhewen.learnble2.event;

public class DeviceItemChangedEvent {
    public String deviceAddress;

    public DeviceItemChangedEvent(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
