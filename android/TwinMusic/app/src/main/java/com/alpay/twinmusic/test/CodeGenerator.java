package com.alpay.twinmusic.test;

import java.util.ArrayList;
import java.util.Arrays;

public class CodeGenerator {

    public enum Notes {
        A, B, C, D, E, F, G, N
    }

    private static boolean inLoop = false;
    private static boolean sampleSelected = false;
    private static boolean pauseState = false;
    private static boolean beatAdded = false;

    private static String code = "";
    private static String beatCode = "";
    private static int octave = 4;
    private static String selectedSound = "piano";
    private static String selectedWave = "sine";
    private static String duration = "4n";
    private static ArrayList<String> notes = new ArrayList<>();

    private static final String synth_options = "{ 'oscillator' : { 'type' : '%s' }, " +
            "'envelope' : { 'attack' : 0.1, 'decay': 0.1, 'sustain': 0.9, 'release': 1 } }";
    private static final String synth_code = "var synth = new Tone.Synth(" + synth_options + ").toMaster();" +
            "var music = %s;" +
            "function playMusic(){\n" +
            "    var part = new Tone.Part(function(time, note){\n" +
            "        synth.triggerAttackRelease(note.note, note.duration, time);\n" +
            "    }, music).start(0);\n" +
            "   Tone.Transport.loop = %s;" +
            "    Tone.Transport.start('+0.1');\n" +
            "}" +
            "playMusic(music)";
    private static final String sample_code = "var instruments = SampleLibrary.load({instruments: ['%s']});\n" +
            "Tone.Buffer.on('load', function () {\n" +
            "instruments['%s'].toMaster(); " +
            "var interval = new Tone.Sequence(function (time, note) {\n" +
            "instruments['%s'].triggerAttackRelease(note, 1); }, %s, '4n'); " +
            "interval.loop = %s; " +
            "interval.start(0); " +
            "Tone.Transport.start('+0.1'); });";
    private static final String add_beat = "var kick = new Tone.MembraneSynth();\n" +
            "var kickdistortion = new Tone.Distortion(8);\n" +
            "var kickdelay = new Tone.PingPongDelay({\n" +
            "  'delayTime' : '8n',\n" +
            "  'feedback' : 0.3,\n" +
            "  'wet' : 0.5\n" +
            "});\n" +
            "var kickphaser = new Tone.Phaser();\n" +
            "kick.chain(kickdistortion, kickdelay, kickphaser, Tone.Master);\n" +
            "\n" +
            "var kickloop = new Tone.Loop(function(time) {\n" +
            "  kick.triggerAttackRelease('C1', '8n', time);\n" +
            "}, '4n').start();";
    private static final String pause_play = "Tone.Transport.pause();\n";

    public static String getCode() {
        clearCode();
        if (sampleSelected) {
            for (int i = 0; i < notes.size(); i++) {
                notes.set(i, String.format(notes.get(i), i * 0.5, octave, duration));
            }
            code += String.format(sample_code, selectedSound, selectedSound, selectedSound, Arrays.toString(notes.toArray()), inLoop);
        }else{
            for (int i = 0; i < notes.size(); i++) {
                notes.set(i, String.format(notes.get(i), i * 0.5, octave, duration));
            }
            code += String.format(synth_code, selectedWave, Arrays.toString(notes.toArray()), inLoop);
        }
        if (beatAdded){
            code += beatCode;
        }
        return code;
    }

    public static String resume(){
        pauseState = false;
        return getCode();
    }

    public static String pause(){
        pauseState = true;
        return pause_play;
    }

    public static boolean isPaused(){
        return pauseState;
    }

    public static void changeOctave(int oct) {
        octave = oct;
    }

    public static boolean isSampleSelected() {
        return sampleSelected;
    }


    public static void startSynth() {
        notes = new ArrayList<>();
    }

    public static void clearCode() {
        code = "";
    }

    public static void deleteLastPart() {
        if (code.length() > 0) {
            int lastLineIndex = code.lastIndexOf('\n');
            code = code.substring(0, lastLineIndex);
        }
    }

    public static void startLoop() {
        inLoop = true;
    }

    public static void addBeat(){
        beatCode = "";
        beatAdded = true;
        beatCode += add_beat;
    }

    public static void deleteBeat(){
        beatAdded = false;
        beatCode = "";
    }

    public static void selectSample(String sample) {
        selectedSound = sample;
        sampleSelected = true;
    }

    public static void selectSynthWave(String wave){
        selectedWave = wave;
    }

    public static void addNote(Notes note) {
        switch (note) {
            case A:
                notes.add("{ time : %f, note : 'A%d', dur : '%s'}");
                break;
            case B:
                notes.add("{ time : %f, note : 'B%d', dur : '%s'}");
                break;
            case C:
                notes.add("{ time : %f, note : 'C%d', dur : '%s'}");
                break;
            case D:
                notes.add("{ time : %f, note : 'D%d', dur : '%s'}");
                break;
            case E:
                notes.add("{ time : %f, note : 'E%d', dur : '%s'}");
                break;
            case F:
                notes.add("{ time : %f, note : 'F%d', dur : '%s'}");
                break;
            case G:
                notes.add("{ time : %f, note : 'G%d', dur : '%s'}");
                break;
            case N:
                notes.add("null");
                break;
            default:
                break;
        }
    }

}

