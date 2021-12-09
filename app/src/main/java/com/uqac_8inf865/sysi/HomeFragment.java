package com.uqac_8inf865.sysi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private final static String TAG = "HomeFragment";

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private RecyclerView newSpotsRecyclerView, hotSpotsRecyclerView, favoriteSpotsRecyclerView;
    private RecyclerView.Adapter newSpotsAdapter, hotSpotsAdapter, favoriteSpotsAdapter;
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

        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
        bitmapArrayList.add(BitmapFactory.decodeResource(getResources(), R.drawable.test));
        LatLng latLng = new LatLng(-24,34);
        newSpotsArrayList.add(new Spot("Nathan", "Sports",
                "une description", "un titre",latLng,0,
                bitmapArrayList));
        hotSpotsArrayList.add(new Spot("Nathan", "Sports",
                "une description", "un titre",latLng,0,
                bitmapArrayList));

        determineNewSpots();

        newSpotsAdapter = new SpotAdapter(newSpotsArrayList);
        hotSpotsAdapter = new SpotAdapter(hotSpotsArrayList);

        newSpotsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        hotSpotsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        hotSpotsRecyclerView.setLayoutManager(hotSpotsLayoutManager);
        hotSpotsRecyclerView.setAdapter(newSpotsAdapter);

        newSpotsRecyclerView.setLayoutManager(newSpotsLayoutManager);
        newSpotsRecyclerView.setAdapter(newSpotsAdapter);
    }

    private void determineNewSpots() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Log.d(TAG, date.toString());
        //fStore.collection("spots").whereGreaterThanOrEqualTo("date",date);
    }

    private void determineHotSpots(){

    }

    private void determineFavoritesSpots(){

    }
}