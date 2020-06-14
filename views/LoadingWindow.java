package views;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import controller.Controller;

public class LoadingWindow extends JFrame{

    private static final long serialVersionUID = 1L;

    // Window properties
    private final int MIN_WIDTH = 450;
    private final int MIN_HEIGHT = 300;

    private Controller controller;



    public LoadingWindow(Controller controller) {
        this.setBackground(new Color(32,37,44));
        this.controller = controller;
    }

    public void start() {
        setTitle("Network Cloud");
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
    }

    private void exit() {
        setVisible(false);
        dispose();
        controller.exit();
    }
}