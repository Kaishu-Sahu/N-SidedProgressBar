package com.iitr.kaishu.nsidedprogressbar;

import android.app.Activity;
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
import android.widget.Toast;

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
    float withoutAcceleration = 0;
    float withAcceleration = 0;
    private float startPoint = 200;
    private float endPoint = 0;
    private Context context;
    private float velocity = 5;
    private float akinTime = 1;
    private float minDistance = 100;
    boolean first = true;
    boolean wildCard1 = false;
    boolean wildCard2 = false;
    boolean goInside = false;
    boolean isAllowed = true;
    int whereToGo = 0;
    int preWhereToGo = 0;
    float initTag = 0;

    public NSidedProgressBar(Context context) {
        super(context);
        initProgressBar();
        this.context = context;

    }

    public NSidedProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initProgressBar();
        this.context = context;


    }

    public NSidedProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initProgressBar();
        this.context = context;

    }

    private void initProgressBar() {
        paint = new Paint();
        paint.setColor(android.graphics.Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        // CornerPathEffect corEffect = new CornerPathEffect(20);
        //paint.setPathEffect(corEffect);
        path = new Path();
        sideCount = 8;
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

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // canvas.rotate(tempee/20, canvas.getWidth()/2 , canvas.getHeight()/2);
        xCenter = canvas.getWidth() / 2;
        yCenter = canvas.getHeight() / 2;
        setCoordinates();
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
            path.cubicTo(x1VertiCoord[i], y1VertiCoord[i], xVertiCoord[i], yVertiCoord[i], x2VertiCoord[i], y2VertiCoord[i]);
            path.lineTo(x1VertiCoord[(i + 1) % sideCount], y1VertiCoord[(i + 1) % sideCount]);
        }
        //path.lineTo(xVertiCoord[0], yVertiCoord[0]);
        //path.cubicTo(xVertiCoord[0], yVertiCoord[0], xVertiCoord[1], yVertiCoord[1], xVertiCoord[2], yVertiCoord[2]);
        path.close();
        canvas.drawPath(path, paint);

        pm = new PathMeasure(path, false);
        sideLength = pm.getLength() / sideCount;
        Path a = new Path();

        /*if (first) {
            endPoint = (sideCount-1)*sideLength;
        }
        first = false;*/

        withoutAcceleration = velocity;
        withAcceleration = velocity * akinTime;

        /*if (endPoint - startPoint > minDistance) {
            startPoint += withAcceleration;
            endPoint += withoutAcceleration;
            pm.getSegment(startPoint, endPoint, a , true);

        } else if (endPoint >= pm.getLength()) {
            akinTime = 0;
            float[] temp = new float[2];
            pm.getPosTan(startPoint, temp , null);
            path.moveTo(temp[0],temp[1]);
            endPoint = 0;
            startPoint = minDistance + 1;
            pm.getSegment(endPoint, startPoint, a , true);

        } else if (endPoint - startPoint <= minDistance) {
            akinTime = 1;
            startPoint += withoutAcceleration;
            endPoint  += withoutAcceleration;
        } else {

        }*/
/*
        if ((endPoint % pm.getLength()) - (startPoint % pm.getLength()) > minDistance && isAllowed) {
            Log.d("TEST","0");
            if (startPoint >= pm.getLength() || goInside) {
                goInside = true;
                pm.getSegment(0, startPoint % pm.getLength(), a, true);
                canvas.drawPath(a, temp);
                pm.getSegment(endPoint, pm.getLength(), a, true);
                canvas.drawPath(a, temp);
            } else {
                pm.getSegment(endPoint, startPoint, a, true);
            }
        } else if ((startPoint % pm.getLength()) - (endPoint % pm.getLength()) > minDistance && isAllowed) {
            Log.d("TEST","1");


            if (startPoint <= pm.getLength() / 2) {
                akinTime += 0.2;
            } else if (startPoint > pm.getLength() / 2) {
                akinTime -= 0.01;
            }
            endPoint += withoutAcceleration;
            startPoint += withAcceleration;
            if (startPoint >= pm.getLength() || goInside) {
                goInside = true;
                pm.getSegment(0, startPoint % pm.getLength(), a, true);
                canvas.drawPath(a, temp);
                pm.getSegment(endPoint, pm.getLength(), a, true);
                canvas.drawPath(a, temp);
            } else {
                pm.getSegment(endPoint, startPoint, a, true);
            }
        } else {
            Log.d("TEST","2");

            startPoint += withoutAcceleration;
            endPoint += withAcceleration;
            if (startPoint >= pm.getLength() || goInside) {
                goInside = true;
                pm.getSegment(0, startPoint % pm.getLength(), a, true);
                canvas.drawPath(a, temp);
                pm.getSegment(endPoint, pm.getLength(), a, true);
                canvas.drawPath(a, temp);
            } else {
                pm.getSegment(endPoint, startPoint, a, true);
            }
        }*/

        if (whereToGo == 0) {
            firstPath(canvas);
        } else if (whereToGo == 1) {
            thirdPath(canvas);
        } else if (whereToGo == 2) {
            secondPath(canvas);
        } else if (whereToGo == 3) {
            forthPath(canvas);
        }


//        endPoint = endPoint % pm.getLength();
//        startPoint = startPoint % pm.getLength();
      /*  if (startPoint / pm.getLength() > 0) {
            startPoint = startPoint % pm.getLength();
            pm.getSegment(0, startPoint, a , true);
            canvas.drawPath(a, temp);
        } */

        // pm.getPosTan(pm.getLength()/2, )
        //drawLine(canvas);
        //degree += 5;
        invalidate();
        //   canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 50, paint);
    }

    private void setCoordinates() {
        float diffAngle = (float) (2 * Math.PI / sideCount);
        for (int i = 0; i < sideCount; i++) {
            xVertiCoord[i] = xCenter - (float) ((xCenter) * Math.sin((double) (i + 1) * diffAngle));
            yVertiCoord[i] = yCenter - (float) ((xCenter) * Math.cos((double) (i + 1) * diffAngle));

            // Log.d("TEST", "("+xVertiCoord[i]+","+yVertiCoord[i]+")");
        }

        for (int i = 0; i < sideCount; i++) {
            x1VertiCoord[i] = (float) (1 * xVertiCoord[(sideCount + i - 1) % sideCount] + 9 * xVertiCoord[i]) / 10;
            y1VertiCoord[i] = (float) (1 * yVertiCoord[(sideCount + i - 1) % sideCount] + 9 * yVertiCoord[i]) / 10;

            //  Log.d("TEST", "("+x1VertiCoord[i]+","+y1VertiCoord[i]+")");


            x2VertiCoord[i] = (float) (1 * xVertiCoord[(i + 1) % sideCount] + 9 * xVertiCoord[i]) / 10;
            y2VertiCoord[i] = (float) (1 * yVertiCoord[(i + 1) % sideCount] + 9 * yVertiCoord[i]) / 10;


            //  Log.d("TEST", "("+x2VertiCoord[i]+","+y2VertiCoord[i]+")");

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

    private void firstPath(Canvas canvas) {
        Path a = new Path();
        startPoint += withAcceleration;
        endPoint += withoutAcceleration;
        akinTime += 0.1;
        float ga = Math.abs((startPoint % pm.getLength()) - (endPoint % pm.getLength()));
        if (ga <= minDistance) {
            whereToGo = 1;
        }
        if (endPoint >= pm.getLength()) {
            wildCard1 = false;
        }
        if (startPoint >= pm.getLength()) {
            startPoint = 0;
            wildCard1 = true;
        }
        if (wildCard1) {
            pm.getSegment(0, startPoint % pm.getLength(), a, true);
            canvas.drawPath(a, temp);
            pm.getSegment(endPoint, pm.getLength(), a, true);
            canvas.drawPath(a, temp);
            return;
        }


        pm.getSegment(endPoint, startPoint, a, true);

        canvas.drawPath(a, temp);
        preWhereToGo = 0;

    }

    private void secondPath(Canvas canvas) {
        Path a = new Path();
        startPoint += withoutAcceleration;
        endPoint += withAcceleration;
        if (wildCard1) {
            pm.getSegment(0, startPoint % pm.getLength(), a, true);
            canvas.drawPath(a, temp);
            pm.getSegment(endPoint, pm.getLength(), a, true);
            canvas.drawPath(a, temp);
        }
        if (endPoint >= pm.getLength()) {
            wildCard1 = false;
            endPoint = 0;
        }
        pm.getSegment(endPoint, startPoint % pm.getLength(), a, true);
        canvas.drawPath(a, temp);
        float ga = Math.abs((startPoint % pm.getLength()) - (endPoint % pm.getLength()));
        if (ga <= minDistance) {
            whereToGo = 3;
        }
        preWhereToGo = 2;

    }

    private void thirdPath(Canvas canvas) {
        Path a = new Path();

        startPoint += withoutAcceleration;
        endPoint += withoutAcceleration;

        if (preWhereToGo != whereToGo) {
            initTag = endPoint;
        }

        if (startPoint >= pm.getLength() || wildCard1) {
            wildCard1 = true;
            pm.getSegment(0, startPoint % pm.getLength(), a, true);
            canvas.drawPath(a, temp);
            pm.getSegment(endPoint, pm.getLength(), a, true);
            canvas.drawPath(a, temp);
        } else {
            pm.getSegment(endPoint, startPoint, a, true);
            canvas.drawPath(a, temp);
        }

            if (endPoint - initTag >= sideLength / 2) {
                whereToGo = 2;
            } else {
                whereToGo = 1;
            }


        preWhereToGo = 1;
    }

    private void forthPath(Canvas canvas) {
        Path a = new Path();
        startPoint += withoutAcceleration;
        endPoint += withoutAcceleration;

        if (preWhereToGo != whereToGo) {
            initTag = startPoint%pm.getLength();
        }

        if (startPoint >= pm.getLength() || wildCard1) {
            wildCard1 = true;
            pm.getSegment(0, startPoint % pm.getLength(), a, true);
            canvas.drawPath(a, temp);
            pm.getSegment(endPoint, pm.getLength(), a, true);
            canvas.drawPath(a, temp);
        } else {
            pm.getSegment(endPoint%pm.getLength(), startPoint%pm.getLength(), a, true);
            canvas.drawPath(a, temp);
        }

            if (startPoint%pm.getLength() - initTag >= sideLength / 2) {
                whereToGo = 0;
            } else {
                whereToGo = 3;
            }


        preWhereToGo = 3;
    }


}
