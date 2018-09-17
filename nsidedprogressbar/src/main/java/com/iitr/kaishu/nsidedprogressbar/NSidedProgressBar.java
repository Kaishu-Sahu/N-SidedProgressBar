package com.iitr.kaishu.nsidedprogressbar;

import android.app.Activity;
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
    private int sideCount;
    private float[] xVertiCoord;
    private float[] yVertiCoord;
    private float[] x1VertiCoord;
    private float[] x2VertiCoord;
    private float[] y1VertiCoord;
    private float[] y2VertiCoord;
    private float xCenter;
    private float yCenter;
    private float width;
    private float progress;
    private float sideLength;

    private Path path;
    private PathMeasure pm;
    float withoutAcceleration = 0;
    float withAcceleration = 0;
    private float endPoint = 0;
    private Context context;
    private float velocity = 5;
    private float akinTime = 0;
    private float minDistance = 70;
    private float minDistanceSec = 40;
    private float startPoint = minDistanceSec;
    boolean first = true;
    boolean wildCard1 = false;
    boolean wildCard2 = true;
    boolean goInside = false;
    boolean isAllowed = true;
    int whereToGo = 0;
    int preWhereToGo = -1;
    float initTag = 0;
    Path secPath;
    float times = 0;
    int genCount = 0;
    int fps = 60;
    float totalDisStartPoint;
    float tempStartPoint = 0;
    int timetimes = 0;
    long time = System.currentTimeMillis();
    float sideProgress = 0;
    float initialPosition = 0;

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
        primaryPaint.setColor(android.graphics.Color.BLACK);
        primaryPaint.setStrokeWidth(5);
        primaryPaint.setStyle(Paint.Style.STROKE);
        path = new Path();
        sideCount = 3;
        xVertiCoord = new float[sideCount];
        yVertiCoord = new float[sideCount];
        x1VertiCoord = new float[sideCount];
        y1VertiCoord = new float[sideCount];
        x2VertiCoord = new float[sideCount];
        y2VertiCoord = new float[sideCount];
        progress = 20;
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

        int viewWidth = 50 + this.getPaddingLeft() + this.getPaddingRight();
        int viewHeight = 50 + this.getPaddingTop() + this.getPaddingBottom();

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
        setCoordinates();
        path.moveTo(x1VertiCoord[0], y1VertiCoord[0]);
        for (int i = 0; i < sideCount; i++) {
            path.cubicTo(x1VertiCoord[i], y1VertiCoord[i], xVertiCoord[i], yVertiCoord[i], x2VertiCoord[i], y2VertiCoord[i]);
            path.lineTo(x1VertiCoord[(i + 1) % sideCount], y1VertiCoord[(i + 1) % sideCount]);
        }
        path.close();
        sideLength = pm.getLength() / sideCount;
        pm.setPath(path, false);

        totalDisStartPoint = pm.getLength() + minDistance + velocity * fps + 250;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        determinate();
        canvas.drawPath(secPath, secondaryPaint);


        //canvas.drawPath(path, primaryPaint);
      /*  withoutAcceleration = velocity;
        if (akinTime >= 0) {
            withAcceleration = velocity + akinTime;
        } else {
            withAcceleration = velocity + 0.5F;

        }
        if (whereToGo == 0) {
            firstPath(canvas);
        } else if (whereToGo == 1) {
             thirdPath(canvas);
        } else if (whereToGo == 2) {
              secondPath(canvas);
        } else if (whereToGo == 3) {
            forthPath(canvas);
        }*/
    }

    private void setCoordinates() {
        float diffAngle = (float) (2 * Math.PI / sideCount);
        for (int i = 0; i < sideCount; i++) {
            xVertiCoord[i] = xCenter - (float) ((xCenter) * Math.sin((double) (i + 1) * diffAngle));
            yVertiCoord[i] = yCenter - (float) ((xCenter) * Math.cos((double) (i + 1) * diffAngle));
        }

        for (int i = 0; i < sideCount; i++) {
            x1VertiCoord[i] = (float) (1 * xVertiCoord[(sideCount + i - 1) % sideCount] + 9 * xVertiCoord[i]) / 10;
            y1VertiCoord[i] = (float) (1 * yVertiCoord[(sideCount + i - 1) % sideCount] + 9 * yVertiCoord[i]) / 10;

            x2VertiCoord[i] = (float) (1 * xVertiCoord[(i + 1) % sideCount] + 9 * xVertiCoord[i]) / 10;
            y2VertiCoord[i] = (float) (1 * yVertiCoord[(i + 1) % sideCount] + 9 * yVertiCoord[i]) / 10;

        }
    }


    private void drawLine(Canvas canvas) {
        int currentSide = (int) (sideCount * progress / 100);
        float partOnSide = sideLength * (progress / (100 / sideCount));
        float slope = (yVertiCoord[currentSide + 1] - yVertiCoord[currentSide]) / (xVertiCoord[currentSide + 1] - xVertiCoord[currentSide]);
        float xFinalPoint = xVertiCoord[currentSide] + partOnSide * (float) Math.cos(Math.atan(slope));
        float yFinalPoint = yVertiCoord[currentSide] + partOnSide * (float) Math.sin(Math.atan(slope));
        canvas.drawLine(xVertiCoord[currentSide], yVertiCoord[currentSide], xFinalPoint, yFinalPoint, secondaryPaint);
    }

    private void firstPath(Canvas canvas) {




        /*if (preWhereToGo != whereToGo) {
            if ((endPoint - minDistance - sideLength) >= 0) {
                initTag = endPoint - minDistance - sideLength;
            } else {
                initTag = pm.getLength() - minDistance - sideLength - endPoint;
            }
            Toast.makeText(context, initTag+"", Toast.LENGTH_SHORT).show();
        }
        if (startPoint >= initTag || wildCard1) {
            if (wildCard2) {
                wildCard2 = false;
                times =2 * (withAcceleration - velocity) / fps;
            }
            akinTime -= 0.5;
        } else {
            akinTime += 0.5;

        }
        Log.d("TEST", akinTime+""
      );*/


        if (wildCard2) {
            wildCard2 = false;
            if (velocity * fps - minDistanceSec >= startPoint) {
                totalDisStartPoint = pm.getLength() + velocity * fps - minDistance - minDistanceSec;
            } else {
                totalDisStartPoint = pm.getLength() - (minDistanceSec - (velocity * fps - minDistance));
            }

            times = (8 * ((totalDisStartPoint / 2) - (velocity * fps / 2))) / (fps * fps);
            //  Log.d("TEST", totalDisStartPoint+" "+pm.getLength()+ " "+ startPoint + " " + times);
            // Log.d("TEST", totalDisStartPoint+" "+pm.getLength());

        }


        if (tempStartPoint <= totalDisStartPoint / 2) {
            akinTime += times;

        } else {
            akinTime -= times;

            try {
                if (akinTime <= 0) {
                    wildCard2 = true;
                    timetimes = 0;
                    whereToGo = 1;
                    tempStartPoint = 0;
                    akinTime = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        startPoint += withAcceleration;
        endPoint += withoutAcceleration;
        secPath.reset();
        tempStartPoint += withAcceleration;
        float ga = endPoint - startPoint;

        if (ga <= minDistance && ga >= 0) {
            // whereToGo = 1;

            //tempStartPoint = 0;
            akinTime = 0;
        }
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
            return;
        }


        pm.getSegment(endPoint, startPoint, secPath, true);

        canvas.drawPath(secPath, secondaryPaint);
        preWhereToGo = 0;

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

            try {
                if (akinTime <= 0) {
                    wildCard2 = true;
                    timetimes = 0;
                    whereToGo = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            akinTime -= times;
        }

        startPoint += withoutAcceleration;
        endPoint += withAcceleration;
        tempStartPoint += withAcceleration;
   /*     if (endPoint >= pm.getLength() / 2) {
            akinTime -= 0.4;
        } else {
            akinTime += 0.5;
        }
*/
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
        float ga = Math.abs((startPoint % pm.getLength()) - (endPoint % pm.getLength()));

        if (ga <= minDistanceSec) {
            akinTime = 0;
            try {
            } catch (Exception e) {
            }
            whereToGo = 3;
        }
        preWhereToGo = 2;

    }

    private void thirdPath(Canvas canvas) {

        timetimes += 1;
        secPath.reset();

        startPoint += withoutAcceleration;
        endPoint += withoutAcceleration;

        if (preWhereToGo != whereToGo) {
            initTag = endPoint;
        }

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


        preWhereToGo = 1;
    }

    private void forthPath(Canvas canvas) {
        whereToGo = 0;
        startPoint += withoutAcceleration;
        endPoint += withoutAcceleration;
        secPath.reset();
        timetimes++;

        if (preWhereToGo != whereToGo) {
            initTag = startPoint;
        }

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


        preWhereToGo = 3;
    }


    private void determinate() {
        initialPosition = sideLength / 2;
        sideProgress = pm.getLength() / 20;
        if (sideProgress + initialPosition > pm.getLength()) {

        } else {
            pm.getSegment(initialPosition, sideProgress, secPath, true);
        }
    }

}
