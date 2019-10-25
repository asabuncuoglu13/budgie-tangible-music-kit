let inLoop = false;
let sampleSelected = false;
let pauseState = false;
let beatAdded = false;

let code = "";
let beatCode = "";
let octave = 4;
let selectedSound = "piano";
let selectedWave = "sine";
let duration = "4n";
let notes = [];

let synth_code = "var synth = new Tone.Synth("+
    "{ 'oscillator' : { 'type' : '{0}' }, " +
    "'envelope' : { 'attack' : 0.1, 'decay': 0.1, 'sustain': 0.9, 'release': 1 } }"
    +").toMaster();\n" +
    "function playInterval(notes) { \n" +
    "   var interval = new Tone.Sequence(function(time, note){ \n" +
    "       synth.triggerAttackRelease(note.note, note.duration, time); \n" +
    "   }, notes, '4n'); \n" +
    "   interval.loop = {1};\n" +
    "   interval.start(0);\n " +
    "   Tone.Transport.start('+0.2');\n" +
    "}\n" +
    "function triggerSynth(time, note){ \n" +
    "   synth.triggerAttackRelease(note.note, note.duration, time); \n" +
    "} \n" +
    "playInterval([{2}]);\n";

let scheduledPlay = "function playInterval(notes) { \n " +
    "   Tone.Transport.stop();\n" +
    "   Tone.Transport.position = 0;\n" +
    "   Tone.Transport.cancel();\n" +
    "   Tone.Transport.schedule((time) => {\n" +
    "       let relativeTime = 0;\n" +
    "       for (const note of notes) {\n" +
    "           const duration = note.dur;\n" +
    "           synth.triggerAttackRelease(note.note, note.dur, time + relativeTime);\n" +
    "           relativeTime += Tone.Time(duration).toSeconds();\n" +
    "       }\n" +
    "   }, '4m');\n" +
    "   Tone.Transport.bpm.value = 120;\n" +
    "   Tone.Transport.start();\n" +
    "}" +
    "playInterval([{note : 'A3', dur : '4n'}, {note : 'B4', dur : '16n'}]);";

let sample_code = "var instruments = SampleLibrary.load({instruments: ['{0}']});\n" +
    "Tone.Buffer.on('load', function () { \n" +
    "   instruments['{1}'].toMaster(); \n" +
    "   var interval = new Tone.Sequence(function (time, note) { \n" +
    "       instruments['{2}'].triggerAttackRelease(note.note, note.duration, time); \n" +
    "   }, {3}, '4n'); \n" +
    "   interval.loop = {4}; \n" +
    "   interval.start(0); \n" +
    "   Tone.Transport.start('+0.2'); \n" +
    "});";

let add_beat = "var kick = new Tone.MembraneSynth();\n" +
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

let s_code = "fnPlaySong([ ['E,0', 8], ['D,0', 8], ['C,0', 2], ['C,0', 8], ['D,0', 8], ['C,0', 8], ['E,0', 8], ['D,0', 1], ['C,0', 8], ['D,0', 8], ['E,0', 2], ['A,0', 8], ['G,0', 8], ['E,0', 8], ['C,0', 8], ['D,0', 1], ['A,0', 8], ['B,0', 8], ['C,1', 2], ['B,0', 8], ['C,1', 8], ['D,1', 8], ['C,1', 8], ['A,0', 1], ['G,0', 8], ['A,0', 8], ['B,0', 2], ['C,1', 8], ['B,0', 8], ['A,0', 8], ['G,0', 8], ['A,0', 1] ]);";

let change_bpm = "Tone.Transport.bpm.value = {0}";

let pause_play = "Tone.Transport.pause();\n";

function getCode() {
    code = "";
    if (sampleSelected) {
        code += sample_code.format(selectedSound, selectedSound, selectedSound, notes, inLoop);
    }else{
        code += synth_code.format(selectedWave, inLoop, notes);
    }
    if (beatAdded){
        code += beatCode;
    }
    return code;
}

function resume(){
    pauseState = false;
    return getCode();
}

function pause(){
    pauseState = true;
    return pause_play;
}

function isPaused(){
    return pauseState;
}

function changeOctave(oct) {
    octave = oct;
}

function isSampleSelected() {
    return sampleSelected;
}

function changeBPM(bpm){
    return change_bpm.format(bpm);
}


function startSynth() {
}

function clearCode() {
    code = "";
    beatCode = "";
    notes = [];
}

function deleteLastPart() {
    if (code.length() > 0) {
        let lastLineIndex = code.lastIndexOf('\n');
        code = code.substring(0, lastLineIndex);
    }
}

function startLoop() {
    inLoop = true;
}

function addBeat(){
    beatCode = "";
    beatAdded = true;
    beatCode += add_beat;
}

function deleteBeat(){
    beatAdded = false;
    beatCode = "";
}

function shortNote(){
    duration = "8n";
}


function longNote(){
    duration = "2n";
}

function selectSample(sample) {
    selectedSound = sample;
    sampleSelected = true;
}

function selectSynthWave(wave){
    selectedWave = wave;
    sampleSelected = false;
}

function addNote(note) {
    switch (note) {
        case 'A':
            notes.push("{ time : {0}, note : 'A{1}', dur : '{2}'}".format( notes.length * 0.5, octave, duration));
            break;
        case 'B':
            notes.push("{ time : {0}, note : 'B{1}', dur : '{2}'}".format( notes.length * 0.5, octave, duration));
            break;
        case 'C':
            notes.push("{ time : {0}, note : 'C{1}', dur : '{2}'}".format( notes.length * 0.5, octave, duration));
            break;
        case 'D':
            notes.push("{ time : {0}, note : 'D{1}', dur : '{2}'}".format( notes.length * 0.5, octave, duration));
            break;
        case 'E':
            notes.push("{ time : {0}, note : 'E{1}', dur : '{2}'}".format( notes.length * 0.5, octave, duration));
            break;
        case 'F':
            notes.push("{ time : {0}, note : 'F{1}', dur : '{2}'}".format( notes.length * 0.5, octave, duration));
            break;
        case 'G':
            notes.push("{ time : {0}, note : 'G{1}', dur : '{2}'}".format( notes.length * 0.5, octave, duration));
            break;
        case 'N':
            notes.push("null");
            break;
        default:
            break;
    }
}
