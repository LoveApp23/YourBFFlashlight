package com.example.gothi.yourbfflashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;


public class MainActivity extends Activity {


    ImageButton btnSwitch;
    ImageButton FlashBlinkButton;
    ImageView FlashBlinkLogo;
    ImageView FlashLight;
    Parameters params;
    MediaPlayer mp;
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    private boolean isBlinkOn;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        // flash switch button
        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);
        FlashLight = (ImageView) findViewById(R.id.FlashLight);

        //flash switch button
        FlashBlinkButton = (ImageButton) findViewById(R.id.FlashBlinkButton);
        FlashBlinkLogo = (ImageView) findViewById(R.id.FlashBlinkLogo);

        // First check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
        }
        // get the camera
        getCamera();

        // display blink button image
        toggleBlinkImage();


        // displaying button image
        toggleButtonImage();

        // Switch button click event to toggle flash on/off
        FlashBlinkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isBlinkOn) {
                    playSound();
                    // turn off flash
                    turnOffBlink();
                } else {
                    turnOffFlash();
                    playSound();
                    // turn on flash
                    turnOnBlink();
                }
            }
        });



        // Switch button click event to toggle flash on/off
        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    playSound();
                    // turn off flash
                    turnOffFlash();
                } else {
                   turnOffBlink();
                    playSound();
                    // turn on flash
                    turnOnFlash();
                }
            }
        });

    }

    // Get the camera
    public void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();

            } catch (RuntimeException e) {
                Log.e(e.getMessage(), "Camera Error. Failed to Open. Error:Application will close! ");
                finish();
            }
        }
    }

    //for simulating flash blink
    Runnable flashBlinkRunnable = new Runnable()
    {
        public void run()
        {

            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
            camera.startPreview();

            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();

            //--->
            handler.post(flashBlinkRunnable);
        }
    };


    // Turning On flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
            toggleButtonImage();



        }

    }

    // Turn On flash Blink
    private void turnOnBlink() {
        if (!isBlinkOn) {
            if (camera == null || params == null) {
                return;
            }
            flashBlinkRunnable.run();
            isBlinkOn = true;
            toggleBlinkImage();
        }
    }



    // Turning Off flash
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;

            }
            //play sound
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
            toggleButtonImage();


        }
    }

    //stop flash blink light
    public void turnOffBlink()
    {
        if (camera == null || params == null) {
            return;
        }
        handler.removeCallbacks(flashBlinkRunnable);
        camera.stopPreview();
        isBlinkOn = false;
        toggleBlinkImage();

    }

    // Playing sound
    // will play button toggle sound on flash on / off
    private void playSound(){
        if(isFlashOn){
            mp = MediaPlayer.create(MainActivity.this, R.raw.soundonclick);
        }else{
            mp = MediaPlayer.create(MainActivity.this, R.raw.soundonclick);
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }



    /*
     * Toggle switch button images
     * changing image states to on / off
     * */
    private void toggleButtonImage() {
        if (isFlashOn) {
            btnSwitch.setImageResource(R.drawable.poweronline);
            FlashLight.setImageResource(R.drawable.flashlight);


        } else {
            btnSwitch.setImageResource(R.drawable.poweroffline);
            FlashLight.setImageResource(R.drawable.flashlightoff);
        }
    }

    private void toggleBlinkImage() {
        if (isBlinkOn) {
            FlashBlinkLogo.setImageResource(R.drawable.blinklighton);
            FlashBlinkButton.setImageResource(R.drawable.blinkonline);
        } else {
            FlashBlinkLogo.setImageResource(R.drawable.blinklightoff);
            FlashBlinkButton.setImageResource(R.drawable.blinkoffline);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }



    @Override
    protected void onPause() {
        super.onPause();

        //close the intents
        turnOffBlink();
        turnOffFlash();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        turnOnFlash();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}