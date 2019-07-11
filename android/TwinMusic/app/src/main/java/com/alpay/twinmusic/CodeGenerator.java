package com.alpay.twinmusic;

public class CodeGenerator {

    public enum Notes {
        A, B, C, D, E, F, G
    }

    private static boolean inLoop = false;
    private static boolean sampleSelected = false;

    private static String code = "";
    private static String loopcode = "";
    private static String insideSampleCode = "";
    private static int octave = 4;
    private static String selectedSound = "piano";

    public static final String start_synth = "var synth = new Tone.Synth().toMaster();\n";
    public static final String start_loop = "const loop = new Tone.Loop(function(time) { %s }, '4n').start(0); Tone.Transport.start();\n";
    private static final String add_note = "synth.triggerAttackRelease('%s%d', '8n', %s);\n";
    private static final String add_note_with_sample = "instruments['" + selectedSound + "'].triggerAttack('%s%d', '8n', %s);";
    private static String select_sample = "var instruments = SampleLibrary.load({ instruments: ['" + selectedSound + "'] });  Tone.Buffer.on('load', function() { instruments['" + selectedSound + "'].toMaster(); %s });";

    public static String getCode() {
        loopcode = (loopcode.length() > 0) ? String.format(start_loop, loopcode) : "%s";
        insideSampleCode = (insideSampleCode.length() > 0) ? String.format(select_sample, insideSampleCode) : "";
        return code + String.format(loopcode, insideSampleCode);
    }

    public static void changeOctave(int oct) {
        octave = oct;
    }

    public static boolean isSampleSelected() {
        return sampleSelected;
    }

    private static void addNewCodeBlock(String text) {
        if (inLoop) {
            if (sampleSelected) {
                insideSampleCode += text;
            }
            loopcode += text;
        } else {
            if (sampleSelected) {
                insideSampleCode += text;
            }
            code += text;
        }
    }

    public static void startSynth() {
        if (code.length() == 0) {
            addNewCodeBlock(start_synth);
        }
    }

    public static void clearCode() {
        loopcode = "";
        code = "";
        insideSampleCode = "";
    }

    public static void deleteLastPart() {
        if (code.length() > 0) {
            int lastLineIndex = code.lastIndexOf('\n');
            code = code.substring(0, lastLineIndex);
        }
    }

    public static void exitLoop() {
        inLoop = false;
    }

    public static void startLoop() {
        inLoop = true;
    }

    public static void selectSample(String sample) {
        selectedSound = sample;
        sampleSelected = true;
    }

    public static void addNoteWithSample(Notes note) {
        switch (note) {
            case A:
                addNewCodeBlock(String.format(add_note_with_sample, "A", octave, ""));
                break;
            case B:
                addNewCodeBlock(String.format(add_note_with_sample, "B", octave, ""));
                break;
            case C:
                addNewCodeBlock(String.format(add_note_with_sample, "C", octave, ""));
                break;
            case D:
                addNewCodeBlock(String.format(add_note_with_sample, "D", octave, ""));
                break;
            case E:
                addNewCodeBlock(String.format(add_note_with_sample, "E", octave, ""));
                break;
            case F:
                addNewCodeBlock(String.format(add_note_with_sample, "F", octave, ""));
                break;
            case G:
                addNewCodeBlock(String.format(add_note_with_sample, "G", octave, ""));
                break;
            default:
                break;
        }
    }

    public static void addNote(Notes note) {
        if (inLoop) {
            switch (note) {
                case A:
                    addNewCodeBlock(String.format(add_note, "A", octave, "time"));
                    break;
                case B:
                    addNewCodeBlock(String.format(add_note, "B", octave, "time"));
                    break;
                case C:
                    addNewCodeBlock(String.format(add_note, "C", octave, "time"));
                    break;
                case D:
                    addNewCodeBlock(String.format(add_note, "D", octave, "time"));
                    break;
                case E:
                    addNewCodeBlock(String.format(add_note, "E", octave, "time"));
                    break;
                case F:
                    addNewCodeBlock(String.format(add_note, "F", octave, "time"));
                    break;
                case G:
                    addNewCodeBlock(String.format(add_note, "G", octave, "time"));
                    break;
                default:
                    break;
            }
        } else {
            switch (note) {
                case A:
                    addNewCodeBlock(String.format(add_note, "A", octave, ""));
                    break;
                case B:
                    addNewCodeBlock(String.format(add_note, "B", octave, ""));
                    break;
                case C:
                    addNewCodeBlock(String.format(add_note, "C", octave, ""));
                    break;
                case D:
                    addNewCodeBlock(String.format(add_note, "D", octave, ""));
                    break;
                case E:
                    addNewCodeBlock(String.format(add_note, "E", octave, ""));
                    break;
                case F:
                    addNewCodeBlock(String.format(add_note, "F", octave, ""));
                    break;
                case G:
                    addNewCodeBlock(String.format(add_note, "G", octave, ""));
                    break;
                default:
                    break;
            }
        }

    }

}
