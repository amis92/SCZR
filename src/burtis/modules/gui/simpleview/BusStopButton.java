package burtis.modules.gui.simpleview;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusStopEvent;

class BusStopButton extends JPanel
{
    private BlockingQueue<ProgramEvent> bQueue;
    private final String stationName;
    final JButton button;

    public BusStopButton(final String stationName,
                         final BlockingQueue<ProgramEvent> bQueue)
    {
        super(new FlowLayout());
        this.bQueue = bQueue;
        this.stationName = stationName;
        
        ImageIcon icon = new ImageIcon("bus_stop.png");
        button = new JButton(stationName, icon);
        button.addMouseListener(new MouseAdapter() {
        
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
        add(button);
        // button.setToolTipText("Oczekuje: ");
    }

    public JButton getButton()
    {
        return button;
    }
}
