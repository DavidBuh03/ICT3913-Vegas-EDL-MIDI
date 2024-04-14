package um.fyp.GUIHelper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
public abstract class Window {
    private JFrame frame;
    private List<Component> components = new ArrayList<>();
    private JMenuBar menuBar = new JMenuBar();

    public JFrame getFrame() {
        return frame;
    }
    public List<Component> getComponents() {
        return components;
    }
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public abstract void menuBarItems(JMenuBar bar);
    public abstract void uiElements();
    public void add(Component component, boolean repaint) {
        frame.add(component);
        components.add(component);
        if (repaint) frame.repaint();
    }
    public void add(Component component, String layout, boolean repaint) {
        frame.add(component, layout);
        components.add(component);
        if (repaint) frame.repaint();
    }

    public void remove(Component component, boolean repaint) {
        frame.remove(component);
        components.remove(component);
        if (repaint) frame.repaint();
    }

    public void initSize(int width, int height, boolean center) {
        this.frame.pack();
        this.frame.setSize(width, height);
        if (center) this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    public Window(int width, int height, String title, boolean center, boolean mainWindow) {


        this.frame = new JFrame();
        this.frame.setLayout(new BorderLayout());
        this.frame.setJMenuBar(menuBar);
        menuBarItems(menuBar);
        this.frame.setTitle(title);
        if (mainWindow) {
            this.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        } else {
            this.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    frame.dispose();
                }
            });
        }
            uiElements();
            initSize(width, height, center);




    }

}
