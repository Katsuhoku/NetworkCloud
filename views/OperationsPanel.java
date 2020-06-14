package views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.JPanel;
import controller.Controller;

public class OperationsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JButton openButton;
    private JButton sendButton;
    private JButton deleteButton;
    private JButton createDirButton;
    private JButton updateButton;
    private JLabel loader;


    private Controller controller;

    public OperationsPanel(Controller controller) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.controller = controller;
        setBackground(new Color(32,37,44));
        init();
    }

    public void init() {
        openButton = new JButton("Open");
        openButton.setEnabled(false);
        openButton.setBackground(new Color(49, 54, 63));
        openButton.setForeground(Color.WHITE);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.openButtonEvent();
            }
            
        });

        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.setBackground(new Color(49, 54, 63));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.sendEvent();
            }
            
        });

        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(49, 54, 63));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.deleteEvent();
            }
            
        });

        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(49, 54, 63));
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.updateButtonEvent();
            }
            
        });

        createDirButton = new JButton("Create Directory");
        createDirButton.setBackground(new Color(49, 54, 63));
        createDirButton.setForeground(Color.WHITE);
        createDirButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.createDirectoryEvent();
            }
            
        });

        

        loader = new JLabel(new ImageIcon("images/ajax-loader.gif"), JLabel.CENTER);
        loader.setVisible(false);

        add(openButton);
        add(sendButton);
        add(deleteButton);
        add(updateButton);
        add(createDirButton);
        add(Box.createHorizontalGlue());
        add(loader);
    }


    public void setSendButtonEnable(boolean b){
        sendButton.setEnabled(b);
    }

    public void setOpenButtonEnable(boolean b){
        openButton.setEnabled(b);
    }

    public void setDeleteButtonEnable(boolean b){
        deleteButton.setEnabled(b);
    }

    public void setLoaderVisible(boolean b){
        loader.setVisible(b);
    }
}