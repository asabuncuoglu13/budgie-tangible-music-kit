let index = 0;
let inLoop = false;
let gap = 30;

let song = [];
let loopedSong = [];

let trigger = 0;
let autoplay = false;
let oscS, oscT;
let lowFreq = false;
let highFreq = true;
let osc = "sine";
let recorder, soundFile;
let loopTimes = 2;
let currentBlock = "start";
let p_a3, p_a4, p_b3, p_b4, p_c3, p_c4, p_d3, p_d4, p_e3, p_e4, p_f3, p_f4, p_g3, p_g4;
let g_a3, g_a4, g_b3, g_b4, g_c3, g_c4, g_d3, g_d4, g_e3, g_e4, g_f3, g_f4, g_g3, g_g4;

let box, drum, myPart;
let boxPat = [1, 0, 0, 2, 0, 2, 0, 0];
let drumPat = [0, 1, 1, 0, 2, 0, 1, 0];

function preload() {
    p_a3 = loadSound('sound/piano/A3.mp3');
    p_a4 = loadSound('sound/piano/A4.mp3');
    p_b3 = loadSound('sound/piano/B3.mp3');
    p_b4 = loadSound('sound/piano/B4.mp3');
    p_c3 = loadSound('sound/piano/C3.mp3');
    p_c4 = loadSound('sound/piano/C4.mp3');
    p_d3 = loadSound('sound/piano/D3.mp3');
    p_d4 = loadSound('sound/piano/D4.mp3');
    p_e3 = loadSound('sound/piano/E3.mp3');
    p_e4 = loadSound('sound/piano/E4.mp3');
    p_f3 = loadSound('sound/piano/F3.mp3');
    p_f4 = loadSound('sound/piano/F4.mp3');
    p_g3 = loadSound('sound/piano/G3.mp3');
    p_g4 = loadSound('sound/piano/G4.mp3');

    g_a3 = loadSound('sound/guitar/A3.mp3');
    g_a4 = loadSound('sound/guitar/A4.mp3');
    g_b3 = loadSound('sound/guitar/B3.mp3');
    g_b4 = loadSound('sound/guitar/B4.mp3');
    g_c3 = loadSound('sound/guitar/C3.mp3');
    g_c4 = loadSound('sound/guitar/C4.mp3');
    g_d3 = loadSound('sound/guitar/D3.mp3');
    g_d4 = loadSound('sound/guitar/D4.mp3');
    g_e3 = loadSound('sound/guitar/E3.mp3');
    g_e4 = loadSound('sound/guitar/E4.mp3');
    g_f3 = loadSound('sound/guitar/F3.mp3');
    g_f4 = loadSound('sound/guitar/F4.mp3');
    g_g3 = loadSound('sound/guitar/G3.mp3');
    g_g4 = loadSound('sound/guitar/G4.mp3');

    box = loadSound('sound/beatbox.mp3');
    drum = loadSound('sound/drum.mp3');
}

function setup() {
    createCanvas(windowWidth, windowHeight);

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

    myPart = new p5.Part();
}

function playSong() {
    if (inLoop) {
        createLoopedSong(loopTimes);
        myPart.loop();
    }
    if (!autoplay) {
        index = 0;
        autoplay = true;
    }
    myPart.start();
}

function playBox(time, playbackRate) {
    box.rate(playbackRate);
    box.play(time);
}

