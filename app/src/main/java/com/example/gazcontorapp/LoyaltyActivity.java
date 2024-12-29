package com.example.gazcontorapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class LoyaltyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty);
        // Display the "back" button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
            getSupportActionBar().setTitle("Loyalty Panel");
        }
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
