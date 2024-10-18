package me.chenhewen.learnble2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionItem {

    public ActionItem(String serviceUuid, String characteristicUuid, String title, SendDataType sendDataType, String sendString, byte[] sendHex) {
        this.serviceUuid = serviceUuid;
        this.characteristicUuid = characteristicUuid;
        this.title = title;
        this.sendDataType = sendDataType;
        this.sendString = sendString;
        this.sendHex = sendHex;
    }

    public String serviceUuid;
    public String characteristicUuid;
    public String title;
    public SendDataType sendDataType;
    public String sendString;
    public byte[] sendHex;

    public byte[] getToSendingData() {
        if (sendDataType == SendDataType.STRING) {
            return sendString.getBytes();
        } else {
            return sendHex;
        }
    }

    public String getDisplayHexString() {
        if (sendDataType == SendDataType.STRING) {
            return "0x: " + convertStringToHexString(sendString);
        } else if (sendDataType == SendDataType.HEX) {
            return "0x: " + convertByteArrayToHexString(sendHex);
        }

        return "";
    }

    public static String convertStringToHexString(String str) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            String hex = Integer.toHexString(str.charAt(i));
            if (hex.length() < 2) {
                hexString.append("0");
            }
            hexString.append(hex);
            if (i < str.length() - 1) {
                hexString.append(", ");
            }
        }

        return hexString.toString();
    }

    public static byte[] convertHexStringToByteArray(String hex) {
        // 去除字符串中的空格（如果有）
        hex = hex.replaceAll("\\s+", "");

        // 如果长度是奇数，自动在前面补 '0'
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }

        // 计算结果数组的长度
        int len = hex.length();
        byte[] data = new byte[len / 2];  // 每两个字符为一个字节

        // 遍历字符串，每两个字符构成一个字节
        for (int i = 0; i < len; i += 2) {
            // 将两个字符（16进制表示的数）转换为一个字节
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    public static String convertByteArrayToHexString(byte[] array) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            hexString.append(String.format("%02x", array[i]));
            if (i < array.length - 1) {
                hexString.append(", ");
            }
        }

        return hexString.toString();
    }

    public enum SendDataType {
        STRING("String"),
        HEX("Hex");

        private final String rawValue;

        // 构造函数
        SendDataType(String rawValue) {
            this.rawValue = rawValue;
        }

        // 自定义方法，返回自定义的字符串
        public String getRawValue() {
            return rawValue;
        }

        public static String[] getAllRawValues() {
            return new String[] {"String", "Hex"};
        }
    }

    public static List<ActionItem> mockItems = new ArrayList<>(Arrays.asList(
            new ActionItem("service-AA-BB-CC-DD", "characteristic-11-22-33", "Hello", ActionItem.SendDataType.STRING, "hello", null),
            new ActionItem("service-BB-CC-DD-EE", "characteristic-22-33-44", "World", ActionItem.SendDataType.HEX, null, new byte[] {0x57, 0x6f, 0x72, 0x6c, 0x64})
    ));
}