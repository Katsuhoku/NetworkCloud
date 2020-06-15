package views;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import controller.Controller;

public class NodesPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private HashMap<String, JButton> nodeButtons;
    private Controller controller;

    public NodesPanel(Controller controller, ArrayList<String> nodeNames) {
        super(new GridLayout(nodeNames.size(), 0));
        this.controller = controller;
        init(nodeNames);
    }

    private void init(ArrayList<String> nodeNames) {
        nodeButtons = new HashMap<>();

        for (String nodeName : nodeNames) {
            JButton nodeButton = new JButton(nodeName);
            nodeButton.setPreferredSize(new Dimension(100, 25));
            nodeButton.setBackground(new Color(49, 54, 63));
            if (nodeName.equals(controller.getLocalName())) nodeButton.setForeground(Color.WHITE);
            else nodeButton.setForeground(Color.ORANGE);
            nodeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Cambio de nodo (Panel)
                    controller.showFilesPanel(((JButton)e.getSource()).getText());
                }
            });

            nodeButtons.put(nodeName, nodeButton);
            add(nodeButton);
        }
    }

    public void setConnected(String nodeName, boolean value) {
        if (value) nodeButtons.get(nodeName).setForeground(Color.GREEN);
        else nodeButtons.get(nodeName).setForeground(Color.ORANGE);
    }
}