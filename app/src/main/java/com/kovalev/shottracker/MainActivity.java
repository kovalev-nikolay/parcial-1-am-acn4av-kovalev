package com.kovalev.shottracker;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_MODE = "mode";

    private TextView tvStatsModeTitle;
    private TextView tvAccuracyPercent;
    private TextView tvShotsCount;
    private ImageView imgStatsRing;

    private FrameLayout btnJuntos;
    private FrameLayout btnLibres;
    private FrameLayout btnCampo;
    private FrameLayout btnTres;

    private LinearLayout layoutHomeContent;
    private TextView tvDynamicModeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClicks();

        String mode = getSharedPreferences("app", MODE_PRIVATE)
                .getString(KEY_MODE, "juntos");

        switch (mode) {
            case "libres": showLibresStats(); break;
            case "campo": showCampoStats(); break;
            case "tres": showTresStats(); break;
            default: showJuntosStats();
        }
    }

    private void initViews() {
        tvStatsModeTitle = findViewById(R.id.tvStatsModeTitle);
        tvAccuracyPercent = findViewById(R.id.tvAccuracyPercent);
        tvShotsCount = findViewById(R.id.tvShotsCount);
        imgStatsRing = findViewById(R.id.imgStatsRing);

        btnJuntos = findViewById(R.id.btnJuntos);
        btnLibres = findViewById(R.id.btnLibres);
        btnCampo = findViewById(R.id.btnCampo);
        btnTres = findViewById(R.id.btnTres);

        layoutHomeContent = findViewById(R.id.layoutHomeContent);
        createDynamicModeInfo();
    }

    private void createDynamicModeInfo() {
        tvDynamicModeInfo = new TextView(this);
        tvDynamicModeInfo.setText("Modo seleccionado: Juntos");
        tvDynamicModeInfo.setTextColor(getColor(R.color.color_orange));
        tvDynamicModeInfo.setTextSize(14);
        tvDynamicModeInfo.setPadding(0, 12, 0, 0);

        layoutHomeContent.addView(tvDynamicModeInfo);
    }

    private void setupClicks() {
        btnJuntos.setOnClickListener(v -> showJuntosStats());
        btnLibres.setOnClickListener(v -> showLibresStats());
        btnCampo.setOnClickListener(v -> showCampoStats());
        btnTres.setOnClickListener(v -> showTresStats());

        findViewById(R.id.navTraining).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrainingActivity.class);
            startActivity(intent);
        });
    }

    private void showJuntosStats() {
        tvStatsModeTitle.setText(R.string.mode_juntos);
        tvAccuracyPercent.setText("27%");
        tvShotsCount.setText("270 / 1000");
        imgStatsRing.setImageResource(R.drawable.stats_ring_static);
        setActiveButton(btnJuntos);
        tvDynamicModeInfo.setText(
                getString(R.string.dynamic_mode, getString(R.string.mode_juntos))
        );

        getSharedPreferences("app", MODE_PRIVATE)
                .edit().putString(KEY_MODE, "juntos").apply();
    }

    private void showLibresStats() {
        tvStatsModeTitle.setText(R.string.mode_libres);
        tvAccuracyPercent.setText("64%");
        tvShotsCount.setText("64 / 100");
        imgStatsRing.setImageResource(R.drawable.stats_ring_libres);
        setActiveButton(btnLibres);
        tvDynamicModeInfo.setText(
                getString(R.string.dynamic_mode, getString(R.string.mode_libres))
        );

        getSharedPreferences("app", MODE_PRIVATE)
                .edit().putString(KEY_MODE, "libres").apply();
    }

    private void showCampoStats() {
        tvStatsModeTitle.setText(R.string.mode_campo);
        tvAccuracyPercent.setText("42%");
        tvShotsCount.setText("84 / 200");
        imgStatsRing.setImageResource(R.drawable.stats_ring_campo);
        setActiveButton(btnCampo);
        tvDynamicModeInfo.setText(
                getString(R.string.dynamic_mode, getString(R.string.mode_campo))
        );

        getSharedPreferences("app", MODE_PRIVATE)
                .edit().putString(KEY_MODE, "campo").apply();
    }

    private void showTresStats() {
        tvStatsModeTitle.setText(R.string.mode_tres);
        tvAccuracyPercent.setText("31%");
        tvShotsCount.setText("31 / 100");
        imgStatsRing.setImageResource(R.drawable.stats_ring_tres);
        setActiveButton(btnTres);
        tvDynamicModeInfo.setText(
                getString(R.string.dynamic_mode, getString(R.string.mode_tres))
        );

        getSharedPreferences("app", MODE_PRIVATE)
                .edit().putString(KEY_MODE, "tres").apply();
    }

    private void setActiveButton(FrameLayout activeButton) {
        btnJuntos.setBackgroundResource(R.drawable.bg_button_train);
        btnLibres.setBackgroundResource(R.drawable.bg_button_train);
        btnCampo.setBackgroundResource(R.drawable.bg_button_train);
        btnTres.setBackgroundResource(R.drawable.bg_button_train);

        activeButton.setBackgroundResource(R.drawable.bg_button_active);
    }
}