function playDrum(time, playbackRate) {
    drum.rate(playbackRate);
    drum.play(time);
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

function changeBPM(num) {
    myPart.setBPM(num);
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
    drawNote(note, dur);
    switch (note) {
        case 'A':
            if (lowFreq) {
                song.push({ note: 57, duration: dur, soundType: osc, display: "A" });
            } else {
                song.push({ note: 69, duration: dur, soundType: osc, display: "A" });
            }
            break;
        case 'B':
            if (lowFreq) {
                song.push({ note: 59, duration: dur, soundType: osc, display: "b" });
            } else {
                song.push({ note: 71, duration: dur, soundType: osc, display: "B" });
            }
            break;
        case 'C':
            if (lowFreq) {
                song.push({ note: 48, duration: dur, soundType: osc, display: "C" });
            } else {
                song.push({ note: 60, duration: dur, soundType: osc, display: "C" });
            }
            break;
        case 'D':
            if (lowFreq) {
                song.push({ note: 50, duration: dur, soundType: osc, display: "D" });
            } else {
                song.push({ note: 62, duration: dur, soundType: osc, display: "D" });
            }
            break;
        case 'E':
            if (lowFreq) {
                song.push({ note: 52, duration: dur, soundType: osc, display: "E" });
            } else {
                song.push({ note: 64, duration: dur, soundType: osc, display: "E" });
            }
            break;
        case 'F':
            if (lowFreq) {
                song.push({ note: 53, duration: dur, soundType: osc, display: "F" });
            } else {
                song.push({ note: 65, duration: dur, soundType: osc, display: "F" });
            }
            break;
        case 'G':
            if (lowFreq) {
                song.push({ note: 59, duration: dur, soundType: osc, display: "G" });
            } else {
                song.push({ note: 71, duration: dur, soundType: osc, display: "G" });
            }
            break;
        case 'N':
            song.push({ note: 0, duration: dur, soundType: osc, display: "N" });
            break;
        default:
            break;
    }
}

function addPiano() {
    osc = "piano";
}

function addGuitar() {
    osc = "guitar";
}

function addSine() {
    osc = "sine";
}

function addSquare() {
    osc = "square";
}


function playNote(note, duration, soundType) {
    drawNote(note, duration);
    if (soundType === "piano") {
        switch (note) {
            case 48:
                p_c3.setVolume(1);
                p_c3.play();
                p_c3.stop(1);
                break;
            case 50:
                p_d3.setVolume(1);
                p_d3.play();
                p_d3.stop(1);
                break;
            case 52:
                p_e3.setVolume(1);
                p_e3.play();
                p_e3.stop(1);
                break;
            case 53:
                p_f3.setVolume(1);
                p_f3.play();
                p_f3.stop(1);
                break;
            case 55:
                p_g3.setVolume(1);
                p_g3.play();
                p_g3.stop(1);
                break;
            case 57:
                p_a3.setVolume(1);
                p_a3.play();
                p_a3.stop(1);
                break;
            case 59:
                p_b3.setVolume(1);
                p_b3.play();
                p_b3.stop(1);
                break;
            case 60:
                p_c4.setVolume(1);
                p_c4.play();
                p_c4.stop(1);
                break;
            case 62:
                p_d4.setVolume(1);
                p_d4.play();
                p_d4.stop(1);
                break;
            case 64:
                p_e4.setVolume(1);
                p_e4.play();
                p_e4.stop(1);
                break;
            case 65:
                p_f4.setVolume(1);
                p_f4.play();
                p_f4.stop(1);
                break;
            case 67:
                p_g4.setVolume(1);
                p_g4.play();
                p_g4.stop(1);
                break;
            case 69:
                p_a4.setVolume(1);
                p_a4.play();
                p_a4.stop(1);
                break;
            case 71:
                p_b4.setVolume(1);
                p_b4.play();
                p_b4.stop(1);
                break;
            default:
                break;
        }
    }
    if (soundType === "guitar") {
        switch (note) {
            case 48:
                g_c3.setVolume(1);
                g_c3.play();
                g_c3.stop(1);
                break;
            case 50:
                g_d3.setVolume(1);
                g_d3.play();
                g_d3.stop(1);
                break;
            case 52:
                g_e3.setVolume(1);
                g_e3.play();
                g_e3.stop(1);
                break;
            case 53:
                g_f3.setVolume(1);
                g_f3.play();
                g_f3.stop(1);
                break;
            case 55:
                g_g3.setVolume(1);
                g_g3.play();
                g_g3.stop(1);
                break;
            case 57:
                g_a3.setVolume(1);
                g_a3.play();
                g_a3.stop(1);
                break;
            case 59:
                g_b3.setVolume(1);
                g_b3.play();
                g_b3.stop(1);
                break;
            case 60:
                g_c4.setVolume(1);
                g_c4.play();
                g_c4.stop(1);
                break;
            case 62:
                g_d4.setVolume(1);
                g_d4.play();
                g_d4.stop(1);
                break;
            case 64:
                g_e4.setVolume(1);
                g_e4.play();
                g_e4.stop(1);
                break;
            case 65:
                g_f4.setVolume(1);
                g_f4.play();
                g_f4.stop(1);
                break;
            case 67:
                g_g4.setVolume(1);
                g_g4.play();
                g_g4.stop(1);
                break;
            case 69:
                g_a4.setVolume(1);
                g_a4.play();
                g_a4.stop(1);
                break;
            case 71:
                g_b4.setVolume(1);
                g_b4.play();
                g_b4.stop(1);
                break;
            default:
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

// TODO: Change it as an NFC function
function mousePressed() {
    getAudioContext().resume();
}

function touchStarted() {
    getAudioContext().resume()
}

// TODO: Change it to save block
function keyTyped() {
    switch (key) {
        case 'a' || 'A':
            addNote('A', 200);
            break;
        case 'b' || 'B':
            addNote('B', 200);
            break;
        case 'c' || 'C':
            addNote('C', 200);
            break;
        case 'd' || 'D':
            addNote('D', 200);
            break;
        case 'e' || 'E':
            addNote('E', 200);
            break;
        case 'f' || 'F':
            addNote('F', 200);
            break;
        case 'g' || 'G':
            addNote('G', 200);
            break;
        case 'n' || 'N':
            addNote('N', 200);
            break;
        case '1':
            startSynth();
            break;
        case 'l' || 'L':
            startLoop();
            break;
        case 'f' || 'F':
            changeFreq('bass');
            break;
        case 't' || 'T':
            changeFreq('treble');
            break;
        case '3':
            changeBPM(30);
            break;
        case '6':
            changeBPM(60);
            break;
        case 'u' || 'U':
            addGuitar();
            break;
        case 'i' || 'I':
            addSine();
            break;
        case 'r' || 'R':
            addSquare();
            break;
        case 'o' || 'O':
            addPiano();
            break;
        case 'p' || 'P':
            playSong();
            break;
        case 's' || 'S':
            save(soundFile, 'mySound.wav');
            break;
        default:
            break;
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
        document.getElementById("loopTimes").innerHTML = loopTimes;
    }
}

function startNewPart() {
    let boxPhrase = new p5.Phrase('box', playBox, boxPat);
    let drumPhrase = new p5.Phrase('drum', playDrum, drumPat);

    myPart.addPhrase(boxPhrase);
    myPart.addPhrase(drumPhrase);
}

function drawStaff() {
    let lineY = height / 2 - 75;
    for (let i = 0; i < 5; i++) {
        line(0, lineY, width, lineY);
        lineY += gap;
    }
}

function drawNote(note, dur) {
    ellipseMode(CENTER);
    let noteY = height / 2 - 75;
    let diff = note.charCodeAt(0) - "A".charCodeAt(0);
    ellipse(width / 2, noteY + diff * (gap / 2), gap + 10, gap);
}

function draw() {
    background(255);
    drawStaff();
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
}