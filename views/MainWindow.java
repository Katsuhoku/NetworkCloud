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
    private final int MIN_WIDTH = 1280;
    private final int MIN_HEIGHT = 720;
    
    private Controller controller;

    // Components

    // Panels
    private JPanel actionsPanel;
    private JPanel mainPanel;
    private JPanel detailsPanel;
    private JPanel storageUnitsPanel;
    private JPanel currentPathPanel;

    private FilesListPanel filesListPanel;

    // Botones
    private JButton sendButton;
    private JButton deleteButton;
    private JButton mkdirButton;

    private JButton upperDirButton;

    // TextFields
    private JTextField pathField;

    public MainWindow(Controller controller) {
        this.controller = controller;
    }

    public void start() {
        setTitle("Network Cloud");
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Actions panel
        sendButton = new JButton("Envíar");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

            }
        });
        sendButton.setBackground(Color.WHITE);
        sendButton.setEnabled(false);

        deleteButton = new JButton("Eliminar");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

            }
        });
        deleteButton.setBackground(Color.WHITE);
        deleteButton.setEnabled(false);

        mkdirButton = new JButton("Crear Directorio");
        mkdirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

            }
        });
        mkdirButton.setBackground(Color.WHITE);
        mkdirButton.setEnabled(false);

        actionsPanel = new JPanel();
        actionsPanel.add(sendButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(mkdirButton);
        mainPanel.add(actionsPanel, BorderLayout.PAGE_START);

        // storageUnitsPanel
        storageUnitsPanel = new JPanel();
        storageUnitsPanel.setBackground(Color.BLACK);
        mainPanel.add(storageUnitsPanel, BorderLayout.LINE_START);

        // detailsPanel
        pathField = new JTextField(50);
        upperDirButton = new JButton("<-");
        
        currentPathPanel = new JPanel();
        currentPathPanel.add(upperDirButton);
        currentPathPanel.add(pathField);

        filesListPanel = new FilesListPanel();

        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.add(currentPathPanel, BorderLayout.PAGE_START);
        detailsPanel.add(filesListPanel, BorderLayout.CENTER);

        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void exit() {
        setVisible(false);
        dispose();
        controller.exit();
    }
}