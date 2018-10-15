package com.iitr.kaishu.nsidedprogressbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

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
    private int tempPriColorPos = 0;
    private int tempSecColorPos = 0;


    //Properties
    private int sideCount = 3;
    private int fps = 60;
    private float progress = 0;
    private float baseSpeed = 5;
    private float minDistance = 70;
    private float minDistanceSec = 40;
    private boolean isClockWise = false;
    private int[] primaryColors;
    private int[] secondaryColors;

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
    private static Timer timer;

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

        primaryColors = new int[1];
        secondaryColors = new int[1];
        primaryColors[0] = Color.parseColor("#E0E0E0");
        secondaryColors[0] = Color.parseColor("#6499fa");
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

        primaryPaint.setColor(primaryColors[tempPriColorPos]);
        secondaryPaint.setColor(secondaryColors[tempSecColorPos]);
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

        totalDisStartPoint = pathMeasure.getLength() + minDistance + baseSpeed * fps + 250;
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
        }, 0, 1000 / fps);timer.purge();
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

       /* DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        barWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, barWidth, metrics);
        rimWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rimWidth, metrics);
        circleRadius =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, circleRadius, metrics);

        circleRadius =
                (int) a.getDimension(R.styleable.ProgressWheel_matProg_circleRadius, circleRadius);

        fillRadius = a.getBoolean(R.styleable.ProgressWheel_matProg_fillRadius, false);

        barWidth = (int) a.getDimension(R.styleable.ProgressWheel_matProg_barWidth, barWidth);

        rimWidth = (int) a.getDimension(R.styleable.ProgressWheel_matProg_rimWidth, rimWidth);

        float baseSpinSpeed =
                a.getFloat(R.styleable.ProgressWheel_matProg_spinSpeed, spinSpeed / 360.0f);
        spinSpeed = baseSpinSpeed * 360;

        barSpinCycleTime =
                a.getInt(R.styleable.ProgressWheel_matProg_barSpinCycleTime, (int) barSpinCycleTime);

        barColor = a.getColor(R.styleable.ProgressWheel_matProg_barColor, barColor);

        rimColor = a.getColor(R.styleable.ProgressWheel_matProg_rimColor, rimColor);

        linearProgress = a.getBoolean(R.styleable.ProgressWheel_matProg_linearProgress, false);

        if (a.getBoolean(R.styleable.ProgressWheel_matProg_progressIndeterminate, false)) {
            spin();
        }

        // Recycle
        a.recycle();*/
    }


    private void firstPath(Canvas canvas) {
        if (tempState2) {
            tempState2 = false;
            if (baseSpeed * fps - minDistance >= startPoint) {
                totalDisStartPoint = pathMeasure.getLength() + baseSpeed * fps - minDistance - minDistanceSec;
            } else {
                totalDisStartPoint = pathMeasure.getLength() - (minDistanceSec - (baseSpeed * fps - minDistance));
            }

            times = (8 * ((totalDisStartPoint / 2) - (baseSpeed * fps / 2))) / (fps * fps);

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
            if (baseSpeed * fps - minDistanceSec >= endPoint) {
                totalDisStartPoint = pathMeasure.getLength() + baseSpeed * fps - minDistance - minDistanceSec;
            } else {
                totalDisStartPoint = pathMeasure.getLength() - (minDistanceSec - (baseSpeed * fps - minDistance));
            }

            times = (8 * ((totalDisStartPoint / 2) - (baseSpeed * fps / 2))) / (fps * fps);

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


    private void onSpinComplete() {
        primaryPaint.setColor(primaryColors[(tempPriColorPos+1) % primaryColors.length]);
        secondaryPaint.setColor(secondaryColors[(tempSecColorPos+1) % secondaryColors.length]);
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
            timer=new Timer();
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
            }, 0, 1000 / fps);
        }

    }


    public void setProgress(float progress) {
        this.progress = progress;
    }


    public void setPrimaryPaint(Paint primaryPaint) {
        this.primaryPaint = primaryPaint;
    }

    public void setPrimaryPaintColors(int[] color) {
        this.primaryColors = color;
    }

    public void setSecondaryPaint(Paint secondaryPaint) {
        this.secondaryPaint = secondaryPaint;
    }

    public void setSecondaryPaintColors(int[] color) {
        this.secondaryColors = color;
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
        timer.cancel();
        initiateDraw();
    }

}
