package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StatFs;
import android.os.Environment;
import android.os.BatteryManager;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

// Второй экран проверка состояния телефона низкоуровневыми функциями java

public class SecondActivity extends AppCompatActivity {
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView textGreeting = findViewById(R.id.textGreeting);
        TextView textStorage = findViewById(R.id.textStorage);
        TextView textRam = findViewById(R.id.textRam);
        TextView textBattery = findViewById(R.id.textBattery);
        Button buttonBack = findViewById(R.id.buttonBack);

        String name = getIntent().getStringExtra(String.valueOf(R.string.name_transport));
        textGreeting.setText(name != null && !name.isEmpty() ? "Привет, " + name + "!" : "Привет, гость!");

        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalBlocks = stat.getBlockCountLong();

        double freeGb = availableBlocks * blockSize / (1024.0 * 1024.0 * 1024.0);
        double totalGb = totalBlocks * blockSize / (1024.0 * 1024.0 * 1024.0);

        textStorage.setText(String.format("Хранилище: %.1f ГБ свободно из %.1f ГБ", freeGb, totalGb));

        // 3. ОПЕРАТИВНАЯ ПАМЯТЬ
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long freeMemory = maxMemory - usedMemory;

        textRam.setText(String.format("ОЗУ: %d МБ свободно из %d МБ", freeMemory, maxMemory));

        // 4. БАТАРЕЯ
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        textBattery.setText("Батарея: " + percentage + "%");

        // 5. Кнопка назад
        buttonBack.setOnClickListener(v -> finish());
    }
}