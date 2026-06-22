package com.kovalev.shottracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessionListActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "training_sessions";
    private static final String KEY_SESSIONS = "sessions";

    private TextView tvSessionListMode;
    private LinearLayout layoutSessionRows;
    private TrendChartView trendChartView;

    private String selectedMode;
    private String selectedModeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_session_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedMode = getIntent().getStringExtra(StatsActivity.EXTRA_MODE);
        selectedModeTitle = getIntent().getStringExtra(StatsActivity.EXTRA_MODE_TITLE);

        if (selectedMode == null) {
            selectedMode = "all";
        }

        if (selectedModeTitle == null) {
            selectedModeTitle = getString(R.string.mode_juntos);
        }

        initViews();
        setupClicks();
        showSessions();
    }

    private void initViews() {
        tvSessionListMode = findViewById(R.id.tvSessionListMode);
        layoutSessionRows = findViewById(R.id.layoutSessionRows);
        trendChartView = findViewById(R.id.trendChartView);

        tvSessionListMode.setText(selectedModeTitle);
    }

    private void setupClicks() {
        findViewById(R.id.btnSessionListBack).setOnClickListener(v -> finish());
    }

    private void showSessions() {
        layoutSessionRows.removeAllViews();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedSessions = preferences.getString(KEY_SESSIONS, "");

        if (savedSessions.isEmpty()) {
            trendChartView.setPercentages(new ArrayList<>());
            addEmptyMessage();
            return;
        }

        String[] sessions = savedSessions.split("\n");

        List<String[]> visibleSessions = new ArrayList<>();
        List<Integer> percentages = new ArrayList<>();

        for (String session : sessions) {
            String[] data = session.split(";");

            if (data.length < 9) {
                continue;
            }

            String mode = data[0];

            if (!selectedMode.equals("all") && !selectedMode.equals(mode)) {
                continue;
            }

            visibleSessions.add(data);
            percentages.add(parseInt(data[6]));
        }

        trendChartView.setPercentages(percentages);

        if (visibleSessions.isEmpty()) {
            addEmptyMessage();
            return;
        }

        for (int i = visibleSessions.size() - 1; i >= 0; i--) {
            addSessionRow(visibleSessions.get(i));
        }
    }

    private void addSessionRow(String[] data) {
        long endedAt = parseLong(data[3]);
        int madeCount = parseInt(data[4]);
        int totalCount = parseInt(data[5]);
        int percent = parseInt(data[6]);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundResource(R.drawable.bg_card_dark);
        row.setPadding(22, 16, 18, 16);

        TextView rowInfo = new TextView(this);
        rowInfo.setText(
                getString(
                        R.string.session_row_format,
                        formatShortDate(endedAt),
                        percent,
                        madeCount,
                        totalCount
                )
        );
        rowInfo.setTextColor(getColor(R.color.color_white));
        rowInfo.setTextSize(15);

        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );

        TextView rowAction = new TextView(this);
        rowAction.setText(R.string.session_row_action);
        rowAction.setTextColor(getColor(R.color.color_orange));
        rowAction.setTextSize(15);
        rowAction.setGravity(Gravity.CENTER_VERTICAL);
        rowAction.setPadding(12, 0, 0, 0);

        row.addView(rowInfo, infoParams);
        row.addView(rowAction);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, 0, 0, 12);

        row.setOnClickListener(v -> openSessionResult(data));

        layoutSessionRows.addView(row, rowParams);
    }

    private void openSessionResult(String[] data) {
        String modeTitle = data[1];
        long startedAt = parseLong(data[2]);
        long endedAt = parseLong(data[3]);
        int madeCount = parseInt(data[4]);
        int totalCount = parseInt(data[5]);
        int maxStreak = parseInt(data[7]);
        double averageTime = parseDouble(data[8]);

        Intent intent = new Intent(SessionListActivity.this, SessionResultActivity.class);
        intent.putExtra(SessionResultActivity.EXTRA_MODE_TITLE, modeTitle);
        intent.putExtra(SessionResultActivity.EXTRA_MADE_COUNT, madeCount);
        intent.putExtra(SessionResultActivity.EXTRA_TOTAL_COUNT, totalCount);
        intent.putExtra(SessionResultActivity.EXTRA_MAX_STREAK, maxStreak);
        intent.putExtra(SessionResultActivity.EXTRA_AVERAGE_TIME, averageTime);
        intent.putExtra(SessionResultActivity.EXTRA_STARTED_AT, startedAt);
        intent.putExtra(SessionResultActivity.EXTRA_ENDED_AT, endedAt);
        intent.putExtra(SessionResultActivity.EXTRA_FROM_HISTORY, true);

        startActivity(intent);
    }

    private void addEmptyMessage() {
        TextView emptyText = new TextView(this);
        emptyText.setText(R.string.session_empty_message);
        emptyText.setTextColor(getColor(R.color.color_orange));
        emptyText.setTextSize(15);
        emptyText.setGravity(Gravity.CENTER);
        emptyText.setPadding(0, 28, 0, 0);

        layoutSessionRows.addView(emptyText);
    }

    private String formatShortDate(long time) {
        if (time == 0) {
            return getString(R.string.empty_date);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        return formatter.format(new Date(time));
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

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }
}