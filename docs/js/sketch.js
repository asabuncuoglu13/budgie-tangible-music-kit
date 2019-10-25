let index = 0;
let inLoop = false;

let song = [];

let trigger = 0;
let autoplay = false;
let osc, oscS, oscT;
let lowFreq = false;
let highFreq = true;

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

    osc = new p5.TriOsc();
    osc.start();
    osc.amp(0);
}

function playSong() {
    if (!autoplay) {
        index = 0;
        autoplay = true;
    }
}

function changeFreq(val) {
    if (val === 2) {
        lowFreq = true;
        highFreq = false;
    } else {
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
                song.push({note: 57, duration: dur, display: "A"});
            } else {
                song.push({note: 69, duration: dur, display: "A"});
            }
            break;
        case 'B':
            if (lowFreq) {
                song.push({note: 59, duration: dur, display: "b"});
            } else {
                song.push({note: 71, duration: dur, display: "B"});
            }
            break;
        case 'C':
            if (lowFreq) {
                song.push({note: 48, duration: dur, display: "C"});
            } else {
                song.push({note: 60, duration: dur, display: "C"});
            }
            break;
        case 'D':
            if (lowFreq) {
                song.push({note: 50, duration: dur, display: "D"});
            } else {
                song.push({note: 62, duration: dur, display: "D"});
            }
            break;
        case 'E':
            if (lowFreq) {
                song.push({note: 52, duration: dur, display: "E"});
            } else {
                song.push({note: 64, duration: dur, display: "E"});
            }
            break;
        case 'F':
            if (lowFreq) {
                song.push({note: 53, duration: dur, display: "F"});
            } else {
                song.push({note: 65, duration: dur, display: "F"});
            }
            break;
        case 'G':
            if (lowFreq) {
                song.push({note: 59, duration: dur, display: "G"});
            } else {
                song.push({note: 71, duration: dur, display: "G"});
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
        osc = new p5.SinOsc();
        osc.start();
        osc.amp(0);
    } else {
        osc = new p5.TriOsc();
        osc.start();
        osc.amp(0);
    }
}


function playNote(note, duration) {
    osc.freq(midiToFreq(note));
    osc.fade(0.5, 0.2);
    if (duration) {
        setTimeout(function () {
            osc.fade(0, 0.2);
        }, duration - 50);
    }
}

function draw() {
    if (autoplay && millis() > trigger) {
        playNote(song[index].note, song[index].duration);
        trigger = millis() + song[index].duration;
        index++;
    } else if (index >= song.length) {
        autoplay = false;
    }
}