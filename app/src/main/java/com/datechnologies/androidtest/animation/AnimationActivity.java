package com.datechnologies.androidtest.animation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.datechnologies.androidtest.MainActivity;
import com.datechnologies.androidtest.R;

import java.io.IOException;
import java.util.Random;

/**
 * Screen that displays the D & A Technologies logo.
 * The icon can be moved around on the screen as well as animated.
 * */

public class AnimationActivity extends AppCompatActivity implements View.OnTouchListener {

    //==============================================================================================
    // Class Properties
    //==============================================================================================
    private ImageView DAImage;
    private float dx,dy;
    int lastAction;
    private MediaPlayer mediaPlayer;

    //==============================================================================================
    // Static Class Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, AnimationActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        // TODO: Make the UI look like it does in the mock-up. Allow for horizontal screen rotation.
        // TODO: Add a ripple effect when the buttons are clicked

        // TODO: When the fade button is clicked, you must animate the D & A Technologies logo.
        // TODO: It should fade from 100% alpha to 0% alpha, and then from 0% alpha to 100% alpha

        // TODO: The user should be able to touch and drag the D & A Technologies logo around the screen.

        // TODO: Add a bonus to make yourself stick out. Music, color, fireworks, explosions!!!

        DAImage = findViewById(R.id.DAImage);
        //setting on touch listener to the image
        DAImage.setOnTouchListener(this);

        //playing corny fun music
        mediaPlayer = new MediaPlayer();
        String url = "https://www.bensound.com/bensound-music/bensound-littleidea.mp3"; // your URL here
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(url);
            //async start so it didnt freeze on activity open
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        //fun color swapper
        RelativeLayout animationLayout = findViewById(R.id.animationLayout);
        Runnable colorSwapper = () -> {
            try {
                while (true){
                    //waits 2 seconds and switches color
                    Thread.sleep(2000);
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    //need to run on UI thread
                    runOnUiThread(() -> animationLayout.setBackgroundColor(color));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        //new thread to run the color change so it doesnt freeze
        Thread colorSwapperThread = new Thread(colorSwapper,"SwapperThread");
        colorSwapperThread.start();

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                dx = view.getX() - event.getRawX();
                dy = view.getY() - event.getRawY();
                //setting last action to not trigger other events
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case  MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dy);
                view.setX(event.getRawX() + dx);
                //setting last action to not trigger other events
                lastAction = MotionEvent.ACTION_MOVE;
                break;
            //have to use this instead of onclick listener because on click is overridden by on touch
            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN){
                    //starting animation
                    DAImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.da_animation));
                }
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        //stopping music when going back
        mediaPlayer.stop();
        super.onDestroy();
    }
}
