package burtis.modules.gui.simpleview;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.modules.gui.events.ProgramEvent;

public class ProgressBarPanel extends JPanel 
{
    private BlockingQueue<ProgramEvent> bQueue;
    private List<MockupBus> schedule = null;
    
    public ProgressBarPanel(BlockingQueue<ProgramEvent> bQueue) 
    {
        this.bQueue = bQueue;
        
        
    }
    
    public void refreshProgressBarPanel(List<MockupBus> list) 
    {
        this.schedule = list;
        setLayout(new GridLayout(list.size(), 1));
        
        for(int i=0; i<list.size(); i++) 
        {
            JProgressBar pb = new JProgressBar();
            MockupBus currentBus = list.get(i);
            
            this.add(pb);
            
            pb.setForeground(Color.BLUE);
            pb.setStringPainted(true);
            pb.setValue(currentBus.getLengthPassed());
            pb.setString(currentBus.getId().toString());
            
            pb.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    System.out.println("Progress Bar");
                }
                
                public void mouseEntered(MouseEvent e) {
                    //System.out.println("Entered Bar");
                    pb.setForeground(Color.RED);
                }

                public void mouseExited(MouseEvent e) {
                    //System.out.println("Exited Bar");
                    pb.setForeground(Color.BLUE);
                }
            });
        }
    }

}