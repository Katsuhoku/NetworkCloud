package views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import controller.Controller;

public class MainPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private NodesPanel nodesPanel;
    private OperationsPanel operationsPanel;
    private FilesPanel filesPanel;

    private Controller controller;
    
    
    public MainPanel(Controller controller){
       super(new GridBagLayout());
       this.controller = controller;
       init();
    }
    
    private void init(){
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        nodesPanel = new NodesPanel(controller, new String[] {"Nodo 1", "Nodo 2","Nodo 3", "Nodo 4"});
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.VERTICAL;
        add(nodesPanel, c);
        
        
        c = new GridBagConstraints();
        operationsPanel = new OperationsPanel(controller);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(operationsPanel, c);
        
        
        //Utilizar tarjetas
        c = new GridBagConstraints();
        filesPanel = new FilesPanel(controller);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        add(filesPanel, c);
    }
    
}