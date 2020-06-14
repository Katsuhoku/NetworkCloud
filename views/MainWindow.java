package views;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import controller.Controller;

/**
 * The main (and only) window of Network Cloud system. As is
 * described in the documentation, it has four sections: Storage
 * Units Panel, Actions Panel, Current Path Panel and Main Panel,
 * wich can display the Directory Content or the System Current Status.
 */

public class MainWindow extends JFrame {
    // Window properties
    private final int MIN_WIDTH = 850;
    private final int MIN_HEIGHT = 550;
    
    private Controller controller;

    // Components

    // Panels
    private MainPanel mainPanel;

    public MainWindow(Controller controller) {
        this.setBackground(new Color(32,37,44));
        this.controller = controller;
    }

    public void start() {
        setTitle("Network Cloud");
        //setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exit();
            }
        });

        init();

        setVisible(true);
    }

    private void init() {
        // Main panel
        mainPanel = new MainPanel(controller);
        add(mainPanel);
    }

    private void exit() {
        setVisible(false);
        dispose();
        controller.exit();
    }


    public MainPanel getMainPanel(){
        return mainPanel;
    }
}