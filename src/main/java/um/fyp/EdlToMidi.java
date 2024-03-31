package um.fyp;

import um.fyp.Config.EDLConfig;
import um.fyp.EDLObjects.EDL;
import um.fyp.EDLObjects.TrackTimings;
import um.fyp.MIDIObjects.CreateMIDI;
import um.fyp.MIDIObjects.MIDIConfig;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EdlToMidi {
    public static void setTrackList(File inputEdl, DefaultListModel<String> model, List<EDLConfig> configs) {
        model.removeAllElements();
        configs.removeAll(configs);
        int lastTrackNumber = 0;
        int trackCounter = 0;
        int trackEventCount = 0;
        double firstTiming = 0;
        double playRate = 0;
        Scanner read;
        String row;
        String mediaType = "";
        EDL lastEvent;
        boolean trackRead = false;
        try (BufferedReader br = new BufferedReader(new FileReader(inputEdl))) {
            //skip header
            br.readLine();

            while ((row = br.readLine()) != null) {
                String[] rowSplit = row.split("; ");
                if (trackRead && Integer.parseInt(rowSplit[1])!= lastTrackNumber) {
                    trackRead = false;
                    model.addElement("Track " + trackCounter + " - " + mediaType + " - First Timing:" + (firstTiming/1000)*playRate + " seconds - " + trackEventCount + " events");
                } else if (trackRead) {
                    trackEventCount++;
                }
                if (!trackRead) {
                    trackCounter++;
                    trackEventCount = 1;
                    lastTrackNumber = Integer.parseInt(rowSplit[1]);
                    firstTiming = Double.parseDouble(rowSplit[13]);
                    playRate = Double.parseDouble(rowSplit[4]);
                    mediaType = rowSplit[10];
                    trackRead = true;
                }
            }
            model.addElement("Track " + trackCounter + " - " + mediaType + " - First Timing:" + (firstTiming/1000)*playRate + " seconds - " + trackEventCount + " events");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public static List<EDLConfig> configure(File inputEdl) {
        //JFrame frame = new JFrame();

        List<TrackTimings> allTimings = new ArrayList<>();
        List<Double> lastTrackTimings = new ArrayList<>();
        List<Double> lastPlayRate = new ArrayList<>();
        List<Double> lastFadeTimeIn = new ArrayList<>();
        List<Double> lastFadeTimeOut = new ArrayList<>();
        int lastCurveIn = 0;
        int lastCurveOut = 0;
        int lastTrackNumber = 0;
        int trackCounter = 0;
        int trackEventCount = 0;
        double firstTiming = 0;
        double playRate = 0;
        Scanner read;
        String row;
        String mediaType = "";
        EDL lastEvent;
        List<String> lastFilePaths = new ArrayList<>();
        boolean trackRead = false;
        try (BufferedReader br = new BufferedReader(new FileReader(inputEdl))) {
            //skip header
            br.readLine();

            while ((row = br.readLine()) != null) {
                String[] rowSplit = row.split("; ");
                if (trackRead && Integer.parseInt(rowSplit[1])!= lastTrackNumber) {
                    allTimings.add(new TrackTimings(mediaType, lastTrackTimings, lastPlayRate, lastFadeTimeIn, lastFadeTimeOut, lastFilePaths, lastCurveIn, lastCurveOut));
                    lastTrackTimings = new ArrayList<>();
                    lastPlayRate = new ArrayList<>();
                    lastFadeTimeIn = new ArrayList<>();
                    lastFadeTimeOut = new ArrayList<>();
                    lastFilePaths = new ArrayList<>();

                    trackRead = false;
                } else if (trackRead) {
                    trackEventCount++;

                    if (!lastTrackTimings.contains(Double.parseDouble(rowSplit[13]))
                        || !lastPlayRate.contains(Double.parseDouble(rowSplit[4]))
                        //|| !lastFadeTimeIn.contains(Double.parseDouble(rowSplit[15]))
                        //|| !lastFadeTimeOut.contains(Double.parseDouble(rowSplit[16]))
                        || !lastFilePaths.contains(rowSplit[11])
                        //|| (lastFadeTimeIn.contains(Double.parseDouble(rowSplit[15])) && lastFadeTimeIn.getLast()!=Double.parseDouble(rowSplit[15]))
                        //|| (lastFadeTimeOut.contains(Double.parseDouble(rowSplit[16])) && lastFadeTimeOut.getLast()!=Double.parseDouble(rowSplit[16]))
                    )
                    {
                        lastTrackTimings.add(Double.parseDouble(rowSplit[13]));
                        lastPlayRate.add(Double.parseDouble(rowSplit[4]));
                        lastFadeTimeIn.add(Double.parseDouble(rowSplit[15]));
                        lastFadeTimeOut.add(Double.parseDouble(rowSplit[16]));
                        lastFilePaths.add(rowSplit[11]);
                    }

                }
                if (!trackRead) {
                    trackCounter++;
                    trackEventCount = 1;
                    lastTrackNumber = Integer.parseInt(rowSplit[1]);
                    lastTrackTimings.add(Double.parseDouble(rowSplit[13]));
                    lastPlayRate.add(Double.parseDouble(rowSplit[4]));
                    lastFadeTimeIn.add(Double.parseDouble(rowSplit[15]));
                    lastFadeTimeOut.add(Double.parseDouble(rowSplit[16]));
                    lastCurveIn = Integer.parseInt(rowSplit[18]);
                    lastCurveOut = Integer.parseInt(rowSplit[20]);
                    lastFilePaths.add(rowSplit[11]);



                    playRate = Double.parseDouble(rowSplit[4]);
                    mediaType = rowSplit[10];
                    trackRead = true;
                }
            }
            allTimings.add(new TrackTimings(mediaType, lastTrackTimings, lastPlayRate, lastFadeTimeIn, lastFadeTimeOut, lastFilePaths, lastCurveIn, lastCurveOut));
            List<Integer> pairs = TrackTimings.getPairs(allTimings);
            List<EDLConfig> configList = new ArrayList<>();
            int audioTrackCounter = -1;
            for (int i = 0; i < allTimings.size(); i++) {
                if (allTimings.get(i).type.equals("AUDIO")) {
                    audioTrackCounter++;
                    for (int j = 0; j < allTimings.get(i).timings.size(); j++) {
                        //trackCount++;
                        configList.add(new EDLConfig(true,
                                                            audioTrackCounter,
                                                            pairs.contains(i),
                                                            false,
                                                            0,
                                                            allTimings.get(i).filePaths.get(j),
                                                            allTimings.get(i).playRate.get(j),
                                                            allTimings.get(i).timings.get(j),
                                                            allTimings.get(i).fadeTimeIn.get(j),
                                                            allTimings.get(i).fadeTimeOut.get(j),
                                                            2,
                                                            allTimings.get(i).curveIn,
                                                            allTimings.get(i).curveOut
                                                        ));

                    }
                }
            }



        return configList;
            //int i = 0;





        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void convertToMidi(File inputEdl, List<EDLConfig> configs, MIDIConfig midiConfig) {
        DecimalFormat fourDigits = new DecimalFormat("0.0000");
        DecimalFormat sixDigits = new DecimalFormat("0.000000");
        String row;
        Track currentTrack;

        long endTick = 0;
        try {
            int trackNum = 0;
            Sequence sequence = new Sequence(Sequence.PPQ, midiConfig.ppq);

            CreateMIDI.initialiseMIDI(sequence, midiConfig.bpm, midiConfig.timeSignature[0], midiConfig.timeSignature[1], midiConfig.sysGM, false);
            int tickLength = CreateMIDI.getTickLength(sequence);
            for (EDLConfig c : configs) {
                trackNum++;
                if (trackNum == 1) {
                    currentTrack = CreateMIDI.newTrack(sequence, "Track " + trackNum + " - " + c.fileName + ": " + c.streamStart, 0, 0, midiConfig.omniPoly);
                } else {
                    currentTrack = CreateMIDI.newTrack(sequence, "Track " + trackNum + " - " + c.fileName + ": " + c.streamStart, 0, 0, false);
                }

                try (BufferedReader br = new BufferedReader(new FileReader(inputEdl))) {
                    br.readLine();
                    while ((row = br.readLine()) != null) {

                        String[] rowSplit = row.split("; ");
                        if (rowSplit[10].equals("AUDIO")) {
                            if (       rowSplit[1].equals(String.valueOf(c.track))
                                    && rowSplit[11].equals(c.fileName)
                                    && rowSplit[4].equals(sixDigits.format(c.playRate))
                                    && rowSplit[13].equals(fourDigits.format(c.streamStart))
                                    //&& rowSplit[15].equals(fourDigits.format(c.fadeTimeIn))
                                    //&& rowSplit[16].equals(fourDigits.format(c.fadeTimeOut))
                                    && rowSplit[7].equals(String.valueOf(c.stretchMethod))
                                //&& rowSplit[18].equals(String.valueOf(c.curveIn))
                                //&& rowSplit[20].equals(String.valueOf(c.curveOut))
                            )
                            {
                                endTick = Math.max(endTick,
                                        CreateMIDI.addNote(currentTrack,
                                                tickLength,
                                                (int)(Double.parseDouble((rowSplit[26]))) + 60,
                                                (Double.parseDouble(rowSplit[2])),
                                                (Double.parseDouble(rowSplit[3])),
                                                (Double.parseDouble(rowSplit[17])*100)
                                        )
                                );
                            }
                        }
                    }
                    //endTrack

                }
                for (Track track : sequence.getTracks()) {
                    CreateMIDI.endTrack(track, endTick);
                }
                CreateMIDI.writeMIDIToFile(sequence, "OutputTests\\MIDI\\TheKantapapa Original Test.mid");

            }



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }








        try (BufferedReader br = new BufferedReader(new FileReader(inputEdl))) {
            br.readLine();

            //while ()



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



}
//List<ArrayList<EDL>> tracks = new ArrayList<ArrayList<EDL>>();