package com.guyazran.spinthebottle;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.util.ArrayList;

public class BottleActivity extends AppCompatActivity implements View.OnTouchListener, Animation.AnimationListener {

    private ImageView imgBottle;
    private float currentAngle = 0;
    private float previousAngle = 0;
    private float startAngle = 0;
    private float centerYOnStart;

    private Animation spin;
    private GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottle);

        imgBottle = (ImageView) findViewById(R.id.bottle);
        imgBottle.setOnTouchListener(this);

        gestureDetector = new GestureDetector(this, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float centerX = imgBottle.getWidth() / 2;
        float centerY = imgBottle.getHeight() / 2;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                imgBottle.clearAnimation();
                currentAngle = (float) Math.toDegrees(Math.atan2(x - centerX, centerY - y));
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                previousAngle = currentAngle;
                currentAngle = (float) Math.toDegrees(Math.atan2(x - centerX, centerY - y));
                rotate(previousAngle, currentAngle, 0);
                break;
            }

            case MotionEvent.ACTION_UP: {
                currentAngle += startAngle;
                imgBottle.clearAnimation();
                saveBottleRotation();
                break;
            }
        }

        gestureDetector.onTouchEvent(event);

        return true;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            centerYOnStart = imgBottle.getHeight() / 2;

            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float y = e2.getY();

            if (y > centerYOnStart){
                velocityX *= -1;
            }

            spin(velocityX);
            return true;
        }
    }

    private void rotate(double fromDegrees, double toDegrees, long durationMillis) {
        RotateAnimation rotate = new RotateAnimation((float) fromDegrees, (float) toDegrees, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillEnabled(true);
        rotate.setFillAfter(true);
        imgBottle.startAnimation(rotate);
    }

    private void spin(float velocity){

        if (velocity < 100 && velocity > -100){
            return;
        }

        if (velocity > 10000){
            velocity = 10000;
        }
        if (velocity < -10000){
            velocity = -10000;
        }

        long spinDuration = (long) Math.abs(velocity);
        float spinAmount = (velocity / (16000/spinDuration));

        spin = new RotateAnimation(0, spinAmount, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        spin.setInterpolator(new DecelerateInterpolator(1.0f + (spinDuration / 10000) * 2));
        spin.setDuration(spinDuration);
        spin.setFillEnabled(true);
        spin.setAnimationListener(this);

        imgBottle.startAnimation(spin);
        currentAngle += spinAmount;
    }

    private void saveBottleRotation(){
        float rounds = currentAngle / 360;
        int ones = (int) (currentAngle / 360);
        float difference = rounds - ones;
        currentAngle = 360*difference;

        if (currentAngle < 0){
            currentAngle += 360;
        }

        startAngle = currentAngle;

        imgBottle.setRotation(currentAngle);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        saveBottleRotation();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}