package me.chenhewen.learn;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

public class TabFragmentItem {
    public TabLayout.Tab tab;
    public Fragment fragment;
    public boolean isClosable;
    public boolean defaultSelected;

    public TabFragmentItem(TabLayout.Tab tab, Fragment fragment, boolean isClosable, boolean defaultSelected) {
        this.tab = tab;
        this.fragment = fragment;
        this.isClosable = isClosable;
        this.defaultSelected = defaultSelected;
    }
}
