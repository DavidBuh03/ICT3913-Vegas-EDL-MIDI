package um.fyp;
import um.fyp.GUIHelper.Window;
import javax.swing.*;
import java.awt.*;

public class HelpWindow extends Window {


    public HelpWindow() {
        super(320, 240, "About", true, false);
        getFrame().setLayout(new GridLayout(2, 1));
    }


    @Override
    public void menuBarItems(JMenuBar bar) {

    }

    @Override
    public void uiElements() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 1));
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 1));


        JLabel title = new JLabel("<html><body><p style=\"text-align: center\">Vegas EDL - MIDI<br>Bidirectional Converter</p></body></html>", SwingConstants.CENTER);
        Font font = new Font("Arial", Font.BOLD, 25);
        title.setFont(font);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(title);

        JLabel credits = new JLabel("Created for ICT3913 FYP 2024", SwingConstants.CENTER);
        bottomPanel.add(credits, false);

        JButton close = new JButton("Close");
        close.setFont(font);
        close.addActionListener(e -> {
            getFrame().dispose();
        });
        bottomPanel.add(close);
        add(topPanel, false);
        add(bottomPanel, false);
    }
}
