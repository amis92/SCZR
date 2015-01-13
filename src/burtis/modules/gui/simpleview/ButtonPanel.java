package burtis.modules.gui.simpleview;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.BlockingQueue;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import burtis.modules.gui.events.ProgramEvent;

public class ButtonPanel extends JPanel
{
    private BlockingQueue<ProgramEvent> bQueue;
    private JButton addPassenger = new JButton("Add Passenger");
    private JButton setMinGenTime = new JButton("MinGenTime");    
    private JButton setMaxGenTime = new JButton("MaxGenTime");    
    
    public ButtonPanel(BlockingQueue<ProgramEvent> bQueue) {
        this.bQueue = bQueue;
        
        add(addPassenger);
        add(setMinGenTime);
        add(setMaxGenTime);
        
        addPassenger.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextField passengerId = new JTextField(2);
                JTextField passengerOrigin = new JTextField(15);
                JTextField passengerDestin = new JTextField(15);
                
                Object[] params = { new JLabel("Id pasazera"), 
                                    passengerId, 
                                    new JLabel("Stacja bazowa"),
                                    passengerOrigin, 
                                    new JLabel("Stacja docelowa"),
                                    passengerDestin};
                
                int d = JOptionPane.showConfirmDialog(getParent(), params, "Dodaj pasazera", 
                                                        JOptionPane.OK_CANCEL_OPTION);
                
                if(d == 0) {
                    JOptionPane.showMessageDialog(null, "Dodales pasazera");
                }
                
                // A teraz tak serio - jak dodac tego pasazera???
            }
        });
        
        setMinGenTime.addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent e) {
               JTextField minGenTime = new JTextField(10);
               
               Object[] params = { new JLabel("minGenTime"), 
                                   minGenTime};
               
               int d = JOptionPane.showConfirmDialog(getParent(), params, "Ustaw minGenTime", 
                       JOptionPane.OK_CANCEL_OPTION);
               
               if(d == 0) {
                   JOptionPane.showMessageDialog(null, "Ustawiles minGenTime na " + minGenTime.getText());
               }
               
               // jak to ustawic ???
           }
        });
        
        setMaxGenTime.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextField maxGenTime = new JTextField(10);
                
                Object[] params = { new JLabel("maxGenTime"), 
                                    maxGenTime};
                
                int d = JOptionPane.showConfirmDialog(getParent(), params, "Ustaw maxGenTime", 
                        JOptionPane.OK_CANCEL_OPTION);
                
                if(d == 0) {
                    JOptionPane.showMessageDialog(null, "Ustawiles maxGenTime na " + maxGenTime.getText());
                }
                
                // jak to ustawic ???
            }
         });        
        
        
        
        
        
        
    }

}
