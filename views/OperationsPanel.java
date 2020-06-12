package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import controller.Controller;

public class OperationsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JButton openButton;
    private JButton sendButton;
    private JButton deleteButton;
    private JButton createDirButton;
    private Controller controller;

    public OperationsPanel(Controller controller) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.controller = controller;
        init();
    }

    public void init() {
        openButton = new JButton("Open");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Solo dsponible cuando un directorio este seleccionado
                //Pedir directorio
                //Actualizar directorio
            }
            
        });

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Solo disponible cuando un archivo este seleccionado
                //??
            }
            
        });

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Elimina ya sea directorios o archivos, ya sean locales o remotos
                //??
            }
            
        });

        createDirButton = new JButton("Create Directory");
        createDirButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Crea directorio ya sea local o remoto
                //??

            }
            
        });

        add(openButton);
        add(sendButton);
        add(deleteButton);
        add(createDirButton);
    }
}