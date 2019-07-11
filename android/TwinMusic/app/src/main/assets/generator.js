var code = "";

function startToneLibrary() {
    code += "var synth = new Tone.Synth().toMaster();\n";
}

function playMidC() {
    code += "synth.triggerAttackRelease('C4', '8n');\n";
}
