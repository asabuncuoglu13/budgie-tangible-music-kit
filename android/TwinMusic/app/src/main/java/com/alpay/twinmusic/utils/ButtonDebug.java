package com.alpay.twinmusic.utils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alpay.twinmusic.CodeGenerator;
import com.alpay.twinmusic.InfoActivity;
import com.alpay.twinmusic.NFCTag;
import com.alpay.twinmusic.R;

public class ButtonDebug {

    AppCompatActivity activity;

    public ButtonDebug(AppCompatActivity appCompatActivity) {
        activity = appCompatActivity;
    }

    public void setButtons() {
        Button startButton = activity.findViewById(R.id.startbutton);
        Button startLoopButton = activity.findViewById(R.id.startloopbutton);
        Button pianoButton = activity.findViewById(R.id.pianobutton);
        Button harmoniumButton = activity.findViewById(R.id.harmoniumbutton);
        Button celloButton = activity.findViewById(R.id.cellobutton);
        Button addNoteAButton = activity.findViewById(R.id.addnoteA);
        Button addNoteBButton = activity.findViewById(R.id.addnoteB);
        Button addNoteCButton = activity.findViewById(R.id.addnoteC);
        Button addNoteDButton = activity.findViewById(R.id.addnoteD);
        Button lowFreqButton = activity.findViewById(R.id.lowfreq);
        Button medFreqButton = activity.findViewById(R.id.medfreq);
        Button highFreqButton = activity.findViewById(R.id.highfreq);
        Button sineButton = activity.findViewById(R.id.sineButton);
        Button squareButton = activity.findViewById(R.id.squareButton);
        Button speakButton = activity.findViewById(R.id.speakButton);
        Button nullNoteButton = activity.findViewById(R.id.addnotenull);
        Button addBeatButton = activity.findViewById(R.id.addbeat);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.startSynth();
            }
        });

        startLoopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.startLoop();
            }
        });

        pianoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.selectSample("piano");
            }
        });

        harmoniumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.selectSample("harmonium");
            }
        });

        celloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.selectSample("cello");
            }
        });

        addBeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.addBeat();
            }
        });

        addNoteAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.addNote(CodeGenerator.Notes.A);
            }
        });

        addNoteBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.addNote(CodeGenerator.Notes.B);
            }
        });

        addNoteCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.addNote(CodeGenerator.Notes.C);
            }
        });

        addNoteDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.addNote(CodeGenerator.Notes.D);
            }
        });

        nullNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.addNote(CodeGenerator.Notes.N);
            }
        });

        lowFreqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.changeOctave(2);
            }
        });


        medFreqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.changeOctave(3);
            }
        });


        highFreqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.changeOctave(4);
            }
        });

        sineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.selectSynthWave(NFCTag.SINE_WAVE);
            }
        });

        squareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeGenerator.selectSynthWave(NFCTag.SQUARE_WAVE);
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InfoActivity.class);
                activity.startActivity(intent);
            }
        });

    }
}
