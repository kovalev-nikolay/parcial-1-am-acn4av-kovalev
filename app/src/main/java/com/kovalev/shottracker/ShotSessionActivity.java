package com.kovalev.shottracker;

import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShotSessionActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "training_sessions";
    private static final String KEY_SESSIONS = "sessions";
    private TextView tvSessionMode;
    private TextView tvSessionCounter;
    private TextView tvSessionStatus;

    private String mode;
    private String modeTitle;

    private int madeCount = 0;
    private int totalCount = 0;
    private int currentStreak = 0;
    private int maxStreak = 0;

    private long startedAt;
    private long lastShotAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shot_session);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mode = getIntent().getStringExtra(TrainingActivity.EXTRA_MODE);
        modeTitle = getIntent().getStringExtra(TrainingActivity.EXTRA_MODE_TITLE);

        if (mode == null) {
            mode = "juntos";
        }

        if (modeTitle == null) {
            modeTitle = "Juntos";
        }

        startedAt = System.currentTimeMillis();
        lastShotAt = startedAt;

        initViews();
        setupClicks();
        updateCounter();
        startStatusBlink();
    }

    private void initViews() {
        tvSessionMode = findViewById(R.id.tvSessionMode);
        tvSessionCounter = findViewById(R.id.tvSessionCounter);
        tvSessionStatus = findViewById(R.id.tvSessionStatus);

        tvSessionMode.setText(modeTitle);
    }

    private void setupClicks() {
        findViewById(R.id.btnShotMade).setOnClickListener(v -> registerShot(true));
        findViewById(R.id.btnShotMissed).setOnClickListener(v -> registerShot(false));

        findViewById(R.id.btnFinishSession).setOnClickListener(v -> openResultScreen());
    }

    private void registerShot(boolean made) {
        totalCount++;
        lastShotAt = System.currentTimeMillis();

        if (made) {
            madeCount++;
            currentStreak++;

            if (currentStreak > maxStreak) {
                maxStreak = currentStreak;
            }
        } else {
            currentStreak = 0;
        }

        updateCounter();
    }

    private void updateCounter() {
        tvSessionCounter.setText(madeCount + " / " + totalCount);
    }

    private void startStatusBlink() {
        AlphaAnimation blinkAnimation = new AlphaAnimation(1.0f, 0.25f);
        blinkAnimation.setDuration(600);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);

        tvSessionStatus.startAnimation(blinkAnimation);
    }

    private void openResultScreen() {
        long endedAt = lastShotAt;

        double averageTime = 0;
        if (totalCount > 0) {
            averageTime = ((lastShotAt - startedAt) / 1000.0) / totalCount;
        }

        int percent = 0;
        if (totalCount > 0) {
            percent = Math.round((madeCount * 100f) / totalCount);
        }

        saveSessionLocally(endedAt, percent, averageTime);

        Intent intent = new Intent(ShotSessionActivity.this, SessionResultActivity.class);
        intent.putExtra(SessionResultActivity.EXTRA_MODE_TITLE, modeTitle);
        intent.putExtra(SessionResultActivity.EXTRA_MADE_COUNT, madeCount);
        intent.putExtra(SessionResultActivity.EXTRA_TOTAL_COUNT, totalCount);
        intent.putExtra(SessionResultActivity.EXTRA_MAX_STREAK, maxStreak);
        intent.putExtra(SessionResultActivity.EXTRA_AVERAGE_TIME, averageTime);
        intent.putExtra(SessionResultActivity.EXTRA_STARTED_AT, startedAt);
        intent.putExtra(SessionResultActivity.EXTRA_ENDED_AT, endedAt);

        startActivity(intent);
        finish();
    }

    private void saveSessionLocally(long endedAt, int percent, double averageTime) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String oldSessions = preferences.getString(KEY_SESSIONS, "");

        String averageTimeText = String.format(Locale.US, "%.1f", averageTime);

        String newSession = mode + ";"
                + modeTitle + ";"
                + startedAt + ";"
                + endedAt + ";"
                + madeCount + ";"
                + totalCount + ";"
                + percent + ";"
                + maxStreak + ";"
                + averageTimeText;

        String updatedSessions;

        if (oldSessions.isEmpty()) {
            updatedSessions = newSession;
        } else {
            updatedSessions = oldSessions + "\n" + newSession;
        }

        preferences.edit()
                .putString(KEY_SESSIONS, updatedSessions)
                .apply();
    }
}