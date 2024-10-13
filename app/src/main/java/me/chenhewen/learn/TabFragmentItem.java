package me.chenhewen.learn;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

public class TabFragmentItem {
    public TabLayout.Tab tab;
    public Fragment fragment;
    public Boolean isClosable;

    public TabFragmentItem(TabLayout.Tab tab, Fragment fragment, Boolean isClosable) {
        this.tab = tab;
        this.fragment = fragment;
        this.isClosable = isClosable;
    }
}
