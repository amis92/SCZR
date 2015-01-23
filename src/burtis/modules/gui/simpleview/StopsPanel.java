package burtis.modules.gui.simpleview;

import java.awt.FlowLayout;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import burtis.common.mockups.MockupBusStop;
import burtis.modules.gui.View;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusStopEvent;

public class StopsPanel extends JPanel
{
    private static final long serialVersionUID = -2853079806846633380L;
    private final static Logger logger = Logger.getLogger(View.class.getName());
    private final BlockingQueue<ProgramEvent> bQueue;
    private List<MockupBusStop> lastStopList;

    public StopsPanel(BlockingQueue<ProgramEvent> bQueue)
    {
        this.bQueue = bQueue;
    }

    public void refresh(List<MockupBusStop> freshList)
    {
        if (stopListChanged(freshList))
        {
            createStops(freshList);
        }
    }

    private void createStops(List<MockupBusStop> freshList)
    {
        this.lastStopList = freshList;
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        final ImageIcon icon = new ImageIcon("bus_stop.png");
        for (MockupBusStop stop : lastStopList)
        {
            final String stationName = stop.getName();
            final JButton stopButton = new JButton(stationName, icon);
            this.add(stopButton);
            stopButton.addActionListener(e ->
            {
                try
                {
                    bQueue.put(new ShowBusStopEvent(stationName));
                }
                catch (InterruptedException ex)
                {
                    logger.warning("Couldn't send ShowBusStopEvent.");
                }
            });
        }
    }

    /**
     * Checks whether both new and last used list diverged.
     * 
     * @param freshList
     *            - the new list of passengers.
     * @return true if they did, false if not.
     */
    private boolean stopListChanged(List<MockupBusStop> freshList)
    {
        if (lastStopList == null || lastStopList.size() != freshList.size())
        {
            return true;
        }
        int size = lastStopList.size();
        for (int index = 0; index < size; ++index)
        {
            if (!lastStopList.get(index).equals(freshList.get(index)))
            {
                return true;
            }
        }
        return false;
    }
}
