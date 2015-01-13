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
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusEvent;

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
            MyProgressBar pb = new MyProgressBar(i);
            MockupBus currentBus = list.get(i);
            
            this.add(pb);
            
            pb.setForeground(Color.BLUE);
            pb.setStringPainted(true);
            pb.setValue(currentBus.getLengthPassed());
            pb.setString( currentBus.getId().toString() );
            
            pb.setString("Bus Id: "     + currentBus.getId().toString() + 
                         ", Progress: "   + currentBus.getLengthPassed() + 
                         ", Bus State: "  + currentBus.getState());
            
            pb.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    System.out.println( pb.getId() );
                    putInQueue(new ShowBusEvent( pb.getId() ) );
                    
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

    /**
     * Metoda pozwalająca na włożenie obiektu odpowiadającego za zdarzenie do kolejki akcji do wykonania
     * 
     * @param e
     */
    private void putInQueue(ProgramEvent ev)
    {
        try
        {
            bQueue.put(ev);
        }
        catch (InterruptedException err)
        {
            throw new RuntimeException();
        }
    }   
}

class MyProgressBar extends JProgressBar {
    private Integer Id;
    
    public MyProgressBar(Integer Id) {
        this.Id = Id;
    }
    
    public Integer getId() {
        return Id;
    }
}