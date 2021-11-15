package com.uqac_8inf865.sysi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Toolbar toolbar;

    private Deque<Integer> integerDeque;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bottomNavigationView = findViewById(R.id.bottomNavigationView);

        this.integerDeque = new ArrayDeque<>(bottomNavigationView.getMaxItemCount()-1);

        this.toolbar = findViewById(R.id.toolbarView);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null){
            int id_save = savedInstanceState.getInt("id_bottomNavigationView");
            integerDeque = (ArrayDeque<Integer>) savedInstanceState.getSerializable("queue");
            loadFragment(getFragment(id_save));
            bottomNavigationView.setSelectedItemId(id_save);
        }else{
            integerDeque.push(R.id.page_home);
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.page_home);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (integerDeque.contains(id)){
                if (id == R.id.page_home){
                    if (integerDeque.size() != 1){
                        if (flag){
                            integerDeque.addFirst(R.id.page_home);
                            flag = false;
                        }
                    }
                }
                integerDeque.remove(id);
            }
            integerDeque.push(id);
            loadFragment(getFragment(id));
            return true;
        });
        bottomNavigationView.setOnItemReselectedListener(item -> {
        });

        askLocationPermission();
    }

    private Fragment getFragment(int id) {
        if (id == R.id.page_home){
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
            return new HomeFragment();
        }
        else if (id == R.id.page_map){
            bottomNavigationView.getMenu().getItem(1).setChecked(true);
            return new MapsFragment();
        }
        else if (id == R.id.page_community){
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
            return new CommunityFragment();
        }
        else if (id == R.id.page_profil){
            bottomNavigationView.getMenu().getItem(3).setChecked(true);
            return new ProfilFragment();
        }
        else {
            return null;
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .replace(R.id.fragmentContainerView, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        integerDeque.pop();
        if (!integerDeque.isEmpty()){
            loadFragment(getFragment(integerDeque.peek()));
        }
        else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int id = bottomNavigationView
                .getMenu()
                .findItem(bottomNavigationView.getSelectedItemId())
                .getItemId();
        outState.putInt("id_bottomNavigationView", id);
        outState.putSerializable("queue",(ArrayDeque<Integer>)integerDeque);
        super.onSaveInstanceState(outState);
    }

    private void askLocationPermission() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 180000, 50, locationListener);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,     PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
}