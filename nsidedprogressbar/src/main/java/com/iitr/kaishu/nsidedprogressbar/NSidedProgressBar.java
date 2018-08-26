package com.iitr.kaishu.nsidedprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class NSidedProgressBar extends View {

    private Paint paint;
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
    private Paint temp;
    private float degree;
    private Path path;
    private PathMeasure pm;
    float tempee = 0;
    public NSidedProgressBar(Context context) {
        super(context);
        initProgressBar();

    }

    public NSidedProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initProgressBar();

    }

    public NSidedProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initProgressBar();
    }

    private void initProgressBar() {
        paint = new Paint();
        paint.setColor(android.graphics.Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
       // CornerPathEffect corEffect = new CornerPathEffect(20);
        //paint.setPathEffect(corEffect);
        path = new Path();
        sideCount = 3;
        xVertiCoord = new float[sideCount];
        yVertiCoord = new float[sideCount];
        x1VertiCoord = new float[sideCount];
        y1VertiCoord = new float[sideCount];
        x2VertiCoord = new float[sideCount];
        y2VertiCoord = new float[sideCount];
        progress = 20;
        temp = new Paint();
       // temp.setPathEffect(corEffect);
        temp.setStrokeWidth(10);
        temp.setColor(android.graphics.Color.RED);
        temp.setStyle(Paint.Style.STROKE);
        degree = 5;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                NSidedProgressBar.this.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }, 0, 500);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       // canvas.rotate(tempee/20, canvas.getWidth()/2 , canvas.getHeight()/2);

        xCenter = canvas.getWidth() / 2;
        yCenter = canvas.getHeight() / 2;
        setCoordinates();
        sideLength = (float) Math.hypot(xVertiCoord[0] - xVertiCoord[1], yVertiCoord[0] - yVertiCoord[1]);
         path.moveTo(x1VertiCoord[0], y1VertiCoord[0]);
        //  path.moveTo(xVertiCoord[0], yVertiCoord[0]);

        for (int i = 0; i < sideCount; i++) {
           // if ((i + 1) < sideCount) {
                //path.lineTo(xVertiCoord[i+1], yVertiCoord[i+1]);
                //canvas.drawLine(, xVertiCoord[i + 1], yVertiCoord[i + 1], paint);
           // } else {
              //  path.lineTo(xVertiCoord[0], yVertiCoord[0]);

                //                canvas.drawLine(xVertiCoord[i], yVertiCoord[i], xVertiCoord[0], yVertiCoord[0], paint);
            //}
         path.cubicTo(x1VertiCoord[i], y1VertiCoord[i], xVertiCoord[i], yVertiCoord[i], x1VertiCoord[i], y2VertiCoord[i]);
            path.lineTo(x1VertiCoord[(i+1)%sideCount], y1VertiCoord[(i+1)%sideCount]);
        }
        //path.lineTo(xVertiCoord[0], yVertiCoord[0]);
        //path.cubicTo(xVertiCoord[0], yVertiCoord[0], xVertiCoord[1], yVertiCoord[1], xVertiCoord[2], yVertiCoord[2]);
        path.close();
        canvas.drawPath(path, paint);

        pm = new PathMeasure(path, false);
        Path a = new Path();
       // pm.getSegment(tempee, tempee+tempee/2, a , true);
        //canvas.drawPath(a , temp);
        //invalidate();
        if (tempee >= pm.getLength()) {
            tempee = 0;
        }
        tempee+=5;

        if (tempee >= pm.getLength()) {
            tempee = 0;
        }
       // pm.getPosTan(pm.getLength()/2, )
        //drawLine(canvas);
        //degree += 5;
        //invalidate();
        //   canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 50, paint);
    }

    private void setCoordinates() {
        float diffAngle = (float) (2 * Math.PI / sideCount);
        for (int i = 0; i < sideCount; i++) {
            xVertiCoord[i] = xCenter - (float) ((xCenter) * Math.sin((double) (i+1) * diffAngle));
            yVertiCoord[i] = yCenter - (float) ((xCenter) * Math.cos((double) (i+1) * diffAngle));

            Log.d("TEST", "("+xVertiCoord[i]+","+yVertiCoord[i]+")");
        }

        for (int i = 0; i < sideCount; i++) {
            x1VertiCoord[i] = (1 * xVertiCoord[(i+1)%sideCount] + 9 * xVertiCoord[i])/10;
            y1VertiCoord[i] = (1 * yVertiCoord[(i+1)%sideCount] + 9 * yVertiCoord[i])/10;

            Log.d("TEST", "("+x1VertiCoord[i]+","+y1VertiCoord[i]+")");


            x2VertiCoord[i] = (1 * xVertiCoord[(sideCount+i -1)%sideCount] + 9 * xVertiCoord[i])/10;
            y2VertiCoord[i] = (1 * yVertiCoord[(sideCount + i -1)%sideCount] + 9 * yVertiCoord[i])/10;

            Log.d("TEST", "("+x2VertiCoord[i]+","+y2VertiCoord[i]+")");

        }
    }


    private void drawLine(Canvas canvas) {
        int currentSide = (int) (sideCount * progress / 100);
        float partOnSide = sideLength * (progress / (100 / sideCount));
        float slope = (yVertiCoord[currentSide + 1] - yVertiCoord[currentSide]) / (xVertiCoord[currentSide + 1] - xVertiCoord[currentSide]);
        float xFinalPoint = xVertiCoord[currentSide] + partOnSide * (float) Math.cos(Math.atan(slope));
        float yFinalPoint = yVertiCoord[currentSide] + partOnSide * (float) Math.sin(Math.atan(slope));
        canvas.drawLine(xVertiCoord[currentSide], yVertiCoord[currentSide], xFinalPoint, yFinalPoint, temp);
    }


}
