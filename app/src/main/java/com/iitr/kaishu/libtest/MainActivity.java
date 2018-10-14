package com.iitr.kaishu.libtest;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


        Handler a = new Handler();
        a.postDelayed(new Runnable() {
            @Override
            public void run() {
                Paint aa = new Paint();
                aa.setColor(Color.RED);
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
