package views;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.Color;

import controller.Controller;
import model.Operation;



public class FilesPanel extends JPanel{

    private static final long serialVersionUID = 1L;
    private JButton backButton;
    private JTextField pathField;
    private JTable table;
    private JTextArea messages;
    
    private String nodeName;

    private Controller controller;

    //COMO RECIBIRA LA INFORMACION?
    //FALTAN EVENTOS
    public FilesPanel(Controller controller, String nodeName){
        super(new GridBagLayout());
        this.controller = controller;
        this.nodeName = nodeName;
        init();
    }
    
    private void init(){
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        backButton = new JButton("<-");
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        add(backButton, c);
        
        
        c = new GridBagConstraints();
        pathField = new JTextField(nodeName);
        pathField.setBackground(Color.BLACK);
        pathField.setForeground(Color.GREEN);
        pathField.setEditable(false);
        pathField.setBorder(null);
        JScrollPane pathFieldScroll = new JScrollPane(pathField, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pathFieldScroll.setPreferredSize(new Dimension(0,0));
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
        c.gridheight = 1;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        table = new JTable(new DefaultTableModel(new String[] {"Filename", "Last Modified", "isDirectory"}, 0){
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //Solo una fila seleccionada
        table.removeColumn(table.getColumnModel().getColumn(2));
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    //True - Diretory
                    //False - Any other file
                    setOperationsButtons();
                }
            }
        });
         /*              ELIMINAR                */
         DefaultTableModel d = (DefaultTableModel) table.getModel();
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 1", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         d.addRow(new Object[] {"Archivo 2", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), false});
         d.addRow(new Object[] {"Archivo 3", new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())), true});
         /**/
        JScrollPane tableScroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setPreferredSize(new Dimension(0, 0));
        add(tableScroll, c);

        c = new GridBagConstraints();
        messages = new JTextArea();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 0.1;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane messagesScroll = new JScrollPane(messages, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        messagesScroll.setPreferredSize(new Dimension(0, 0));
        add(messagesScroll, c);
    }

    public void setBackButtonEnable(boolean b){
        backButton.setEnabled(b);
    }

    public void updatePath(String path){
        pathField.setText(path);
    }
    
    public void updateTableData(ArrayList<String> files){
        DefaultTableModel d = (DefaultTableModel) table.getModel();
        d.setRowCount(0);
        for (String file : files){
            String[] data = file.split(Operation.SEPARATOR);
            DateFormat df = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
            d.addRow(new Object[] { data[0], df.format(new Date(Long.parseLong(data[1]))), Boolean.parseBoolean(data[2])});
        }
    }


    /*CONTROL DE OPERATIONS BUTTONS */
    public void setOperationsButtons(){
        int row = table.getSelectedRow();
        if (row == -1) {
            controller.setOpenButtonEnable(false);
            controller.setSendButtonEnable(false);
            controller.setDeleteButtonEnable(false);
        }else{
            boolean b = (boolean) table.getModel().getValueAt(row, 2);
            controller.setDeleteButtonEnable(true);
            controller.setOpenButtonEnable(b);
            controller.setSendButtonEnable(!b);
        }
        
    }

}