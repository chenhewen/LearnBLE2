package me.chenhewen.learnble2.dealer;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import me.chenhewen.learnble2.BLEApplication;

public class SettingDealer {

    public SettingDealer(Context context) {
        this.context = context;
        this.preference = PreferenceManager.getDefaultSharedPreferences(context);
    }


    private Context context;
    private final SharedPreferences preference;

    public ClickChoice getSettingClickChoice() {
        String clickAction = preference.getString("click_action", "send_data");
        return ClickChoice.fromString(clickAction);
    }

    public LongClickChoice getSettingLongClickChoice() {
        String longClickAction = preference.getString("long_click_action", "edit_item");
        return LongClickChoice.fromString(longClickAction);
    }

    public enum ClickChoice {
        DO_NOTHING("do_nothing"),
        SEND_DATA("send_data"),
        EDIT_ITEM("edit_item");

        public String rawValue;
        ClickChoice(String rawValue) {
            this.rawValue = rawValue;
        }

        public static ClickChoice fromString(String rawValue) {
            for (ClickChoice choice : ClickChoice.values()) {
                if (choice.rawValue.equals(rawValue)) {
                    return choice;
                }
            }
            // 可以抛出异常或者返回默认值，比如 DO_NOTHING
            throw new IllegalArgumentException("No enum constant with rawValue: " + rawValue);
        }
    }

    public enum LongClickChoice {
        DO_NOTHING("do_nothing"),
        SEND_DATA("send_data"),
        EDIT_ITEM("edit_item");

        public String rawValue;
        LongClickChoice(String rawValue) {
            this.rawValue = rawValue;
        }

        public static LongClickChoice fromString(String rawValue) {
            for (LongClickChoice choice : LongClickChoice.values()) {
                if (choice.rawValue.equals(rawValue)) {
                    return choice;
                }
            }
            // 可以抛出异常或者返回默认值，比如 DO_NOTHING
            throw new IllegalArgumentException("No enum constant with rawValue: " + rawValue);
        }
    }
}
