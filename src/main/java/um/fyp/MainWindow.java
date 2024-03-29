package um.fyp;
import um.fyp.GUIHelper.Window;
import um.fyp.Config.EDLConfig;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class MainWindow extends Window {

    File loadedMIDI;
    File loadedEDL;

    JFileChooser chooser;

    JList<String> componentList;
    JButton edit;

    DefaultListModel<String> fileInfo;
    List<EDLConfig> configs = new ArrayList<EDLConfig>();

    int[] trackInfo;
    int selectedIndex;
    boolean midiLoaded;
    boolean edlLoaded;

    public MainWindow() {
        super(480, 480, "Vegas EDL MIDI Converter", true, true);
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
        componentList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JList list = (JList)e.getSource();
                if (e.getClickCount() == 1) {
                    edit.setEnabled(true);
                    selectedIndex = list.locationToIndex(e.getPoint()) + 1;
                    edit.setText("Edit Track " + selectedIndex);

                }
                if (e.getClickCount() == 2) {
                    new ConfigWindow(configs.get(list.locationToIndex(e.getPoint())));
                    //JOptionPane.showMessageDialog(null, list.locationToIndex(e.getPoint()));
                }

            }
        });

        componentList.setModel(fileInfo);
        JScrollPane scroll = new JScrollPane(componentList);
        midPanel.add(scroll, BorderLayout.CENTER);

        add(midPanel, BorderLayout.CENTER, false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1,2));

        JButton convert = new JButton("Convert (Test)");
        convert.setFont(font);
        edit = new JButton("Edit (Test)");
        edit.setFont(font);
        edit.setEnabled(false);
        bottomPanel.add(edit);
        bottomPanel.add(convert);

        add(bottomPanel, BorderLayout.SOUTH, false);




    }


}
