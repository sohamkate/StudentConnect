package com.soham.studentconnect;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {

    EditText etName, etMobile;
    EditText etEnrollmentNumber, etJoiningYear;
    EditText etDateOfBirth, etProfileImageUrl;

    TextView tvEmail;

    Spinner spinnerCourse, spinnerSemester;

    Button btnSave;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etEnrollmentNumber = findViewById(R.id.etEnrollmentNumber);
        etJoiningYear = findViewById(R.id.etJoiningYear);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etProfileImageUrl = findViewById(R.id.etProfileImageUrl);

        tvEmail = findViewById(R.id.tvEmail);

        spinnerCourse = findViewById(R.id.spinnerCourse);
        spinnerSemester = findViewById(R.id.spinnerSemester);

        btnSave = findViewById(R.id.btnSave);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Course list
        ArrayAdapter<String> courseAdapter = getStringArrayAdapter();

        spinnerCourse.setAdapter(courseAdapter);

        // Semester list
        String[] semesters = {
                "Select Semester",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6"
        };

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                semesters
        );

        semesterAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSemester.setAdapter(semesterAdapter);

        // Firebase email
        if (mAuth.getCurrentUser() != null) {

            String email = mAuth.getCurrentUser().getEmail();

            tvEmail.setText(email);
        }

        // Get name from registration
        String registeredName = getIntent().getStringExtra("name");

        if (registeredName != null) {
            etName.setText(registeredName);
        }

        // Date picker
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Save
        btnSave.setOnClickListener(v -> saveStudentData());
    }

    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter() {
        String[] courses = {
                "Select Course",
                "Computer Engineering",
                "Information Technology",
                "Mechanical Engineering",
                "Civil Engineering",
                "Electronics Engineering"
        };

        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                courses
        );

        courseAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        return courseAdapter;
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {

                            String date =
                                    selectedDay + "/"
                                            + (selectedMonth + 1) + "/"
                                            + selectedYear;

                            etDateOfBirth.setText(date);
                        },
                        year,
                        month,
                        day
                );

        datePickerDialog.show();
    }

    private void saveStudentData() {

        String name = etName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String enrollmentNumber =
                etEnrollmentNumber.getText().toString().trim();
        String joiningYear =
                etJoiningYear.getText().toString().trim();
        String dateOfBirth =
                etDateOfBirth.getText().toString().trim();
        String profileImageUrl =
                etProfileImageUrl.getText().toString().trim();

        String course =
                spinnerCourse.getSelectedItem().toString();

        String semester =
                spinnerSemester.getSelectedItem().toString();

        // Validation
        if (name.isEmpty()) {
            etName.setError("Enter your name");
            return;
        }

        if (mobile.isEmpty()) {
            etMobile.setError("Enter mobile number");
            return;
        }

        if (mobile.length() != 10) {
            etMobile.setError("Enter a valid 10-digit mobile number");
            return;
        }

        if (course.equals("Select Course")) {
            Toast.makeText(
                    this,
                    "Please select a course",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (enrollmentNumber.isEmpty()) {
            etEnrollmentNumber.setError("Enter enrollment number");
            return;
        }

        if (joiningYear.isEmpty()) {
            etJoiningYear.setError("Enter joining year");
            return;
        }

        if (semester.equals("Select Semester")) {
            Toast.makeText(
                    this,
                    "Please select semester",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (dateOfBirth.isEmpty()) {
            etDateOfBirth.setError("Select date of birth");
            return;
        }

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "User is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();

        // Firestore data
        Map<String, Object> student = new HashMap<>();

        student.put("name", name);
        student.put("mobileNumber", mobile);
        student.put("email", email);
        student.put("course", course);
        student.put("enrollmentNumber", enrollmentNumber);
        student.put("joiningYear", joiningYear);
        student.put("semester", semester);
        student.put("dateOfBirth", dateOfBirth);
        student.put("profileImageUrl", profileImageUrl);

        // Save to Firestore
        db.collection("users")
                .document(uid)
                .set(student)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            StudentActivity.this,
                            "Student data saved successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            StudentActivity.this,
                            ProfileActivity.class
                    );

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(
                        StudentActivity.this,
                        "Failed to save data: "
                                + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }
}