package me.chenhewen.learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import me.chenhewen.learnble2.R;

public class ViewPager2FragmentActivity extends FragmentActivity {

    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_pager2_fragment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_pager2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager2 = findViewById(R.id.view_pager2);
        MyFragmentStateAdapter fragmentStateAdapter = new MyFragmentStateAdapter(this);
        viewPager2.setAdapter(fragmentStateAdapter);
    }

    public static class MyFragmentStateAdapter extends FragmentStateAdapter {

        public MyFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new MyFragment(position);
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public static class MyFragment extends Fragment {

        private final int position;

        public MyFragment(int position) {
            this.position = position;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_view_pager2_fragment_item, container, false);
            TextView textView = viewGroup.findViewById(R.id.fragment_text);
            textView.setText(String.format("%s", position));
            return viewGroup;
        }
    }
}