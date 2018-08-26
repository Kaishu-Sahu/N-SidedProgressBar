package com.iitr.kaishu.nsidedprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class NSidedProgressBar extends View {

    private Paint paint;
    private int sideCount;
    private float[] xCoordinates;
    private float[] yCoordinates;
    private float xCenter;
    private float yCenter;
    private float width;
    private float progress;
    private float sideLength;
    private Paint temp;
    private float degree;
    private Path path;

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
        CornerPathEffect corEffect = new CornerPathEffect(50F);
        paint.setPathEffect(corEffect);
        path = new Path();
        sideCount = 4;
        xCoordinates = new float[sideCount];
        yCoordinates = new float[sideCount];
        progress = 20;
        temp = new Paint();
        temp.setStrokeWidth(10);
        temp.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.STROKE);
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
        xCenter = canvas.getWidth() / 2;
        yCenter = canvas.getHeight() / 2;
        setCoordinates();
        sideLength = (float) Math.hypot(xCoordinates[0] - xCoordinates[1], yCoordinates[0] - yCoordinates[1]);

        path.moveTo(xCoordinates[0], yCoordinates[0]);
        for (int i = 0; i < sideCount; i++) {
            if ((i + 1) < sideCount) {

                path.lineTo(xCoordinates[i], yCoordinates[i]);
                canvas.drawLine(, xCoordinates[i + 1], yCoordinates[i + 1], paint);
            } else {
                canvas.drawLine(xCoordinates[i], yCoordinates[i], xCoordinates[0], yCoordinates[0], paint);
            }
        }
        drawLine(canvas);
        canvas.rotate(degree, canvas.getWidth()/2 , canvas.getHeight()/2);
        degree += 5;
        invalidate();
        //   canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 50, paint);
    }

    private void setCoordinates() {
        float diffAngle = (float) (2 * Math.PI / sideCount);
        for (int i = 1; i <= sideCount; i++) {
            xCoordinates[i - 1] = xCenter - (float) ((xCenter) * Math.sin((double) i * diffAngle));
            yCoordinates[i - 1] = yCenter - (float) ((xCenter) * Math.cos((double) i * diffAngle));

        }
    }


    private void drawLine(Canvas canvas) {
        int currentSide = (int) (sideCount * progress / 100);
        float partOnSide = sideLength * (progress / (100 / sideCount));
        float slope = (yCoordinates[currentSide + 1] - yCoordinates[currentSide]) / (xCoordinates[currentSide + 1] - xCoordinates[currentSide]);
        float xFinalPoint = xCoordinates[currentSide] + partOnSide * (float) Math.cos(Math.atan(slope));
        float yFinalPoint = yCoordinates[currentSide] + partOnSide * (float) Math.sin(Math.atan(slope));
        canvas.drawLine(xCoordinates[currentSide], yCoordinates[currentSide], xFinalPoint, yFinalPoint, temp);
    }


}
