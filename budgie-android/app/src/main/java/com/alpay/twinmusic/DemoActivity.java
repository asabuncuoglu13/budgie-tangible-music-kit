package com.alpay.twinmusic;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

import java.util.Locale;


public class DemoActivity extends AppCompatActivity implements ShakeDetector.Listener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private TextToSpeech textToSpeech;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private boolean inLoop = false;
    private boolean inBPM = false;
    private int beatsPerMinute = 120;
    private int loopTimes = 2;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        prepareViews();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    private void prepareViews() {
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        if (isNetworkAvailable()) {
            webView.loadUrl("https://budgi.es/demo.html");
        } else {
            Toast.makeText(this, "You need an active internet connection.", Toast.LENGTH_LONG).show();
        }
    }

    public void evalCode(final String code) {
        webView.evaluateJavascript(code, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDown: " + event.toString());
        return true;
    }

    protected void increaseBPM() {
        if (beatsPerMinute > 180) {
            beatsPerMinute = 180;
        } else {
            beatsPerMinute += 5;
        }
        textToSpeech.speak(String.valueOf(beatsPerMinute), TextToSpeech.QUEUE_ADD, null);
    }

    protected void decreaseBPM() {
        if (beatsPerMinute < 60) {
            beatsPerMinute = 60;
        } else {
            beatsPerMinute -= 5;
        }
        textToSpeech.speak(String.valueOf(beatsPerMinute), TextToSpeech.QUEUE_ADD, null);
    }

    protected void increaseLoop() {
        if (loopTimes > 10) {
            loopTimes = 10;
        } else {
            loopTimes++;
        }
        textToSpeech.speak(String.valueOf(loopTimes), TextToSpeech.QUEUE_ADD, null);
    }

    protected void decreaseLoop() {
        if (loopTimes < 2) {
            loopTimes = 2;
        } else {
            loopTimes--;
        }
        textToSpeech.speak(String.valueOf(loopTimes), TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        try {
            // right to left swipe
            if (inLoop) {
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    evalCode(CodeGenerator.LOOP_UP);
                    increaseLoop();
                }
                if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    evalCode(CodeGenerator.LOOP_DOWN);
                    decreaseLoop();
                }
            }

            if (inBPM) {
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    evalCode(CodeGenerator.BPM_UP);
                    increaseBPM();
                }
                if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    evalCode(CodeGenerator.BPM_DOWN);
                    decreaseBPM();
                }
            }

        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }


    @Override
    public void hearShake() {
        evalCode(CodeGenerator.PAUSE);
        webView.reload();
        Toast.makeText(this, "Shake detected.", Toast.LENGTH_SHORT).show();
    }
}
