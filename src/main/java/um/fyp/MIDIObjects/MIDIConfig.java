package um.fyp.MIDIObjects;

import um.fyp.MIDIConfigWindow;

public class MIDIConfig {
    public int ppq;
    public int bpm;
    public int[] timeSignature;
    public boolean sysGM;
    public boolean omniPoly;

    public MIDIConfig(int ppq, int bpm, int[] timeSignature, boolean sysGM, boolean omniPoly) {
        this.ppq = ppq;
        this.bpm = bpm;
        this.timeSignature = timeSignature;
        this.sysGM = sysGM;
        this.omniPoly = omniPoly;
    }

    public static String[] fields = {
            "Set MIDI Resolution in PPQ",
            "Set MIDI Tempo in BPM",
            "Set MIDI Time Signature",
            "Set System Exclusive General MIDI Enable",
            "Enable Omni and Poly modes"
    };
    public static String[] descriptions = {
            "Set the MIDI sequence \"resolution\" in Pulses Per Quarter (PPQ).\n" +
            "This determines how many clock ticks there are between each quarter note, determining the precision of note timings.",

            "Set the MIDI Tempo in Beats Per Minute (BPM).\n" +
            "This determines how fast the metronome ticks, and how long or short a MIDI tick is (from PPQ).",

            "Set the MIDI Time Signature.\n" +
            "The time signature comprises of how many denominator length notes fit in a bar, specified in the numerator.\n" +
            "E.g. 4/4 = 4 quarter notes in a bar.",

            "This option adds a System Exclusive (SysEx) event to the file to enable General MIDI (GM) Mode.\n" +
            "While this may not be necessary with computers, it may be beneficial for dedicated MIDI synthesisers to use their own GM patches.",

            "Enabling Omni mode allows for use of all 16 channels concurrerntly.\n" +
            "Enabling Poly mode allows for polyphonic track support (multiple notes can be played at a time on a track).\n" +
            "This option combines both, and is enabled by default and is not advisable to disable."
    };


    public static MIDIConfig defaults() {
        return new MIDIConfig(96, 140, new int[]{4, 4}, false, true);
    }

    public static String[] timeSignatureNumerators = {"1", "2", "3", "4", "5", "6", "7", "8"};
    public static String[] timeSignatureDenominators = {"4", "8"};

    public MIDIConfig() {

    }


}
