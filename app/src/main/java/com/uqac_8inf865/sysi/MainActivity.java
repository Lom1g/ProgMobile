package com.uqac_8inf865.sysi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView,
                new HomeFragment()).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.page_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.page_map:
                    selectedFragment = new MapsFragment();
                    break;
                case R.id.page_community:
                    selectedFragment = new CommunityFragment();
                    break;
                case R.id.page_profil:
                    selectedFragment = new ProfilFragment();
                    break;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainerView,
                        selectedFragment).commit();
            }
            return true;
        });
        bottomNavigationView.setOnItemReselectedListener(item -> {
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }
}