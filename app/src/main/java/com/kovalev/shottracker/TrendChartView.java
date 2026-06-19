package com.kovalev.shottracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class TrendChartView extends View {

    private final List<Integer> percentages = new ArrayList<>();

    private Paint linePaint;
    private Paint pointPaint;
    private Paint gridPaint;
    private Paint textPaint;

    public TrendChartView(Context context) {
        super(context);
        init();
    }

    public TrendChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrendChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_orange));
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_orange));
        pointPaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_white));
        gridPaint.setAlpha(45);
        gridPaint.setStrokeWidth(2f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_white));
        textPaint.setAlpha(170);
        textPaint.setTextSize(34f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setPercentages(List<Integer> newPercentages) {
        percentages.clear();
        percentages.addAll(newPercentages);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float paddingLeft = 12f;
        float paddingRight = 12f;
        float paddingTop = 10f;
        float paddingBottom = 10f;

        float chartWidth = width - paddingLeft - paddingRight;
        float chartHeight = height - paddingTop - paddingBottom;

        drawGrid(canvas, paddingLeft, paddingTop, chartWidth, chartHeight);

        if (percentages.isEmpty()) {
            canvas.drawText("Sin datos", width / 2f, height / 2f + 12f, textPaint);
            return;
        }

        if (percentages.size() == 1) {
            float x = width / 2f;
            float y = getYForPercent(percentages.get(0), paddingTop, chartHeight);
            canvas.drawCircle(x, y, 7f, pointPaint);
            return;
        }

        Path path = new Path();

        for (int i = 0; i < percentages.size(); i++) {
            float x = paddingLeft + (chartWidth * i / (percentages.size() - 1));
            float y = getYForPercent(percentages.get(i), paddingTop, chartHeight);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }

            canvas.drawCircle(x, y, 5f, pointPaint);
        }

        canvas.drawPath(path, linePaint);
    }

    private void drawGrid(Canvas canvas, float left, float top, float chartWidth, float chartHeight) {
        float yTop = top;
        float yMiddle = top + chartHeight / 2f;
        float yBottom = top + chartHeight;

        canvas.drawLine(left, yTop, left + chartWidth, yTop, gridPaint);
        canvas.drawLine(left, yMiddle, left + chartWidth, yMiddle, gridPaint);
        canvas.drawLine(left, yBottom, left + chartWidth, yBottom, gridPaint);
    }

    private float getYForPercent(int percent, float top, float chartHeight) {
        int safePercent = Math.max(0, Math.min(100, percent));
        return top + chartHeight - (chartHeight * safePercent / 100f);
    }
}