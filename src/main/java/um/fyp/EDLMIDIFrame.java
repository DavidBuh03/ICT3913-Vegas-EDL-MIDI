package um.fyp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EDLMIDIFrame extends Frame implements ActionListener {

    //EXPERIMENTAL COPY TO TEST FRAME CREATION - SUBJECT TO CHANGE OR REMOVAL
    Button midiEDL, edlMIDI;
    public EDLMIDIFrame() {
        super("Convert EDL to MIDI");
        setSize(300, 300);
        setVisible(true);
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
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == midiEDL) {


        }
        else if (e.getSource() == edlMIDI) {

        }

    }
}
