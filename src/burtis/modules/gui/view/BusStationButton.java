package burtis.modules.gui.view;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusStopEvent;

class BusStationButton extends JPanel
{
    private static final long serialVersionUID = 1L;
    /* Kolejka, do ktorej wrzucamy obiekty odpowidajace eventom */
    private LinkedBlockingQueue<ProgramEvent> bQueue;
    private final String stationName;
    final JButton button;

    public BusStationButton(final String stationName,
            final LinkedBlockingQueue<ProgramEvent> bQueue)
    {
        super(new FlowLayout());
        this.bQueue = bQueue;
        this.stationName = stationName;
        ImageIcon icon = new ImageIcon("bus_stop.png");
        button = new JButton(stationName, icon);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                System.out.println(stationName);
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
