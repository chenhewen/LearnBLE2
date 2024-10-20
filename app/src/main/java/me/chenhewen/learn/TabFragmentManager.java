package me.chenhewen.learn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.chenhewen.learnble2.BLEApplication;
import me.chenhewen.learnble2.R;
import me.chenhewen.learnble2.dealer.BluetoothDealer;

public class TabFragmentManager implements Serializable {

    public TabFragmentManager(TabLayout tabLayout, View fragmentAnchor, Context context, FragmentManager fragmentManager) {
        this.tabLayout = tabLayout;
        this.fragmentAnchor = fragmentAnchor;
        this.context = context;
        this.fragmentManager = fragmentManager;

        init();
    }

    // 视图
    private TabLayout tabLayout;
    private View fragmentAnchor;
    private Context context;
    private FragmentManager fragmentManager;

    // 数据
    private Map<TabLayout.Tab, TabFragmentItem> tabFragmentMap = new LinkedHashMap<>();

    public void init() {
        removeAllFragment();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateCachedFragment(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void removeAllFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // 遍历当前的 Fragment 列表
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null) {
                // 从事务中移除 Fragment
                fragmentTransaction.remove(fragment);
            }
        }

        // 提交事务，完成所有 Fragment 的删除
        fragmentTransaction.commit();
    }

    private void updateFragment(TabLayout.Tab tab) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(fragmentAnchor.getId(), tabFragmentMap.get(tab).fragment);
        transaction.commit();
    }

    private void updateCachedFragment(TabLayout.Tab tab) {
        TabFragmentItem tabFragmentItem = tabFragmentMap.get(tab);
        List<Fragment> allFragments = new ArrayList<>();
        for (TabFragmentItem value : tabFragmentMap.values()) {
            allFragments.add(value.fragment);
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : allFragments) {
            transaction.hide(fragment);
        }

        if (tabFragmentItem != null) {
            if (!allFragments.contains(tabFragmentItem.fragment)) {
                transaction.add(fragmentAnchor.getId(), tabFragmentItem.fragment);
            }
            transaction.show(tabFragmentItem.fragment);
        }
        transaction.commit();
    }

    private TabLayout.Tab createNewTab(String title, boolean isClosable) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View tabView = inflater.inflate(R.layout.activity_tab_layout_custom_tab, tabLayout, false);
        TabLayout.Tab newTab = tabLayout.newTab();
        newTab.setCustomView(tabView);

        TextView tabTextView = tabView.findViewById(R.id.tab_text_view);
        View closeButton = tabView.findViewById(R.id.tab_close_button);

        tabTextView.setText(title);
        if (isClosable) {
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tabCloseListener != null) {
                        int tabIndex = getTabIndex(newTab);
                        // 去除scanner的位置
                        tabCloseListener.onTabClose(tabIndex - 1);
                    }
                    removeTab(newTab);
                }
            });
        } else {
            closeButton.setVisibility(View.GONE);
        }

        return newTab;
    }

    public void addTab(String title, Fragment fragment, String fragmentTag, boolean isClosable) {
        TabLayout.Tab newTab = createNewTab(title, isClosable);
        TabFragmentItem item = new TabFragmentItem(newTab, fragment, isClosable, false);
        tabFragmentMap.put(newTab, item);
        tabLayout.addTab(newTab);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(fragmentAnchor.getId(), fragment, fragmentTag);
        transaction.commit();

        tabLayout.selectTab(newTab);
    }

    public void removeTab(TabLayout.Tab tab) {
        TabFragmentItem removedTabFragmentItem = tabFragmentMap.remove(tab);
        tabLayout.removeTab(tab);

        if (removedTabFragmentItem != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(removedTabFragmentItem.fragment);
            transaction.commit();
        }
    }

    public void removeTabAt(int position) {
        TabLayout.Tab tab = getTabByIndex(position);
        if (tab != null) {
            removeTab(tab);
        }
    }

    public static abstract class OnTabCloseListener {
        public abstract void onTabClose(int position);
    }

    private OnTabCloseListener tabCloseListener;

    public void setOnTebCloseListener(OnTabCloseListener listener) {
        this.tabCloseListener = listener;
    }

    private int getTabIndex(TabLayout.Tab targetTab) {
        int index = 0;
        for (TabLayout.Tab tab : tabFragmentMap.keySet()) {
            if (tab.equals(targetTab)) {
                return index;
            }
            index++;
        }
        // 如果找不到，返回 -1 表示未找到
        return -1;
    }

    private TabLayout.Tab getTabByIndex(int index) {
        int currentIndex = 0;
        for (TabLayout.Tab tab : tabFragmentMap.keySet()) {
            if (currentIndex == index) {
                return tab;
            }
            currentIndex++;
        }
        // 如果索引超出范围，返回 null 或抛出异常
        return null;
    }

//    public void onDestroy() {
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        for (TabFragmentItem tabFragmentItem : tabFragmentMap.values()) {
//            transaction.remove(tabFragmentItem.fragment);
//        }
//        transaction.commit();
//    }

}

