package me.chenhewen.learnble2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chenhewen.learn.TabFragmentItem;
import me.chenhewen.learn.TabFragmentManager;
import me.chenhewen.learn.TabLayoutFragmentActivity;

public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayout.Tab scannerTab = tabLayout.getTabAt(0);
        View fragmentAnchorView = findViewById(R.id.fragment_anchor);

        TabFragmentItem tabFragmentItem1 = new TabFragmentItem(scannerTab, new ScannerFragment(), false, true);
        List<TabFragmentItem> initialTabFragments = new ArrayList<>(Arrays.asList(tabFragmentItem1));
        TabFragmentManager tabFragmentManager = new TabFragmentManager(tabLayout, fragmentAnchorView, getApplicationContext(), getSupportFragmentManager());
        tabFragmentManager.init(initialTabFragments);
    }


}