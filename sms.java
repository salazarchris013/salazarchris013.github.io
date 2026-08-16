package com.example.csalazarcs360m5projecttwo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class sms extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private static final String PREFS_NAME = "SMS_PREFS";
    private static final String KEY_PHONE = "saved_phone";

    Button requestPermissionBtn, sendTestSmsBtn, backBtn, savePhoneBtn;
    TextView permissionStatus;
    EditText phoneInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        requestPermissionBtn = findViewById(R.id.requestPermissionBtn);
        sendTestSmsBtn = findViewById(R.id.testSmsBtn);
        permissionStatus = findViewById(R.id.permissionStatus);
        backBtn = findViewById(R.id.backToInventoryBtn);
        phoneInput = findViewById(R.id.phoneInput);
        savePhoneBtn = findViewById(R.id.savePhoneBtn);

        // Show current permission status on launch
        updatePermissionStatus();
        // Load previously saved phone number into input field
        loadSavedPhone();

        // Request SMS permission from user
        requestPermissionBtn.setOnClickListener(v ->
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.SEND_SMS},
                        SMS_PERMISSION_CODE
                )
        );

        // Save phone number button
        savePhoneBtn.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString().trim();
            if (!phone.isEmpty()) {
                savePhoneNumber(phone);
                Toast.makeText(this, "Phone number saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            }
        });

        // Send test SMS button
        sendTestSmsBtn.setOnClickListener(v -> {
            String savedPhone = getSavedPhoneNumber(this);

            // Check if a phone number has been saved
            if (savedPhone.isEmpty()) {
                Toast.makeText(this, "No phone number saved", Toast.LENGTH_SHORT).show();
                return;
            }

            // Only send if permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {

                sendSms(this, savedPhone,
                        "Inventory Alert: An item is on low stock!");

                Toast.makeText(this, "Test SMS sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "SMS Permission Denied — feature disabled",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Back button returns to inventory screen
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(sms.this, database.class);
            startActivity(intent);
            finish();
        });
    }

    // Update the permission status label on screen
    private void updatePermissionStatus() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            permissionStatus.setText("Permission Status: Granted");
        } else {
            permissionStatus.setText("Permission Status: Denied");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Handle SMS permission request result
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                permissionStatus.setText("Permission Status: Granted");
                Toast.makeText(this,
                        "SMS permission granted",
                        Toast.LENGTH_SHORT).show();

            } else {
                // Permission denied — app continues without SMS
                permissionStatus.setText("Permission Status: Denied");
                Toast.makeText(this,
                        "SMS disabled — app will still work normally",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // Save phone number to SharedPreferences
    private void savePhoneNumber(String phone) {
        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_PHONE, phone).apply();
    }

    // Load saved phone number into input field on launch
    private void loadSavedPhone() {
        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedPhone = prefs.getString(KEY_PHONE, "");
        phoneInput.setText(savedPhone);
    }

    // Static method so other activities can retrieve the saved phone number
    public static String getSavedPhoneNumber(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_PHONE, "");
    }

    // Static method so other activities can trigger SMS alerts directly
    public static void sendSms(Context context,
                               String phoneNumber,
                               String message) {

        // Silently return if permission was not granted
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            SmsManager smsManager;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                smsManager = context.getSystemService(SmsManager.class);
            } else {
                smsManager = SmsManager.getDefault();
            }

            if (smsManager != null) {
                smsManager.sendTextMessage(
                        phoneNumber,
                        null,
                        message,
                        null,
                        null
                );
            }

        } catch (Exception e) {
            // Notify user if SMS fails to send
            Toast.makeText(context,
                    "SMS could not be sent",
                    Toast.LENGTH_SHORT).show();
        }
    }
}