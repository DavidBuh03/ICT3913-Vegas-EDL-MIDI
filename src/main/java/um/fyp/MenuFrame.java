package um.fyp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MenuFrame extends Frame implements ActionListener {
    Button midiEDL, edlMIDI;
    public MenuFrame() {
        super("MIDI and EDL Converter");
        setSize(300, 300);
        setVisible(true);
        setLocationRelativeTo(null);
        setLayout(null);

        int btnFontsize = 30;

        midiEDL = new Button("MIDI to EDL");
        midiEDL.setBounds(50, 70, 200, 80);
        midiEDL.setFont(new Font("Arial", Font.PLAIN, btnFontsize));
        midiEDL.addActionListener(this);

        edlMIDI = new Button("EDL to MIDI");
        edlMIDI.setBounds(50, 170, 200, 80);
        edlMIDI.setFont(new Font("Arial", Font.PLAIN, btnFontsize));
        edlMIDI.addActionListener(this);

        add(midiEDL);
        add(edlMIDI);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == midiEDL) {
            MIDIEDLFrame midiToEdl = new MIDIEDLFrame();

        }
        else if (e.getSource() == edlMIDI) {
            EDLMIDIFrame EdlToMidi = new EDLMIDIFrame();
        }

    }
}
