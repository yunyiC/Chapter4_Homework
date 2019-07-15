package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author ?
 * @date ?
 *
 */
public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        drawAll(canvas);
    }

    private void drawAll(final Canvas canvas){
        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }
    }


    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        /* Step */
        for (int i = 0; i < FULL_ANGLE; i += 6 ) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0) {
                paint.setAlpha(CUSTOM_ALPHA);
            } else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        // se superscript percent
        spannableString.setSpan(new RelativeSizeSpan(0.3f),
                spannableString.toString().length() - 2,
                spannableString.toString().length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        StaticLayout layout = new StaticLayout(spannableString,
                textPaint,
                canvas.getWidth(),
                Layout.Alignment.ALIGN_CENTER,
                1,
                1,
                true);
        canvas.translate(mCenterX - layout.getWidth() / 2f,
                mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas ?
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor
        // TODO 绘制时间值 01,02,03...,12

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(64);
        textPaint.setColor(hoursValuesColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        float angleT = -RIGHT_ANGLE;
        float angleAddT = 30.0f;
        float radiusT = mRadius * 0.75f;
        int countValues = 12;
        for (int i=1;i<=countValues;i++) {
            float angleNowT = (angleT + angleAddT * i);
            float posX = (float)(mCenterX + radiusT * Math.cos(Math.toRadians(angleNowT)));
            float posY = (float)(mCenterY + radiusT * Math.sin(Math.toRadians(angleNowT)));
            //文字位置偏上,调整为中心
            float halfTextHeight = (textPaint.descent() + textPaint.ascent()) / 2f;
            posY -= halfTextHeight;
            //绘制文字
            canvas.drawText(String.format(Locale.getDefault(), "%02d", i), posX, posY, textPaint);
        }
    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas ?
     */
    private void drawNeedles(final Canvas canvas) {
        // Default Color:
        // - secondsNeedleColor
        // - hoursNeedleColor
        // - minutesNeedleColor
        // TODO 绘制钟表指针(时,分,秒)
        Calendar calendar = Calendar.getInstance();
        float angleHour = calendar.get(Calendar.HOUR) * 360.0f / 12.0f;
        float angleMinute = calendar.get(Calendar.MINUTE) * 360.0f / 60.0f;
        float angleSecond = calendar.get(Calendar.SECOND) * 360.0f / 60.0f;

        //绘制时针
        drawNeedle(canvas, angleHour, mRadius * 0.3f, 15, hoursNeedleColor);
        //绘制分钟
        drawNeedle(canvas, angleMinute, mRadius * 0.5f, 10, minutesNeedleColor);
        //绘制秒针
        drawNeedle(canvas, angleSecond, mRadius * 0.7f, 5, secondsNeedleColor);
    }

    /**
     * 绘制单个钟表指针
     *
     * @param canvas 画布
     * @param angle 指针的角度
     * @param length 指针的长度
     * @param width 指针的宽度
     * @param color 指针的颜色
     */
    private void drawNeedle(Canvas canvas, float angle, float length, float width, int color) {
        float startX = mCenterX;
        float startY = mCenterY;
        float stopX = (float)(startX + length * Math.cos(Math.toRadians(angle)));
        float stopY = (float)(startY + length * Math.sin(Math.toRadians(angle)));
        Paint paint = new Paint();
        paint.setStrokeWidth(width);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }


    /**
     * Draw Center Dot
     *
     * @param canvas ?
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        // TODO 绘制钟表中心点
        drawCircle(canvas, 30, centerOuterColor);
        drawCircle(canvas, 20, centerInnerColor);
    }

    /**
     * 在画布中心绘制实心圆
     *
     * @param canvas 画布
     * @param radius 半径
     * @param color 颜色
     */
    private void drawCircle(Canvas canvas, float radius, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCenterX, mCenterY, radius, paint);
    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

}