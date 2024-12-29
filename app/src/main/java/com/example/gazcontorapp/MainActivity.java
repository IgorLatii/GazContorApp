package com.example.gazcontorapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gazcontorapp.utils.SessionManager;
import com.example.gazcontorapp.utils.ValidationUtils;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import api.ApiClient;
import api.ApiService;
import api.models.DataUploadRequest;
import api.models.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView userInfo;
    private EditText meterReading;
    private ImageView photoPreview;
    private Button capturePhotoButton, submitButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private TextView errorMessageMeterData;
    private String contractNumber;
    private TextView photoErrorTextView;
    private String meterNumber;
    private Uri photoUri;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private Bitmap currentPhotoBitmap;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            // If is not logged in, go to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish current activity
            return;
        }

        contractNumber = sessionManager.getContractNumber();
        meterNumber = sessionManager.getMeterNumber();

        setContentView(R.layout.activity_main);

        userInfo = findViewById(R.id.userInfo);
        meterReading = findViewById(R.id.meterReading);
        photoPreview = findViewById(R.id.photoPreview);
        capturePhotoButton = findViewById(R.id.capturePhotoButton);
        submitButton = findViewById(R.id.submitButton);
        errorMessageMeterData = findViewById(R.id.meterReadingErrorTextView);
        photoErrorTextView = findViewById(R.id.photoErrorTextView);
        progressBar = findViewById(R.id.progressBar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        userInfo.setText("Numărul contractului - " + contractNumber + " \n" +
                "Numărul contorului - " + meterNumber);

        // Add hamburger in ActionBar
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Synchronize the state of the button

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Adding a "Back" button
        toggle.setDrawerIndicatorEnabled(true); // Turn on the hamburger icon

        setupNavigationMenu();

        capturePhotoButton.setOnClickListener(v -> capturePhoto());
        submitButton.setOnClickListener(v -> submitData());

        meterReading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ValidationUtils.isValidMeterReading(s.toString())) {
                    meterReading.setError("Format: 12345,123");
                } else {
                    meterReading.setError(null); // removing the error
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        initActivityResultLauncher();

        capturePhotoButton.setOnClickListener(v -> capturePhoto());
    }

    // Set up navigation menu
    private void setupNavigationMenu() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                logout();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_loyalty) {
                startActivity(new Intent(MainActivity.this, LoyaltyActivity.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void initActivityResultLauncher() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            // Загружаем фото из URI в Bitmap
                            InputStream inputStream = getContentResolver().openInputStream(photoUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            // Валидация фото
                            if (ValidationUtils.isValidPhoto(bitmap)) {
                                currentPhotoBitmap = bitmap; // Сохраняем фото
                                photoPreview.setImageBitmap(currentPhotoBitmap); // Предпросмотр
                                photoErrorTextView.setVisibility(View.GONE);      // Скрыть ошибку
                            } else {
                                photoErrorTextView.setText("Фото не соответствует требованиям.");
                                photoErrorTextView.setVisibility(View.VISIBLE);  // Показать ошибку
                                currentPhotoBitmap = null;                      // Сброс фото
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            photoErrorTextView.setText("Ошибка загрузки фото.");
                            photoErrorTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
    }

    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createTempImageFile();
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.gazcontorapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                // Launch camera
                takePictureLauncher.launch(takePictureIntent);
            } catch (IOException ex) {
                Toast.makeText(this, "Ошибка создания фото", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show();
        }
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void submitData() {
        String reading = meterReading.getText().toString().trim();

        // Checking data entry
        boolean isMeterReadingValid = ValidationUtils.isValidMeterReading(reading);
        boolean isPhotoValid = ValidationUtils.isValidPhoto(currentPhotoBitmap);

        // Input data validation
        if (!isMeterReadingValid) {
            errorMessageMeterData.setText("Formatul incorect! (Ex: 12345,678)");
            errorMessageMeterData.setVisibility(View.VISIBLE);
        } else {
            errorMessageMeterData.setVisibility(View.GONE);
        }

        if (!isPhotoValid) {
            photoErrorTextView.setText("Foto nu îndeplinește cerințele. Încearcă din nou.");
            photoErrorTextView.setVisibility(View.VISIBLE);
        } else {
            photoErrorTextView.setVisibility(View.GONE);
        }

        // If both checks are passed, send the data
        if (isMeterReadingValid && isPhotoValid) {
            uploadData(contractNumber, meterNumber, reading, currentPhotoBitmap);
        }
    }

    private void uploadData(String contractNumber, String meterNumber, String meterReading, Bitmap photoBitmap) {
        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Convert photo to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String photoBase64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

        // Forming a request
        DataUploadRequest request = new DataUploadRequest(contractNumber, meterNumber, meterReading, photoBase64);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Log.d("UPLOAD_REQUEST", "Contract: " + contractNumber + ", Meter: " + meterNumber +
                ", Reading: " + meterReading + ", Photo size: " + photoBase64.length());
        Call<ServerResponse> call = apiService.uploadData(request);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(MainActivity.this, "Данные отправлены успешно. " +
                            "После обработки оператором будут начислены пункты лояльности.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.e("UPLOAD_ERROR", "Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(MainActivity.this, "Ошибка отправки данных. " +
                            "Попробуйте позже.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Ошибка подключения: " +
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.logout(); // Clear the session

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) { // Handling a Hamburger Click
            return true;
        }
        return super.onOptionsItemSelected(item); // Other menu items
    }

}
