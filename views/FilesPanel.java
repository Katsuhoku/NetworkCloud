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
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.Color;

import controller.Controller;
import model.Operation;

public class FilesPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JButton backButton;
    private JTextField pathField;
    private JTable table;
    private JTextArea messages;

    private String nodeName;
    private Controller controller;


    public FilesPanel(Controller controller, String nodeName) {
        super(new GridBagLayout());
        this.controller = controller;
        this.nodeName = nodeName;
        init();
    }

    private void init() {
        GridBagConstraints c;

        c = new GridBagConstraints();
        backButton = new JButton("<-");
        backButton.setEnabled(false);
        backButton.setBackground(new Color(49, 54, 63));
        backButton.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.backEvent();
            }
        });
        add(backButton, c);

        c = new GridBagConstraints();
        pathField = new JTextField(nodeName);
        pathField.setBackground(new Color(39, 44, 53));
        pathField.setForeground(Color.GREEN);
        pathField.setEditable(false);
        pathField.setBorder(null);
        JScrollPane pathFieldScroll = new JScrollPane(pathField, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pathFieldScroll.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(69, 74, 83);
                this.trackColor = new Color(39, 44, 53);
            }

            private JButton createZeroButton() {
                JButton b = new JButton();
                b.setMinimumSize(new Dimension(0, 0));
                b.setPreferredSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
        });
        pathFieldScroll.setPreferredSize(new Dimension(0, 0));
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
        table = new JTable(new DefaultTableModel(new String[] { "Filename", "Last Modified", "isDirectory" }, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        table.setFillsViewportHeight(true);
        table.getTableHeader().setBackground(new Color(69, 74, 83));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setBackground(new Color(39, 44, 53));
        table.setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionada
        table.removeColumn(table.getColumnModel().getColumn(2));
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    setOperationsButtons();
                }
            }
        });
        JScrollPane tableScroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(69, 74, 83);
                this.trackColor = new Color(39, 44, 53);
            }

            private JButton createZeroButton() {
                JButton b = new JButton();
                b.setMinimumSize(new Dimension(0, 0));
                b.setPreferredSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
        });

        tableScroll.setPreferredSize(new Dimension(0, 0));
        add(tableScroll, c);

        c = new GridBagConstraints();
        messages = new JTextArea();
        messages.setEditable(false);
        messages.setBackground(new Color(39, 44, 53));
        messages.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 0.15;
        c.fill = GridBagConstraints.BOTH;
        
        JScrollPane messagesScroll = new JScrollPane(messages, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        messagesScroll.setPreferredSize(new Dimension(0, 0));
        messagesScroll.getVerticalScrollBar().setUI(new BasicScrollBarUI(){
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected void configureScrollBarColors(){
                this.thumbColor = new Color(69, 74, 83);
                this.trackColor = new Color(39, 44, 53);
            }

            private JButton createZeroButton(){
                JButton b = new JButton();
                b.setMinimumSize(new Dimension(0,0));
                b.setPreferredSize(new Dimension(0,0));
                b.setMaximumSize(new Dimension(0,0));
                return b;
            }
        });

        messagesScroll.getHorizontalScrollBar().setUI(new BasicScrollBarUI(){
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected void configureScrollBarColors(){
                this.thumbColor = new Color(69, 74, 83);
                this.trackColor = new Color(39, 44, 53);
            }

            private JButton createZeroButton(){
                JButton b = new JButton();
                b.setMinimumSize(new Dimension(0,0));
                b.setPreferredSize(new Dimension(0,0));
                b.setMaximumSize(new Dimension(0,0));
                return b;
            }
        });

        add(messagesScroll, c);
    }

    public void setBackButtonEnable(boolean b){
        backButton.setEnabled(b);
    }
    
    public void updateTableData(ArrayList<String> files){
        DefaultTableModel d = (DefaultTableModel) table.getModel();
        d.setRowCount(0); //Elimita todos las celdas antiguas
        for (String file : files){
            String[] data = file.split(Operation.SEPARATOR);
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            d.addRow(new Object[] {data[0], df.format(new Date(Long.parseLong(data[1]))), Boolean.parseBoolean(data[2])});
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

    public String getPath(){
        String[] p = pathField.getText().split("/", 2);
        return (p.length > 1) ? p[1] : "";
    }

    public void updatePath(String path){
        pathField.setText(nodeName + path);
    }

    public String getSelectedFilename(){
        return (String)table.getModel().getValueAt(table.getSelectedRow(), 0);
    }

    // To print an error
    public void errorMessage(String msg) {
        messages.setForeground(Color.RED);
        messages.setText(msg);
    }

    // To print some other messages
    public void putMessage(String msg) {
        messages.setForeground(Color.CYAN);
        messages.setText(msg);
    }

}