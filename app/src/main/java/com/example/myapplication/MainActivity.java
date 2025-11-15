package com.example.myapplication;


import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;

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
            String name = editName.getText().toString().trim();
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("USER_NAME", name);
            startActivity(intent);
        });
    }
}