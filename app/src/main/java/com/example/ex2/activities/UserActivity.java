package com.example.ex2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ex2.R;
import com.example.ex2.fragments.ConsumerFragment;
import com.example.ex2.fragments.EmptyFragment;
import com.example.ex2.fragments.ProviderFragment;
import com.example.ex2.fragments.SettingsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Dictionary;

public class UserActivity extends AppCompatActivity {
    private TextView tvWelcome;
    DatabaseReference usersRef;
    Dictionary<String, Integer> amountOfItems;
    String userId;
    Fragment operationsFragment, fragment;
    Boolean isCustomer;
    Button logOut;
    //    private RecyclerView recyclerView;
//    private ProductAdapter adapter;
//    private ArrayList<Product> productList;
    FrameLayout frameLayout;
    TabLayout tabLayout;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        frameLayout = findViewById(R.id.user_fragments);
        tabLayout = findViewById(R.id.tabs);
        userId = FirebaseAuth.getInstance().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        tabLayout.getTabAt(1).setIcon(R.drawable.settings);
        tabLayout.getTabAt(0).setIcon(R.drawable.reservation_completed_icon);

        getSupportFragmentManager().beginTransaction().replace(R.id.user_fragments, new EmptyFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        usersRef.child("type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.getValue(String.class);

                if (type.equals("customer")) {
                    operationsFragment = new ConsumerFragment();
                } else {
                    operationsFragment = new ProviderFragment();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.user_fragments, operationsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load data", error.toException());
            }
        });


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fragment = operationsFragment;

                        break;
                    case 1:
                        fragment = new SettingsFragment();

                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.user_fragments, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}

