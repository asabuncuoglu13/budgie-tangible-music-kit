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
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class CodeActivity extends AppCompatActivity implements ShakeDetector.Listener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    public static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter mNfcAdapter;
    private WebView webView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        prepareWebView();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        if (mNfcAdapter == null) {
            Toast.makeText(this, R.string.no_nfc_warning, Toast.LENGTH_LONG).show();
            finish();
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

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    private void prepareWebView() {
        webView = findViewById(R.id.webview);
        textView = findViewById(R.id.code_blocks);
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
                } else if (result.contentEquals(NFCTag.PART)) {
                    evalCode(CodeGenerator.START_PART);
                } else if (result.contentEquals(NFCTag.LOOP)) {
                    evalCode(CodeGenerator.START_LOOP);
                } else if (result.contentEquals(NFCTag.CLEAR_ALL)) {
                    evalCode(CodeGenerator.CLEAR_ALL);
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
                } else if (result.contentEquals(NFCTag.BEAT_SLOW)) {
                    evalCode(CodeGenerator.CHANGE_TEMPO_LOW);
                } else if (result.contentEquals(NFCTag.BEAT_MED)) {
                    evalCode(CodeGenerator.CHANGE_TEMPO_MED);
                } else if (result.contentEquals(NFCTag.BEAT_FAST)) {
                    evalCode(CodeGenerator.CHANGE_TEMPO_HIGH);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {

        }
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
        }
        return true;
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

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                return false;
            }
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                evalCode(CodeGenerator.LOOP_DOWN);
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                evalCode(CodeGenerator.LOOP_UP);
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
