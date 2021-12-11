package com.uqac_8inf865.sysi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";
    private static final int MAP_TYPE_SATELLITE = 2;

    private ActionBar actionBar;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private FloatingActionButton buttonAddSpot, buttonFilterSpot;

    private FragmentActivity activity;
    private boolean setzoom;
    private boolean modeEdition = false;
    private boolean modeGeoLoc = false;
    private LatLng userLatLong;
    private Bitmap image;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = fStorage.getReference();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private GoogleMap gMap;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {

            gMap = googleMap;

            PopupSpot infospot = new PopupSpot(activity);

            //add markers on the Map
            db.collection("spots")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                GeoPoint geoPoint = document.getGeoPoint("coordinate");
                                LatLng latLngMarker = new LatLng(Objects.requireNonNull(geoPoint).getLatitude(), geoPoint.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLngMarker);
                                markerOptions.title((String) document.getData().get("title"));
                                float icon = CategoryDataUtils.getFloatIcon((String) Objects.requireNonNull(document.getData().get("category")));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(icon));
                                googleMap.addMarker(markerOptions);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    });

            // interaction avec la carte
            googleMap.setOnMapClickListener(latLng -> {
                PopupAddSpot spot = new PopupAddSpot(activity, latLng);
                // ajout de spots
                if (modeEdition){
                    declareYesButton(spot, latLng);
                    declareNoButton(spot);
                    spot.build();
                }
            });

            googleMap.setOnMarkerClickListener(marker -> {
                LatLng latLng=marker.getPosition();
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                db.collection("spots")
                        .whereEqualTo("latitude",latitude)
                        .whereEqualTo("longitude",longitude)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    infospot.setTitle(document.getData().get("title").toString());
                                    infospot.setDescription(document.getData().get("description").toString());
                                    infospot.setNotepop(document.getData().get("rating").toString());
                                    infospot.setNoButtonText("Signaler");
                                    infospot.setNeutralButtonText("Retour");
                                    infospot.setYesButtonText("+1");

                                    infospot.getYesButton().setOnClickListener(v -> {
                                        infospot.setNote(Integer.parseInt(document.getData().get("rating").toString()));
                                        db.collection("spots").document(document.getId()).update("rating",infospot.getNote());
                                        infospot.dismiss();
                                    });

                                    infospot.getNeutralButton().setOnClickListener(v -> infospot.dismiss());

                                    infospot.getNoButton().setOnClickListener(v -> {
                                        if (!document.getBoolean("signaled")) {
                                            db.collection("spots").document(document.getId()).update("signaled",true);
                                            db.collection("spots").document(document.getId()).update("suppress","0");
                                            String signaler = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                            db.collection("spots").document(document.getId()).update("signaledby",signaler);
                                        }
                                        infospot.dismiss();
                                    });
                                    infospot.build();
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        });
                return false;
            });

            if(modeGeoLoc){
                locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            userLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, 15));
                        } else {
                            locationManager.requestLocationUpdates(String.valueOf(locationManager.getBestProvider(new Criteria(), true)), 180000, 50, locationListener);
                        }
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

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    if(setzoom){
                        setzoom=false;
                    }else{
                        userLatLong = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, 15));
                }

                googleMap.setMyLocationEnabled(true);
            }
            googleMap.setMapType(MAP_TYPE_SATELLITE);

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().size() > 1){
                PopupAddSpot popupAddSpot = new PopupAddSpot(getActivity(), getArguments().getParcelable("coordinate"));
                popupAddSpot.getTitleTextView().setText(getArguments().getString("title"));
                popupAddSpot.getDescriptionTextView().setText(getArguments().getString("description"));
                popupAddSpot.getSpinner().setSelection(getArguments().getInt("category"));
                popupAddSpot.getImageSpot().setImageBitmap(getArguments().getParcelable("spotPicture"));
                this.image = getArguments().getParcelable("spotPicture");
                declareYesButton(popupAddSpot, getArguments().getParcelable("coordinate"));
                declareNoButton(popupAddSpot);
                popupAddSpot.build();
            }
            else{
                this.modeGeoLoc = getArguments().getBoolean("modeGeoLoc");
            }
        }
        Log.d(TAG, "OnCreate");
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.activity = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        this.buttonAddSpot = view.findViewById(R.id.floatingAddButtonView);
        this.buttonFilterSpot = view.findViewById(R.id.floatingFilterButtonView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        buttonAddSpot.setOnClickListener(v ->{
            if(modeEdition){
                modeEdition = false;
                v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.action)));
            }else{
                modeEdition = true;
                v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
            }
        });
        actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("My map");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_maps_menu,menu);
        if(modeGeoLoc){
            menu.getItem(0).setIcon(R.drawable.ic_baseline_gps_on_24);
        }else{
            menu.getItem(0).setIcon(R.drawable.ic_baseline_gps_off_24);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_gps) {
            if(!modeGeoLoc && askLocationPermission()){
                modeGeoLoc = true;
            }else if(modeGeoLoc){
                modeGeoLoc = false;
            }
            refreshFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void declareYesButton(PopupAddSpot spot, LatLng latLng){
        spot.getYesButton().setOnClickListener(v -> {

            String title = spot.getTitle();
            String description = spot.getDescription();
            String category = spot.getCategory();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            UUID randomUuid = UUID.randomUUID();
            StorageReference imageRef = storageReference.child("pictures_spots/" +
                    randomUuid.toString());
            imageRef.putStream(is);

            Map<String, Object> spotHashMap = new HashMap<>();
            spotHashMap.put("author", fAuth.getCurrentUser().getUid());
            spotHashMap.put("category", category);
            spotHashMap.put("coordinate", latLng);
            spotHashMap.put("date", new Timestamp(new Date(System.currentTimeMillis())));
            spotHashMap.put("description", description);
            spotHashMap.put("rating", "0");
            spotHashMap.put("title", title);

            if (title.matches("") || description
                    .matches("") || category
                    .matches("")) {
                if (category.matches("")) {
                    spot.findViewById(R.id.action_bar_spinner).requestFocus();
                }
                if (description.matches("")) {
                    spot.findViewById(R.id.enterdescrip).requestFocus();
                }
                if (title.matches("")) {
                    spot.findViewById(R.id.entertitle).requestFocus();
                }
            } else {
                db.collection("spots_proposed").add(spotHashMap);
                spotHashMap.clear();
                spot.clear();
                spot.dismiss();
            }
        });
    }

    private void declareNoButton(PopupAddSpot spot){
        spot.getNoButton().setOnClickListener(v -> {
            spot.clear();
            spot.dismiss();
        });
    }




    public void refreshFragment(){
        Bundle bundle = new Bundle();
        Fragment fragment = new MapsFragment();
        bundle.putBoolean("modeGeoLoc", modeGeoLoc);
        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .replace(R.id.fragmentContainerView, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    private boolean askLocationPermission() {
        final boolean[] permissionGranted = new boolean[1];
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                permissionGranted[0] = true;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 180000, 50, locationListener);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                permissionGranted[0] = false;
                showSettingsDialog();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
        return permissionGranted[0];
    }

    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            // this method is called on click on positive
            // button and on clicking shit button we
            // are redirecting our user from our app to the
            // settings page of our app.
            dialog.cancel();
            // below is the intent from which we
            // are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // this method is called when
            // user click on negative button.
            dialog.cancel();
        });
        // below line is used
        // to display our dialog
        builder.show();
    }
}