package views;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;

import controller.Controller;



public class FilesPanel extends JPanel{

    private static final long serialVersionUID = 1L;
    private JButton backButton;
    private JTextField pathField;
    private JTable table;
    
    private Controller controller;

    //COMO RECIBIRA LA INFORMACION?
    //FALTAN EVENTOS
    public FilesPanel(Controller controller){
        super(new GridBagLayout());
        this.controller = controller;
        init();
    }
    
    private void init(){
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        backButton = new JButton("<-");
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.04;
        c.weightx = 0.0001;
        c.fill = GridBagConstraints.BOTH;
        add(backButton, c);
        
        
        c = new GridBagConstraints();
        pathField = new JTextField("Ruta");
        pathField.setBackground(Color.BLACK);
        pathField.setForeground(Color.GREEN);
        pathField.setEditable(false);
        pathField.setBorder(null);
        JScrollPane pathFieldScroll = new JScrollPane(pathField, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pathFieldScroll.setPreferredSize(new Dimension(0,35));
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.04;
        c.fill = GridBagConstraints.BOTH;
        add(pathFieldScroll, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        table = new JTable(new DefaultTableModel(new Object[][] {{"ESIEL", "FECHA", "DIR"}, {"MARCO", "FECHA", "FILE"}}, new String[] {"Filename", "Last Modified", "Type"}){
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        });
        JScrollPane tableScroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(tableScroll, c);
    }

}