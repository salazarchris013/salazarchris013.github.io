package com.example.csalazarcs360m5projecttwo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    EditText editUsername, editPassword;
    Button submitBtn, newBtn;
    InventoryRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repository = new InventoryRepository(this);

        editUsername = findViewById(R.id.editTextUsername);
        editPassword = findViewById(R.id.editTextPassword);
        submitBtn = findViewById(R.id.submitBtn);
        newBtn = findViewById(R.id.newBtn);

        // Login
        submitBtn.setOnClickListener(v -> {
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();

            InputValidator.ValidationResult result = InputValidator.validateCredentials(username, password);
            if (!result.isValid()) {
                Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if(repository.loginUser(username, password)){
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, database.class));
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Create account
        newBtn.setOnClickListener(v -> {
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();

            InputValidator.ValidationResult result = InputValidator.validateCredentials(username, password);
            if (!result.isValid()) {
                Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if(repository.registerUser(username, password)){
                Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User Already Exists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}