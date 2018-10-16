package com.iitr.kaishu.libtest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.iitr.kaishu.nsidedprogressbar.NSidedProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final NSidedProgressBar nSidedProgressBar = findViewById(R.id.NSidedProgressBar);
        int[] priCol = {Color.RED, Color.GREEN, Color.BLUE};
       // nSidedProgressBar.setSecondaryPaintColors(priCol);
        Paint aa = new Paint();

        /*aa.setColor(Color.RED);
        aa.setStrokeWidth(12);
        aa.setStyle(Paint.Style.STROKE);
        nSidedProgressBar.setPrimaryPaint(aa);*/

        Handler a = new Handler();
        a.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                if (launchIntent != null) {
                  //  startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        },5000);













        //   SeekBar seekBar = findViewById(R.id.seekBar);
       /* seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                nSidedProgressBar.setProgress(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
*/

    }
}
