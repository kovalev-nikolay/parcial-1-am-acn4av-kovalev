package com.kovalev.shottracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatsActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_MODE_TITLE = "mode_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupClicks();
    }

    private void setupClicks() {
        findViewById(R.id.btnStatsJuntos).setOnClickListener(v ->
                openSessionList("juntos", "Juntos"));

        findViewById(R.id.btnStatsLibres).setOnClickListener(v ->
                openSessionList("libres", "Tiros libres"));

        findViewById(R.id.btnStatsCampo).setOnClickListener(v ->
                openSessionList("campo", "Tiros de campo"));

        findViewById(R.id.btnStatsTres).setOnClickListener(v ->
                openSessionList("tres", "Tiros de tres"));

        findViewById(R.id.btnStatsBackHome).setOnClickListener(v -> {
            Intent intent = new Intent(StatsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openSessionList(String mode, String modeTitle) {
        Intent intent = new Intent(StatsActivity.this, SessionListActivity.class);
        intent.putExtra(EXTRA_MODE, mode);
        intent.putExtra(EXTRA_MODE_TITLE, modeTitle);
        startActivity(intent);
    }
}