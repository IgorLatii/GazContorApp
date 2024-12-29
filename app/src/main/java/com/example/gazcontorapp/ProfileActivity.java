package com.example.gazcontorapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gazcontorapp.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private TextView profileInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Display the "back" button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
            getSupportActionBar().setTitle("Profile Panel");
        }

        profileInfo = findViewById(R.id.profileInfo);

        SessionManager sessionManager = new SessionManager(this);
        String contractNumber = sessionManager.getContractNumber();
        String meterNumber = sessionManager.getMeterNumber();

        profileInfo.setText("Numărul contractului - " + contractNumber + "\n" +
                "Numărul contorului - " + meterNumber);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Проверяем ID кнопки "назад"
            finish(); // Завершаем активность и возвращаемся назад
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

