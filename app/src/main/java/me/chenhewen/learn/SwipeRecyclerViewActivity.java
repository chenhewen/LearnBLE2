package me.chenhewen.learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.chenhewen.learnble2.R;

public class SwipeRecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_swipe_recycler_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MainListAdapter mainListAdapter = new MainListAdapter(getMealList());
        recyclerView.setAdapter(mainListAdapter);
    }

    public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<String> shoppingList;

        public MainListAdapter(List<String> shoppingList) {
            this.shoppingList = shoppingList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_swipe_recycler_view_item, parent, false);
            return new MainListItem(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MainListItem mainListItem = (MainListItem) holder;
            mainListItem.mealTV.setText(shoppingList.get(position));
            mainListItem.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "INFO CLICKED", Toast.LENGTH_SHORT).show();
                }
            });
            mainListItem.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "EDIT CLICKED", Toast.LENGTH_SHORT).show();
                }
            });
            mainListItem.mealTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Item CLICKED", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return shoppingList.size();
        }
    }

    public static class MainListItem extends RecyclerView.ViewHolder {

        protected TextView mealTV;
        protected ImageButton deleteButton;
        protected ImageButton editButton;

        protected MainListItem(View itemView) {
            super(itemView);
            mealTV = itemView.findViewById(R.id.meal_tv);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton= itemView.findViewById(R.id.edit_button);
        }
    }

    public List<String> getMealList() {
        List<String> mealList = new ArrayList<>();
        mealList.add("Green Thai Curry");
        mealList.add("Granola");
        mealList.add("Poached Eggs");
        mealList.add("Spaghetti");
        mealList.add("Apple Pie");
        mealList.add("Grilled Cheese Sandwich");
        mealList.add("Vegetable Soup");
        mealList.add("Chicken Noodles");
        mealList.add("Fajitas");
        mealList.add("Chicken Pot Pie");
        mealList.add("Pasta and cauliflower casserole with chicken");
        mealList.add("Vegetable stir-fry");
        mealList.add("Sweet potato and orange soup");
        mealList.add("Vegetable Broth");
        return mealList;
    }
}