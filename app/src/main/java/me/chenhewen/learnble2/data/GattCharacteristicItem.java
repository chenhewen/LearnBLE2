package me.chenhewen.learnble2.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GattCharacteristicItem {
    public GattCharacteristicItem(UUID uuid, int properties, List<GattDescriptorItem> gattDescriptorItems) {
        this.uuid = uuid;
        this.properties = properties;
        this.gattDescriptorItems = gattDescriptorItems;
    }

    public UUID uuid;
    public int properties;
    public List<GattDescriptorItem> gattDescriptorItems = new ArrayList<>();

}
