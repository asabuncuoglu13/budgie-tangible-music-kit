let index = 0;
let inLoop = false;
let gap = 30;

let song = [];
let loopedSong = [];
let sloop;
let synth;
let eventIndex = 0;
let beatsPerMinute = 120;
let secondsPerBeat;

let trigger = 0;
let autoplay = false;
let osc;
let lowFreq = false;
let highFreq = true;
let oscType = "sine";
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
    noCanvas();

    synth = new p5.PolySynth();
    sloop = new p5.SoundLoop(soundLoop, 0.2);
    osc = new p5.Oscillator();
    osc.amp(1);

    recorder = new p5.SoundRecorder();
    recorder.setInput();
    soundFile = new p5.SoundFile();

    myPart = new p5.Part();
}

function noteToFreq(note) {
    let notes = ['A', 'A#', 'B', 'C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#'], octave, keyNumber;
    if (note.length === 3) {
        octave = note.charAt(2);
    } else {
        octave = note.charAt(1);
    }
    keyNumber = notes.indexOf(note.slice(0, -1));
    if (keyNumber < 3) {
        keyNumber = keyNumber + 12 + ((octave - 1) * 12) + 1;
    } else {
        keyNumber = keyNumber + ((octave - 1) * 12) + 1;
    }
    return 440 * Math.pow(2, (keyNumber- 49) / 12);
};

function soundLoop(cycleStartTime) {
    let event = song[eventIndex];
    if (event.type === 1) {
        if (event.soundType === "poly"){
            synth.noteAttack(event.pitch, event.velocity, cycleStartTime);
        } else {
            attackNote(event.pitch, event.timeSincePrevEvent, event.soundType);
        }
    } else {
        if (event.soundType  === "poly") {
            synth.noteRelease(event.pitch, cycleStartTime);
        } else {
            releaseNote(event.pitch, event.timeSincePrevEvent, event.soundType);
        }
    }
    // Prepare for next event
    eventIndex++;
    if (eventIndex >= song.length) {
        this.stop();
    } else {
        let nextEvent = song[eventIndex];
        // This cycle will last for the time since previous event of the next event
        secondsPerBeat = 60 / beatsPerMinute;

        let duration = nextEvent.timeSincePrevEvent * secondsPerBeat;
        this.interval = max(duration, 0.01); // Cannot have interval of exactly 0
    }
}

