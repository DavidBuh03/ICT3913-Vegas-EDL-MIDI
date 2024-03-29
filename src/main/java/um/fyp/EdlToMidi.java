package um.fyp;

import um.fyp.Config.EDLConfig;
import um.fyp.EDLObjects.EDL;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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


}
//List<ArrayList<EDL>> tracks = new ArrayList<ArrayList<EDL>>();