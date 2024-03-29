package um.fyp.Config;

import um.fyp.EDLObjects.EDL;

import java.text.DecimalFormat;
import java.util.*;

public class EDLConfig {

    public boolean edited; //added in cause i needed to discern customised from default
    public int track; //audio track number to be translated to MIDI, video track number lost
    public boolean includeVideo; //configurable by checkbox
    public boolean alternateTracks; //configurable by checkbox
    public int pitchOffset; //configurable by comboboxes
    public String fileName; //to be assigned manually / lost when converting to MIDI
    public double playRate; //to be filled in manually / lost when converting to MIDI
    public double streamStart; //to be filled in manually / lost when converting to MIDI
    public double fadeTimeIn; //to be filled in manually / lost when converting to MIDI
    public double fadeTimeOut; //to be filled in manually / lost when converting to MIDI
    public int stretchMethod; //to be filled in manually / lost when converting to MIDI
    public int curveIn; //to be filled in manually / lost when converting to MIDI / different defaults for audio and video
    public int curveOut; //to be filled in manually / lost when converting to MIDI / different defaults for audio and video

    public EDLConfig(boolean edited, int track, boolean includeVideo, boolean alternateTracks, int pitchOffset, String fileName, double playRate, double streamStart, double fadeTimeIn, double fadeTimeOut, int stretchMethod, int curveIn, int curveOut) {
        this.edited = edited;
        this.track = track;
        this.includeVideo = includeVideo;
        this.alternateTracks = alternateTracks;
        this.pitchOffset = pitchOffset;
        this.fileName = fileName;
        this.playRate = playRate;
        this.streamStart = streamStart;
        this.fadeTimeIn = fadeTimeIn;
        this.fadeTimeOut = fadeTimeOut;
        this.stretchMethod = stretchMethod;
        this.curveIn = curveIn;
        this.curveOut = curveOut;
    }

    //had to implement my own csv writer and reader for this format
    public static EDLConfig lineToEDL(String line) {
        Scanner s = new Scanner(line);
        s.useDelimiter("; ");
        EDLConfig result = new EDLConfig();
        result.edited = s.nextBoolean();
        result.track = s.nextInt();
        result.includeVideo = s.nextBoolean();
        result.alternateTracks = s.nextBoolean();
        result.pitchOffset = s.nextInt();
        result.fileName = s.next();
        result.playRate = s.nextDouble();
        result.streamStart = s.nextDouble();
        result.fadeTimeIn = s.nextDouble();
        result.fadeTimeOut = s.nextDouble();
        result.stretchMethod = s.nextInt();
        result.curveIn = s.nextInt();
        result.curveOut = s.nextInt();

        s.close();
        
        return result;
    }
    public static String toString(EDLConfig config) {
        DecimalFormat fourDigits = new DecimalFormat("0.0000");
        DecimalFormat sixDigits = new DecimalFormat("0.000000");

        return  config.edited +
                "; " + config.track +
                "; " + config.includeVideo +
                "; " + config.alternateTracks +
                "; " + config.pitchOffset +
                "; " + config.fileName +
                "; " + config.playRate +
                "; " + fourDigits.format(config.streamStart) +
                "; " + fourDigits.format(config.fadeTimeIn) +
                "; " + fourDigits.format(config.fadeTimeOut) +
                "; " + config.stretchMethod +
                "; " + config.curveIn +
                "; " + config.curveOut +
                '\n';
    }


    public static String header() {
        return "\"Edited\";\"Track\";\"IncludeVideo\";\"AlternateTracks\";\"PitchOffset\";\"FileName\";\"PlayRate\";\"StreamStart\";\"FadeTimeIn\";\"FadeTimeOut\";\"StretchMethod\";\"CurveIn\";\"CurveOut\"\n";
    }

    public static String[] guiParameters = {
            "Play Rate (speed multiplier)",
            "Stream Start timing (in seconds)",
            "Fade-In duration (in seconds)",
            "Fade-Out duration (in seconds)",
            "Stretch Method",
            "Fade-In curve type",
            "Fade-Out curve type"
    };

    public static String[] octaves = {
            "Octave 2",
            "Octave 3",
            "Octave 4 (middle)",
            "Octave 5",
            "Octave 6"
    };

    public static String[] notes = {
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };

    public static String[][] numericComboBoxes = {
            { "2 - Elastique Efficient", "0 - Classic A03"},
            {"2 - Fast Log", "1 - Linear", "-2 - Slow Log", "4 - Smooth Cubic", "-4 - Sharp Cubic"},
            {"-2 - Fast Log", "1 - Linear", "2 - Slow Log", "4 - Smooth Cubic", "-4 - Sharp Cubic"}
    };


    public static String[] infoMessages = {
            "Play Rate: Adjust the playback speed of the clip using a speed multiplier.\nE.g. 1 = 1x speed (normal). 0.5 = 0.5x speed (half speed). 2 = 2x speed (double speed)",
            "Stream Start timing: Define the number of seconds at which to start reading the file.\nE.g. 1.5 = start reading from the 1.5 second mark of the file.",
            "Fade-in duration: Define the length of the fade-in.\nE.g. 1 = Fade-in lasts 1 second.",
            "Fade-out duration: Define the length of the fade-out.\nE.g. 1 = Fade-out lasts 1 second.",
            "Stretch method: Set the time-stretching algorithm to use on a clip. 2 and 0 assigned to the EDL format.\nElastique efficient uses a frequency-domain algorithm, while Classic A03 uses a time-domain OLA algorithm.",
            "Fade-In curve type: set the curve shape of the fade-in.\nNames are assigned to mathematical functions of the same shape.",
            "Fade-Out curve type: set the curve shape of the fade-out.\nNames are assigned to mathematical functions of the same shape.",
    };
    public static Random rand = new Random();
    public static List<Integer> stretchMethodIndexes = new ArrayList<Integer>(Arrays.asList(2, 0));
    public static List<Integer> fadeInIndexes = new ArrayList<Integer>(Arrays.asList(2, 1, -2, 4, -4));
    public static List<Integer> fadeOutIndexes = new ArrayList<Integer>(Arrays.asList(-2, 1, 2, 4, -4));

    public static String getName(String filename) {
        return filename.substring(filename.lastIndexOf("\\")+1);
    }

    public static EDLConfig defaultsNoFile() {
        EDLConfig defaults = new EDLConfig();
        defaults.edited = false;
        defaults.includeVideo = true;
        defaults.alternateTracks = false;
        defaults.pitchOffset = 0;
        defaults.playRate = 1;
        defaults.streamStart = 2 + rand.nextDouble(118);
        defaults.fadeTimeIn = 0.01;
        defaults.fadeTimeOut = 0.01;
        defaults.stretchMethod = 2;
        defaults.curveIn = 2;
        defaults.curveOut = -2;

        return defaults;
    }

    public static EDLConfig defaultsWithFile(int track) {
        EDLConfig defaults = new EDLConfig();
        defaults.edited = false;
        defaults.track = track;
        defaults.includeVideo = true;
        defaults.alternateTracks = false;
        defaults.pitchOffset = 0;
        defaults.fileName = "D:\\users ssd\\Videos\\new vegas exports\\psx.wav";
        defaults.playRate = 1;
        defaults.streamStart = 2 + rand.nextDouble(118);
        defaults.fadeTimeIn = 0.01;
        defaults.fadeTimeOut = 0.01;
        defaults.stretchMethod = 2;
        defaults.curveIn = 2;
        defaults.curveOut = -2;

        return defaults;
    }
    public static EDLConfig edlToConfig(EDL edl) {
        return new EDLConfig();

    }

    public EDLConfig() {
    }
}