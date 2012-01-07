/*
 * 
 * Title: Permutor
 * Version: v 0.1
 * Author: Nick Mulder
 * Date: 01-01-2012
 * 
 * TODO:
 * - pretty up the aboutDialog, looks terrible
 * - Change window icon, use cool anime avatar
 * - "Bail out" button for (too) long strings
 * - Implement some kind of timer system?
 * - Save/Export list (CSV filetype)
 * 
 * DEBUG:
 * - get mainText to auto select on open (Driving me nuts!)
 * - fix 'this' leaks in constructor (not smart enough)
 * - Many more, I'm sure

 */
package permutor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import javax.swing.*;
import java.util.*;

import javax.activation.MimetypesFileTypeMap;
import permute.*;
import reduc.*;

public class Main {

    public static void main(String[] args) {
        Permutor p = new Permutor();
    }
}

class Permutor implements ActionListener, KeyListener {

    final static double VERSION = 0.01;

    /* Permutor Object vaiables */
    JFrame mainFrame = new JFrame("Permutor");
    ImageIcon icon = new ImageIcon("/src/mini.avatar.gif"); // FIXME
    Image image = icon.getImage();                          // FIXME
    JPanel toolBox = new JPanel();
    final JTextField mainText = new JTextField("Text goes here");
    final JButton permuteButton = new JButton("Permute!");
    final JTextArea listingArea = new JTextArea(20, 1);
    // Reduc feature accessories
    final JCheckBox reducCheckBox = new JCheckBox();
    JSpinner reducSpinner = new JSpinner();
    JLabel reducLabel = new JLabel("Reduc?");

