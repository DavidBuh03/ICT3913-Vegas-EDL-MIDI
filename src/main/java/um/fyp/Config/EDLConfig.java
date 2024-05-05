package um.fyp.Config;
import javax.swing.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class EDLConfig implements Cloneable {

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
    //THIS METHOD IS SLOW - DO NOT USE
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
    //USE THIS METHOD INSTEAD
    public static EDLConfig lineToEDL(String[] line) {
        return new EDLConfig(
                Boolean.parseBoolean(line[0]),
                Integer.parseInt(line[1]),
                Boolean.parseBoolean(line[2]),
                Boolean.parseBoolean(line[3]),
                Integer.parseInt(line[4]),
                line[5],
                Double.parseDouble(line[6]),
                Double.parseDouble(line[7]),
                Double.parseDouble(line[8]),
                Double.parseDouble(line[9]),
                Integer.parseInt(line[10]),
                Integer.parseInt(line[11]),
                Integer.parseInt(line[12])
        );
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

    public static String edlHeader(boolean lineBreak) {
        if (lineBreak) return "\"ID\";\"Track\";\"StartTime\";\"Length\";\"PlayRate\";\"Locked\";\"Normalized\";\"StretchMethod\";\"Looped\";\"OnRuler\";\"MediaType\";\"FileName\";\"Stream\";\"StreamStart\";\"StreamLength\";\"FadeTimeIn\";\"FadeTimeOut\";\"SustainGain\";\"CurveIn\";\"GainIn\";\"CurveOut\";\"GainOut\";\"Layer\";\"Color\";\"CurveInR\";\"CurveOutR\";\"PlayPitch\";\"LockPitch\";\"FirstChannel\";\"Channels\"\n";
        else return "\"ID\";\"Track\";\"StartTime\";\"Length\";\"PlayRate\";\"Locked\";\"Normalized\";\"StretchMethod\";\"Looped\";\"OnRuler\";\"MediaType\";\"FileName\";\"Stream\";\"StreamStart\";\"StreamLength\";\"FadeTimeIn\";\"FadeTimeOut\";\"SustainGain\";\"CurveIn\";\"GainIn\";\"CurveOut\";\"GainOut\";\"Layer\";\"Color\";\"CurveInR\";\"CurveOutR\";\"PlayPitch\";\"LockPitch\";\"FirstChannel\";\"Channels\"";
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

    public static EDLConfig defaultsNoFile(int track) {
        EDLConfig defaults = new EDLConfig();
        defaults.edited = false;
        defaults.track = track;
        defaults.includeVideo = true;
        defaults.alternateTracks = false;
        defaults.pitchOffset = 0;
        defaults.playRate = 1;
        defaults.streamStart = 2 + rand.nextDouble(120);
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
        defaults.alternateTracks = true;
        defaults.pitchOffset = 0;
        defaults.fileName = "D:\\users ssd\\Downloads\\middle c.mp4";
        defaults.playRate = 1;
        defaults.streamStart = 0;
        defaults.fadeTimeIn = 0.01;
        defaults.fadeTimeOut = 0.01;
        defaults.stretchMethod = 2;
        defaults.curveIn = 2;
        defaults.curveOut = -2;

        return defaults;
    }

    public static boolean saveConfigToFile(List<EDLConfig> configs, File outputFile) {
        try {
            FileWriter fw = new FileWriter(outputFile);
            fw.write(header());
            int count = 0;
            for(EDLConfig conf : configs) {
                count++;
                EDLConfig auxConfig = (EDLConfig) conf.clone();
                auxConfig.track = count;
                fw.write(toString(auxConfig));
            }
            fw.close();
            return true;



        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public static boolean loadConfigFromFile(File loadedConfig, DefaultListModel<String> fileInfo, List<EDLConfig> configs) {
        String row;
        int counter = -1;
        try (BufferedReader br = new BufferedReader(new FileReader(loadedConfig))) {
            br.readLine();

            while ((row = br.readLine()) != null) {
                counter++;
                String[] rowSplit = row.split("; ");
                if (Boolean.parseBoolean(rowSplit[0])) {
                    if (fileInfo.getElementAt((counter)).contains("Default")) {
                        fileInfo.set((counter), fileInfo.getElementAt((counter)).replace("Default settings loaded", "Settings customised"));
                    }
                }
                else {
                    if (fileInfo.getElementAt((counter)).contains("customised")) {
                        fileInfo.set((counter), fileInfo.getElementAt((counter)).replace("Settings customised", "Default settings loaded"));
                    }
                }
                configs.set(counter, lineToEDL(rowSplit));
                
            }
            return true;



        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cnse) {
            System.out.println("Clone not supported");
        }
        return null;

    }

    public EDLConfig() {
    }
}
