package com.iitr.kaishu.nsidedprogressbar;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class NSidedProgressBar extends View {

    private Paint primaryPaint;
    private Paint secondaryPaint;
    private int sideCount = 5;

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



    private float progress;
    private float sideLength;
    private int preSetHeight = 150;
    private int preSetWidth = 150;

    private Path basePath;
    private PathMeasure pm;
    private float withoutAcceleration = 0;
    private float withAcceleration = 0;
    private float endPoint = 0;
    private Context context;
    private float velocity = 5;
    private float akinTime = 0;
    private float minDistance = 70;
    private float minDistanceSec = 40;
    private float startPoint = minDistanceSec;
    private boolean first = true;
    private boolean wildCard1 = false;
    private boolean wildCard2 = true;
    private boolean goInside = false;
    private boolean isAllowed = true;
    private int whereToGo = 0;
    private float initTag = 0;
    private Path secPath;
    private float times = 0;
    private int genCount = 0;
    private int fps = 60;
    private float totalDisStartPoint;
    private float tempStartPoint = 0;
    private int timetimes = 0;
    private float sideProgress = 0;
    private float initialPosition = 0;
    private float radius = 0;

    public NSidedProgressBar(Context context) {
        super(context);
        this.context = context;
        initProgressBar();
    }

    public NSidedProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initProgressBar();
    }

    public NSidedProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initProgressBar();
    }

    private void initProgressBar() {
        primaryPaint = new Paint();
        primaryPaint.setColor(Color.GRAY);
        primaryPaint.setStrokeWidth(5);
        primaryPaint.setStyle(Paint.Style.STROKE);
        basePath = new Path();
        xVertiCoord = new float[sideCount];
        yVertiCoord = new float[sideCount];
        x1VertiCoord = new float[sideCount];
        y1VertiCoord = new float[sideCount];
        x2VertiCoord = new float[sideCount];
        y2VertiCoord = new float[sideCount];
        xMidPoints = new float[sideCount];
        yMidPoints = new float[sideCount];
        progress = 1;
        secondaryPaint = new Paint();
        // secondaryPaint.setPathEffect(corEffect);
        secondaryPaint.setStrokeWidth(10);
        secondaryPaint.setColor(Color.parseColor("#5592fb"));
        secondaryPaint.setStyle(Paint.Style.STROKE);
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress += 0.5;
                        NSidedProgressBar.this.invalidate();
                    }
                });
            }
        }, 0, 1000 / fps);
        pm = new PathMeasure();
        secPath = new Path();


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

        radius = xCenter - Math.max(getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());

        setCoordinates();
        basePath.reset();
        basePath.moveTo(x1VertiCoord[0], y1VertiCoord[0]);
        for (int i = 0; i < sideCount; i++) {
            basePath.cubicTo(x1VertiCoord[i], y1VertiCoord[i], xVertiCoord[i], yVertiCoord[i], x2VertiCoord[i], y2VertiCoord[i]);
            basePath.lineTo(x1VertiCoord[(i + 1) % sideCount], y1VertiCoord[(i + 1) % sideCount]);
        }
        basePath.close();
        sideLength = pm.getLength() / sideCount;
        pm.setPath(basePath, false);

        totalDisStartPoint = pm.getLength() + minDistance + velocity * fps + 250;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // determinate(canvas);
       /* canvas.drawPath(basePath, primaryPaint);

        canvas.drawPath(secPath, secondaryPaint);*/
        //genCount+=1;
        // canvas.rotate(genCount, canvas.getWidth()/2, getHeight()/2);


        //canvas.drawPath(basePath, primaryPaint);
        withoutAcceleration = velocity;
        if (akinTime >= 0) {
            withAcceleration = velocity + akinTime;
        } else {
            withAcceleration = velocity + 0.5F;
        }
        if (whereToGo == 0) {
            firstPath(canvas);
        } /*else if (whereToGo == 1) {
            thirdPath(canvas);
        }*/ else if (whereToGo == 2) {
            secondPath(canvas);
        } /*else if (whereToGo == 3) {
            forthPath(canvas);
        }*/
    }

    private void setCoordinates() {
        float diffAngle = (float) (2 * Math.PI / sideCount);
        for (int i = 0; i < sideCount; i++) {
            xVertiCoord[i] = xCenter - (float) ((radius) * Math.sin((double) (i + 1) * diffAngle));
            yVertiCoord[i] = yCenter - (float) ((radius) * Math.cos((double) (i + 1) * diffAngle));
        }

        for (int i = 0; i < sideCount; i++) {
            x1VertiCoord[i] = (float) (1 * xVertiCoord[(sideCount + i - 1) % sideCount] + 9 * xVertiCoord[i]) / 10;
            y1VertiCoord[i] = (float) (1 * yVertiCoord[(sideCount + i - 1) % sideCount] + 9 * yVertiCoord[i]) / 10;

            x2VertiCoord[i] = (float) (1 * xVertiCoord[(i + 1) % sideCount] + 9 * xVertiCoord[i]) / 10;
            y2VertiCoord[i] = (float) (1 * yVertiCoord[(i + 1) % sideCount] + 9 * yVertiCoord[i]) / 10;

        }
        for (int i = 0; i < sideCount; i++) {
            xMidPoints[i] = (xVertiCoord[i] + xVertiCoord[(i + 1) % sideCount]) / 2;
            yMidPoints[i] = (yVertiCoord[i] + yVertiCoord[(i + 1) % sideCount]) / 2;
        }
    }


    private void firstPath(Canvas canvas) {
        if (wildCard2) {
            wildCard2 = false;
            if (velocity * fps - minDistance >= startPoint) {
                totalDisStartPoint = pm.getLength() + velocity * fps - minDistance - minDistanceSec;
            } else {
                totalDisStartPoint = pm.getLength() - (minDistanceSec - (velocity * fps - minDistance));
            }

            times = (8 * ((totalDisStartPoint / 2) - (velocity * fps / 2))) / (fps * fps);

        }
        if (tempStartPoint <= totalDisStartPoint / 2) {
            akinTime += times;

        } else {
            akinTime -= times;
            if (akinTime <= 0) {
                timetimes = 0;
                whereToGo = 2;
                tempStartPoint = 0;
                akinTime = 0;
            }

        }
        startPoint += withAcceleration;
        endPoint += withoutAcceleration;
        secPath.reset();
        tempStartPoint += withAcceleration;


        float ga = endPoint - startPoint;

        if (ga >= 0) {
            if (ga <= minDistance) {
                akinTime = 0;
            }
        } else {
            if (pm.getLength() - startPoint + endPoint <= minDistance) {
                akinTime = 0;
            }
        }
        if (endPoint >= pm.getLength()) {
            wildCard1 = false;
            endPoint = 0;
        }
        if (startPoint >= pm.getLength()) {
            startPoint = 0;
            wildCard1 = true;
        }
        if (wildCard1) {
            pm.getSegment(0, startPoint % pm.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
            pm.getSegment(endPoint, pm.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
            return;
        }


        pm.getSegment(endPoint, startPoint, secPath, true);
        canvas.drawPath(secPath, secondaryPaint);

    }

    private void secondPath(Canvas canvas) {

        if (wildCard2) {
            wildCard2 = false;
            tempStartPoint = 0;
            timetimes = 0;
            if (velocity * fps - minDistanceSec >= endPoint) {
                totalDisStartPoint = pm.getLength() + velocity * fps - minDistance - minDistanceSec;
            } else {
                totalDisStartPoint = pm.getLength() - (minDistanceSec - (velocity * fps - minDistance));
            }

            times = (8 * ((totalDisStartPoint / 2) - (velocity * fps / 2))) / (fps * fps);

        }

        secPath.reset();
        if (tempStartPoint <= totalDisStartPoint / 2) {
            akinTime += times;

        } else {
            if (akinTime <= 0) {
                timetimes = 0;
                whereToGo = 0;
                akinTime = 0;
            }
            akinTime -= times;
        }

        startPoint += withoutAcceleration;
        endPoint += withAcceleration;
        tempStartPoint += withAcceleration;

        if (endPoint >= pm.getLength()) {
            endPoint = 0;
            wildCard1 = false;
        }
        if (startPoint >= pm.getLength()) {
            startPoint = 0;
            wildCard1 = true;
        }
        if (wildCard1) {
            pm.getSegment(0, startPoint, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
            pm.getSegment(endPoint, pm.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        } else {
            pm.getSegment(endPoint, startPoint, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        }
        float ga = startPoint - endPoint;

        if (ga >= 0) {
            if (ga <= minDistanceSec) {
                akinTime = 0;
                whereToGo = 0;
            }
        } else {
            if (pm.getLength() + startPoint - endPoint <= minDistanceSec) {
                akinTime = 0;
                whereToGo = 0;
            }
        }

    }

    private void thirdPath(Canvas canvas) {

        timetimes += 1;
        secPath.reset();

        startPoint += withoutAcceleration;
        endPoint += withoutAcceleration;
/*

        if (preWhereToGo != whereToGo) {
            initTag = endPoint;
        }
*/

        if (endPoint >= pm.getLength()) {
            wildCard1 = false;
        }

        if (startPoint >= pm.getLength()) {
            startPoint = 0;
            wildCard1 = true;
        }
        if (wildCard1) {
            pm.getSegment(0, startPoint % pm.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
            pm.getSegment(endPoint, pm.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        } else {
            pm.getSegment(endPoint, startPoint, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        }

        if (/*endPoint - initTag >= sideLength / 4*/ timetimes >= 0) {
            timetimes = 0;
            whereToGo = 2;
        } else {
            whereToGo = 1;
        }


  //      preWhereToGo = 1;
    }

    private void forthPath(Canvas canvas) {
        whereToGo = 0;
        startPoint += withoutAcceleration;
        endPoint += withoutAcceleration;
        secPath.reset();
        timetimes++;

       /* if (preWhereToGo != whereToGo) {
            initTag = startPoint;
        }*/

        if (endPoint >= pm.getLength()) {
            endPoint = 0;
            wildCard1 = false;
        }

        if (startPoint >= pm.getLength()) {
            startPoint = 0;
            wildCard1 = true;
        }


        if (wildCard1) {
            pm.getSegment(0, startPoint % pm.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
            pm.getSegment(endPoint, pm.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        } else {
            pm.getSegment(endPoint % pm.getLength(), startPoint % pm.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        }

        /*if (Math.abs(startPoint - initTag) >= sideLength / 3) {
            whereToGo = 0;
        } else {
            whereToGo = 3;
        }*/
        //if (/*withAcceleration - velocity >= 5 || withAcceleration - velocity <= 5*/timetimes >= 0) {
        whereToGo = 0;
        //} else {
        //   whereToGo = 3;
        // }


        //preWhereToGo = 3;
    }


    private void determinate(Canvas canvas) {
        secPath.reset();

        initialPosition = (float) Math.hypot(x1VertiCoord[0] - xMidPoints[0], y1VertiCoord[0] - yMidPoints[0]);
        sideProgress = pm.getLength() * progress / 100;
        if (sideProgress + initialPosition > pm.getLength()) {
            pm.getSegment(initialPosition, pm.getLength(), secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
            pm.getSegment(0, sideProgress - pm.getLength() + initialPosition, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        } else {
            pm.getSegment(initialPosition, sideProgress + initialPosition, secPath, true);
            canvas.drawPath(secPath, secondaryPaint);
        }
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }


    public Paint getPrimaryPaint() {
        return primaryPaint;
    }

    public void setPrimaryPaint(Paint primaryPaint) {
        this.primaryPaint = primaryPaint;
    }

    public Paint getSecondaryPaint() {
        return secondaryPaint;
    }

    public void setSecondaryPaint(Paint secondaryPaint) {
        this.secondaryPaint = secondaryPaint;
    }

    public int getSideCount() {
        return sideCount;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }
}
