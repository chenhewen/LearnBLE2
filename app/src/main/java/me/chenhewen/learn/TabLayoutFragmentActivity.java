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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        View fragmentAnchorView = findViewById(R.id.fragment_anchor);
        Button addButton = findViewById(R.id.add_button);

        TabFragmentItem tabFragmentItem1 = new TabFragmentItem(tab1, new MyFragment1(), false, true);
        TabFragmentItem tabFragmentItem2 = new TabFragmentItem(tab2, new MyFragment2(), false, false);
        List<TabFragmentItem> initialTabFragments = new ArrayList<>(Arrays.asList(tabFragmentItem1, tabFragmentItem2));
        TabFragmentManager tabFragmentManager = new TabFragmentManager(tabLayout, fragmentAnchorView, getApplicationContext(), getSupportFragmentManager());
        tabFragmentManager.init(initialTabFragments);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int v1 = (int) (Math.random() * 1000);
                tabFragmentManager.addTab(v1 + "", new MyFragmentCustom(v1), true);
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