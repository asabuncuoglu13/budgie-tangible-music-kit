package com.alpay.twinmusic;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.HashMap;

public class Speak {
    public static final int START = R.raw.start;
    public static final int LOOP = R.raw.loop;
    public static final int ADD_NOTE_A = R.raw.notea;

    private static SoundPool soundPool;
    private static HashMap soundPoolMap;

    /** Populate the SoundPool*/
    public static void initSounds(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap(3);
        soundPoolMap.put( START, soundPool.load(context, R.raw.start, 1) );
        soundPoolMap.put( LOOP, soundPool.load(context, R.raw.loop, 2) );
        soundPoolMap.put( ADD_NOTE_A, soundPool.load(context, R.raw.notea, 3) );
    }

    public static void playSound(Context context, int soundID){
        MediaPlayer mp = MediaPlayer.create(context, soundID);
        mp.start();
    }
}
