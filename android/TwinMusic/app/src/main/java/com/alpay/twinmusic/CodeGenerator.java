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

    private static String code = "";
    private static int octave = 4;
    private static String selectedSound = "piano";
    private static String selectedWave = "sine";
    private static ArrayList<String> notes = new ArrayList<>();

    private static final String synth_options = "{ 'oscillator' : { 'type' : '%s' }, 'envelope' : { 'attack' : 0.1, 'decay': 0.1, 'sustain': 0.9, 'release': 1 } }";
    private static final String synth_code = "var synth = new Tone.Synth("+ synth_options +").toMaster();" +
            "function playInterval(notes) { var interval = new Tone.Sequence(function(time, note){ synth.triggerAttackRelease(note, 1); }, notes, '8n'); interval.loop = %s; interval.start(0); Tone.Transport.start('+0.1');}" +
            "function triggerSynth(time){ synth.triggerAttackRelease('8n', time) } playInterval(%s);";
    private static final String sample_code = "var instruments = SampleLibrary.load({instruments: ['%s']}); Tone.Buffer.on('load', function () { instruments['%s'].toMaster(); var interval = new Tone.Sequence(function (time, note) { instruments['%s'].triggerAttackRelease(note, 1); }, %s, '4n'); interval.loop = %s; interval.start(0); Tone.Transport.start('+0.1'); });";
    private static final String start_play = "Tone.Transport.start(+0.1);\n";

    public static String getCode() {
        clearCode();
        if (sampleSelected) {
            for (int i = 0; i < notes.size(); i++) {
                notes.set(i, String.format(notes.get(i), octave));
            }
            code += String.format(sample_code, selectedSound, selectedSound, selectedSound, Arrays.toString(notes.toArray()), inLoop);
        }else{
            for (int i = 0; i < notes.size(); i++) {
                notes.set(i, String.format(notes.get(i), octave));
            }
            code += String.format(synth_code, selectedWave, inLoop, Arrays.toString(notes.toArray()));
        }
        return code;
    }

    public static String pause(){
        pauseState = true;
        return getCode();
    }

    public static String resume(){
        pauseState = false;
        return start_play;
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
                notes.add("'A%d'");
                break;
            case B:
                notes.add("'B%d'");
                break;
            case C:
                notes.add("'C%d'");
                break;
            case D:
                notes.add("'D%d'");
                break;
            case E:
                notes.add("'E%d'");
                break;
            case F:
                notes.add("'F%d'");
                break;
            case G:
                notes.add("'G%d'");
                break;
            case N:
                notes.add("null");
                break;
            default:
                break;
        }
    }

}