package com.alpay.twinmusic;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.HashMap;

public class Speak {
    public static final int BEAT_FAST = R.raw.beatfast;
    public static final int BEAT_MED = R.raw.beatmed;
    public static final int BEAT_SLOW = R.raw.beatslow;
    public static final int CELLO = R.raw.cello;
    public static final int CLEAR_ALL = R.raw.clearall;
    public static final int CODE = R.raw.code;
    public static final int FREQ_HIGH = R.raw.freqhigh;
    public static final int FREQ_LOW = R.raw.freqlow;
    public static final int FREQ_MED = R.raw.freqmed;
    public static final int HARMONIUM = R.raw.harmonium;
    public static final int NOTE_LONG = R.raw.longnote;
    public static final int LOOP = R.raw.loop;
    public static final int ADD_NOTE_A = R.raw.notea;
    public static final int ADD_NOTE_B = R.raw.noteb;
    public static final int ADD_NOTE_C = R.raw.notec;
    public static final int ADD_NOTE_D = R.raw.noted;
    public static final int ADD_NOTE_E = R.raw.notee;
    public static final int ADD_NOTE_F = R.raw.notef;
    public static final int ADD_NOTE_G = R.raw.noteg;
    public static final int ADD_NOTE_NULL = R.raw.notenull;
    public static final int PIANO = R.raw.piano;
    public static final int RUN = R.raw.run;
    public static final int NOTE_SHORT = R.raw.shortnote;
    public static final int SINE_WAVE = R.raw.sinewave;
    public static final int SQUARE_WAVE = R.raw.squarewave;
    public static final int START = R.raw.start;

    private static SoundPool soundPool;
    private static HashMap soundPoolMap;

    /** Populate the SoundPool*/
    public static void initSounds(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap(26);
        soundPoolMap.put(BEAT_FAST, soundPool.load(context, R.raw.beatfast, 1));
        soundPoolMap.put(BEAT_MED, soundPool.load(context, R.raw.beatmed, 2));
        soundPoolMap.put(BEAT_SLOW, soundPool.load(context, R.raw.beatslow, 3));
        soundPoolMap.put(CELLO, soundPool.load(context, R.raw.cello, 4));
        soundPoolMap.put(CLEAR_ALL, soundPool.load(context, R.raw.clearall, 5));
        soundPoolMap.put(CODE, soundPool.load(context, R.raw.code, 6));
        soundPoolMap.put(FREQ_HIGH, soundPool.load(context, R.raw.freqhigh,7));
        soundPoolMap.put(FREQ_LOW, soundPool.load(context, R.raw.freqlow, 8));
        soundPoolMap.put(FREQ_MED, soundPool.load(context, R.raw.freqmed,9));
        soundPoolMap.put(HARMONIUM, soundPool.load(context, R.raw.harmonium, 10));
        soundPoolMap.put(NOTE_LONG, soundPool.load(context, R.raw.longnote, 11));
        soundPoolMap.put(LOOP, soundPool.load(context, R.raw.loop, 12));
        soundPoolMap.put(ADD_NOTE_A, soundPool.load(context, R.raw.notea, 13));
        soundPoolMap.put(ADD_NOTE_B, soundPool.load(context, R.raw.noteb, 14));
        soundPoolMap.put(ADD_NOTE_C, soundPool.load(context, R.raw.notec, 15));
        soundPoolMap.put(ADD_NOTE_D, soundPool.load(context, R.raw.noted, 16));
        soundPoolMap.put(ADD_NOTE_E, soundPool.load(context, R.raw.notee, 17));
        soundPoolMap.put(ADD_NOTE_F, soundPool.load(context, R.raw.notef, 18));
        soundPoolMap.put(ADD_NOTE_G, soundPool.load(context, R.raw.noteg, 19));
        soundPoolMap.put(ADD_NOTE_NULL, soundPool.load(context, R.raw.notenull, 20));
        soundPoolMap.put(PIANO, soundPool.load(context, R.raw.piano, 21));
        soundPoolMap.put(RUN, soundPool.load(context, R.raw.run, 22));
        soundPoolMap.put(NOTE_SHORT, soundPool.load(context, R.raw.shortnote, 23));
        soundPoolMap.put(SINE_WAVE, soundPool.load(context, R.raw.sinewave, 24));
        soundPoolMap.put(SQUARE_WAVE, soundPool.load(context, R.raw.squarewave, 25));
        soundPoolMap.put(START, soundPool.load(context, R.raw.start, 26));
    }

    public static void playSound(Context context, int soundID){
        MediaPlayer mp = MediaPlayer.create(context, soundID);
        mp.start();
    }
}
