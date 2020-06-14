package views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JPanel;
import controller.Controller;

public class MainPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private NodesPanel nodesPanel;
    private OperationsPanel operationsPanel;

    private JPanel filesPanelsParent;
    private HashMap<String, FilesPanel> filesPanels;
    private CardLayout cards;

    private Controller controller;

    public MainPanel(Controller controller) {
        super(new GridBagLayout());
        this.controller = controller;
        setBackground(new Color(32, 37, 44));
        init();
    }

    private void init() {
        GridBagConstraints c;

        c = new GridBagConstraints();
        // MODIFIRCAR NODE NAMES
        nodesPanel = new NodesPanel(controller, new String[] { "1", "2", "3", "4" });
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTH;
        //c.fill = GridBagConstraints.VERTICAL;
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
        
        
        //Se utilizan tarjetas y una tabla hash 
        
        cards = new CardLayout();
        filesPanels = new HashMap<>();
        filesPanelsParent = new JPanel(cards);
        //MODIFICAR NODE NAMES
        for (String nodeName : new String[] {"1", "2","3", "4"}){
            FilesPanel fp = new FilesPanel(controller, nodeName + "/");
            filesPanels.put(nodeName, fp);
            filesPanelsParent.add(fp, nodeName);
        }
            
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        cards.show(filesPanelsParent, "Nodo 1");
        add(filesPanelsParent, c);
    }


    public OperationsPanel getOperationsPanel(){
        return operationsPanel;
    }

    public NodesPanel getNodesPanel(){
        return nodesPanel;
    }

    public FilesPanel getFilesPanel(String nodeName){
        return filesPanels.get(nodeName);
    }

    public void showFilesPanel(String nodeName){
        cards.show(filesPanelsParent, nodeName);
    }
    
}