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
let measure = 1;

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

let drum;
let drumPat = false;

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
}

function soundLoop(cycleStartTime) {
    recorder.record(soundFile);
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
        recorder.stop();
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
    releaseLoopedSong(loopTimes);
    if (sloop.isPlaying) {
        sloop.stop();
        synth.noteRelease(); // Release all notes
    } else {
        // Reset counters
        eventIndex = 0;
        sloop.start();
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

function changeBPM() {
    currentBlock = "bpm";
}

function changeMeasure() {
    currentBlock = "measure";
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
    releaseLoopedSong(loopTimes);
}

function releaseLoopedSong(num) {
    let flattenedLoopedSong = [];
    for (let i = 0; i < num; i++) {
        flattenedLoopedSong = flattenedLoopedSong.concat(loopedSong);
    }
    for (let i= 0; i < flattenedLoopedSong.length; i++){
        song.push(flattenedLoopedSong[i]);
    }
    loopedSong = [];
}

function addNote(note, dur) {
    if (inLoop){
        if (lowFreq) {
            loopedSong.push({pitch: note + '3', velocity:1, timeSincePrevEvent:0, type:1, soundType: oscType, display: note+'3'});
            loopedSong.push({pitch: note + '3', velocity:1, timeSincePrevEvent:dur, type:0, soundType: oscType, display: note+'3'});
        } else {
            loopedSong.push({pitch: note + '4', velocity:1, timeSincePrevEvent:0, type:1, soundType: oscType, display: note+'4'});
            loopedSong.push({pitch: note + '4', velocity:1, timeSincePrevEvent:dur, type:0, soundType: oscType, display: note+'4'});
        }
    } else {
        if (lowFreq) {
            song.push({pitch: note + '3', velocity:1, timeSincePrevEvent:0, type:1, soundType: oscType, display: note+'3'});
            song.push({pitch: note + '3', velocity:1, timeSincePrevEvent:dur, type:0, soundType: oscType, display: note+'3'});
        } else {
            song.push({pitch: note + '4', velocity:1, timeSincePrevEvent:0, type:1, soundType: oscType, display: note+'4'});
            song.push({pitch: note + '4', velocity:1, timeSincePrevEvent:dur, type:0, soundType: oscType, display: note+'4'});
        }
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

function attackNote(note, duration, soundType) {
    if (drumPat) {
        drum.setVolume(1);
        drum.play();
    }
    if (soundType === "piano") {
        switch (note) {
            case "C3":
                p_c3.setVolume(1);
                p_c3.play();
                break;
            case "D3":
                p_d3.setVolume(1);
                p_d3.play();
                break;
            case "E3":
                p_e3.setVolume(1);
                p_e3.play();
                break;
            case "F3":
                p_f3.setVolume(1);
                p_f3.play();
                break;
            case "G3":
                p_g3.setVolume(1);
                p_g3.play();
                break;
            case "A3":
                p_a3.setVolume(1);
                p_a3.play();
                break;
            case "B3":
                p_b3.setVolume(1);
                p_b3.play();
                break;
            case "C4":
                p_c4.setVolume(1);
                p_c4.play();
                break;
            case "D4":
                p_d4.setVolume(1);
                p_d4.play();
                break;
            case "E4":
                p_e4.setVolume(1);
                p_e4.play();
                break;
            case "F4":
                p_f4.setVolume(1);
                p_f4.play();
                break;
            case "G4":
                p_g4.setVolume(1);
                p_g4.play();
                break;
            case "A4":
                p_a4.setVolume(1);
                p_a4.play();
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
                break;
            case "D3":
                g_d3.setVolume(1);
                g_d3.play();
                break;
            case "E3":
                g_e3.setVolume(1);
                g_e3.play();
                break;
            case "F3":
                g_f3.setVolume(1);
                g_f3.play();
                break;
            case "G3":
                g_g3.setVolume(1);
                g_g3.play();
                break;
            case "A3":
                g_a3.setVolume(1);
                g_a3.play();
                break;
            case "B3":
                g_b3.setVolume(1);
                g_b3.play();
                break;
            case "C4":
                g_c4.setVolume(1);
                g_c4.play();
                break;
            case "D4":
                g_d4.setVolume(1);
                g_d4.play();
                break;
            case "E4":
                g_e4.setVolume(1);
                g_e4.play();
                break;
            case "F4":
                g_f4.setVolume(1);
                g_f4.play();
                break;
            case "G4":
                g_g4.setVolume(1);
                g_g4.play();
                break;
            case "A4":
                g_a4.setVolume(1);
                g_a4.play();
                break;
            case "B4":
                g_b4.setVolume(1);
                g_b4.play();
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
    if (drumPat) {
        drum.stop(measure);
    }
    if (soundType === "piano") {
        switch (note) {
            case "C3":
                p_c3.fade(measure, measure);
                break;
            case "D3":
                p_d3.stop(measure);
                break;
            case "E3":
                p_e3.stop(measure);
                break;
            case "F3":
                p_f3.stop(measure);
                break;
            case "G3":
                p_g3.stop(measure);
                break;
            case "A3":
                p_a3.stop(measure);
                break;
            case "B3":
                p_b3.stop(measure);
                break;
            case "C4":
                p_c4.fade(measure, measure);
                break;
            case "D4":
                p_d4.stop(measure);
                break;
            case "E4":
                p_e4.stop(measure);
                break;
            case "F4":
                p_f4.stop(measure);
                break;
            case "G4":
                p_g4.stop(measure);
                break;
            case "A4":
                p_a4.stop(measure);
                break;
            case "B4":
                p_b4.stop(measure);
                break;
            default:
                break;
        }
    }
    if (soundType === "guitar") {
        switch (note) {
            case "C3":
                g_c3.stop(measure);
                break;
            case "D3":
                g_d3.stop(measure);
                break;
            case "E3":
                g_e3.stop(measure);
                break;
            case "F3":
                g_f3.stop(measure);
                break;
            case "G3":
                g_g3.stop(measure);
                break;
            case "A3":
                g_a3.stop(measure);
                break;
            case "B3":
                g_b3.stop(measure);
                break;
            case "C4":
                g_c4.stop(measure);
                break;
            case "D4":
                g_d4.stop(measure);
                break;
            case "E4":
                g_e4.stop(measure);
                break;
            case "F4":
                g_f4.stop(measure);
                break;
            case "G4":
                g_g4.stop(measure);
                break;
            case "A4":
                g_a4.stop(measure);
                break;
            case "B4":
                g_b4.stop(measure);
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

function kick() {
    drumPat = !drumPat;
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
            addNote('A', measure);
            break;
        case 'b' || 'B':
            addNote('B', measure);
            break;
        case 'c' || 'C':
            addNote('C', measure);
            break;
        case 'd' || 'D':
            addNote('D', measure);
            break;
        case 'e' || 'E':
            addNote('E', measure);
            break;
        case 'f' || 'F':
            addNote('F', measure);
            break;
        case 'g' || 'G':
            addNote('G', measure);
            break;
        case 'n' || 'N':
            addNote('N', measure);
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
        case '2':
            changeBPM();
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
            saveFile();
            break;
        default:
            break;
    }
}

function saveFile() {
    save(soundFile, 'myAlgorithmicMusic.wav');
}

function increaseMeasure() {
    if (measure === 1) measure = 0.5;
    if (measure === 2) measure = 1;
    if (measure === 4) measure = 2;
}

function decreaseMeasure() {
    if (measure === 2) measure = 4;
    if (measure === 1) measure = 2;
    if (measure === 0.5) measure = 1;
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
    if (currentBlock === "measure") {
        if (keyCode === UP_ARROW) {
            increaseMeasure();
        }
        if (keyCode === DOWN_ARROW) {
            decreaseMeasure();
        }
        document.getElementById("measureTimes").innerHTML = 4/measure;
    }
}

function draw() {
    background(255);
}