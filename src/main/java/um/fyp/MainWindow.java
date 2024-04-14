package um.fyp;
import um.fyp.EDLObjects.Analysis;
import um.fyp.EDLObjects.TrackTimings;
import um.fyp.GUIHelper.Window;
import um.fyp.Config.EDLConfig;
import um.fyp.MIDIObjects.MIDIConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class MainWindow extends Window {
    Random random = new Random();

    Thread midiToEdlThread = new Thread(new Runnable() {
        @Override
        public void run() {
            MidiToEdl.convertToEdl(loadedMIDI, configs, polyphonies, outputFile, ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)));
        }
    });

    File loadedMIDI;
    File loadedEDL;
    File outputFile;
    File loadConfig;

    JFileChooser chooser;

    JList<String> componentList;
    JButton edit;
    JButton convert;
    JButton randomise;

    public static DefaultListModel<String> fileInfo;
    public static List<EDLConfig> configs = new ArrayList<>();
    public List<Integer> polyphonies = new ArrayList<>();

    public static MIDIConfig midiConfig = new MIDIConfig();

    int[] trackInfo;
    int selectedIndex;
    boolean midiLoaded;
    boolean edlLoaded;

    public MainWindow() {
        super(560, 560, "Vegas EDL MIDI Converter", true, true);
    }

    @Override
    public void menuBarItems(JMenuBar bar) {
        JMenu file = new JMenu("File");
        JMenuItem importMidi = new JMenuItem("Import MIDI");
        JMenuItem importEdl = new JMenuItem("Import Vegas EDL");

        JMenuItem saveToFile = new JMenuItem("Save config to file");
        JMenuItem loadFromFile = new JMenuItem("Load config from file");

        JMenuItem quit = new JMenuItem("Exit");
        file.add(importMidi);
        file.add(importEdl);
        file.addSeparator();
        file.add(saveToFile);
        file.add(loadFromFile);
        file.addSeparator();
        file.add(quit);
        quit.addActionListener(e -> {
            System.exit(0);
        });

        importMidi.addActionListener(e -> {
            chooser = new JFileChooser();
            FileNameExtensionFilter midiFilter = new FileNameExtensionFilter("MIDI Files", "mid");
            chooser.setFileFilter(midiFilter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                loadedMIDI = new File(chooser.getSelectedFile().getAbsolutePath());
                loadedEDL = null;
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("Loading MIDI...");
                 if (MidiToEdl.setTrackList(loadedMIDI, fileInfo, configs, polyphonies)) {
                     System.gc();
                     ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("MIDI Loaded");
                     ((JLabel)((JPanel)getComponents().get(0)).getComponent(1)).setText("File: " + chooser.getSelectedFile().getAbsolutePath());
                     convert.setEnabled(true);
                     randomise.setEnabled(true);
                     removeMouseListenersFromList();
                     addMouseListenersToList();
                     convert.setText("Convert To EDL");
                     if (convert.getActionListeners().length != 0) convert.removeActionListener(convert.getActionListeners()[0]);
                     convert.addActionListener(c -> {
                         chooser = new JFileChooser();
                         FileNameExtensionFilter outputFilter = new FileNameExtensionFilter("Vegas EDL", "txt");
                         chooser.setFileFilter(outputFilter);
                         String loadedName = loadedMIDI.getAbsolutePath();
                         outputFile = new File(loadedName.substring(0, loadedName.lastIndexOf('.')) + ".txt");
                         chooser.setSelectedFile(outputFile);
                         if (chooser.showSaveDialog(chooser) == JFileChooser.APPROVE_OPTION) {
                             String outputFileName = chooser.getSelectedFile().getAbsolutePath();
                             if (!outputFileName.endsWith(".txt")) outputFileName+=".txt";
                             outputFile = new File(outputFileName);
                             //MidiToEdl.convertToEdl(loadedMIDI, configs, polyphonies, outputFile, ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)));
                             midiToEdlThread.start();
                         }


                     });

                     if (edit.getActionListeners().length != 0) edit.removeActionListener(edit.getActionListeners()[0]);
                     edit.addActionListener(c -> {
                         new EDLConfigWindow(configs.get(selectedIndex));
                     });
                 }
                 else {
                     ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("Error loading MIDI");
                     ((JLabel)((JPanel)getComponents().get(1)).getComponent(0)).setText("File: None");
                     loadedMIDI = null;
                     loadedEDL = null;
                     convert.setEnabled(false);
                     edit.setEnabled(false);
                 }





            }
        });

        importEdl.addActionListener(e -> {
            chooser = new JFileChooser();
            FileNameExtensionFilter edlFilter = new FileNameExtensionFilter("Vegas EDL Files", "txt");
            chooser.setFileFilter(edlFilter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                loadedEDL = new File(chooser.getSelectedFile().getAbsolutePath());
                loadedMIDI = null;
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("EDL Loaded");
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(1)).setText("File: " + chooser.getSelectedFile().getAbsolutePath());
                EdlToMidi.setTrackList(loadedEDL, fileInfo, configs);
                removeMouseListenersFromList();
                midiConfig = MIDIConfig.defaults();
                configs = EdlToMidi.configure(loadedEDL);
                convert.setText("Convert To MIDI");
                convert.setEnabled(true);
                randomise.setEnabled(false);
                if (convert.getActionListeners().length != 0) convert.removeActionListener(convert.getActionListeners()[0]);
                convert.addActionListener(c -> {

                    EdlToMidi.convertToMidi(loadedEDL, configs, midiConfig);
                });

                edit.setText("Edit MIDI parameters");
                edit.setEnabled(true);
                if (edit.getActionListeners().length != 0) edit.removeActionListener(edit.getActionListeners()[0]);
                edit.addActionListener(c -> {
                    new MIDIConfigWindow(midiConfig);
                });
            }
        });

        bar.add(file);

        JMenu config = new JMenu("Config");

        JMenuItem editAll = new JMenu("Edit all at once");
        JMenuItem resetAll = new JMenu("Reset all to default");
        JMenuItem randomiseTimings = new JMenu("Randomise all timings");

        JMenuItem listAllTimings = new JMenu("List All Timings");


        saveToFile.addActionListener(e -> {
            if (loadedMIDI != null || loadedEDL != null) {
                chooser = new JFileChooser();
                FileNameExtensionFilter outputFilter = new FileNameExtensionFilter("Config Files", "conf");
                chooser.setFileFilter(outputFilter);
                String loadedName = "";
                if (loadedMIDI != null) loadedName = loadedMIDI.getAbsolutePath();
                else if (loadedEDL != null) loadedName = loadedEDL.getAbsolutePath();
                outputFile = new File(loadedName.substring(0, loadedName.lastIndexOf('.')) + ".conf");
                chooser.setSelectedFile(outputFile);
                if (chooser.showSaveDialog(chooser) == JFileChooser.APPROVE_OPTION) {
                    String outputFileName = chooser.getSelectedFile().getAbsolutePath();
                    if (!outputFileName.endsWith(".conf")) outputFileName+=".conf";
                    outputFile = new File(outputFileName);
                    if (EDLConfig.saveConfigToFile(configs, outputFile)) {
                        JOptionPane.showMessageDialog(null, "Config saved successfully", "Save config", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error in saving config", "Save config", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else {
                System.out.println("file is null");
            }


        });

        loadFromFile.addActionListener(e -> {
            if (loadedMIDI != null || loadedEDL != null) {
                chooser = new JFileChooser();
                FileNameExtensionFilter midiFilter = new FileNameExtensionFilter("Config Files", "conf");
                chooser.setFileFilter(midiFilter);
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    loadConfig = new File(chooser.getSelectedFile().getAbsolutePath());
                    if (EDLConfig.loadConfigFromFile(loadConfig, fileInfo, configs)) {
                        JOptionPane.showMessageDialog(null, "Config loaded successfully", "Load config", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error in loading configs", "Load config", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else {
                System.out.println("file is null");
            }

        });



        JMenu help = new JMenu("Help");
        JMenuItem helpAbout = new JMenuItem("About");
        help.add(helpAbout);
        helpAbout.addActionListener(e -> {
            HelpWindow help1 = new HelpWindow();

        });
        bar.add(help);

        JMenu analysis = new JMenu("Analysis");
        JMenuItem timingsList = new JMenuItem("All timings");
        JMenuItem timingsAnalysis = new JMenuItem("Timing analysis");

        analysis.add(timingsList);

        timingsList.addActionListener(e -> {
            Analysis.timings(configs);
        });
        bar.add(analysis);
    }

    @Override
    public void uiElements() {
        Font font = new Font("Arial", Font.ITALIC, 25);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JLabel status = new JLabel("Waiting for file input");
        status.setFont(font);
        JLabel fileLoaded = new JLabel("File: None");

        topPanel.add(status, BorderLayout.CENTER);
        topPanel.add(fileLoaded, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH, false);

        JPanel midPanel = new JPanel();
        midPanel.setLayout(new GridLayout(1, 1));
        midPanel.setBorder(new TitledBorder("File information"));
        fileInfo = new DefaultListModel<>();
        componentList = new JList<String>();


        componentList.setModel(fileInfo);
        componentList.setEnabled(false);
        JScrollPane scroll = new JScrollPane(componentList);
        midPanel.add(scroll, BorderLayout.CENTER);

        add(midPanel, BorderLayout.CENTER, false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2,1));

        convert = new JButton("Convert");
        convert.setFont(font);
        convert.setEnabled(false);
        JPanel editButtons = new JPanel();
        editButtons.setLayout(new GridLayout(1, 3));


        edit = new JButton("Edit");
        edit.setFont(font);
        edit.setEnabled(false);
        editButtons.add(edit);

        randomise = new JButton("Randomise timings");
        randomise.setFont(font);
        randomise.setEnabled(false);
        editButtons.add(randomise);
        randomise.addActionListener(e -> {
            int counter = -1;
            for(EDLConfig conf : configs) {
                counter++;
                conf.edited = true;
                conf.streamStart = random.nextDouble(180);
                if (MainWindow.fileInfo.getElementAt(counter).contains("Default")) {
                    MainWindow.fileInfo.set(counter, MainWindow.fileInfo.getElementAt(counter).replace("Default settings loaded", "Settings customised"));
                }
            }
        });

        bottomPanel.add(editButtons);
        bottomPanel.add(convert);

        add(bottomPanel, BorderLayout.SOUTH, false);




    }
    public void addMouseListenersToList() {
        componentList.setEnabled(true);
        componentList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JList list = (JList)e.getSource();
                if (e.getClickCount() == 1) {
                    edit.setEnabled(true);
                    selectedIndex = list.locationToIndex(e.getPoint());
                    edit.setText("Edit Track " + (selectedIndex+1));

                }
                if (e.getClickCount() == 2) {
                    new EDLConfigWindow(configs.get(list.locationToIndex(e.getPoint())));
                    //JOptionPane.showMessageDialog(null, list.locationToIndex(e.getPoint()));
                }

            }
        });
    }
    public void removeMouseListenersFromList() {
        componentList.setEnabled(false);
        if (componentList.getMouseListeners().length > 2) componentList.removeMouseListener(componentList.getMouseListeners()[2]);
    }


}