    /*  CONSTRUCTOR */
    protected Permutor() {

        mainFrame.setIconImage(image);
        listingArea.setEditable(false);
        permuteButton.addActionListener(this);
        mainText.addKeyListener(this);

        reducSpinner.setEnabled(false);
        reducSpinner.setMaximumSize(new Dimension(45, 23));

        // Create Menu items
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Export to CSV");
        JMenuItem quitItem = new JMenuItem("Quit");

        saveItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                exportCSV();
            }
        });
        quitItem.addActionListener(new ActionListener() {
            /* anonymous class */

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(saveItem);
        fileMenu.add(quitItem);
        JMenu viewMenu = new JMenu("View");
        ButtonGroup buttonGroup = new ButtonGroup();


        // assign array of LookAndFeels from the system to lnfInfo
        // Type = UIManager.LookAndFeelInfo
        final UIManager.LookAndFeelInfo[] lnfInfo =
                UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < lnfInfo.length; i++) {
            JRadioButtonMenuItem item =
                    new JRadioButtonMenuItem(lnfInfo[i].getName(), i == 0);
            final String className = lnfInfo[i].getClassName();
            item.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae1) {
                    try {
                        UIManager.setLookAndFeel(className);
                    } catch (Exception xe) {
                        System.out.println(xe);
                    }
                    SwingUtilities.updateComponentTreeUI(mainFrame);
                }
            });
            buttonGroup.add(item);
            viewMenu.add(item);
        }
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(new ActionListener() {
            // What to do when 'About' item is "clicked"

            public void actionPerformed(ActionEvent e) {

                showAboutDialog();
            }
        });

        helpMenu.add(aboutItem);

        JMenuBar mb = new JMenuBar();
        mb.add(fileMenu);
        mb.add(viewMenu);
        mb.add(helpMenu);


        // Create GUI
        toolBox.setLayout(new BoxLayout(toolBox, BoxLayout.X_AXIS));
        toolBox.add(permuteButton);
        toolBox.add(Box.createRigidArea(new Dimension(75, 0)));
        toolBox.add(reducLabel);
        toolBox.add(reducCheckBox);
        toolBox.add(reducSpinner);

        mainText.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (mainText.getText().length() != reducSpinner.getValue()) {
                    reducSpinner.setModel(
                            new SpinnerNumberModel(mainText.getText().length(),
                            2, mainText.getText().length(), 1));
                }

            }
        });


        // Reduc accessories implementation
        reducCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent l) {
                if (reducCheckBox.isSelected() == false) {
                    reducSpinner.setEnabled(false);
                    reducSpinner.setValue(0);
                } else {
                    reducSpinner.setEnabled(true);
                    reducSpinner.setValue(mainText.getText().length());
                }
            }
        });

        // Set up the mainFrame
        mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane(),
                BoxLayout.Y_AXIS));
        mainFrame.add(toolBox);
        mainFrame.setJMenuBar(mb);
        mainFrame.add(mainText);
        mainFrame.add(new JScrollPane(listingArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        mainFrame.setSize(300, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainText.selectAll();   // FIXME (Pleeeeaase!)

    }

    public void actionPerformed(ActionEvent e) {
        if (mainText.getText().length() != reducSpinner.getValue()) {

            try {
                reducSpinner.commitEdit();
            } catch (ParseException pe) {
                JOptionPane op = new JOptionPane("Error:"
                        + pe, JOptionPane.WARNING_MESSAGE);
                op.setVisible(true);    // Remove line
            }
        }
        if (mainText.getText().length() > 10) {
            int result = JOptionPane.showConfirmDialog(mainFrame,
                    "Permuting strings longer than 10 characters could render \n"
                    + "youR computer unstable. Do you wish to continue?",
                    "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            switch (result) {
                case JOptionPane.YES_OPTION:
                    doPerm();
                    mainText.selectAll();
                    break;
                case JOptionPane.NO_OPTION:
                    break;
                case JOptionPane.CANCEL_OPTION:
                    break;
                default:
                    break;

            }
        } else {
            doPerm();
        }
    }       // End actionPerformed()

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            if (mainText.getText().length() > 10) {
                int result = JOptionPane.showConfirmDialog(mainFrame, "Permuting string longer than 10 characters could render \n"
                        + "your computer unstable. Do you wish to continue?");
                switch (result) {
                    case JOptionPane.YES_OPTION:
                        doPerm();
                        mainText.selectAll();
                        break;
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        break;
                    default:
                        break;

                }
            } else {
                doPerm();
            }
        }
    }

    private void doPerm() {
        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        listingArea.setText("");   // needs to clear JTextArea !!!
        String str = mainText.getText();
        List<String> pList = new ArrayList();

        if (reducCheckBox.isSelected()
                && ((Integer) reducSpinner.getValue()
                < mainText.getText().length())) {
            Permute perm = new Permute(str);    //Instantiate Permute
            pList = perm.getList();     // Commit to pList
            try {
                reducSpinner.commitEdit();
                int value = (Integer) reducSpinner.getValue();           // DEBUG
                System.err.printf("Value of reducSpinner: " + value + "\n");   // DEBUG
            } catch (ParseException pe) {                             // DEBUG
                System.err.printf("Error: " + pe);                  // DEBUG
            }

            Reduc r = new Reduc(str, (Integer) reducSpinner.getValue());

            String[] list = r.getArray();
            for (String z : list) {
                perm = new Permute(z);
                Iterator p_It = perm.getList().iterator();
                while (p_It.hasNext()) {
                    pList.add((String) p_It.next());     // Commit to pList
                }
            }
        } else {

            Permute perm = new Permute(str);
            pList = perm.getList();     // Commit to pList
        }
        for (String next : pList) {
            listingArea.append(next + "\n");
        }

        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        mainText.selectAll();
    }

    private void showAboutDialog() {
        JLabel titleLabel = new JLabel("Permutor v" + VERSION);
        final JDialog aboutDiag = new JDialog(mainFrame, "About");
        aboutDiag.setSize(400, 250);
        aboutDiag.add(titleLabel);
        aboutDiag.setVisible(true);
    }

    private boolean exportCSV() {
        FileDialog saveDialog = new FileDialog(mainFrame, "Export to", FileDialog.SAVE);
        saveDialog.setVisible(true);
        StringBuilder sb = new StringBuilder();
        
        // Pointless -- can't bring myself to take it out though :(
        MimetypesFileTypeMap fileTypeOf = new MimetypesFileTypeMap();
        
        // Assess if file already has .csv extention
        File saveFile;
        String file = saveDialog.getFile();
        if( !lastFour(file).equals(".csv")){
            saveFile = new File(saveDialog.getDirectory() +
                    file + ".csv");
        } else {
            saveFile = new File(saveDialog.getDirectory() + file);
        }
        System.err.print(fileTypeOf.getContentType(saveFile) + "\n");

        sb.append(listingArea.getText());

        try {

            FileWriter fw = new FileWriter(saveFile);
            PrintWriter pw = new PrintWriter(fw);
            String csvString = sb.toString();
            pw.print(csvString);
            fw.close();

        } catch (IOException ioe) {
            System.err.print("Error: " + ioe + "\n");
            return false;
        }
        return true;
    }
    
    private String lastFour(String s){
        String result;
        result = s.substring(s.length() - 4);
        return result;
    }
}
