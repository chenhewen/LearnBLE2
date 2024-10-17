package me.chenhewen.learnble2.event;

import java.util.List;

import me.chenhewen.learnble2.data.GattServiceItem;

public class ServiceDiscoveredEvent {

    public ServiceDiscoveredEvent(String address, List<GattServiceItem> gattServiceItems) {
        this.address = address;
        this.gattServiceItems = gattServiceItems;
    }

    public String address;
    public List<GattServiceItem> gattServiceItems;

}
