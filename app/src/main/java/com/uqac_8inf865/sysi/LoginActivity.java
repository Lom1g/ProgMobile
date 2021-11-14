package com.uqac_8inf865.sysi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout til_email, til_password;
    Button mLoginBtn;
    TextView mCreateBtn, forgotTextLink;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        til_email = findViewById(R.id.til_email);
        til_password = findViewById(R.id.til_password);
        mLoginBtn = findViewById(R.id.btn_Login);
        mCreateBtn = findViewById(R.id.createText);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        forgotTextLink = findViewById(R.id.forgotPassword);

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        mLoginBtn.setOnClickListener(v -> {
            String email = til_email.getEditText().getText().toString().trim();
            String password = til_password.getEditText().getText().toString().trim();

            //Delete all the potential previous errors
            til_email.setErrorEnabled(false);
            til_password.setErrorEnabled(false);

            if (TextUtils.isEmpty(email)) {
                til_email.setError("Email is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                til_password.setError("Password is Required.");
                return;
            }

            if (password.length() < 6) {
                til_password.setError("Password must be >=6 characters.");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            //authentificate the user

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });


        });

        mCreateBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        forgotTextLink.setOnClickListener(v -> {
            final EditText resetMail = new EditText(v.getContext());
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password?");
            passwordResetDialog.setMessage("Enter your email to receive reset link.");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
                //extract the email en send reset link
                String mail = resetMail.getText().toString();

                if (!TextUtils.isEmpty(mail)) {
                    fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Error! Reset link is not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Email is required!", Toast.LENGTH_SHORT).show();
                }
            });

            passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
                //close the dialog
            });

            passwordResetDialog.create().show();
        });
    }
}