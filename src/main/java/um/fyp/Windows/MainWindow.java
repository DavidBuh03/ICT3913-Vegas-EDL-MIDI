package um.fyp.Windows;
import um.fyp.EDLObjects.Analysis;
import um.fyp.Conversion.EdlToMidi;
import um.fyp.GUIHelper.Window;
import um.fyp.Config.EDLConfig;
import um.fyp.Config.MIDIConfig;
import um.fyp.Conversion.MidiToEdl;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class MainWindow extends Window {
    Random random = new Random();
    boolean fileLoadStatus;
    Runnable midiToEdlRunnable = new Runnable() {
        @Override
        public void run() {
            disableEverything();
            MidiToEdl.convertToEdl(loadedMIDI, configs, polyphonies, outputFile, ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)));
            enableElements(0);
        }
    };

    Runnable edlToMidiRunnable = new Runnable() {
        @Override
        public void run() {
            disableEverything();
            EdlToMidi.convertToMidi(loadedEDL, configs, midiConfig, outputFile, ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)));
            enableElements(1);
        }
    };

    Thread runnerThread;

    File loadedMIDI;
    File loadedEDL;
    File outputFile;
    File loadConfig;

    JFileChooser chooser;

    JList<String> trackList;
    JButton edit;
    JButton convert;
    JButton randomise;
    JButton editAll;

    public static DefaultListModel<String> fileInfo;
    public static List<EDLConfig> configs = new ArrayList<>();
    public List<Integer> polyphonies = new ArrayList<>();

    public static MIDIConfig midiConfig = new MIDIConfig();

    int selectedIndex;
    public MainWindow() {
        super(560, 560, "Vegas EDL MIDI Converter", true, true);
    }

    @Override
    public void menuBarItems(JMenuBar bar) {
        JMenu file = new JMenu("File");
        JMenuItem openMidi = new JMenuItem("Open MIDI");
        JMenuItem openEdl = new JMenuItem("Open Vegas EDL");

        JMenuItem saveToFile = new JMenuItem("Save config to file");
        JMenuItem loadFromFile = new JMenuItem("Load config from file");

        JMenuItem quit = new JMenuItem("Exit");
        file.add(openMidi);
        file.add(openEdl);
        file.addSeparator();
        file.add(saveToFile);
        file.add(loadFromFile);
        file.addSeparator();
        file.add(quit);
        quit.addActionListener(e -> {
            System.exit(0);
        });

        openMidi.addActionListener(e -> {
            chooser = new JFileChooser();
            FileNameExtensionFilter midiFilter = new FileNameExtensionFilter("MIDI Files", "mid");
            chooser.setFileFilter(midiFilter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                loadedMIDI = new File(chooser.getSelectedFile().getAbsolutePath());
                loadedEDL = null;
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("Loading MIDI...");
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(1)).setText("File: " + chooser.getSelectedFile().getAbsolutePath());
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).repaint();
                fileLoadStatus = false;

                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        disableEverything();
                        fileLoadStatus = MidiToEdl.setTrackList(loadedMIDI, fileInfo, configs, polyphonies);
                        Thread.sleep(100);
                        return null;
                    }
                    @Override
                    protected void done() {
                        if (fileLoadStatus) {
                            System.gc();
                            ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("MIDI Loaded");
                            ((JMenu)getMenuBar().getComponent(0)).getMenuComponent(4).setEnabled(true);
                            enableElements(0);
                            editAll.addActionListener(c -> {
                                new EDLConfigWindow(null);
                            });
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
                                    runnerThread = new Thread(midiToEdlRunnable);
                                    runnerThread.start();
                                }


                            });

                            if (edit.getActionListeners().length != 0) edit.removeActionListener(edit.getActionListeners()[0]);
                            edit.addActionListener(c -> {
                                new EDLConfigWindow(configs.get(selectedIndex));
                            });
                        }
                        else {
                            ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("Error loading MIDI");
                            ((JLabel)((JPanel)getComponents().get(0)).getComponent(1)).setText("File: None");
                            loadedMIDI = null;
                            loadedEDL = null;
                            convert.setEnabled(false);
                            edit.setEnabled(false);
                        }
                    }
                };
                worker.execute();
            }
        });

        openEdl.addActionListener(e -> {
            chooser = new JFileChooser();
            FileNameExtensionFilter edlFilter = new FileNameExtensionFilter("Vegas EDL Files", "txt");
            chooser.setFileFilter(edlFilter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                loadedEDL = new File(chooser.getSelectedFile().getAbsolutePath());
                loadedMIDI = null;
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("Loading EDL...");
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(1)).setText("File: " + chooser.getSelectedFile().getAbsolutePath());
                fileLoadStatus = false;
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        disableEverything();
                        fileLoadStatus = EdlToMidi.setTrackList(loadedEDL, fileInfo, configs);
                        //Thread.sleep(100);
                        return null;
                    }
                    @Override
                    protected void done() {

                        if (fileLoadStatus) {
                            ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("EDL Loaded");
                            ((JMenu)getMenuBar().getComponent(0)).getMenuComponent(4).setEnabled(false);
                            removeMouseListenersFromList();
                            midiConfig = MIDIConfig.defaults();
                            configs = EdlToMidi.configure(loadedEDL);
                            convert.setText("Convert To MIDI");
                            enableElements(1);
                            if (convert.getActionListeners().length != 0) convert.removeActionListener(convert.getActionListeners()[0]);
                            convert.addActionListener(c -> {
                                chooser = new JFileChooser();
                                FileNameExtensionFilter outputFilter = new FileNameExtensionFilter("MIDI Files", "mid");
                                chooser.setFileFilter(outputFilter);
                                String loadedName = loadedEDL.getAbsolutePath();
                                outputFile = new File(loadedName.substring(0, loadedName.lastIndexOf('.')) + ".mid");
                                chooser.setSelectedFile(outputFile);
                                if (chooser.showSaveDialog(chooser) == JFileChooser.APPROVE_OPTION) {
                                    String outputFileName = chooser.getSelectedFile().getAbsolutePath();
                                    if (!outputFileName.endsWith(".mid")) outputFileName+=".mid";
                                    outputFile = new File(outputFileName);
                                    runnerThread = new Thread(edlToMidiRunnable);
                                    runnerThread.start();
                                }
                            });

                            edit.setText("Edit MIDI parameters");
                            edit.setEnabled(true);
                            if (edit.getActionListeners().length != 0) edit.removeActionListener(edit.getActionListeners()[0]);
                            edit.addActionListener(c -> {
                                new MIDIConfigWindow(midiConfig);
                            });
                        }
                        else {
                            ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("Error loading EDL");
                            ((JLabel)((JPanel)getComponents().get(0)).getComponent(1)).setText("File: None");
                            loadedMIDI = null;
                            loadedEDL = null;
                            convert.setEnabled(false);
                            edit.setEnabled(false);
                        }
                    }
                };
                worker.execute();

            }
        });

        bar.add(file);

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
                JOptionPane.showMessageDialog(null, "No file is loaded yet", "Save config", JOptionPane.ERROR_MESSAGE);
            }


        });

        loadFromFile.addActionListener(e -> {
            if (loadedMIDI != null || loadedEDL != null) {
                chooser = new JFileChooser();
                FileNameExtensionFilter midiFilter = new FileNameExtensionFilter("Config Files", "conf");
                chooser.setFileFilter(midiFilter);
                String loadedName = "";
                if (loadedMIDI != null) loadedName = loadedMIDI.getAbsolutePath();
                else if (loadedEDL != null) loadedName = loadedEDL.getAbsolutePath();
                outputFile = new File(loadedName.substring(0, loadedName.lastIndexOf('.')) + ".conf");
                chooser.setSelectedFile(outputFile);
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
                JOptionPane.showMessageDialog(null, "No file is loaded yet", "Load Config", JOptionPane.ERROR_MESSAGE);
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
        JMenuItem usefulnessList = new JMenuItem("Timing usefulness list");

        analysis.add(timingsList);
        analysis.add(timingsAnalysis);
        analysis.add(usefulnessList);

        timingsList.addActionListener(e -> {
            Analysis.timings(configs, false);
        });
        timingsAnalysis.addActionListener(e -> {
            Analysis.timingAnalysis(configs);
        });
        usefulnessList.addActionListener(e -> {
            Analysis.timings(configs, true);
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
        trackList = new JList<String>();


        trackList.setModel(fileInfo);
        trackList.setEnabled(false);
        JScrollPane scroll = new JScrollPane(trackList);
        midPanel.add(scroll, BorderLayout.CENTER);

        add(midPanel, BorderLayout.CENTER, false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2,1));

        JPanel convertPanel = new JPanel();
        convertPanel.setLayout(new GridLayout(1, 2));

        convert = new JButton("Convert");
        convert.setFont(font);
        convert.setEnabled(false);
        JPanel editButtons = new JPanel();
        editButtons.setLayout(new GridLayout(1, 2));


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

        editAll = new JButton("Edit all tracks");
        editAll.setFont(font);
        editAll.setEnabled(false);

        convertPanel.add(editAll);
        convertPanel.add(convert);

        bottomPanel.add(editButtons);
        bottomPanel.add(convertPanel);

        add(bottomPanel, BorderLayout.SOUTH, false);




    }
    public void addMouseListenersToList() {
        trackList.setEnabled(true);
        trackList.addMouseListener(new MouseAdapter() {
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
        //trackList.setEnabled(false);
        if (trackList.getMouseListeners().length > 2) trackList.removeMouseListener(trackList.getMouseListeners()[2]);
    }

    public void disableEverything() {
        getMenuBar().setEnabled(false);
        trackList.setEnabled(false);
        edit.setEnabled(false);
        randomise.setEnabled(false);
        editAll.setEnabled(false);
        convert.setEnabled(false);
    }

    public void enableElements(int type) {
        switch(type) {
            case 0: {
                getMenuBar().setEnabled(true);
                trackList.setEnabled(true);
                edit.setEnabled(true);
                randomise.setEnabled(true);
                editAll.setEnabled(true);
                convert.setEnabled(true);
            }; break;
            case 1: {
                getMenuBar().setEnabled(true);
                edit.setEnabled(true);
                convert.setEnabled(true);
            }; break;
            default: {
                System.out.println("something went wrong and idk what it is");
            }
        }
    }


}
