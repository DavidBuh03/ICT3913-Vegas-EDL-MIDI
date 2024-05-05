package um.fyptest.Obsolete;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class EDL {
    public int id; //to be filled in by counter / lost when converting to MIDI
    public int track; //audio track number to be translated to MIDI, video track number lost
    public double startTime; //to be translated to MIDI ticks
    public double length; //to be translated to note event pairs
    public double playRate; //to be filled in manually / lost when converting to MIDI
    public boolean locked; //always false
    public boolean normalized; //always false
    public int stretchMethod; //to be filled in manually / lost when converting to MIDI
    public boolean looped; //always true
    public boolean onRuler; //always false
    public boolean mediaType; //configurable by checkbox
    public String fileName; //to be assigned manually / lost when converting to MIDI
    public int stream; //always 0
    public double streamStart; //to be filled in manually / lost when converting to MIDI
    public double streamLength; //same as length
    public double fadeTimeIn; //to be filled in manually / lost when converting to MIDI
    public double fadeTimeOut; //to be filled in manually / lost when converting to MIDI
    public double sustainGain; //scaled to velocity
    public int curveIn; //to be filled in manually / lost when converting to MIDI / different defaults for audio and video
    public double gainIn; //always 0
    public int curveOut; //to be filled in manually / lost when converting to MIDI / different defaults for audio and video
    public double gainOut; //always 0
    public int layer; //always 0
    public int color; //always -1
    public int curveInR; //same as curveIn
    public int curveOutR; //same as curveOut
    public double playPitch; //to be translated over from MIDI noteon values / notes translating to pitch values outside range -24 <= x <= 24 will be lost when converting to EDL
    public boolean lockPitch; //always false
    public int firstChannel; //always 0
    public int channels; //always 0
    public EDL(int id, int track, double startTime, double length, double playRate, boolean locked, boolean normalized, int stretchMethod, boolean looped, boolean onRuler, boolean mediaType, String fileName, int stream, double streamStart, double streamLength, double fadeTimeIn, double fadeTimeOut, double sustainGain, int curveIn, double gainIn, int curveOut, double gainOut, int layer, int color, int curveInR, int curveOutR, double playPitch, boolean lockPitch, int firstChannel, int channels) {
        this.id = id;
        this.track = track;
        this.startTime = startTime;
        this.length = length;
        this.playRate = playRate;
        this.locked = locked;
        this.normalized = normalized;
        this.stretchMethod = stretchMethod;
        this.looped = looped;
        this.onRuler = onRuler;
        this.mediaType = mediaType;
        this.fileName = fileName;
        this.stream = stream;
        this.streamStart = streamStart;
        this.streamLength = streamLength;
        this.fadeTimeIn = fadeTimeIn;
        this.fadeTimeOut = fadeTimeOut;
        this.sustainGain = sustainGain;
        this.curveIn = curveIn;
        this.gainIn = gainIn;
        this.curveOut = curveOut;
        this.gainOut = gainOut;
        this.layer = layer;
        this.color = color;
        this.curveInR = curveInR;
        this.curveOutR = curveOutR;
        this.playPitch = playPitch;
        this.lockPitch = lockPitch;
        this.firstChannel = firstChannel;
        this.channels = channels;
    }

    //had to implement my own csv writer and reader for this format
    public static EDL lineToEDL(String line) {
        Scanner s = new Scanner(line);
        s.useDelimiter("; ");
        EDL result = new EDL();
        result.id = s.nextInt();
        result.track = s.nextInt();
        result.startTime = s.nextDouble();
        result.length = s.nextDouble();
        result.playRate = s.nextDouble();
        result.locked = s.nextBoolean();
        result.normalized = s.nextBoolean();
        result.stretchMethod = s.nextInt();
        result.looped = s.nextBoolean();
        result.onRuler = s.nextBoolean();
        result.mediaType = ((s.next().equals("VIDEO")) ? true : false);
        result.fileName = s.next();
        result.stream = s.nextInt();
        result.streamStart = s.nextDouble();
        result.streamLength = s.nextDouble();
        result.fadeTimeIn = s.nextDouble();
        result.fadeTimeOut = s.nextDouble();
        result.sustainGain = s.nextDouble();
        result.curveIn = s.nextInt();
        result.gainIn = s.nextDouble();
        result.curveOut = s.nextInt();
        result.gainOut = s.nextDouble();
        result.layer = s.nextInt();
        result.color = s.nextInt();
        result.curveInR = s.nextInt();
        result.curveOutR = s.nextInt();
        result.playPitch = s.nextDouble();
        result.lockPitch = s.nextBoolean();
        result.firstChannel = s.nextInt();
        result.channels = s.nextInt();

        s.close();
        
        return result;
    }

    public static String toString(EDL edl) {
        DecimalFormat fourDigits = new DecimalFormat("0.0000");
        DecimalFormat sixDigits = new DecimalFormat("0.000000");

        return  edl.id +
                "; " + edl.track +
                "; " + fourDigits.format(edl.startTime) +
                "; " + fourDigits.format(edl.length) +
                "; " + sixDigits.format(edl.playRate) +
                "; " + String.valueOf(edl.locked).toUpperCase() +
                "; " + String.valueOf(edl.normalized).toUpperCase() +
                "; " + edl.stretchMethod +
                "; " + String.valueOf(edl.looped).toUpperCase() +
                "; " + String.valueOf(edl.onRuler).toUpperCase() +
                "; " + ((edl.mediaType) ? "VIDEO" : "AUDIO") +
                "; " + "\"" + edl.fileName + "\"" +
                "; " + edl.stream +
                "; " + fourDigits.format(edl.streamStart) +
                "; " + fourDigits.format(edl.streamLength) +
                "; " + fourDigits.format(edl.fadeTimeIn) +
                "; " + fourDigits.format(edl.fadeTimeOut) +
                "; " + sixDigits.format(edl.sustainGain) +
                "; " + edl.curveIn +
                "; " + sixDigits.format(edl.gainIn) +
                "; " + edl.curveOut +
                "; " + sixDigits.format(edl.gainOut) +
                "; " + edl.layer +
                "; " + edl.color +
                "; " + edl.curveInR +
                "; " + edl.curveOutR +
                "; " + sixDigits.format(edl.playPitch) +
                "; " + String.valueOf(edl.lockPitch).toUpperCase() +
                "; " + edl.firstChannel +
                "; " + edl.channels +
                '\n';
    }

    public static List<EDL> fileToEdlList(File edlFile) {
        List<EDL> events = new ArrayList<EDL>();
        try {
            Scanner read = new Scanner(edlFile);
            //skip header
            read.nextLine();
            while (read.hasNext()) {
                events.add(lineToEDL(read.nextLine()));
            }
            read.close();
            return events;


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public EDL() {
    }
}
