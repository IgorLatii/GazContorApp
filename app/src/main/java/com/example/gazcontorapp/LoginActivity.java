package com.example.gazcontorapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gazcontorapp.utils.SessionManager;
import com.example.gazcontorapp.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText contractNumber, meterNumber;
    private TextView errorMessage;
    private Button loginButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        contractNumber = findViewById(R.id.contractNumber);
        meterNumber = findViewById(R.id.meterNumber);
        errorMessage = findViewById(R.id.errorMessage);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> attemptLogin());

        contractNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ValidationUtils.isValidContract(s.toString())) {
                    contractNumber.setError("Format: 123/1234567890");
                } else {
                    contractNumber.setError(null); // Ошибка исчезнет, если формат верный
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        meterNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ValidationUtils.isValidMeter(s.toString())) {
                    meterNumber.setError("Format: 8 cifre");
                } else {
                    meterNumber.setError(null); // Ошибка исчезнет, если формат верный
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void attemptLogin() {
        String contract = contractNumber.getText().toString().trim();
        String meter = meterNumber.getText().toString().trim();

        if (!ValidationUtils.isValidContract(contract)) {
            errorMessage.setText("Invalid contract number format");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (!ValidationUtils.isValidMeter(meter)) {
            errorMessage.setText("Invalid meter number format");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        errorMessage.setVisibility(View.GONE);

        // Simulate successful login
        sessionManager = new SessionManager(this);
        sessionManager.setLogin(true);  // Сохраняем статус логина
        sessionManager.saveLoginDetails(contract, meter);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("contractNumber", contract);
        intent.putExtra("meterNumber", meter);
        startActivity(intent);
        finish();
    }
}

