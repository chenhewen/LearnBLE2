package me.chenhewen.learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

import me.chenhewen.learnble2.R;

public class TabLayoutFragmentActivity extends AppCompatActivity {

    Map<TabLayout.Tab, Fragment> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tab_layout_fragment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                if (tab.getPosition() == 0) {
                    transaction.replace(R.id.fragment_anchor, MyFragment1.class, null);
                } else if (tab.getPosition() == 1) {
                    transaction.replace(R.id.fragment_anchor, MyFragment2.class, null);
                } else {
                    transaction.replace(R.id.fragment_anchor, map.get(tab));
                }

                transaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_anchor, MyFragment1.class, null);
        transaction.commit();

        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabLayout.Tab tab = tabLayout.newTab();
                int v1 = (int) (Math.random() * 1000);

                LayoutInflater inflater = LayoutInflater.from(TabLayoutFragmentActivity.this);
                View customTabView = inflater.inflate(R.layout.activity_tab_layout_custom_tab, null);
                TextView tabTextView = customTabView.findViewById(R.id.tab_text_view);
                View tabCloseButton = customTabView.findViewById(R.id.tab_close_button);
                tabTextView.setText("" + v1);
                tabCloseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int nextP = Math.max(tab.getPosition() - 1, 0);
                        tabLayout.removeTab(tab);
                        TabLayout.Tab tab = tabLayout.getTabAt(nextP);
                        tabLayout.selectTab(tab);
                    }
                });

                tab.setCustomView(customTabView);
                tabLayout.addTab(tab);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                MyFragmentCustom fragment = new MyFragmentCustom(v1);
                transaction.replace(R.id.fragment_anchor, fragment, v1 + "");
                transaction.commit();

                map.put(tab, fragment);
                tabLayout.selectTab(tab);
            }
        });

        View openSheetButton = findViewById(R.id.open_sheet_button);
        openSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(TabLayoutFragmentActivity.this);
                View sheetContentView = LayoutInflater.from(TabLayoutFragmentActivity.this).inflate(R.layout.bottom_sheet, null);
                bottomSheetDialog.setContentView(sheetContentView);
                bottomSheetDialog.show();

                View sendButton = sheetContentView.findViewById(R.id.send_button);
                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
    }

    public static class MyFragment1 extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ViewGroup fragment1_content = (ViewGroup) inflater.inflate(R.layout.activity_tab_layout_fragment_1, container, false);
            return fragment1_content;
        }
    }

    public static class MyFragment2 extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ViewGroup fragment2_content = (ViewGroup) inflater.inflate(R.layout.activity_tab_layout_fragment_2, container, false);
            return fragment2_content;
        }
    }

    public static class MyFragmentCustom extends Fragment {

        public MyFragmentCustom(int number) {
            this.number = number;
        }

        private int number;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ViewGroup fragment_content = (ViewGroup) inflater.inflate(R.layout.activity_tab_layout_fragment_custom, container, false);
            TextView textView = fragment_content.findViewById(R.id.text_view);
            textView.setText("" + number);
            return fragment_content;
        }
    }
}