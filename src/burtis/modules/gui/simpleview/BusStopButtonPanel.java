package burtis.modules.gui.simpleview;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.swing.JPanel;

import burtis.common.mockups.MockupBusStop;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusStopEvent;

public class BusStopButtonPanel extends JPanel
{
    private static final long serialVersionUID = -2853079806846633380L;
    BlockingQueue<ProgramEvent> bQueue;

    public BusStopButtonPanel(BlockingQueue<ProgramEvent> bQueue)
    {
        this.bQueue = bQueue;
    }

    public void refresh(List<MockupBusStop> busStopList)
    {
        removeAll();
        setLayout(new GridLayout(1, busStopList.size()));
        Iterator<MockupBusStop> it = busStopList.iterator();
        while (it.hasNext())
        {
            MockupBusStop mbs = it.next();
            String stationName = mbs.getName();
            BusStopButton bsb = new BusStopButton(stationName, bQueue);
            bsb.getButton().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event)
                {
                    try
                    {
                        bQueue.put(new ShowBusStopEvent(stationName));
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            add(bsb);
        }
    }
}
