package com.alpay.twinmusic;

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
    private static final String synth_code = "var synth = new Tone.Synth("+ synth_options +").toMaster();\n" +
            "function playInterval(notes) { \n" +
            "   var interval = new Tone.Sequence(function(time, note){ \n" +
            "       synth.triggerAttackRelease(note.note, note.duration, time); \n" +
            "   }, notes, '4n'); \n" +
            "   interval.loop = %s;\n" +
            "   interval.start(0);\n " +
            "   Tone.Transport.start('+0.2');\n" +
            "}\n" +
            "function triggerSynth(time, note){ \n" +
            "   synth.triggerAttackRelease(note.note, note.duration, time); \n" +
            "} \n" +
            "playInterval(%s);\n";
    private static final String sample_code = "var instruments = SampleLibrary.load({instruments: ['%s']});\n" +
            "Tone.Buffer.on('load', function () { \n" +
            "   instruments['%s'].toMaster(); \n" +
            "   var interval = new Tone.Sequence(function (time, note) { \n" +
            "       instruments['%s'].triggerAttackRelease(note, 1); \n" +
            "   }, %s, '4n'); \n" +
            "   interval.loop = %s; \n" +
            "   interval.start(0); \n" +
            "   Tone.Transport.start('+0.2'); \n" +
            "});";
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
            code += String.format(sample_code, selectedSound, selectedSound, selectedSound, Arrays.toString(notes.toArray()), inLoop);
        }else{
            code += String.format(synth_code, selectedWave, inLoop, Arrays.toString(notes.toArray()));
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

    /*public static void addNote(Notes note) {
        switch (note) {
            case A:
                notes.add(String.format("'A%d'", octave));
                break;
            case B:
                notes.add(String.format("'B%d'", octave));
                break;
            case C:
                notes.add(String.format("'C%d'", octave));
                break;
            case D:
                notes.add(String.format("'D%d'", octave));
                break;
            case E:
                notes.add(String.format("'E%d'", octave));
                break;
            case F:
                notes.add(String.format("'F%d'", octave));
                break;
            case G:
                notes.add(String.format("'G%d'", octave));
                break;
            case N:
                notes.add("null");
                break;
            default:
                break;
        }
    }*/

    public static void addNote(Notes note) {
        switch (note) {
            case A:
                notes.add(String.format("{ time : %f, note : 'A%d', dur : '%s'}", notes.size() * 0.5, octave, duration));
                break;
            case B:
                notes.add(String.format("{ time : %f, note : 'B%d', dur : '%s'}", notes.size() * 0.5, octave, duration));
                break;
            case C:
                notes.add(String.format("{ time : %f, note : 'C%d', dur : '%s'}", notes.size() * 0.5, octave, duration));
                break;
            case D:
                notes.add(String.format("{ time : %f, note : 'D%d', dur : '%s'}", notes.size() * 0.5, octave, duration));
                break;
            case E:
                notes.add(String.format("{ time : %f, note : 'E%d', dur : '%s'}", notes.size() * 0.5, octave, duration));
                break;
            case F:
                notes.add(String.format("{ time : %f, note : 'F%d', dur : '%s'}", notes.size() * 0.5, octave, duration));
                break;
            case G:
                notes.add(String.format("{ time : %f, note : 'G%d', dur : '%s'}", notes.size() * 0.5, octave, duration));
                break;
            case N:
                notes.add("null");
                break;
            default:
                break;
        }
    }

}