function playSong() {
    if (sloop.isPlaying) {
        sloop.stop();
        synth.noteRelease(); // Release all notes
    } else {
        // Reset counters
        eventIndex = 0;
        sloop.start();
    }

  /*  if (inLoop) {
        createLoopedSong(loopTimes);
        myPart.loop();
    }
    if (!autoplay) {
        index = 0;
        autoplay = true;
    }
    myPart.start();*/
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

function changeBPM() {
    currentBlock = "bpm";
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
    if (lowFreq) {
        song.push({pitch: note + '3', velocity:1, timeSincePrevEvent:0, type:1, soundType: oscType, display: note+'3'});
        song.push({pitch: note + '3', velocity:1, timeSincePrevEvent:dur, type:0, soundType: oscType, display: note+'3'});
    } else {
        song.push({pitch: note + '4', velocity:1, timeSincePrevEvent:0, type:1, soundType: oscType, display: note+'4'});
        song.push({pitch: note + '4', velocity:1, timeSincePrevEvent:dur, type:0, soundType: oscType, display: note+'4'});
    }
}

function addPiano() {
    oscType = "piano";
}

function addGuitar() {
    oscType = "guitar";
}

function addSine() {
    oscType = "sine";
}

function addSquare() {
    oscType = "square";
}


function playNote(note, duration, soundType) {
    if (soundType === "piano") {
        switch (note) {
            case "C3":
                p_c3.setVolume(1);
                p_c3.play();
                p_c3.stop(1);
                break;
            case "D3":
                p_d3.setVolume(1);
                p_d3.play();
                p_d3.stop(1);
                break;
            case "E3":
                p_e3.setVolume(1);
                p_e3.play();
                p_e3.stop(1);
                break;
            case "F3":
                p_f3.setVolume(1);
                p_f3.play();
                p_f3.stop(1);
                break;
            case "G3":
                p_g3.setVolume(1);
                p_g3.play();
                p_g3.stop(1);
                break;
            case "A3":
                p_a3.setVolume(1);
                p_a3.play();
                p_a3.stop(1);
                break;
            case "B3":
                p_b3.setVolume(1);
                p_b3.play();
                p_b3.stop(1);
                break;
            case "C4":
                p_c4.setVolume(1);
                p_c4.play();
                p_c4.stop(1);
                break;
            case "D4":
                p_d4.setVolume(1);
                p_d4.play();
                p_d4.stop(1);
                break;
            case "E4":
                p_e4.setVolume(1);
                p_e4.play();
                p_e4.stop(1);
                break;
            case "F4":
                p_f4.setVolume(1);
                p_f4.play();
                p_f4.stop(1);
                break;
            case "G4":
                p_g4.setVolume(1);
                p_g4.play();
                p_g4.stop(1);
                break;
            case "A4":
                p_a4.setVolume(1);
                p_a4.play();
                p_a4.stop(1);
                break;
            case "B4":
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
            case "C3":
                g_c3.setVolume(1);
                g_c3.play();
                g_c3.stop(1);
                break;
            case "D3":
                g_d3.setVolume(1);
                g_d3.play();
                g_d3.stop(1);
                break;
            case "E3":
                g_e3.setVolume(1);
                g_e3.play();
                g_e3.stop(1);
                break;
            case "F3":
                g_f3.setVolume(1);
                g_f3.play();
                g_f3.stop(1);
                break;
            case "G3":
                g_g3.setVolume(1);
                g_g3.play();
                g_g3.stop(1);
                break;
            case "A3":
                g_a3.setVolume(1);
                g_a3.play();
                g_a3.stop(1);
                break;
            case "B3":
                g_b3.setVolume(1);
                g_b3.play();
                g_b3.stop(1);
                break;
            case "C4":
                g_c4.setVolume(1);
                g_c4.play();
                g_c4.stop(1);
                break;
            case "D4":
                g_d4.setVolume(1);
                g_d4.play();
                g_d4.stop(1);
                break;
            case "E4":
                g_e4.setVolume(1);
                g_e4.play();
                g_e4.stop(1);
                break;
            case "F4":
                g_f4.setVolume(1);
                g_f4.play();
                g_f4.stop(1);
                break;
            case "G4":
                g_g4.setVolume(1);
                g_g4.play();
                g_g4.stop(1);
                break;
            case "A4":
                g_a4.setVolume(1);
                g_a4.play();
                g_a4.stop(1);
                break;
            case "B4":
                g_b4.setVolume(1);
                g_b4.play();
                g_b4.stop(1);
                break;
            default:
                break;
        }
    }
    if (soundType === "square") {
        osc.start();
        osc.freq(noteToFreq(note));
        osc.amp(1);
        osc.setType('square');
        osc.stop(duration);
    } else if (soundType === "sine") {
        osc.start();
        osc.freq(noteToFreq(note));
        osc.amp(1);
        osc.setType('sine');
        osc.stop(duration);
    }
}


function attackNote(note, duration, soundType) {
    if (soundType === "piano") {
        switch (note) {
            case "C3":
                p_c3.setVolume(1);
                p_c3.play();
                p_c3.stop(1);
                break;
            case "D3":
                p_d3.setVolume(1);
                p_d3.play();
                p_d3.stop(1);
                break;
            case "E3":
                p_e3.setVolume(1);
                p_e3.play();
                p_e3.stop(1);
                break;
            case "F3":
                p_f3.setVolume(1);
                p_f3.play();
                p_f3.stop(1);
                break;
            case "G3":
                p_g3.setVolume(1);
                p_g3.play();
                p_g3.stop(1);
                break;
            case "A3":
                p_a3.setVolume(1);
                p_a3.play();
                p_a3.stop(1);
                break;
            case "B3":
                p_b3.setVolume(1);
                p_b3.play();
                p_b3.stop(1);
                break;
            case "C4":
                p_c4.setVolume(1);
                p_c4.play();
                p_c4.stop(1);
                break;
            case "D4":
                p_d4.setVolume(1);
                p_d4.play();
                p_d4.stop(1);
                break;
            case "E4":
                p_e4.setVolume(1);
                p_e4.play();
                p_e4.stop(1);
                break;
            case "F4":
                p_f4.setVolume(1);
                p_f4.play();
                p_f4.stop(1);
                break;
            case "G4":
                p_g4.setVolume(1);
                p_g4.play();
                p_g4.stop(1);
                break;
            case "A4":
                p_a4.setVolume(1);
                p_a4.play();
                p_a4.stop(1);
                break;
            case "B4":
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
            case "C3":
                g_c3.setVolume(1);
                g_c3.play();
                g_c3.stop(1);
                break;
            case "D3":
                g_d3.setVolume(1);
                g_d3.play();
                g_d3.stop(1);
                break;
            case "E3":
                g_e3.setVolume(1);
                g_e3.play();
                g_e3.stop(1);
                break;
            case "F3":
                g_f3.setVolume(1);
                g_f3.play();
                g_f3.stop(1);
                break;
            case "G3":
                g_g3.setVolume(1);
                g_g3.play();
                g_g3.stop(1);
                break;
            case "A3":
                g_a3.setVolume(1);
                g_a3.play();
                g_a3.stop(1);
                break;
            case "B3":
                g_b3.setVolume(1);
                g_b3.play();
                g_b3.stop(1);
                break;
            case "C4":
                g_c4.setVolume(1);
                g_c4.play();
                g_c4.stop(1);
                break;
            case "D4":
                g_d4.setVolume(1);
                g_d4.play();
                g_d4.stop(1);
                break;
            case "E4":
                g_e4.setVolume(1);
                g_e4.play();
                g_e4.stop(1);
                break;
            case "F4":
                g_f4.setVolume(1);
                g_f4.play();
                g_f4.stop(1);
                break;
            case "G4":
                g_g4.setVolume(1);
                g_g4.play();
                g_g4.stop(1);
                break;
            case "A4":
                g_a4.setVolume(1);
                g_a4.play();
                g_a4.stop(1);
                break;
            case "B4":
                g_b4.setVolume(1);
                g_b4.play();
                g_b4.stop(1);
                break;
            default:
                break;
        }
    }
    if (soundType === "square") {
        osc.start();
        osc.freq(noteToFreq(note));
        osc.amp(1);
        osc.setType('square');
    } else if (soundType === "sine") {
        osc.start();
        osc.freq(noteToFreq(note));
        osc.amp(1);
        osc.setType('sine');
    }
}


function releaseNote(note, duration, soundType) {
    if (soundType === "piano") {
        switch (note) {
            case "C3":
                p_c3.setVolume(1);
                p_c3.play();
                p_c3.stop(1);
                break;
            case "D3":
                p_d3.setVolume(1);
                p_d3.play();
                p_d3.stop(1);
                break;
            case "E3":
                p_e3.setVolume(1);
                p_e3.play();
                p_e3.stop(1);
                break;
            case "F3":
                p_f3.setVolume(1);
                p_f3.play();
                p_f3.stop(1);
                break;
            case "G3":
                p_g3.setVolume(1);
                p_g3.play();
                p_g3.stop(1);
                break;
            case "A3":
                p_a3.setVolume(1);
                p_a3.play();
                p_a3.stop(1);
                break;
            case "B3":
                p_b3.setVolume(1);
                p_b3.play();
                p_b3.stop(1);
                break;
            case "C4":
                p_c4.setVolume(1);
                p_c4.play();
                p_c4.stop(1);
                break;
            case "D4":
                p_d4.setVolume(1);
                p_d4.play();
                p_d4.stop(1);
                break;
            case "E4":
                p_e4.setVolume(1);
                p_e4.play();
                p_e4.stop(1);
                break;
            case "F4":
                p_f4.setVolume(1);
                p_f4.play();
                p_f4.stop(1);
                break;
            case "G4":
                p_g4.setVolume(1);
                p_g4.play();
                p_g4.stop(1);
                break;
            case "A4":
                p_a4.setVolume(1);
                p_a4.play();
                p_a4.stop(1);
                break;
            case "B4":
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
            case "C3":
                g_c3.setVolume(1);
                g_c3.play();
                g_c3.stop(1);
                break;
            case "D3":
                g_d3.setVolume(1);
                g_d3.play();
                g_d3.stop(1);
                break;
            case "E3":
                g_e3.setVolume(1);
                g_e3.play();
                g_e3.stop(1);
                break;
            case "F3":
                g_f3.setVolume(1);
                g_f3.play();
                g_f3.stop(1);
                break;
            case "G3":
                g_g3.setVolume(1);
                g_g3.play();
                g_g3.stop(1);
                break;
            case "A3":
                g_a3.setVolume(1);
                g_a3.play();
                g_a3.stop(1);
                break;
            case "B3":
                g_b3.setVolume(1);
                g_b3.play();
                g_b3.stop(1);
                break;
            case "C4":
                g_c4.setVolume(1);
                g_c4.play();
                g_c4.stop(1);
                break;
            case "D4":
                g_d4.setVolume(1);
                g_d4.play();
                g_d4.stop(1);
                break;
            case "E4":
                g_e4.setVolume(1);
                g_e4.play();
                g_e4.stop(1);
                break;
            case "F4":
                g_f4.setVolume(1);
                g_f4.play();
                g_f4.stop(1);
                break;
            case "G4":
                g_g4.setVolume(1);
                g_g4.play();
                g_g4.stop(1);
                break;
            case "A4":
                g_a4.setVolume(1);
                g_a4.play();
                g_a4.stop(1);
                break;
            case "B4":
                g_b4.setVolume(1);
                g_b4.play();
                g_b4.stop(1);
                break;
            default:
                break;
        }
    }
    if (soundType === "square") {
        osc.stop(duration);
    } else if (soundType === "sine") {
        osc.stop(duration);
    }
}



// TODO: Change it as an NFC function
function mousePressed() {
    getAudioContext().resume();
}

function touchStarted() {
    getAudioContext().resume();
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
        case 'o' || 'O':
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
        case 's' || 'S':
            addSquare();
            break;
        case 'p' || 'P':
            addPiano();
            break;
        case 'r' || 'R':
            playSong();
            break;
        case 'v' || 'V':
            save(soundFile, 'mySound.wav');
            break;
        default:
            break;
    }
}

