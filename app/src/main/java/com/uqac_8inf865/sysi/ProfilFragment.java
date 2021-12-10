package com.uqac_8inf865.sysi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfilFragment extends Fragment {

    private final static String TAG = "ProfilFragment";

    private ImageView profilImageView;
    private TextView profilTextView;

    private ActionBar actionBar;

    private StorageReference storageReference;
    private FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        this.profilImageView = view.findViewById(R.id.profilImageView);
        this.profilTextView = view.findViewById(R.id.profilTextView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fStore.collection("users").document(fAuth.getCurrentUser()
                .getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String pseudo = (String) task.getResult().getData().get("pseudo");
                // pk ça ne s'actualise pas ??
                Log.d(TAG, pseudo);
                profilTextView.setText(pseudo);
            }
        });
        actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("My profil");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_profil_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}