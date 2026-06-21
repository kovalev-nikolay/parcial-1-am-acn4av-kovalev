package com.kovalev.shottracker;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.content.Intent;
import java.util.Calendar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_MODE = "mode";
    private static final String TRAINING_PREFS_NAME = "training_sessions";
    private static final String KEY_SESSIONS = "sessions";
    private static final long LAST_MONTH_MS = 30L * 24 * 60 * 60 * 1000;

    private TextView tvStatsModeTitle;
    private TextView tvAccuracyPercent;
    private TextView tvShotsCount;
    private BasketballProgressView imgStatsRing;
    private TextView tvBasketballIcon;

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
        seedDemoSessions();

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
        tvBasketballIcon = findViewById(R.id.tvBasketballIcon);

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

        findViewById(R.id.navStats).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navTraining).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrainingActivity.class);
            startActivity(intent);
        });
    }

    private void showJuntosStats() {
        tvStatsModeTitle.setText(R.string.mode_juntos);
        int percent = updateStatsFromSavedSessions("all");
        imgStatsRing.setProgressPercent(percent);
        moveBasketballIcon(percent);
        setActiveButton(btnJuntos);
        tvDynamicModeInfo.setText(
                getString(R.string.dynamic_mode, getString(R.string.mode_juntos))
        );

        getSharedPreferences("app", MODE_PRIVATE)
                .edit().putString(KEY_MODE, "juntos").apply();
    }

    private void showLibresStats() {
        tvStatsModeTitle.setText(R.string.mode_libres);
        int percent = updateStatsFromSavedSessions("libres");
        imgStatsRing.setProgressPercent(percent);
        moveBasketballIcon(percent);
        setActiveButton(btnLibres);
        tvDynamicModeInfo.setText(
                getString(R.string.dynamic_mode, getString(R.string.mode_libres))
        );

        getSharedPreferences("app", MODE_PRIVATE)
                .edit().putString(KEY_MODE, "libres").apply();
    }

    private void showCampoStats() {
        tvStatsModeTitle.setText(R.string.mode_campo);
        int percent = updateStatsFromSavedSessions("campo");
        imgStatsRing.setProgressPercent(percent);
        moveBasketballIcon(percent);
        setActiveButton(btnCampo);
        tvDynamicModeInfo.setText(
                getString(R.string.dynamic_mode, getString(R.string.mode_campo))
        );

        getSharedPreferences("app", MODE_PRIVATE)
                .edit().putString(KEY_MODE, "campo").apply();
    }

    private void showTresStats() {
        tvStatsModeTitle.setText(R.string.mode_tres);
        int percent = updateStatsFromSavedSessions("tres");
        imgStatsRing.setProgressPercent(percent);
        moveBasketballIcon(percent);
        setActiveButton(btnTres);
        tvDynamicModeInfo.setText(
                getString(R.string.dynamic_mode, getString(R.string.mode_tres))
        );

        getSharedPreferences("app", MODE_PRIVATE)
                .edit().putString(KEY_MODE, "tres").apply();
    }

    private int updateStatsFromSavedSessions(String selectedMode) {
        int[] stats = calculateStats(selectedMode);

        int made = stats[0];
        int total = stats[1];

        int percent = 0;
        if (total > 0) {
            percent = Math.round((made * 100f) / total);
        }

        tvAccuracyPercent.setText(percent + "%");
        tvShotsCount.setText(made + " / " + total);

        return percent;
    }

    private int[] calculateStats(String selectedMode) {
        int madeTotal = 0;
        int shotsTotal = 0;

        String savedSessions = getSharedPreferences(TRAINING_PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_SESSIONS, "");

        if (savedSessions.isEmpty()) {
            return new int[]{0, 0};
        }

        long now = System.currentTimeMillis();
        long monthAgo = now - LAST_MONTH_MS;

        String[] sessions = savedSessions.split("\n");

        for (String session : sessions) {
            String[] data = session.split(";");

            if (data.length < 9) {
                continue;
            }

            String mode = data[0];
            long endedAt = parseLong(data[3]);

            if (endedAt < monthAgo) {
                continue;
            }

            if (!selectedMode.equals("all") && !selectedMode.equals(mode)) {
                continue;
            }

            madeTotal += parseInt(data[4]);
            shotsTotal += parseInt(data[5]);
        }

        return new int[]{madeTotal, shotsTotal};
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private void moveBasketballIcon(int percent) {
        tvBasketballIcon.post(() -> {
            float ringRadius = imgStatsRing.getWidth() * 0.34f;

            float angleDegrees = -90f + (360f * percent / 100f);
            double angleRadians = Math.toRadians(angleDegrees);

            float x = (float) Math.cos(angleRadians) * ringRadius;
            float y = (float) Math.sin(angleRadians) * ringRadius;

            tvBasketballIcon.setTranslationX(x);
            tvBasketballIcon.setTranslationY(y);
        });
    }
    private void setActiveButton(FrameLayout activeButton) {
        btnJuntos.setBackgroundResource(R.drawable.bg_button_train);
        btnLibres.setBackgroundResource(R.drawable.bg_button_train);
        btnCampo.setBackgroundResource(R.drawable.bg_button_train);
        btnTres.setBackgroundResource(R.drawable.bg_button_train);

        activeButton.setBackgroundResource(R.drawable.bg_button_active);
    }


    private void seedDemoSessions() {
        StringBuilder builder = new StringBuilder();

        int[] juntosPercents = {25, 31, 33, 25, 15, 26, 35, 45, 42, 44, 30, 35, 48, 49, 55};
        int[] libresPercents = {62, 69, 68, 67, 53, 39, 45, 79, 74, 56, 64, 78, 79, 68, 85};
        int[] campoPercents = {35, 36, 45, 69, 41, 32, 25, 55, 37, 49, 50, 52, 55, 44, 52};
        int[] tresPercents = {15, 19, 28, 22, 23, 24, 36, 23, 22, 25, 34, 31, 47, 33, 42};

        addDemoSessions(builder, "juntos", "Juntos", juntosPercents);
        addDemoSessions(builder, "libres", "Tiros libres", libresPercents);
        addDemoSessions(builder, "campo", "Tiros de campo", campoPercents);
        addDemoSessions(builder, "tres", "Tiros de tres", tresPercents);

        getSharedPreferences(TRAINING_PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_SESSIONS, builder.toString())
                .apply();
    }

    private void addDemoSessions(StringBuilder builder, String mode, String modeTitle, int[] percents) {
        int totalShots = 20;

        for (int i = 0; i < percents.length; i++) {
            int daysAgo = 28 - (i * 2);
            int percent = percents[i];
            int madeCount = Math.round(totalShots * percent / 100f);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -daysAgo);
            calendar.set(Calendar.HOUR_OF_DAY, 18 + (i % 4));
            calendar.set(Calendar.MINUTE, 10 + i);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long startedAt = calendar.getTimeInMillis();
            long endedAt = startedAt + (10 * 60 * 1000L) + (i * 15000L);

            int maxStreak = Math.max(1, madeCount / 4);
            double averageTime = 1.0 + (i % 5) * 0.2;

            if (builder.length() > 0) {
                builder.append("\n");
            }

            builder.append(mode).append(";")
                    .append(modeTitle).append(";")
                    .append(startedAt).append(";")
                    .append(endedAt).append(";")
                    .append(madeCount).append(";")
                    .append(totalShots).append(";")
                    .append(percent).append(";")
                    .append(maxStreak).append(";")
                    .append(String.format(java.util.Locale.US, "%.1f", averageTime));
        }
    }
}