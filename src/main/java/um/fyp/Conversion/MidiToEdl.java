package um.fyp.Conversion;

import um.fyp.Config.EDLConfig;
import um.fyp.MIDIObjects.CreateMIDI;
import um.fyp.MIDIObjects.Note;

import javax.sound.midi.*;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MidiToEdl {

    public static boolean setTrackList(File inputMidi, DefaultListModel<String> model, List<EDLConfig> configs, List<Integer> polyphonies) {
        model.removeAllElements();
        configs.removeAll(configs);
        polyphonies.removeAll(polyphonies);
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
                int polyphony = 0;
                int maxPolyphony = 0;
                if (!notesFound) trackOffset++;
                for (int i = 0; i < track.size(); i++) {
                    if (track.get(i).getMessage() instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) track.get(i).getMessage();
                        if (sm.getCommand() == ShortMessage.NOTE_ON) {
                            if (sm.getData2() == 0) {
                                --polyphony;
                            }
                            else {
                                notesFound = true;
                                lastTrackNoteCount++;
                                maxPolyphony = Math.max(maxPolyphony, ++polyphony);
                            }
                        }
                        else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                            --polyphony;
                        }
                    }
                }
                if (notesFound) {
                    model.addElement("Track " + (trackCount - trackOffset) + " - " + lastTrackNoteCount + " Notes - Max Polyphony: " + maxPolyphony + " - Default settings loaded");
                    Thread.sleep(3);
                    configs.add(EDLConfig.defaultsWithFile((trackCount - trackOffset)));
                    polyphonies.add(maxPolyphony);

                }
            }
            Thread.sleep(10);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.removeAllElements();
            configs.removeAll(configs);
            model.addElement("Error loading file");
            return false;
        }
    }

    public static void convertToEdl(File inputMidi, List<EDLConfig> configs,List<Integer> polys, File outputFile, JLabel status) {
        DecimalFormat fourDigits = new DecimalFormat("0.0000");
        DecimalFormat sixDigits = new DecimalFormat("0.000000");
        status.setText("Conversion started");
        status.repaint();

        try {
            int idCount = 0;
            int trackCount = -1;
            int alternateTrackOffset = 0;
            int trackOffset = 0;
            Note[] notes = new Note[1];
            ArrayList<ArrayList<String>> audioTrackEdl = new ArrayList<>();
            ArrayList<ArrayList<String>> videoTrackEdl = new ArrayList<>();
            ArrayList<Long> videoMarkers = new ArrayList<>();
            int edlVideoTrackCount = 1;
            int edlAudioTrackCount = 0;
            boolean firstNoteFound = false;
            Sequence sequence = MidiSystem.getSequence(inputMidi);
            FileWriter fw = new FileWriter(outputFile);
            fw.write(EDLConfig.edlHeader(true));
            int tickLength = CreateMIDI.getTickLength(sequence);
            EDLConfig lastConfig = new EDLConfig();
            Track[] tracks = sequence.getTracks();
            for (Track track : tracks) {
                trackCount++;

                if (firstNoteFound) {
                    status.setText("Processing track " + (trackCount+1) + "/" + (tracks.length-(trackOffset)));
                    status.repaint();
                    lastConfig = configs.get(trackCount);
                    notes = new Note[polys.get(trackCount)];
                    audioTrackEdl = new ArrayList<>();
                    videoTrackEdl = new ArrayList<>();
                    for (int j = 0; j < notes.length; j++) {
                        audioTrackEdl.add(new ArrayList<>());
                    }
                    if (lastConfig.includeVideo) {
                        videoTrackEdl.add(new ArrayList<>());
                        if (lastConfig.alternateTracks) {
                            videoTrackEdl.add(new ArrayList<>());
                        }
                    }

                }
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);

                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sMessage = (ShortMessage) message;
                        if (sMessage.getCommand() == ShortMessage.NOTE_OFF || sMessage.getData2() == 0) {
                            Note offNote = new Note(sMessage.getData1(), sMessage.getData2(), event.getTick());
                            edlLoop:
                            for (int j = 0; j < notes.length; j++) {
                                if (notes[j] != null) {
                                    if (notes[j].note == offNote.note) {
                                        //idCount++;
                                        String audioLine =
                                                "; " + (edlAudioTrackCount+j) +
                                                        "; " + fourDigits.format((notes[j].startTick*tickLength)/1000) +
                                                        "; " + fourDigits.format(((event.getTick() - notes[j].startTick)*tickLength)/1000) +
                                                        "; " + sixDigits.format((lastConfig.playRate)) +
                                                        "; " + String.valueOf(false).toUpperCase() +
                                                        "; " + String.valueOf(false).toUpperCase() +
                                                        "; " + lastConfig.stretchMethod +
                                                        "; " + String.valueOf(true).toUpperCase() +
                                                        "; " + String.valueOf(false).toUpperCase() +
                                                        "; " + "AUDIO" +
                                                        "; " + "\"" + lastConfig.fileName + "\"" +
                                                        "; " + 0 +
                                                        "; " + fourDigits.format(lastConfig.streamStart*1000/lastConfig.playRate) +
                                                        "; " + fourDigits.format(((event.getTick() - notes[j].startTick)*tickLength)/1000) +
                                                        "; " + fourDigits.format(lastConfig.fadeTimeIn*1000) +
                                                        "; " + fourDigits.format(lastConfig.fadeTimeOut*1000) +
                                                        "; " + sixDigits.format(notes[j].velocity/127f) +
                                                        "; " + lastConfig.curveIn +
                                                        "; " + sixDigits.format(0) +
                                                        "; " + lastConfig.curveOut +
                                                        "; " + sixDigits.format(0) +
                                                        "; " + 0 +
                                                        "; " + -1 +
                                                        "; " + lastConfig.curveIn +
                                                        "; " + lastConfig.curveOut +
                                                        "; " + ((notes[j].note+lastConfig.pitchOffset)-60) +
                                                        "; " + String.valueOf(false).toUpperCase() +
                                                        "; " + 0 +
                                                        "; " + 0 +
                                                        '\n';
                                        audioTrackEdl.get(j).add(audioLine);

                                        if (lastConfig.includeVideo) {
                                            if (videoMarkers.contains(notes[j].startTick)) {
                                                String videoLine =
                                                        "; " + (lastConfig.alternateTracks ? (edlVideoTrackCount+alternateTrackOffset) : edlVideoTrackCount) +
                                                                "; " + fourDigits.format((notes[j].startTick*tickLength)/1000) +
                                                                "; " + fourDigits.format(((event.getTick() - notes[j].startTick)*tickLength)/1000) +
                                                                "; " + sixDigits.format((lastConfig.playRate)) +
                                                                "; " + String.valueOf(false).toUpperCase() +
                                                                "; " + String.valueOf(false).toUpperCase() +
                                                                "; " + 0 +
                                                                "; " + String.valueOf(true).toUpperCase() +
                                                                "; " + String.valueOf(false).toUpperCase() +
                                                                "; " + "VIDEO" +
                                                                "; " + "\"" + lastConfig.fileName + "\"" +
                                                                "; " + 0 +
                                                                "; " + fourDigits.format(lastConfig.streamStart*1000/lastConfig.playRate) +
                                                                "; " + fourDigits.format(((event.getTick() - notes[j].startTick)*tickLength)/1000) +
                                                                "; " + fourDigits.format(lastConfig.fadeTimeIn*1000) +
                                                                "; " + fourDigits.format(lastConfig.fadeTimeOut*1000) +
                                                                "; " + 1 +
                                                                "; " + lastConfig.curveIn +
                                                                "; " + sixDigits.format(0) +
                                                                "; " + lastConfig.curveOut +
                                                                "; " + sixDigits.format(0) +
                                                                "; " + 0 +
                                                                "; " + -1 +
                                                                "; " + lastConfig.curveIn +
                                                                "; " + lastConfig.curveOut +
                                                                "; " + 0 +
                                                                "; " + String.valueOf(false).toUpperCase() +
                                                                "; " + 0 +
                                                                "; " + 0 +
                                                                '\n';
                                                videoMarkers.remove(notes[j].startTick);
                                                if (lastConfig.alternateTracks) {
                                                    videoTrackEdl.get(alternateTrackOffset).add(videoLine);

                                                }
                                                else {
                                                    videoTrackEdl.get(0).add(videoLine);
                                                }
                                                ++alternateTrackOffset;
                                                alternateTrackOffset%=2;
                                            }

                                        }
                                        notes[j] = null;
                                        break edlLoop;
                                    }
                                }

                            }



                        }
                        else if (sMessage.getCommand() == ShortMessage.NOTE_ON) {
                            if (sMessage.getData2() !=0) {
                                if (!firstNoteFound) {
                                    firstNoteFound = true;
                                    trackOffset = trackCount;
                                    trackCount = 0;
                                    status.setText("Processing track " + (trackCount+1) + "/" + (tracks.length-(trackOffset)));
                                    status.repaint();
                                    lastConfig = configs.get(trackCount);
                                    notes = new Note[polys.get(trackCount)];
                                    audioTrackEdl = new ArrayList<>();
                                    for (int j = 0; j < notes.length; j++) {
                                        audioTrackEdl.add(new ArrayList<>());
                                    }
                                    if (lastConfig.includeVideo) {
                                        videoTrackEdl.add(new ArrayList<>());
                                        if (lastConfig.alternateTracks) {
                                            videoTrackEdl.add(new ArrayList<>());
                                        }
                                    }
                                }
                                noteloop:
                                for (int j = 0; j < notes.length; j++) {
                                    if (notes[j] == null) {
                                        notes[j] = new Note(sMessage.getData1(), sMessage.getData2(), event.getTick());
                                        if (!videoMarkers.contains(notes[j].startTick)) {
                                            videoMarkers.add(notes[j].startTick);
                                        }
                                        break noteloop;
                                    }

                                }


                            }

                        }

                    }
                }
                if (firstNoteFound) {
                    edlAudioTrackCount+=notes.length;
                    if (lastConfig.includeVideo) {
                        edlVideoTrackCount++;
                        if (lastConfig.alternateTracks) {
                            edlVideoTrackCount++;
                        }
                    }
                    for (int i = 0; i < videoTrackEdl.size(); i++) {
                        for (int j = 0; j < videoTrackEdl.get(i).size(); j++) {
                            fw.write(++idCount + videoTrackEdl.get(i).get(j));
                        }
                    }

                    for (int i = 0; i < audioTrackEdl.size(); i++) {
                        for (int j = 0; j < audioTrackEdl.get(i).size(); j++) {
                            fw.write(++idCount + audioTrackEdl.get(i).get(j));
                        }
                    }
                }


            }
            fw.close();
            sequence = null;
            status.setText("Conversion successful!");
            status.repaint();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

}


