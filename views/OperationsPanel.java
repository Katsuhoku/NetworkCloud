package views;

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
        init();
    }

    public void init() {
        openButton = new JButton("Open");
        openButton.setEnabled(false);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Solo dsponible cuando un directorio este seleccionado
                //Pedir directorio
                //Actualizar directorio
            }
            
        });

        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Solo disponible cuando un archivo este seleccionado
                //??
            }
            
        });

        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Elimina ya sea directorios o archivos, ya sean locales o remotos
                //??
            }
            
        });

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Actualiza directorio actual
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