function increaseBPM() {
    (beatsPerMinute > 180) ? beatsPerMinute = 180 : beatsPerMinute+=5;
}

function decreaseBPM() {
    (beatsPerMinute < 60) ? beatsPerMinute = 60 : beatsPerMinute-=5;
}


function increaseLoop() {
    (loopTimes > 10) ? loopTimes = 10 : loopTimes++;
}

function decreaseLoop() {
    (loopTimes < 2) ? loopTimes = 2 : loopTimes--;
}

function keyPressed() {
    if (currentBlock === "loop") {
        if (keyCode === UP_ARROW) {
            increaseLoop();
        }
        if (keyCode === DOWN_ARROW) {
            decreaseLoop();
        }
        document.getElementById("loopTimes").innerHTML = loopTimes;
    }
    if (currentBlock === "bpm") {
        if (keyCode === UP_ARROW) {
            increaseBPM();
        }
        if (keyCode === DOWN_ARROW) {
            decreaseBPM();
        }
        document.getElementById("bpmTimes").innerHTML = beatsPerMinute;
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
    let diff = (typeof note === 'number') ? note - 65 : note.charCodeAt(0) - 65;
    ellipse(width / 2, noteY + diff * (gap / 2), gap + 10, gap);
}

function generateID() {
    return '_' + Math.random().toString(36).substr(2, 9);
}

function draw() {
    background(255);
    /*if (autoplay && millis() > trigger) {
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
    }*/
}