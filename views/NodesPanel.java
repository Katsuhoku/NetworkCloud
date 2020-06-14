package views;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import controller.Controller;

public class NodesPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JButton[] nodes;
    private Controller controller;

    public NodesPanel(Controller controller, ArrayList<String> nodeNames) {
        super(new GridLayout(nodeNames.size(), 0));
        this.controller = controller;
        init(nodeNames);
    }

    private void init(ArrayList<String> nodeNames) {
        nodes = new JButton[nodeNames.size()];

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new JButton(nodeNames.get(i));
            nodes[i].setPreferredSize(new Dimension(100,25));
            nodes[i].setBackground(new Color(49, 54, 63));
            nodes[i].setForeground(Color.WHITE);
            nodes[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //Cambio de nodo (Panel)
                    //Llamar al controlador
                    controller.showFilesPanel(((JButton)e.getSource()).getText());
                }
            });
            add(nodes[i]);
        }
    }
}