package com.example.myapplication;


import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;

import com.example.myapplication.ui.profile.ProfileActivity;

// Главный экран
public class MainActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        textView.setText(R.string.profile_title);

        EditText editName = findViewById(R.id.editName);
        Button buttonProfile = findViewById(R.id.buttonProfile);

        buttonProfile.setOnClickListener(v -> {
            String username = editName.getText().toString().trim();
            if (!username.isEmpty()) {
                Intent intent = new Intent(this, com.example.myapplication.ui.profile.ProfileActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            } else {
                textView.setText("Введите GitHub ник!");
            }
        });
    }
}