package com.soham.studentconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvMobile, tvCourse;
    TextView tvEnrollmentNumber, tvJoiningYear;
    TextView tvSemester, tvDateOfBirth;

    Button btnEdit, btnLogout;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvMobile = findViewById(R.id.tvMobile);
        tvCourse = findViewById(R.id.tvCourse);

        tvEnrollmentNumber =
                findViewById(R.id.tvEnrollmentNumber);

        tvJoiningYear =
                findViewById(R.id.tvJoiningYear);

        tvSemester =
                findViewById(R.id.tvSemester);

        tvDateOfBirth =
                findViewById(R.id.tvDateOfBirth);

        btnEdit = findViewById(R.id.btnEdit);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadStudentData();

        btnEdit.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    StudentActivity.class
            );

            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Intent intent = new Intent(
                    ProfileActivity.this,
                    MainActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    private void loadStudentData() {

        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        tvName.setText(
                                getValue(documentSnapshot, "name")
                        );

                        tvEmail.setText(
                                getValue(documentSnapshot, "email")
                        );

                        tvMobile.setText(
                                getValue(documentSnapshot, "mobileNumber")
                        );

                        tvCourse.setText(
                                getValue(documentSnapshot, "course")
                        );

                        tvEnrollmentNumber.setText(
                                getValue(
                                        documentSnapshot,
                                        "enrollmentNumber"
                                )
                        );

                        tvJoiningYear.setText(
                                getValue(
                                        documentSnapshot,
                                        "joiningYear"
                                )
                        );

                        tvSemester.setText(
                                getValue(
                                        documentSnapshot,
                                        "semester"
                                )
                        );

                        tvDateOfBirth.setText(
                                getValue(
                                        documentSnapshot,
                                        "dateOfBirth"
                                )
                        );
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        ProfileActivity.this,
                        "Error loading data: "
                                + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    private String getValue(
            com.google.firebase.firestore.DocumentSnapshot document,
            String field
    ) {

        Object value = document.get(field);

        if (value == null) {
            return "Not provided";
        }

        return value.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAuth != null &&
                mAuth.getCurrentUser() != null) {

            loadStudentData();
        }
    }
}