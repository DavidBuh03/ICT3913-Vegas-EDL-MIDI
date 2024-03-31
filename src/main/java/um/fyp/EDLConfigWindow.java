package um.fyp;

import um.fyp.Config.EDLConfig;
import um.fyp.GUIHelper.Window;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.floor;
import static um.fyp.Config.EDLConfig.*;

public class EDLConfigWindow extends Window {
    JLabel trackName;
    JLabel defaultsStatus;
    JCheckBox includeVideo;
    JCheckBox alternateTracks;
    JComboBox<String> octave;
    JComboBox<String> note;
    JLabel path;
    List<JPanel> textFieldPairs;
    int index;


    public EDLConfigWindow(EDLConfig config) {

        super(480, 530, "Edit track", true, false);
        uiElements();
        initSize(480, 530, true);
        setElements(config);
        index = config.track-1;
    }

    public void setElements(EDLConfig c) {
        if (c.track != 0) trackName.setText("Editing track " + c.track);
        defaultsStatus.setText(c.edited ? "Custom settings loaded" : "Default settings loaded");
        includeVideo.setSelected(c.includeVideo);
        alternateTracks.setSelected(c.alternateTracks);
        octave.setSelectedIndex((int)(2-floor((c.pitchOffset/12))));
        note.setSelectedIndex(c.pitchOffset%12);
        if (c.fileName != null) path.setText(getName(c.fileName));
        ((JTextField)textFieldPairs.get(0).getComponent(1)).setText(String.valueOf(c.playRate));
        ((JTextField)textFieldPairs.get(1).getComponent(1)).setText(String.valueOf(c.streamStart));
        ((JTextField)textFieldPairs.get(2).getComponent(1)).setText(String.valueOf(c.fadeTimeIn));
        ((JTextField)textFieldPairs.get(3).getComponent(1)).setText(String.valueOf(c.fadeTimeOut));
        ((JComboBox<?>)textFieldPairs.get(4).getComponent(1)).setSelectedIndex(stretchMethodIndexes.indexOf(c.stretchMethod));
        ((JComboBox<?>)textFieldPairs.get(5).getComponent(1)).setSelectedIndex(fadeInIndexes.indexOf(c.curveIn));
        ((JComboBox<?>)textFieldPairs.get(6).getComponent(1)).setSelectedIndex(fadeOutIndexes.indexOf(c.curveOut));

    }


    @Override
    public void menuBarItems(JMenuBar bar) {
        //empty because there is no menu bar in this window
    }



    @Override
    public void uiElements() {
        Font textFont = new Font("Arial", Font.PLAIN, 23);
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setAlignmentY(-30);
        JPanel status = new JPanel();
        status.setLayout(new GridLayout(2, 1, 0, 10));
        trackName = new JLabel("", SwingConstants.CENTER);
        trackName.setFont(textFont);
        defaultsStatus = new JLabel("", SwingConstants.CENTER);
        defaultsStatus.setFont(textFont);
        status.add(trackName);
        status.add(defaultsStatus);

        JPanel edlSpecific = new JPanel();
        edlSpecific.setLayout(new GridLayout(3, 1, 0, 8));
        edlSpecific.setBorder(new TitledBorder("EDL-specific settings"));
        JPanel checkboxesPanel = new JPanel();
        checkboxesPanel.setLayout(new GridLayout(1, 2));
        includeVideo = new JCheckBox("Include Video");

        alternateTracks = new JCheckBox("Alternate Tracks");

        checkboxesPanel.add(includeVideo);
        checkboxesPanel.add(alternateTracks);


        JPanel alignMidiPanel = new JPanel();
        JLabel assign = new JLabel("Assign Pitch value 0 to:");
        alignMidiPanel.setLayout(new GridLayout(1, 2));
        octave = new JComboBox<String>(EDLConfig.octaves);

        note = new JComboBox<String>(EDLConfig.notes);


        alignMidiPanel.add(assign);
        alignMidiPanel.add(octave);
        alignMidiPanel.add(note);

        edlSpecific.add(checkboxesPanel);
        edlSpecific.add(alignMidiPanel);

        JPanel filePicker = new JPanel();
        filePicker.setLayout(new BorderLayout());
        JLabel media = new JLabel("Source Media file: ");
        path = new JLabel("");
        JButton browse = new JButton("Browse file");

        browse.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter mediaFilter = new FileNameExtensionFilter("Media Files", "wav", "flac", "mp3", "ogg", "mp4", "avi", "mov");
            FileNameExtensionFilter vegFilter = new FileNameExtensionFilter("Veg offsets", "veg");
            chooser.addChoosableFileFilter(mediaFilter);
            chooser.addChoosableFileFilter(vegFilter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                path.setText(chooser.getSelectedFile().getName());
            }
        });

        filePicker.add(media, BorderLayout.WEST);
        filePicker.add(path, BorderLayout.CENTER);
        filePicker.add(browse, BorderLayout.EAST);



        edlSpecific.add(filePicker);

        topPanel.add(status, BorderLayout.CENTER);
        topPanel.add(edlSpecific, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH, false);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(9, 1, 0, 5));
        middlePanel.setBorder(new TitledBorder("Event parameters"));

        textFieldPairs = new ArrayList<JPanel>();
        for (int i = 0; i < guiParameters.length; i++) {

            JPanel textFieldpair = new JPanel();
            textFieldpair.setLayout(new BorderLayout());

            JLabel fieldName = new JLabel(guiParameters[i]);
            fieldName.setPreferredSize(new Dimension(190, 0));
            textFieldpair.add(fieldName, BorderLayout.WEST);

            if (i<4) {
                JTextField fieldInput = new JTextField();
                textFieldpair.add(fieldInput, BorderLayout.CENTER);

            } else {
                JComboBox<String> fieldInput = new JComboBox<String>(numericComboBoxes[i-4]);
                textFieldpair.add(fieldInput, BorderLayout.CENTER);

            }



            JButton info = new JButton("Info");
            textFieldpair.add(info, BorderLayout.EAST);

            int finalI = i;
            info.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, EDLConfig.infoMessages[finalI], "Parameter Info", JOptionPane.INFORMATION_MESSAGE);
            });

            textFieldPairs.add(textFieldpair);
            middlePanel.add(textFieldpair);
        }

        add(middlePanel, BorderLayout.CENTER, false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));

        JButton cancel = new JButton("Cancel");
        cancel.setFont(buttonFont);
        JButton loadDefaults = new JButton("Load Defaults");
        loadDefaults.setFont(buttonFont);
        JButton save = new JButton("Save");
        save.setFont(buttonFont);

        cancel.addActionListener(e -> {
            getFrame().dispose();
        });

        loadDefaults.addActionListener(e -> {
            setElements(EDLConfig.defaultsNoFile());
        });


        bottomPanel.add(cancel);
        bottomPanel.add(loadDefaults);
        bottomPanel.add(save);

        add(bottomPanel, BorderLayout.SOUTH, true);
    }
}
