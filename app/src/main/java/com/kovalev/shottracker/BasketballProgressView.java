package com.kovalev.shottracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BasketballProgressView extends View {

    private int progressPercent = 0;

    private Paint ringBackgroundPaint;
    private Paint ringProgressPaint;
    private Paint ringHighlightPaint;
    private Paint ballPaint;
    private Paint ballLinePaint;

    public BasketballProgressView(Context context) {
        super(context);
        init();
    }

    public BasketballProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasketballProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ringBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringBackgroundPaint.setColor(Color.rgb(55, 55, 55));
        ringBackgroundPaint.setStyle(Paint.Style.STROKE);
        ringBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        ringProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringProgressPaint.setColor(Color.rgb(255, 122, 26));
        ringProgressPaint.setStyle(Paint.Style.STROKE);
        ringProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        ringHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringHighlightPaint.setColor(Color.rgb(255, 170, 75));
        ringHighlightPaint.setStyle(Paint.Style.STROKE);
        ringHighlightPaint.setStrokeCap(Paint.Cap.ROUND);

        ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballPaint.setColor(Color.rgb(255, 122, 26));
        ballPaint.setStyle(Paint.Style.FILL);

        ballLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballLinePaint.setColor(Color.rgb(25, 18, 14));
        ballLinePaint.setStyle(Paint.Style.STROKE);
        ballLinePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setProgressPercent(int percent) {
        progressPercent = Math.max(0, Math.min(100, percent));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float size = Math.min(getWidth(), getHeight());
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float strokeWidth = size * 0.075f;
        float ringRadius = size * 0.34f;
        float ballRadius = size * 0.06f;

        ringBackgroundPaint.setStrokeWidth(strokeWidth);
        ringProgressPaint.setStrokeWidth(strokeWidth);
        ringHighlightPaint.setStrokeWidth(strokeWidth * 0.45f);

        RectF ringRect = new RectF(
                centerX - ringRadius,
                centerY - ringRadius,
                centerX + ringRadius,
                centerY + ringRadius
        );

        float startAngle = -90f;
        float sweepAngle = 360f * progressPercent / 100f;

        canvas.drawArc(ringRect, 0, 360, false, ringBackgroundPaint);
        canvas.drawArc(ringRect, startAngle, sweepAngle, false, ringProgressPaint);

        if (progressPercent > 8) {
            float highlightSweep = Math.min(55f, sweepAngle * 0.45f);

            canvas.drawArc(
                    ringRect,
                    startAngle + sweepAngle - highlightSweep - 10f,
                    highlightSweep,
                    false,
                    ringHighlightPaint
            );
        }
    }

    private void drawBall(Canvas canvas, float centerX, float centerY, float ringRadius, float ballRadius, float angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees);

        float ballX = centerX + (float) Math.cos(angleRadians) * ringRadius;
        float ballY = centerY + (float) Math.sin(angleRadians) * ringRadius;

        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);

        ballLinePaint.setStrokeWidth(ballRadius * 0.18f);

        canvas.drawLine(
                ballX - ballRadius,
                ballY,
                ballX + ballRadius,
                ballY,
                ballLinePaint
        );

        canvas.drawLine(
                ballX,
                ballY - ballRadius,
                ballX,
                ballY + ballRadius,
                ballLinePaint
        );

        RectF leftArc = new RectF(
                ballX - ballRadius * 0.9f,
                ballY - ballRadius,
                ballX + ballRadius * 0.35f,
                ballY + ballRadius
        );

        RectF rightArc = new RectF(
                ballX - ballRadius * 0.35f,
                ballY - ballRadius,
                ballX + ballRadius * 0.9f,
                ballY + ballRadius
        );

        canvas.drawArc(leftArc, -90, 180, false, ballLinePaint);
        canvas.drawArc(rightArc, 90, 180, false, ballLinePaint);
    }
}