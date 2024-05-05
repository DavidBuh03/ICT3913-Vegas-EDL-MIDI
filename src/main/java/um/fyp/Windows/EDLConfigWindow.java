package um.fyp.Windows;

import um.fyp.Config.EDLConfig;
import um.fyp.GUIHelper.Window;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;
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
    JLabel errorName;
    JButton save;
    List<JPanel> textFieldPairs;
    public static Color defaultColor = new Color(51, 51, 51);
    int index;
    EDLConfig currentConfig;

    EDLConfig lastDefault;
    File sourceFile;
    public static String errorMessage = "";
    ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            defaultsStatus.setText("Settings customised");
        }
    };

    public EDLConfigWindow(EDLConfig config) {

        super(480, 530, "Edit track", true, false);
        if (config == null) {
            setElements(EDLConfig.defaultsWithFile(0));
            index = -1;
        }
        else {
            setElements(config);
            index = config.track-1;
        }
        addActionListenersToAllComponents();

    }

    public void setElements(EDLConfig c) {
        currentConfig = c;
        if (c.track != 0) trackName.setText("Editing track " + c.track);
        else trackName.setText("Editing all tracks");
        defaultsStatus.setText(c.edited ? "Custom settings loaded" : "Default settings loaded");
        includeVideo.setSelected(c.includeVideo);
        alternateTracks.setSelected(c.alternateTracks);
        int realoffset = c.pitchOffset*(-1);
        octave.setSelectedIndex((int)(2+floor(((double)realoffset/12))));
        if (realoffset<0) note.setSelectedIndex(12-Math.abs(realoffset%12));
        else note.setSelectedIndex(Math.abs(realoffset%12));
        if (c.fileName != null) {
            sourceFile = new File(c.fileName);
            path.setText(getName(c.fileName));
        }
        ((JTextField)textFieldPairs.get(0).getComponent(1)).setText(String.valueOf(c.playRate));
        ((JTextField)textFieldPairs.get(1).getComponent(1)).setText(String.valueOf(c.streamStart));
        ((JTextField)textFieldPairs.get(2).getComponent(1)).setText(String.valueOf(c.fadeTimeIn));
        ((JTextField)textFieldPairs.get(3).getComponent(1)).setText(String.valueOf(c.fadeTimeOut));
        ((JComboBox<?>)textFieldPairs.get(4).getComponent(1)).setSelectedIndex(stretchMethodIndexes.indexOf(c.stretchMethod));
        ((JComboBox<?>)textFieldPairs.get(5).getComponent(1)).setSelectedIndex(fadeInIndexes.indexOf(c.curveIn));
        ((JComboBox<?>)textFieldPairs.get(6).getComponent(1)).setSelectedIndex(fadeOutIndexes.indexOf(c.curveOut));

    }

    public void save() {
        EDLConfig config = new EDLConfig();
        config.edited = false;
        config.track = currentConfig.track;
        config.includeVideo = includeVideo.isSelected();
        config.alternateTracks = alternateTracks.isSelected();
        config.pitchOffset = ((12*octave.getSelectedIndex()) - 24) + note.getSelectedIndex();
        config.pitchOffset*=(-1);
        config.fileName = sourceFile.getAbsolutePath();
        config.playRate = Double.parseDouble(((JTextField)textFieldPairs.get(0).getComponent(1)).getText());
        config.streamStart = Double.parseDouble(((JTextField)textFieldPairs.get(1).getComponent(1)).getText());
        config.fadeTimeIn =  Double.parseDouble(((JTextField)textFieldPairs.get(2).getComponent(1)).getText());
        config.fadeTimeOut = Double.parseDouble(((JTextField)textFieldPairs.get(3).getComponent(1)).getText());
        config.stretchMethod = stretchMethodIndexes.get(((JComboBox<?>)textFieldPairs.get(4).getComponent(1)).getSelectedIndex());
        config.curveIn = fadeInIndexes.get(((JComboBox<?>)textFieldPairs.get(5).getComponent(1)).getSelectedIndex());
        config.curveOut = fadeOutIndexes.get(((JComboBox<?>)textFieldPairs.get(6).getComponent(1)).getSelectedIndex());


        config.edited = !defaultsStatus.getText().equals("Default settings loaded");

        if (config.track == 0) {
            for (int i = 0; i < MainWindow.configs.size(); i++) {
                config.track = i+1;
                MainWindow.configs.set(i, (EDLConfig) config.clone());
                if (config.edited) {
                    if (MainWindow.fileInfo.getElementAt((i)).contains("Default")) {
                        MainWindow.fileInfo.set((i), MainWindow.fileInfo.getElementAt((i)).replace("Default settings loaded", "Settings customised"));
                    }
                }
                else {
                    if (MainWindow.fileInfo.getElementAt((i)).contains("customised")) {
                        MainWindow.fileInfo.set((i), MainWindow.fileInfo.getElementAt((i)).replace("Settings customised", "Default settings loaded"));
                    }
                }
            }
        }
        else {
            MainWindow.configs.set((currentConfig.track-1), config);

            if (config.edited) {
                if (MainWindow.fileInfo.getElementAt((currentConfig.track-1)).contains("Default")) {
                    MainWindow.fileInfo.set((currentConfig.track-1), MainWindow.fileInfo.getElementAt((currentConfig.track-1)).replace("Default settings loaded", "Settings customised"));
                }
            }
            else {
                if (MainWindow.fileInfo.getElementAt((currentConfig.track-1)).contains("customised")) {
                    MainWindow.fileInfo.set((currentConfig.track-1), MainWindow.fileInfo.getElementAt((currentConfig.track-1)).replace("Settings customised", "Default settings loaded"));
                }
            }
        }

    }

    public void addActionListenersToAllComponents() {
        includeVideo.addActionListener(listener);
        alternateTracks.addActionListener(listener);
        octave.addActionListener(listener);
        note.addActionListener(listener);
        for (int i = 0; i < textFieldPairs.size(); i++) {
            if (textFieldPairs.get(i).getComponent(1) instanceof JTextField) {
                int finalI = i;
                textFieldPairs.get(i).getComponent(1).addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        defaultsStatus.setText("Settings customised");
                        if (isValidNumber(((JTextField)(textFieldPairs.get(finalI).getComponent(1))).getText())) {
                            textFieldPairs.get(finalI).getComponent(0).setForeground(defaultColor);
                            errorName.setVisible(false);
                            save.setEnabled(true);
                        }
                        else {
                            textFieldPairs.get(finalI).getComponent(0).setForeground(Color.RED);
                            errorName.setVisible(true);
                            errorName.setText(errorMessage);
                            save.setEnabled(false);
                        }
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {


                    }
                    @Override
                    public void keyReleased(KeyEvent e) {
                        defaultsStatus.setText("Settings customised");
                        if (isValidNumber(((JTextField)(textFieldPairs.get(finalI).getComponent(1))).getText())) {
                            textFieldPairs.get(finalI).getComponent(0).setForeground(defaultColor);
                            errorName.setVisible(false);
                            save.setEnabled(true);
                        }
                        else {
                            textFieldPairs.get(finalI).getComponent(0).setForeground(Color.RED);
                            errorName.setVisible(true);
                            errorName.setText(errorMessage);
                            save.setEnabled(false);
                        }
                    }
                });
            }
            else if (textFieldPairs.get(i).getComponent(1) instanceof JComboBox<?>) {
                ((JComboBox<?>) textFieldPairs.get(i).getComponent(1)).addActionListener(listener);
            }
        }
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
            chooser.setFileFilter(mediaFilter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                path.setText(chooser.getSelectedFile().getName());
                sourceFile = chooser.getSelectedFile();
                defaultsStatus.setText("Settings customised");
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

        JPanel error = new JPanel();
        error.setLayout(new BorderLayout());
        errorName = new JLabel("Error:");
        errorName.setFont(textFont);
        errorName.setForeground(Color.RED);
        errorName.setVisible(false);
        error.add(errorName);
        JPanel blank = new JPanel();
        middlePanel.add(blank);
        middlePanel.add(error);



        add(middlePanel, BorderLayout.CENTER, false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));

        JButton cancel = new JButton("Cancel");
        cancel.setFont(buttonFont);
        JButton loadDefaults = new JButton("Load Defaults");
        loadDefaults.setFont(buttonFont);
        save = new JButton("Save");
        save.setFont(buttonFont);

        cancel.addActionListener(e -> {
            getFrame().dispose();
        });

        loadDefaults.addActionListener(e -> {
            lastDefault = EDLConfig.defaultsWithFile(currentConfig.track);
            setElements(lastDefault);

            //setElements wouldn't do the thing so i did it here
            defaultsStatus.setText("Default settings loaded");
            for (int i = 0; i < 4; i++) {
                textFieldPairs.get(i).getComponent(0).setForeground(defaultColor);
            }
            errorName.setVisible(false);
            save.setEnabled(true);
        });

        save.addActionListener(e -> {
            save();
            getFrame().dispose();
        });


        bottomPanel.add(cancel);
        bottomPanel.add(loadDefaults);
        bottomPanel.add(save);

        add(bottomPanel, BorderLayout.SOUTH, true);
    }

    public static boolean isValidNumber(String number) {
        if (number.isEmpty()) {
            errorMessage = "Error: no fields can be left blank";
            return false;
        }
        else {
            try {
                Double.parseDouble(number);
                return true;
            } catch (Exception e) {
                errorMessage = "Error: no fields can contain an invalid number";

                return false;
            }

        }
    }
}
