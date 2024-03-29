package um.fyp;

import um.fyp.Config.EDLConfig;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.*;
import java.io.File;
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

}


