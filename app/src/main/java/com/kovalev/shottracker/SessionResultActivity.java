package com.kovalev.shottracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessionResultActivity extends AppCompatActivity {

    public static final String EXTRA_MODE_TITLE = "mode_title";
    public static final String EXTRA_MADE_COUNT = "made_count";
    public static final String EXTRA_TOTAL_COUNT = "total_count";
    public static final String EXTRA_MAX_STREAK = "max_streak";
    public static final String EXTRA_AVERAGE_TIME = "average_time";
    public static final String EXTRA_STARTED_AT = "started_at";
    public static final String EXTRA_ENDED_AT = "ended_at";

    public static final String EXTRA_FROM_HISTORY = "from_history";

    private TextView tvResultMode;
    private TextView tvResultPercent;
    private TextView tvResultShots;
    private TextView tvResultMaxStreak;
    private TextView tvResultAverageTime;
    private TextView tvResultStart;
    private TextView tvResultEnd;
    private Button btnResultBackTraining;
    private boolean fromHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_session_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        fromHistory = getIntent().getBooleanExtra(EXTRA_FROM_HISTORY, false);

        showResultData();
        setupClicks();
    }

    private void initViews() {
        tvResultMode = findViewById(R.id.tvResultMode);
        tvResultPercent = findViewById(R.id.tvResultPercent);
        tvResultShots = findViewById(R.id.tvResultShots);
        tvResultMaxStreak = findViewById(R.id.tvResultMaxStreak);
        tvResultAverageTime = findViewById(R.id.tvResultAverageTime);
        tvResultStart = findViewById(R.id.tvResultStart);
        tvResultEnd = findViewById(R.id.tvResultEnd);
        btnResultBackTraining = findViewById(R.id.btnResultBackTraining);
    }

    private void showResultData() {
        String modeTitle = getIntent().getStringExtra(EXTRA_MODE_TITLE);
        int madeCount = getIntent().getIntExtra(EXTRA_MADE_COUNT, 0);
        int totalCount = getIntent().getIntExtra(EXTRA_TOTAL_COUNT, 0);
        int maxStreak = getIntent().getIntExtra(EXTRA_MAX_STREAK, 0);
        double averageTime = getIntent().getDoubleExtra(EXTRA_AVERAGE_TIME, 0);
        long startedAt = getIntent().getLongExtra(EXTRA_STARTED_AT, 0);
        long endedAt = getIntent().getLongExtra(EXTRA_ENDED_AT, 0);

        if (modeTitle == null) {
            modeTitle = "Juntos";
        }

        int percent = 0;
        if (totalCount > 0) {
            percent = Math.round((madeCount * 100f) / totalCount);
        }

        tvResultMode.setText(modeTitle);
        tvResultPercent.setText(percent + "%");
        tvResultShots.setText("Aciertos / intentos: " + madeCount + " / " + totalCount);
        tvResultMaxStreak.setText("Máxima racha: " + maxStreak);
        tvResultAverageTime.setText(String.format(Locale.getDefault(), "Tiempo promedio por tiro: %.1f seg", averageTime));
        tvResultStart.setText("Inicio: " + formatDate(startedAt));
        tvResultEnd.setText("Fin: " + formatDate(endedAt));
        if (fromHistory) {
            btnResultBackTraining.setText("Volver al historial");
        } else {
            btnResultBackTraining.setText("Volver a entrenamientos");
        }
    }

    private String formatDate(long time) {
        if (time == 0) {
            return "--";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return formatter.format(new Date(time));
    }

    private void setupClicks() {
        btnResultBackTraining.setOnClickListener(v -> {
            if (fromHistory) {
                finish();
            } else {
                Intent intent = new Intent(SessionResultActivity.this, TrainingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.btnResultBackHome).setOnClickListener(v -> {
            Intent intent = new Intent(SessionResultActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}