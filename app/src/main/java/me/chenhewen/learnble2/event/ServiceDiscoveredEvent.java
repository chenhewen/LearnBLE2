package me.chenhewen.learnble2.event;

import java.util.List;

import me.chenhewen.learnble2.data.GattServiceItem;

public class ServiceDiscoveredEvent {
    public List<GattServiceItem> gattServiceItems;

    public ServiceDiscoveredEvent(List<GattServiceItem> gattServiceItems) {
        this.gattServiceItems = gattServiceItems;
    }
}
