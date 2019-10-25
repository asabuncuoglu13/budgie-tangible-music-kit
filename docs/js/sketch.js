let index = 0;
let inLoop = false;

let song = [];

let trigger = 0;
let autoplay = false;
let oscS, oscT;
let lowFreq = false;
let highFreq = true;
let drum, beatbox;
let osc = "sine";
let recorder, soundFile;

function preload() {
    beatbox = loadSound('sound/beatbox.mp3');
    drum = loadSound('sound/drum.mp3');
}

function setup() {
    createCanvas(10, 10);

    // Triangle oscillator
    oscT = new p5.TriOsc();
    oscT.start();
    oscT.amp(0);

    // Sine oscillator
    oscS = new p5.SinOsc();
    oscS.start();
    oscS.amp(0);

    recorder = new p5.SoundRecorder();
    recorder.setInput();
    soundFile = new p5.SoundFile();
}

function playSong() {
    if (!autoplay) {
        index = 0;
        autoplay = true;
    }
}

function changeFreq(val) {
    if (val === 'bass') {
        lowFreq = true;
        highFreq = false;
    } else if (val === 'treble') {
        lowFreq = false;
        highFreq = true;
    }
}

function startSynth() {
    clearCode();
}

function clearCode() {
    song = [];
}

function startLoop() {
    inLoop = !inLoop;
}

function addNote(note, dur) {
    switch (note) {
        case 'A':
            if (lowFreq) {
                song.push({note: 57, duration: dur, soundType: osc, display: "A"});
            } else {
                song.push({note: 69, duration: dur, soundType: osc, display: "A"});
            }
            break;
        case 'B':
            if (lowFreq) {
                song.push({note: 59, duration: dur, soundType: osc, display: "b"});
            } else {
                song.push({note: 71, duration: dur, soundType: osc, display: "B"});
            }
            break;
        case 'C':
            if (lowFreq) {
                song.push({note: 48, duration: dur, soundType: osc, display: "C"});
            } else {
                song.push({note: 60, duration: dur, soundType: osc, display: "C"});
            }
            break;
        case 'D':
            if (lowFreq) {
                song.push({note: 50, duration: dur, soundType: osc, display: "D"});
            } else {
                song.push({note: 62, duration: dur, soundType: osc, display: "D"});
            }
            break;
        case 'E':
            if (lowFreq) {
                song.push({note: 52, duration: dur, soundType: osc, display: "E"});
            } else {
                song.push({note: 64, duration: dur, soundType: osc, display: "E"});
            }
            break;
        case 'F':
            if (lowFreq) {
                song.push({note: 53, duration: dur, soundType: osc, display: "F"});
            } else {
                song.push({note: 65, duration: dur, soundType: osc, display: "F"});
            }
            break;
        case 'G':
            if (lowFreq) {
                song.push({note: 59, duration: dur, soundType: osc, display: "G"});
            } else {
                song.push({note: 71, duration: dur, soundType: osc, display: "G"});
            }
            break;
        case 'N':
            song.push("null");
            break;
        default:
            break;
    }
}

function setOssiloscope(type) {
    if (type === "sine") {
        osc = "sine";
    } else {
        osc = "square";
    }
}


function playNote(note, duration, soundType) {
    if (soundType === "square") {
        oscT.freq(midiToFreq(note));
        oscT.fade(0.5, 0.2);
        if (duration) {
            setTimeout(function () {
                oscT.fade(0, 0.2);
            }, duration - 50);
        }
    } else if (soundType === "sine") {
        oscS.freq(midiToFreq(note));
        oscS.fade(0.5, 0.2);
        if (duration) {
            setTimeout(function () {
                oscS.fade(0, 0.2);
            }, duration - 50);
        }

    }
}


function keyTyped() {
    if (key === 's') {
        save(soundFile, 'mySound.wav');
    }
}

function draw() {
    if (autoplay && millis() > trigger) {
        recorder.record(soundFile);
        playNote(song[index].note, song[index].duration, song[index].soundType);
        trigger = millis() + song[index].duration;
        index++;
        if (index >= song.length){
            recorder.stop();
        }
    } else if (index >= song.length) {
        autoplay = false;
    }
    /*
    if (frameCount % 30 === 0) {
        drum.play();
    }
    if (frameCount % 60 === 0) {
        beatbox.play();
    }
    */
}