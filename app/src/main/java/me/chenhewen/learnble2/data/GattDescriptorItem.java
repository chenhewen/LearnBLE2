package me.chenhewen.learnble2.data;

import java.util.UUID;

public class GattDescriptorItem {
    public GattDescriptorItem(UUID uuid, String valueInString, byte[] valueInBytes) {
        this.uuid = uuid;
        this.valueInString = valueInString;
        this.valueInBytes = valueInBytes;
    }

    public UUID uuid;
    public String valueInString;
    public byte[] valueInBytes;
}
