package me.chenhewen.learnble2.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GattServiceItem {

    public GattServiceItem(UUID uuid, List<GattCharacteristicItem> characteristicItems) {
        this.uuid = uuid;
        this.characteristicItems = characteristicItems;
    }

    public UUID uuid;
    public List<GattCharacteristicItem> characteristicItems = new ArrayList<>();
}
