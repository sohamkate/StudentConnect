package com.soham.studentconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnReset;
    TextView tvBackLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnReset = findViewById(R.id.btnReset);
        tvBackLogin = findViewById(R.id.tvBackLogin);

        mAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Enter your email");
                etEmail.requestFocus();
                return;
            }

            btnReset.setEnabled(false);

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {

                        btnReset.setEnabled(true);

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Password reset link sent to your email",
                                    Toast.LENGTH_LONG
                            ).show();

                        } else {

                            String error = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Unknown error";

                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Error: " + error,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        tvBackLogin.setOnClickListener(v -> finish());
    }
}