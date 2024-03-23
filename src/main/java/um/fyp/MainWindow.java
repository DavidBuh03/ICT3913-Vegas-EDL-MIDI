package um.fyp;
import um.fyp.GUIHelper.Window;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class MainWindow extends Window {

    File loadedMIDI;
    File loadedEDL;

    JFileChooser chooser;

    JList<String> componentList;

    DefaultListModel<String> dummy;

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
            }
        });

        importEdl.addActionListener(e -> {
            chooser = new JFileChooser();
            FileNameExtensionFilter edlFilter = new FileNameExtensionFilter("Vegas EDL Files", "txt");
            chooser.setFileFilter(edlFilter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                loadedEDL = new File(chooser.getSelectedFile().getAbsolutePath());
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
        fillList();

        componentList = new JList<String>();
        componentList.setModel(dummy);
        JScrollPane scroll = new JScrollPane(componentList);
        midPanel.add(scroll, BorderLayout.CENTER);

        add(midPanel, BorderLayout.CENTER, false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1,1 ));

        JButton convert = new JButton("Convert (Test)");
        convert.setFont(font);
        bottomPanel.add(convert);

        add(bottomPanel, BorderLayout.SOUTH, false);




    }

    public void fillList() {
        dummy = new DefaultListModel<>();
        for (int i = 0; i<30; i++) {

            dummy.addElement("Track " + i);
        }
    }


}
