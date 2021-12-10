package com.uqac_8inf865.sysi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private final static String TAG = "HomeFragment";

    private ActionBar actionBar;

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private RecyclerView newSpotsRecyclerView, hotSpotsRecyclerView, favoriteSpotsRecyclerView;
    private SpotAdapter newSpotsAdapter, hotSpotsAdapter, favoriteSpotsAdapter;
    private RecyclerView.LayoutManager newSpotsLayoutManager, hotSpotsLayoutManager, favoritesSpotsLayoutManager;

    private ArrayList<Spot> newSpotsArrayList, hotSpotsArrayList, favoriteSpotsArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newSpotsArrayList = new ArrayList<>();
        hotSpotsArrayList = new ArrayList<>();
        favoriteSpotsArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        newSpotsRecyclerView = view.findViewById(R.id.newSpotsRecyclerView);
        hotSpotsRecyclerView = view.findViewById(R.id.hotSpotsRecyclerView);
        favoriteSpotsRecyclerView = view.findViewById(R.id.favoritesSpotsRecyclerView);
        newSpotsRecyclerView.setHasFixedSize(true);
        hotSpotsRecyclerView.setHasFixedSize(true);
        favoriteSpotsRecyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Home");
        }

        determineFavoritesSpots();

        newSpotsAdapter = new SpotAdapter(newSpotsArrayList);
        hotSpotsAdapter = new SpotAdapter(hotSpotsArrayList);
        favoriteSpotsAdapter = new SpotAdapter(favoriteSpotsArrayList);

        newSpotsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        hotSpotsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        favoritesSpotsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        hotSpotsRecyclerView.setLayoutManager(hotSpotsLayoutManager);
        hotSpotsRecyclerView.setAdapter(newSpotsAdapter);
        hotSpotsAdapter.setOnItemClickListener(position -> {
            try {
                displaySpot(position,"hots");
            }catch (IllegalStateException e){
                Log.e(TAG, String.valueOf(e));
            }
        });

        newSpotsRecyclerView.setLayoutManager(newSpotsLayoutManager);
        newSpotsRecyclerView.setAdapter(newSpotsAdapter);
        newSpotsAdapter.setOnItemClickListener(position -> {
            try {
                displaySpot(position,"news");
            }catch (IllegalStateException e){
                Log.e(TAG, String.valueOf(e));
            }
        });

        favoriteSpotsRecyclerView.setLayoutManager(favoritesSpotsLayoutManager);
        favoriteSpotsRecyclerView.setAdapter(favoriteSpotsAdapter);
        favoriteSpotsAdapter.setOnItemClickListener(position -> {
            try {
                displaySpot(position,"favorites");
            }catch (IllegalStateException e){
                Log.e(TAG, String.valueOf(e));
            }
        });
    }

    private void displaySpot(int position, String func) {
        Spot spot;
        switch (func){
            case "news":
                spot = newSpotsArrayList.get(position);
            case "hots":
                spot = hotSpotsArrayList.get(position);
            case "favorites":
                spot = favoriteSpotsArrayList.get(position);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + func);
        }
        Fragment fragment = new SpotFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("spot", spot);
        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .replace(R.id.fragmentContainerView, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    private void determineNewSpots() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Log.d(TAG, date.toString());
        //fStore.collection("spots").whereGreaterThanOrEqualTo("date",date);
    }

    private void determineHotSpots(){

    }

    private void determineFavoritesSpots(){
        Log.d(TAG,"ici");
        fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("favorites_spots").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> taskUser) {
                if(taskUser.isSuccessful()){
                    for(QueryDocumentSnapshot document : taskUser.getResult()){
                        // que faire si l'utilisateur met en favori un spot signale ou supprime ??
                        fStore.collection("spots").document(document.getId())
                                .get().addOnCompleteListener(taskSpot -> {
                                    if(taskSpot.isSuccessful()){

                                        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
                                        bitmapArrayList.add(BitmapFactory.decodeResource(getResources(), R.drawable.test));

                                        Map<String, Object> map = taskSpot.getResult().getData();

                                        LatLng latLng = new LatLng(((GeoPoint) map.get("coordinate")).getLatitude(),
                                                ((GeoPoint) map.get("coordinate")).getLongitude());

                                        favoriteSpotsArrayList.add(new Spot((String) map.get("author"),(String) map.get("category") ,
                                                (String) map.get("description"), (String) map.get("title"), latLng,
                                                (long) map.get("rating"), bitmapArrayList ));
                                        favoriteSpotsAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
            }
        });
    }
}