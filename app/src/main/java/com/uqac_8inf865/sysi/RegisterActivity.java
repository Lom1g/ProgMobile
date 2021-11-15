package com.uqac_8inf865.sysi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout til_pseudo, til_email, til_password, til_password2;
    private Button mRegisterBtn;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        til_pseudo = findViewById(R.id.til_pseudo);
        til_email = findViewById(R.id.til_email);
        til_password = findViewById(R.id.til_password);
        til_password2 = findViewById(R.id.til_password2);

        mRegisterBtn = findViewById(R.id.btn_Register);
        mLoginBtn = findViewById(R.id.already);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        fStore = FirebaseFirestore.getInstance();

        mRegisterBtn.setOnClickListener(v -> {
            final String email = til_email.getEditText().getText().toString().trim();
            String password = til_password.getEditText().getText().toString().trim();
            String password2 = til_password2.getEditText().getText().toString().trim();
            final String pseudo =til_pseudo.getEditText().getText().toString().trim();

            //Delete all the potential previous errors
            til_pseudo.setErrorEnabled(false);
            til_email.setErrorEnabled(false);
            til_password.setErrorEnabled(false);
            til_password2.setErrorEnabled(false);

            if(TextUtils.isEmpty(email)){
                til_email.setError("Email is Required.");
                return;
            }

            if(TextUtils.isEmpty(password)){
                til_password.setError("Password is Required.");
                return;
            }

            if(!Objects.equals(password,password2)){
                til_password2.setError("Passwords must be the same.");
                return;
            }

            if(password.length()<6 || password2.length()<6){
                til_password.setError("Password must be >=6 characters.");
                return;
            }


            progressBar.setVisibility(View.VISIBLE);

            //register the user in the firebase

            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "User created.",Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("fName",pseudo);
                    user.put("email",email);
                    user.put("rank","user");
                    user.put("points",0);
                    documentReference.set(user).addOnSuccessListener((OnSuccessListener<Void>) aVoid ->
                            Log.d("TAG1","On success : user Profile is created and register in firebase db for:"+ userID ))
                            .addOnFailureListener(e -> Log.d("TAG1", "onFailure: "+e.toString() ));
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }else{
                    Toast.makeText(RegisterActivity.this, "Error!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        });

        mLoginBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),LoginActivity.class)));
    }
}