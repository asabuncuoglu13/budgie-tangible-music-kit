let index = 0;
let inLoop = false;

let song = [];
let loopedSong = [];

let trigger = 0;
let autoplay = false;
let oscS, oscT;
let lowFreq = false;
let highFreq = true;
let drum, beatbox;
let osc = "sine";
let recorder, soundFile;
let loopTimes = 2;
let currentBlock = "start";

function preload() {
    p_a3 = loadSound('sound/piano/a3.mp3');
    p_a4 = loadSound('sound/piano/a4.mp3');
    p_b3 = loadSound('sound/piano/b3.mp3');
    p_b4 = loadSound('sound/piano/b4.mp3');
    p_c3 = loadSound('sound/piano/c3.mp3');
    p_c4 = loadSound('sound/piano/c4.mp3');
    p_d3 = loadSound('sound/piano/d3.mp3');
    p_d4 = loadSound('sound/piano/d4.mp3');
    p_e3 = loadSound('sound/piano/e3.mp3');
    p_e4 = loadSound('sound/piano/e4.mp3');
    p_f3 = loadSound('sound/piano/f3.mp3');
    p_f4 = loadSound('sound/piano/f4.mp3');
    p_g3 = loadSound('sound/piano/g3.mp3');
    p_g4 = loadSound('sound/piano/g4.mp3');
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
    if (inLoop) {
        createLoopedSong(loopTimes);
    }
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
    currentBlock = "loop";
}

function createLoopedSong(num) {
    loopedSong = [];
    for (let i = 0; i < num; i++) {
        loopedSong = loopedSong.concat(song);
    }
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

function addPiano() {
    osc = "piano";
}

function setOssiloscope(type) {
    if (type === "sine") {
        osc = "sine";
    } else {
        osc = "square";
    }
}


function playNote(note, duration, soundType) {
    if (soundType === "piano") {
        switch (note) {
            case 48:
                p_c3.setVolume(1);
                p_c3.play();
                break;
            case 50:
                p_d3.setVolume(1);
                p_d3.play();
                break;
            case 52:
                p_e3.setVolume(1);
                p_e3.play();
                break;
            case 53:
                p_f3.setVolume(1);
                p_f3.play();
                break;
            case 55:
                p_g3.setVolume(1);
                p_g3.play();
                break;
            case 57:
                p_a3.setVolume(1);
                p_a3.play();
                break;
            case 59:
                p_b3.setVolume(1);
                p_b3.play();
                break;
            case 60:
                p_c4.setVolume(1);
                p_c4.play();
                break;
            case 62:
                p_d4.setVolume(1);
                p_d4.play();
                break;
            case 64:
                p_e4.setVolume(1);
                p_e4.play();
                break;
            case 65:
                p_f4.setVolume(1);
                p_f4.play();
                break;
            case 67:
                p_g4.setVolume(1);
                p_g4.play();
                break;
            case 69:
                p_a4.setVolume(1);
                p_a4.play();
                break;
            case 71:
                p_b4.setVolume(1);
                p_b4.play();
                break;
        }
    }
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

function mousePressed() {
    getAudioContext().resume();
}

function keyTyped() {
    if (key === 's') {
        save(soundFile, 'mySound.wav');
    }
}

function keyPressed() {
    if (currentBlock === "loop") {
        if (keyCode === UP_ARROW) {
            (loopTimes > 10) ? loopTimes = 10 : loopTimes++;
        }
        if (keyCode === DOWN_ARROW) {
            (loopTimes < 2) ? loopTimes = 2 : loopTimes--;
        }
    }
}

function draw() {
    if (autoplay && millis() > trigger) {
        recorder.record(soundFile);
        if (inLoop) {
            playNote(loopedSong[index].note, loopedSong[index].duration, loopedSong[index].soundType);
            trigger = millis() + loopedSong[index].duration;
        } else {
            playNote(song[index].note, song[index].duration, song[index].soundType);
            trigger = millis() + song[index].duration;
        }
        index++;
        if (index >= song.length + 2) {
            recorder.stop();
        }
    } else if (inLoop ? index >= loopedSong.length : index >= song.length) {
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