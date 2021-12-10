package com.uqac_8inf865.sysi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class SpotFragment extends Fragment {

    private final static String TAG = "SpotFragment";

    private Spot spot;

    private ActionBar actionBar;

    private TextView title, localisation, category, favorite, description;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.spot = (Spot) getArguments().getSerializable("spot");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot, container, false);
        this.title = view.findViewById(R.id.textViewTitle);
        this.localisation = view.findViewById(R.id.textViewLocalisation);
        this.category = view.findViewById(R.id.textViewCategory);
        this.favorite = view.findViewById(R.id.textViewFavorite);
        this.description = view.findViewById(R.id.textViewDescription);
        this.imageView = view.findViewById(R.id.imageViewSpot);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.title.setText(spot.getTitle());
        this.localisation.setText("1000");
        this.category.setText(spot.getCategory());
        this.favorite.setText("0");
        this.description.setText(spot.getDescription());
        this.imageView.setImageBitmap(spot.getBitmapArrayList().get(0));

        actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            Fragment fragment = new HomeFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainerView, fragment, fragment.getClass().getSimpleName())
                    .commit();
            return true;
        }
        return false;
    }
}