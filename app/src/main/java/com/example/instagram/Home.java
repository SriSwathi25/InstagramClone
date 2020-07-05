package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagram.Fragments.HomeFragment;
import com.example.instagram.Fragments.NotificationFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.nav_home : {fragment = new HomeFragment();
                                            break;}
                    case R.id.nav_search : {fragment = new SearchFragment();
                                            break;}
                    case R.id.nav_profile : {fragment = new ProfileFragment();
                        getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("mine","true").commit();
                                            break;}
                    case R.id.nav_heart : { fragment = new NotificationFragment();
                                            break; }
                    case R.id.nav_add : { fragment = null;
                    startActivity(new Intent(Home.this, PostActivity.class));
                        break; }
                }
                if(fragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }



                return true;
            }

        });
        Bundle intent = getIntent().getExtras();
        if(intent != null){
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId",profileId).commit();
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("mine","false").commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        }
        else {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

    }
}