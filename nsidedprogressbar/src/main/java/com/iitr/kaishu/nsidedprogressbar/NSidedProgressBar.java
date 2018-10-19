package com.iitr.kaishu.nsidedprogressbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class NSidedProgressBar extends View {


    //Required Variables
    private Context context;
    private Paint primaryPaint;
    private Paint secondaryPaint;
    private Path basePath;
    private Path secPath;
    private PathMeasure pathMeasure;


    // All coordinates
    private float[] xVertiCoord;
    private float[] yVertiCoord;
    private float[] x1VertiCoord;
    private float[] x2VertiCoord;
    private float[] y1VertiCoord;
    private float[] y2VertiCoord;
    private float[] xMidPoints;
    private float[] yMidPoints;
    private float xCenter;
    private float yCenter;


    //Temporary Variables
    private float tempDiffValue;
    private boolean tempState1 = false;
    private boolean tempState2 = true;
    private float tempStartPoint = 0;
    private boolean exit = false;


    //Properties
    private int sideCount = 4;
    private int refreshRate = 60;
    private float progress = 0;
    private float baseSpeed = 10;
    private float minDistance = 70;
    private float minDistanceSec = 40;
    private boolean isClockWise = true;
    private int primaryColor;
    private int secondaryColor;

    //Init
    private float sideLength;
    private int preSetHeight = 150;
    private int preSetWidth = 150;
    private float withoutAcceleration = 0;
    private float withAcceleration = 0;
    private float akinTime = 0;
    private float startPoint = minDistanceSec;
    private float endPoint = 0;
    private int whereToGo = 1;
    private float times = 0;
    private float totalDisStartPoint;
    private float sideProgress = 0;
    private float initialPosition = 0;
    private float radius = 0;
    private Timer timer;


    public NSidedProgressBar(Context context, int sideCount) {
        super(context);
        this.context = context;
        this.sideCount = sideCount;
        initProgressBar();
    }

    public NSidedProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        xmlAttributes(context.obtainStyledAttributes(attrs, R.styleable.NSidedProgressBar));
        initProgressBar();
    }

    public NSidedProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        xmlAttributes(context.obtainStyledAttributes(attrs, R.styleable.NSidedProgressBar));
        initProgressBar();
    }

    private void initProgressBar() {

        basePath = new Path();
        secPath = new Path();
        pathMeasure = new PathMeasure();

        setPaints();

        xVertiCoord = new float[sideCount];
        yVertiCoord = new float[sideCount];
        x1VertiCoord = new float[sideCount];
        y1VertiCoord = new float[sideCount];
        x2VertiCoord = new float[sideCount];
        y2VertiCoord = new float[sideCount];
        xMidPoints = new float[sideCount];
        yMidPoints = new float[sideCount];

    }

    private void setPaints() {
        primaryPaint = new Paint();
        primaryPaint.setStrokeWidth(8);
        primaryPaint.setStyle(Paint.Style.STROKE);

        secondaryPaint = new Paint();
        secondaryPaint.setStrokeWidth(10);
        secondaryPaint.setStyle(Paint.Style.STROKE);

        primaryPaint.setColor(primaryColor);
        secondaryPaint.setColor(secondaryColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int viewWidth = preSetWidth + this.getPaddingLeft() + this.getPaddingRight();
        int viewHeight = preSetWidth + this.getPaddingTop() + this.getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(viewWidth, widthSize);
        } else {
            width = viewWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(viewHeight, heightSize);
        } else {
            height = viewHeight;
        }

        xCenter = width / 2;
        yCenter = height / 2;

        radius = xCenter - Math.max(getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom()) - 10;
        initiateDraw();

        setMeasuredDimension(width, height);
    }


    private void initiateDraw() {
        setCoordinates();
        basePath.reset();
        basePath.moveTo(x1VertiCoord[0], y1VertiCoord[0]);
        for (int i = 0; i < sideCount; i++) {
            basePath.cubicTo(x1VertiCoord[i], y1VertiCoord[i], xVertiCoord[i], yVertiCoord[i], x2VertiCoord[i], y2VertiCoord[i]);
            basePath.lineTo(x1VertiCoord[(i + 1) % sideCount], y1VertiCoord[(i + 1) % sideCount]);
        }
        basePath.close();
        sideLength = pathMeasure.getLength() / sideCount;
        pathMeasure.setPath(basePath, false);

        totalDisStartPoint = pathMeasure.getLength() + minDistance + baseSpeed * refreshRate + 250;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(basePath, primaryPaint);


        withoutAcceleration = baseSpeed;
        if (akinTime >= 0) {
            withAcceleration = baseSpeed + akinTime;
        } else {
            withAcceleration = baseSpeed + 0.5F;
        }
        if (whereToGo == 1) {
            firstPath(canvas);
        } else if (whereToGo == 2) {
            secondPath(canvas);
        }
    }

    private void setCoordinates() {
        float diffAngle;
        if (isClockWise) {
            diffAngle = (float) (-2 * Math.PI / sideCount);
        } else {
            diffAngle = (float) (2 * Math.PI / sideCount);
        }
        for (int i = 0; i < sideCount; i++) {
            xVertiCoord[i] = xCenter - (float) ((radius) * Math.sin((double) (i + 1) * diffAngle));
            yVertiCoord[i] = yCenter - (float) ((radius) * Math.cos((double) (i + 1) * diffAngle));
        }

        for (int i = 0; i < sideCount; i++) {
            x1VertiCoord[i] = (1 * xVertiCoord[(sideCount + i - 1) % sideCount] + 9 * xVertiCoord[i]) / 10;
            y1VertiCoord[i] = (1 * yVertiCoord[(sideCount + i - 1) % sideCount] + 9 * yVertiCoord[i]) / 10;

            x2VertiCoord[i] = (1 * xVertiCoord[(i + 1) % sideCount] + 9 * xVertiCoord[i]) / 10;
            y2VertiCoord[i] = (1 * yVertiCoord[(i + 1) % sideCount] + 9 * yVertiCoord[i]) / 10;
        }

        for (int i = 0; i < sideCount; i++) {
            xMidPoints[i] = (xVertiCoord[i] + xVertiCoord[(i + 1) % sideCount]) / 2;
            yMidPoints[i] = (yVertiCoord[i] + yVertiCoord[(i + 1) % sideCount]) / 2;
        }
    }


    private void xmlAttributes(TypedArray array) {
        sideCount = array.getInt(R.styleable.NSidedProgressBar_nsidedProg_sideCount, 3);
        primaryColor = array.getColor(R.styleable.NSidedProgressBar_nsidedProg_primaryColor, Color.parseColor("#E0E0E0"));
        secondaryColor = array.getColor(R.styleable.NSidedProgressBar_nsidedProg_secondaryColor, Color.parseColor("#6499fa"));
        baseSpeed = array.getFloat(R.styleable.NSidedProgressBar_nsidedProg_baseSpeed, 5);
        refreshRate = array.getInt(R.styleable.NSidedProgressBar_nsidedProg_refreshRate, 60);
    }


    private void firstPath(Canvas canvas) {
        if (tempState2) {
            tempState2 = false;
            if (baseSpeed * refreshRate - minDistance >= startPoint) {
                totalDisStartPoint = pathMeasure.getLength() + baseSpeed * refreshRate - minDistance - minDistanceSec;
            } else {
                totalDisStartPoint = pathMeasure.getLength() - (minDistanceSec - (baseSpeed * refreshRate - minDistance));
            }

            times = (8 * ((totalDisStartPoint / 2) - (baseSpeed * refreshRate / 2))) / (refreshRate * refreshRate);

        }
        if (tempStartPoint <= totalDisStartPoint / 2) {
            akinTime += times;

        } else {
            akinTime -= times;
            if (akinTime <= 0) {
                exit = true;
                tempStartPoint = 0;
                akinTime = 0;
            }

        }
        startPoint += withAcceleration;
        endPoint += withoutAcceleration;
        secPath.reset();
        tempStartPoint += withAcceleration;


        tempDiffValue = endPoint - startPoint;

        if (tempDiffValue >= 0) {
            if (tempDiffValue <= minDistance) {
                exit = true;
            }
        } else {
            if (pathMeasure.getLength() - startPoint + endPoint <= minDistance) {
                exit = true;
            }
        }
        if (endPoint >= pathMeasure.getLength()) {
            tempState1 = false;
            endPoint = 0;
        }
        if (startPoint >= pathMeasure.getLength()) {
            startPoint = 0;
            tempState1 = true;
        }
        if (tempState1) {
            pathMeasure.getSegment(0, startPoint % pathMeasure.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
            pathMeasure.getSegment(endPoint, pathMeasure.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        } else {
            pathMeasure.getSegment(endPoint, startPoint, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        }
        if (exit) {
            exit = false;
            whereToGo = 2;
            akinTime = 0;
        }
    }

    private void secondPath(Canvas canvas) {
        if (tempState2) {
            tempState2 = false;
            tempStartPoint = 0;
            if (baseSpeed * refreshRate - minDistanceSec >= endPoint) {
                totalDisStartPoint = pathMeasure.getLength() + baseSpeed * refreshRate - minDistance - minDistanceSec;
            } else {
                totalDisStartPoint = pathMeasure.getLength() - (minDistanceSec - (baseSpeed * refreshRate - minDistance));
            }
            times = (8 * ((totalDisStartPoint / 2) - (baseSpeed * refreshRate / 2))) / (refreshRate * refreshRate);
        }

        secPath.reset();
        if (tempStartPoint <= totalDisStartPoint / 2) {
            akinTime += times;

        } else {
            akinTime -= times;
            if (akinTime <= 0) {
                exit = true;
            }
        }

        startPoint += withoutAcceleration;
        endPoint += withAcceleration;
        tempStartPoint += withAcceleration;

        if (endPoint >= pathMeasure.getLength()) {
            endPoint = 0;
            tempState1 = false;
        }
        if (startPoint >= pathMeasure.getLength()) {
            startPoint = 0;
            tempState1 = true;
        }
        if (tempState1) {
            pathMeasure.getSegment(0, startPoint, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
            pathMeasure.getSegment(endPoint, pathMeasure.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        } else {
            pathMeasure.getSegment(endPoint, startPoint, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        }
        tempDiffValue = startPoint - endPoint;

        if (tempDiffValue >= 0) {
            if (tempDiffValue <= minDistanceSec) {
                exit = true;
            }
        } else {
            if (pathMeasure.getLength() + startPoint - endPoint <= minDistanceSec) {
                exit = true;
            }
        }

        if (exit) {
            exit = false;
            akinTime = 0;
            whereToGo = 1;
        }

    }


    private void determinate(Canvas canvas) {
        secPath.reset();

        initialPosition = (float) Math.hypot(x1VertiCoord[0] - xMidPoints[0], y1VertiCoord[0] - yMidPoints[0]);
        sideProgress = pathMeasure.getLength() * progress / 100;
        if (sideProgress + initialPosition > pathMeasure.getLength()) {
            pathMeasure.getSegment(initialPosition, pathMeasure.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
            pathMeasure.getSegment(0, sideProgress - pathMeasure.getLength() + initialPosition, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        } else {
            pathMeasure.getSegment(initialPosition, sideProgress + initialPosition, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == View.INVISIBLE) {
            timer.cancel();
        } else {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NSidedProgressBar.this.invalidate();
                        }
                    });
                }
            }, 0, 1000 / refreshRate);
        }
    }


    public void setProgress(float progress) {
        this.progress = progress;
    }


    public void setPrimaryPaint(Paint primaryPaint) {
        this.primaryPaint = primaryPaint;
    }

    public void setPrimaryPaintColors(int color) {
        this.primaryColor = color;
        primaryPaint.setColor(color);

    }

    public void setSecondaryPaint(Paint secondaryPaint) {
        this.secondaryPaint = secondaryPaint;
    }

    public void setSecondaryPaintColors(int color) {
        this.secondaryColor = color;
        secondaryPaint.setColor(color);
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }


    public void setBaseSpeed(float baseSpeed) {
        this.baseSpeed = baseSpeed;
    }


    public Paint getPrimaryPaint() {
        return primaryPaint;
    }


    public Paint getSecondaryPaint() {
        return secondaryPaint;
    }


    public int getSideCount() {
        return sideCount;
    }


    public float getBaseSpeed() {
        return baseSpeed;
    }


    public void update() {
        if (timer != null) {
            timer.cancel();
        }
        initiateDraw();
    }
}
