package um.fyp;
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

public class MainWindow extends Window {

    File loadedMIDI;
    File loadedEDL;

    JFileChooser chooser;

    JList<String> componentList;
    JButton edit;
    JButton convert;

    DefaultListModel<String> fileInfo;
    public static List<EDLConfig> configs = new ArrayList<EDLConfig>();

    public static MIDIConfig midiConfig = new MIDIConfig();

    int[] trackInfo;
    int selectedIndex;
    boolean midiLoaded;
    boolean edlLoaded;

    public MainWindow() {
        super(560, 480, "Vegas EDL MIDI Converter", true, true);
    }

    @Override
    public void menuBarItems(JMenuBar bar) {
        JMenu file = new JMenu("File");
        JMenuItem importMidi = new JMenuItem("Import MIDI");
        JMenuItem importEdl = new JMenuItem("Import Vegas EDL");
        JMenuItem quit = new JMenuItem("Exit");
        file.add(importMidi);
        file.add(importEdl);
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
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("MIDI Loaded: " + chooser.getSelectedFile().getName());
                MidiToEdl.setTrackList(loadedMIDI, fileInfo, configs);
                removeMouseListenersFromList();
                addMouseListenersToList();
                convert.setText("Convert To EDL");
                if (convert.getActionListeners().length != 0) convert.removeActionListener(convert.getActionListeners()[0]);
                convert.addActionListener(c -> {
                    //configs = EdlToMidi.configure(loadedEDL);
                    MidiToEdl.convertToEdl(loadedMIDI, configs);
                });

                if (edit.getActionListeners().length != 0) edit.removeActionListener(edit.getActionListeners()[0]);
                edit.addActionListener(c -> {
                    new EDLConfigWindow(configs.get(selectedIndex));
                });



            }
        });

        importEdl.addActionListener(e -> {
            chooser = new JFileChooser();
            FileNameExtensionFilter edlFilter = new FileNameExtensionFilter("Vegas EDL Files", "txt");
            chooser.setFileFilter(edlFilter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                loadedEDL = new File(chooser.getSelectedFile().getAbsolutePath());
                ((JLabel)((JPanel)getComponents().get(0)).getComponent(0)).setText("EDL Loaded: " + chooser.getSelectedFile().getName());
                EdlToMidi.setTrackList(loadedEDL, fileInfo, configs);
                removeMouseListenersFromList();
                midiConfig = MIDIConfig.defaults();
                convert.setText("Convert To MIDI");

                if (convert.getActionListeners().length != 0) convert.removeActionListener(convert.getActionListeners()[0]);
                convert.addActionListener(c -> {
                    configs = EdlToMidi.configure(loadedEDL);
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

        JMenu help = new JMenu("Help");
        JMenuItem helpAbout = new JMenuItem("About");
        help.add(helpAbout);
        helpAbout.addActionListener(e -> {
            HelpWindow help1 = new HelpWindow();

        });
        bar.add(help);
    }

    @Override
    public void uiElements() {
        Font font = new Font("Arial", Font.ITALIC, 25);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 1));

        JLabel status = new JLabel("Status: Waiting for file input");
        status.setFont(font);
        topPanel.add(status);
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
        bottomPanel.setLayout(new GridLayout(1,2));

        convert = new JButton("Convert (Test)");
        convert.setFont(font);
        edit = new JButton("Edit (Test)");
        edit.setFont(font);
        edit.setEnabled(false);
        bottomPanel.add(edit);
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
