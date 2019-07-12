package com.alpay.twinmusic;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.alpay.twinmusic.utils.ButtonDebug;
import com.alpay.twinmusic.utils.Utils;
import com.squareup.seismic.ShakeDetector;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class InfoActivity extends AppCompatActivity implements ShakeDetector.Listener {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter mNfcAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        context = this;
        Speak.initSounds(this);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
        if (mNfcAdapter == null) {
            Utils.showToast(this, R.string.no_nfc_warning, Toast.LENGTH_LONG);
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Utils.showToast(this, R.string.nfc_disabled_warning, Toast.LENGTH_LONG);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        }
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


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else {
                Utils.showToast(this, R.string.nfc_wrong_mime_error, Toast.LENGTH_SHORT);
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
                if (result.contentEquals(NFCTag.START)) {
                   Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.LOOP)) {
                    Speak.playSound(context, Speak.LOOP);
                } else if (result.contentEquals(NFCTag.CLEAR_ALL)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.LOW_FREQ)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.MEDIUM_FREQ)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.HIGH_FREQ)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.PIANO_SOUND)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.CELLO_SOUND)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.HARMONIUM_SOUND)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_A)) {
                    Speak.playSound(context, Speak.ADD_NOTE_A);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_B)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_C)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_D)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_E)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_F)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.ADD_NOTE_G)) {
                    Speak.playSound(context, Speak.START);
                } else if (result.contentEquals(NFCTag.RUN)) {
                    Speak.playSound(context, Speak.START);
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

    @Override
    public void hearShake() {
        Toast.makeText(this, "Shake detected.", Toast.LENGTH_SHORT).show();
    }
}
