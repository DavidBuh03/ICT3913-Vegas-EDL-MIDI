package um.fyp;

import um.fyp.Config.EDLConfig;
import um.fyp.GUIHelper.Window;
import um.fyp.MIDIObjects.MIDIConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static um.fyp.Config.EDLConfig.guiParameters;
import static um.fyp.Config.EDLConfig.numericComboBoxes;
import static um.fyp.MIDIObjects.MIDIConfig.descriptions;
import static um.fyp.MIDIObjects.MIDIConfig.fields;

public class MIDIConfigWindow extends Window {

    List<JPanel> textFieldPairs;
    JComboBox<String> numerator;
    JComboBox<String> denominator;
    public MIDIConfigWindow(MIDIConfig config) {
        super(450, 320, "Configure MIDI", true, false);
        uiElements();
        setElements(config);
        initSize(480, 320, true);
    }

    @Override
    public void menuBarItems(JMenuBar bar) {

    }

    public void setElements(MIDIConfig c) {
        ((JTextField)textFieldPairs.get(0).getComponent(1)).setText(String.valueOf(c.ppq));
        ((JTextField)textFieldPairs.get(1).getComponent(1)).setText(String.valueOf(c.bpm));
        numerator.setSelectedIndex(c.timeSignature[0]-1);
        denominator.setSelectedIndex(c.timeSignature[1]/4-1);
        ((JCheckBox)textFieldPairs.get(3).getComponent(0)).setSelected(c.sysGM);
        ((JCheckBox)textFieldPairs.get(4).getComponent(0)).setSelected(c.omniPoly);
    }

    public void save() {
        MIDIConfig savedConfig = new MIDIConfig();
        savedConfig.ppq = Integer.parseInt(((JTextField)textFieldPairs.get(0).getComponent(1)).getText());
        savedConfig.bpm = Integer.parseInt(((JTextField)textFieldPairs.get(1).getComponent(1)).getText());
        savedConfig.timeSignature = new int[] {numerator.getSelectedIndex()+1, (denominator.getSelectedIndex()+1)*4};
        savedConfig.sysGM = ((JCheckBox)textFieldPairs.get(2).getComponent(0)).isSelected();
        savedConfig.omniPoly = ((JCheckBox)textFieldPairs.get(3).getComponent(0)).isSelected();

        MainWindow.midiConfig = savedConfig;
    }

    @Override
    public void uiElements() {
        Font textFont = new Font("Arial", Font.PLAIN, 23);
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);
        JPanel topPanel = new JPanel();
        JLabel editing = new JLabel("MIDI Output Parameters", SwingConstants.CENTER);
        editing.setFont(textFont);
        topPanel.add(editing);
        add(topPanel, BorderLayout.NORTH, false);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(6, 1, 0, 5));
        middlePanel.setBorder(new TitledBorder("Customise MIDI metadata"));

        JPanel timeSignature = new JPanel();
        numerator = new JComboBox<String>(MIDIConfig.timeSignatureNumerators);
        numerator.setPreferredSize(new Dimension(60, 22));
        JLabel divide = new JLabel("/");
        denominator = new JComboBox<String>(MIDIConfig.timeSignatureDenominators);
        denominator.setPreferredSize(new Dimension(60, 22));
        timeSignature.add(numerator);
        timeSignature.add(divide);
        timeSignature.add(denominator);

        textFieldPairs = new ArrayList<JPanel>();
        for (int i = 0; i < 5; i++) {
            JPanel textFieldpair = new JPanel();
            textFieldpair.setLayout(new BorderLayout());
            if (i<3) {
                JLabel fieldName = new JLabel(fields[i]);
                fieldName.setPreferredSize(new Dimension(190, 0));
                textFieldpair.add(fieldName, BorderLayout.WEST);
                if (i < 2) {
                    JTextField fieldInput = new JTextField();
                    textFieldpair.add(fieldInput, BorderLayout.CENTER);
                } else {
                    textFieldpair.add(timeSignature, BorderLayout.CENTER);

                }


            } else {
                JCheckBox fieldInput = new JCheckBox(fields[i]);
                textFieldpair.add(fieldInput, BorderLayout.CENTER);

            }



            JButton info = new JButton("Info");
            textFieldpair.add(info, BorderLayout.EAST);

            int finalI = i;
            info.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, descriptions[finalI], "Parameter Info", JOptionPane.INFORMATION_MESSAGE);
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
            setElements(MIDIConfig.defaults());
        });

        save.addActionListener(e -> {
            save();
            getFrame().dispose();
        });


        bottomPanel.add(cancel);
        bottomPanel.add(loadDefaults);
        bottomPanel.add(save);

        add(bottomPanel, BorderLayout.SOUTH, true);




        //enable sysex general midi
        //enable omni and poly
        //set ppq
        //set bpm

    }
}
