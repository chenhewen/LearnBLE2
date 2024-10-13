package me.chenhewen.learn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.chenhewen.learnble2.R;

public class TabFragmentManager {

    public TabFragmentManager(TabLayout tabLayout, View fragmentAnchor, Context context, FragmentManager fragmentManager) {
        this.tabLayout = tabLayout;
        this.fragmentAnchor = fragmentAnchor;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    private TabLayout tabLayout;
    private View fragmentAnchor;
    private Context context;
    private FragmentManager fragmentManager;

    private Map<TabLayout.Tab, TabFragmentItem> tabFragmentMap = new LinkedHashMap<>();

    public void init(List<TabFragmentItem> initialTabFragments) {
        for (TabFragmentItem initialTabFragment : initialTabFragments) {
            tabFragmentMap.put(initialTabFragment.tab, initialTabFragment);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateFragment(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void updateFragment(TabLayout.Tab tab) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(fragmentAnchor.getId(), tabFragmentMap.get(tab).fragment);
        transaction.commit();
    }

    private TabLayout.Tab createNewTab(String title) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View tabView = inflater.inflate(R.layout.activity_tab_layout_custom_tab, tabLayout, false);
        TabLayout.Tab newTab = tabLayout.newTab();
        newTab.setCustomView(tabView);

        TextView tabTextView = tabView.findViewById(R.id.tab_text_view);
        View closeButton = tabView.findViewById(R.id.tab_close_button);

        tabTextView.setText(title);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTab(newTab);
            }
        });


        return newTab;
    }

    public void addTab(String title, Fragment fragment, Boolean isClosable) {
        TabLayout.Tab newTab = createNewTab(title);
        TabFragmentItem item = new TabFragmentItem(newTab, fragment, isClosable);
        tabFragmentMap.put(newTab, item);
    }

    public void removeTab(TabLayout.Tab tab) {
        tabFragmentMap.remove(tab);

//        int tabPosition = tab.getPosition();
//        if (tabPosition == tabFragmentMap.size() - 1) {
//            // the last tab
//            if (tabPosition != 0) {
//                tabLayout.selectTab(tabLayout.getTabAt(tabPosition - 1));
//            }
//        }
    }
}

