package com.alpay.twinmusic;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;


public class CodeActivity extends AppCompatActivity implements ShakeDetector.Listener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private TextToSpeech textToSpeech;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private boolean inLoop = false;
    private boolean inBPM = false;
    private boolean inMeasure = false;
    private int beatsPerMinute = 120;
    private int loopTimes = 2;
    private double measure = 1;

    public static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter mNfcAdapter;
    private WebView webView;
    private TextView textView;
    private TextView loopText;
    private TextView bpmText;
    private TextView measureText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        prepareViews();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        if (mNfcAdapter == null) {
            Toast.makeText(this, R.string.no_nfc_warning, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, DemoActivity.class);
            startActivity(intent);
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, R.string.nfc_disabled_warning, Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        }
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

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mNfcAdapter != null) {
            setupForegroundDispatch(this, mNfcAdapter);
        }
    }

    @Override
    protected void onPause() {
        if(mNfcAdapter != null){
            stopForegroundDispatch(this, mNfcAdapter);
        }
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void prepareViews() {
        webView = findViewById(R.id.webview);
        textView = findViewById(R.id.code_blocks);
        loopText = findViewById(R.id.loop_text);
        bpmText = findViewById(R.id.bpm_text);
        measureText = findViewById(R.id.measure_text);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        if (isNetworkAvailable()) {
            webView.loadUrl("https://budgi.es/device.html");
        } else {
            webView.loadUrl("file:///android_asset/index.html");
        }
    }

    public void evalCode(final String code) {
        webView.evaluateJavascript(code, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                textView.setText(code);
            }
        });
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else {
                Toast.makeText(this, R.string.nfc_wrong_mime_error, Toast.LENGTH_SHORT);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        // unsupported encoding
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0063;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                TextView textView = findViewById(R.id.info_text);
                textView.setText(result);
                if (result.contentEquals(NFCTag.START)) {
                    evalCode(CodeGenerator.START_SYNTH);
                } else if (result.contentEquals(NFCTag.LOOP)) {
                    inLoop = !inLoop;
                    evalCode(CodeGenerator.START_LOOP);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_A)) {
                    evalCode(CodeGenerator.ADD_NOTE_A);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_B)) {
                    evalCode(CodeGenerator.ADD_NOTE_B);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_C)) {
                    evalCode(CodeGenerator.ADD_NOTE_C);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_D)) {
                    evalCode(CodeGenerator.ADD_NOTE_D);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_E)) {
                    evalCode(CodeGenerator.ADD_NOTE_E);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_F)) {
                    evalCode(CodeGenerator.ADD_NOTE_F);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_G)) {
                    evalCode(CodeGenerator.ADD_NOTE_G);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_NULL)) {
                    evalCode(CodeGenerator.ADD_NOTE_N);
                } else if (result.contentEquals(NFCTag.LOW_FREQ)) {
                    evalCode(CodeGenerator.CHANGE_FREQ_BASS);
                } else if (result.contentEquals(NFCTag.HIGH_FREQ)) {
                    evalCode(CodeGenerator.CHANGE_FREQ_TREBLE);
                } else if (result.contentEquals(NFCTag.PIANO_SOUND)) {
                    evalCode(CodeGenerator.ADD_PIANO);
                } else if (result.contentEquals(NFCTag.GUITAR_SOUND)) {
                    evalCode(CodeGenerator.ADD_GUITAR);
                } else if (result.contentEquals(NFCTag.SINE_WAVE)) {
                    evalCode(CodeGenerator.ADD_SINE);
                } else if (result.contentEquals(NFCTag.SQUARE_WAVE)) {
                    evalCode(CodeGenerator.ADD_SQUARE);
                } else if (result.contentEquals(NFCTag.BPM)) {
                    inBPM = !inBPM;
                    evalCode(CodeGenerator.CHANGE_BPM);
                } else if (result.contentEquals(NFCTag.MEASURE)) {
                    inMeasure = !inMeasure;
                    evalCode(CodeGenerator.CHANGE_MEASURE);
                } else if (result.contentEquals(NFCTag.KICK)) {
                    evalCode(CodeGenerator.KICK);
                } else if (result.contentEquals(NFCTag.SAVE)) {
                    evalCode(CodeGenerator.SAVE);
                } else if (result.contentEquals(NFCTag.RUN)) {
                    evalCode(CodeGenerator.PLAY_SONG);
                } else if (result.contentEquals(NFCTag.SPEAK)) {
                    launchSpeak();
                } else {
                    //TODO: error handling
                }
            }
        }
    }

    public static void setupForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    public void launchSpeak() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
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
        bpmText.setText(String.format(getString(R.string.bpm_times), String.valueOf(beatsPerMinute)));
        textToSpeech.speak(String.valueOf(beatsPerMinute), TextToSpeech.QUEUE_ADD, null);
    }

    protected void decreaseBPM() {
        if (beatsPerMinute < 60) {
            beatsPerMinute = 60;
        } else {
            beatsPerMinute -= 5;
        }
        bpmText.setText(String.format(getString(R.string.bpm_times), String.valueOf(beatsPerMinute)));
        textToSpeech.speak(String.valueOf(beatsPerMinute), TextToSpeech.QUEUE_ADD, null);
    }

    protected void increaseLoop() {
        if (loopTimes > 10) {
            loopTimes = 10;
        } else {
            loopTimes++;
        }
        loopText.setText(String.format(getString(R.string.loop_times), String.valueOf(loopTimes)));
        textToSpeech.speak(String.valueOf(loopTimes), TextToSpeech.QUEUE_ADD, null);
    }

    protected void decreaseLoop() {
        if (loopTimes < 2) {
            loopTimes = 2;
        } else {
            loopTimes--;
        }
        loopText.setText(String.format(getString(R.string.loop_times), String.valueOf(loopTimes)));
        textToSpeech.speak(String.valueOf(loopTimes), TextToSpeech.QUEUE_ADD, null);
    }

    protected void increaseMeasure() {
        if (measure == 1.0) measure = 0.5;
        if (measure == 2.0) measure = 1.0;
        if (measure == 4.0) measure = 2.0;
        measureText.setText(String.format(getString(R.string.measure_times), String.valueOf(measure)));
        textToSpeech.speak(String.valueOf(measure), TextToSpeech.QUEUE_ADD, null);
    }

    protected void decreaseMeasure() {
        if (measure == 2.0) measure = 4.0;
        if (measure == 1.0) measure = 2.0;
        if (measure == 0.5) measure = 1.0;
        measureText.setText(String.format(getString(R.string.measure_times), String.valueOf(measure)));
        textToSpeech.speak(String.valueOf(measure), TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        try {
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

            if (inMeasure) {
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    evalCode(CodeGenerator.MEASURE_UP);
                    increaseMeasure();
                }
                if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    evalCode(CodeGenerator.MEASURE_DOWN);
                    decreaseMeasure();
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
