package um.fyp;

import um.fyp.Config.EDLConfig;
import um.fyp.EDLObjects.EDL;
import um.fyp.MIDIObjects.CreateMIDI;
import um.fyp.MIDIObjects.MIDIConfig;

import javax.sound.midi.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.Math.*;

import java.text.DecimalFormat;
import java.util.List;

public class MidiToEdl {

    public static void setTrackList(File inputMidi, DefaultListModel<String> model, List<EDLConfig> configs) {
        model.removeAllElements();
        configs.removeAll(configs);
        int lastTrackNoteCount = 0;
        try {
            Sequence sequence = MidiSystem.getSequence(inputMidi);
            int trackOffset = -1;
            int trackCount = 0;
            boolean notesFound = false;
            Track[] tracks = sequence.getTracks();
            for (Track track : tracks) {
                lastTrackNoteCount = 0;
                trackCount++;
                if (!notesFound) trackOffset++;
                for (int i = 0; i < track.size(); i++) {
                    if (track.get(i).getMessage() instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) track.get(i).getMessage();
                        if (sm.getCommand() == ShortMessage.NOTE_ON) {
                            notesFound = true;
                            lastTrackNoteCount++;
                        }
                    }
                }
                if (notesFound) {
                    model.addElement("Track " + (trackCount - trackOffset) + " - " + lastTrackNoteCount + " Notes - Default settings loaded");
                    configs.add(EDLConfig.defaultsWithFile((trackCount - trackOffset)));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.removeAllElements();
            configs.removeAll(configs);
            model.addElement("Error loading file");
        }
    }

    public static void convertToEdl(File inputMidi, List<EDLConfig> configs) {
        DecimalFormat fourDigits = new DecimalFormat("0.0000");
        DecimalFormat sixDigits = new DecimalFormat("0.000000");

        try {
            long lastStartTick = 0;
            int note = 0;
            int velocity = 0;
            int idCount = 0;
            int trackCount = -1;
            boolean firstNoteFound = false;
            Sequence sequence = MidiSystem.getSequence(inputMidi);
            File file = new File("OutputTests\\EDL\\MidiConvert.txt");
            FileWriter fw = new FileWriter(file);
            fw.write(EDL.header(true));
            int tickLength = CreateMIDI.getTickLength(sequence);
            EDLConfig lastConfig = new EDLConfig();
            for (Track track : sequence.getTracks()) {
                trackCount++;
                if (firstNoteFound) {
                    lastConfig = configs.get(trackCount);
                }

                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);

                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sMessage = (ShortMessage) message;
                        if (sMessage.getCommand() == ShortMessage.NOTE_ON) {
                            if (!firstNoteFound) {
                                firstNoteFound = true;
                                trackCount = 0;
                                lastConfig = configs.get(trackCount);
                            }
                            lastStartTick = event.getTick();
                            note = sMessage.getData1();
                            velocity = sMessage.getData2();

                        }
                        else if (sMessage.getCommand() == ShortMessage.NOTE_OFF) {
                            idCount++;
                            String line = idCount +
                                    "; " + trackCount +
                                    "; " + fourDigits.format((lastStartTick*tickLength)/1000) +
                                    "; " + fourDigits.format(((event.getTick() - lastStartTick)*tickLength)/1000) +
                                    "; " + sixDigits.format((lastConfig.playRate)) +
                                    "; " + String.valueOf(false).toUpperCase() +
                                    "; " + String.valueOf(false).toUpperCase() +
                                    "; " + lastConfig.stretchMethod +
                                    "; " + String.valueOf(true).toUpperCase() +
                                    "; " + String.valueOf(false).toUpperCase() +
                                    "; " + ((lastConfig.includeVideo) ? "VIDEO" : "AUDIO") +
                                    "; " + "\"" + lastConfig.fileName + "\"" +
                                    "; " + 0 +
                                    "; " + fourDigits.format(lastConfig.streamStart*1000) +
                                    "; " + fourDigits.format(((event.getTick() - lastStartTick)*tickLength)/1000) +
                                    "; " + fourDigits.format(lastConfig.fadeTimeIn) +
                                    "; " + fourDigits.format(lastConfig.fadeTimeOut) +
                                    "; " + sixDigits.format(velocity/127f) +
                                    "; " + lastConfig.curveIn +
                                    "; " + sixDigits.format(0) +
                                    "; " + lastConfig.curveOut +
                                    "; " + sixDigits.format(0) +
                                    "; " + 0 +
                                    "; " + -1 +
                                    "; " + lastConfig.curveIn +
                                    "; " + lastConfig.curveOut +
                                    "; " + ((note+lastConfig.pitchOffset)-60) +
                                    "; " + String.valueOf(false).toUpperCase() +
                                    "; " + 0 +
                                    "; " + 0 +
                                    '\n';
                            fw.write(line);
                        }
                    }
                }


            }
            fw.close();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

}


