package burtis.modules.gui.simpleview;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import burtis.common.mockups.MockupBus;
import burtis.modules.gui.View;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusEvent;

public class BusProgressPanel extends JPanel
{
    private static final long serialVersionUID = 7949410421119596681L;
    private BlockingQueue<ProgramEvent> bQueue;
    private final static Logger logger = Logger.getLogger(View.class.getName());
    private List<MockupBus> lastUsedList;
    private List<MyProgressBar> progressBars;

    public BusProgressPanel(BlockingQueue<ProgramEvent> bQueue)
    {
        this.bQueue = bQueue;
    }

    public void refresh(List<MockupBus> freshList)
    {
        if (busListChanged(freshList))
        {
            createBars(freshList);
        }
        updateValues(freshList);
    }

    private void updateValues(List<MockupBus> freshList)
    {
        lastUsedList = freshList;
        for (int i = 0; i < freshList.size(); ++i)
        {
            MyProgressBar pb = progressBars.get(i);
            MockupBus currentBus = freshList.get(i);
            pb.setString(currentBus.toString(true));
            pb.setValue(currentBus.getLengthPassed());
        }
    }

    private void createBars(List<MockupBus> freshList)
    {
        progressBars = new ArrayList<MyProgressBar>(freshList.size());
        removeAll();
        setLayout(new GridLayout(freshList.size(), 1));
        for (int i = 0; i < freshList.size(); ++i)
        {
            MyProgressBar pb = new MyProgressBar(i);
            pb.setForeground(Color.BLUE);
            pb.setStringPainted(true);
            pb.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e)
                {
                    logger.info("ShowBusEvent, busId: " + pb.getId());
                    putInQueue(new ShowBusEvent(pb.getId()));
                }

                public void mouseEntered(MouseEvent e)
                {
                    pb.setForeground(Color.RED);
                }

                public void mouseExited(MouseEvent e)
                {
                    pb.setForeground(Color.BLUE);
                }
            });
            progressBars.add(pb);
            this.add(pb);
        }
    }

    private boolean busListChanged(List<MockupBus> freshList)
    {
        if (progressBars != null && lastUsedList.size() == freshList.size())
        {
            for (int i = 0; i < freshList.size(); ++i)
            {
                if (!freshList.get(i).equals(lastUsedList.get(i)))
                    return false;
            }
        }
        return true;
    }

    /**
     * Metoda pozwalająca na włożenie obiektu odpowiadającego za zdarzenie do
     * kolejki akcji do wykonania
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

class MyProgressBar extends JProgressBar
{
    private static final long serialVersionUID = -5387793725988281756L;
    private Integer Id;

    public MyProgressBar(Integer Id)
    {
        this.Id = Id;
    }

    public Integer getId()
    {
        return Id;
    }
}
