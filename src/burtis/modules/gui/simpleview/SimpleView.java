package burtis.modules.gui.simpleview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowListener;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.modules.gui.View;
import burtis.modules.gui.events.ConnectEvent;
import burtis.modules.gui.events.DisconnectEvent;
import burtis.modules.gui.events.GoEvent;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.StepEvent;
import burtis.modules.gui.events.StopEvent;

public class SimpleView implements View
{
    private long currentTime = 0;
    private final PassengerInfoPanel passengerInfoPanel = new PassengerInfoPanel();
    private final static Logger logger = Logger.getLogger(View.class.getName());
    private final LinkedBlockingQueue<ProgramEvent> bQueue;
    // private List<MockupBusStop> busStops;
    /**
     * Toolbar and buttons
     */
    private final JButton connectionButton = new JButton();
    private final JLabel timeLabel = new JLabel("    Time: "
            + Long.toString(currentTime));
    private final JLabel connectedLabel = new JLabel();
    private final ProgressBarPanel progressBarPanel;
    private final BusStopButtonPanel stopsButtonPanel;
    private Mockup mockup;
    private final Supplier<Boolean> isConnected;

    public SimpleView(LinkedBlockingQueue<ProgramEvent> bQueue,
            WindowListener exitListener, Supplier<Boolean> isConnected)
    {
        this.bQueue = bQueue;
        this.isConnected = isConnected;
        final JFrame frame = new JFrame();
        if (exitListener == null)
        {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        else
        {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(exitListener);
        }
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
        frame.setSize(800, 600);
        frame.setTitle("burtis");
        // status flow toolbar
        JButton goButton = new JButton("Go");
        JButton stepButton = new JButton("Pause/Step");
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> putInQueue(new StopEvent()));
        goButton.addActionListener(e -> putInQueue(new GoEvent()));
        stepButton.addActionListener(e -> putInQueue(new StepEvent()));
        connectionButton.addActionListener(e -> tryChangeConnectionStatus());
        connectionButton.setBackground(Color.GREEN);
        refreshConnectionStatus();
        JToolBar statusToolbar = new JToolBar();
        statusToolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusToolbar.add(connectionButton);
        statusToolbar.add(stopButton);
        statusToolbar.add(goButton);
        statusToolbar.add(stepButton);
        statusToolbar.add(timeLabel);
        statusToolbar.add(connectedLabel);
        statusToolbar.setRollover(true);
        // management toolbar
        ButtonPanel buttonPanel = new ButtonPanel(bQueue, () -> mockup,
                isConnected);
        JPanel toolbars = new JPanel(new GridLayout(0, 1));
        toolbars.add(statusToolbar, BorderLayout.PAGE_START);
        toolbars.add(buttonPanel, BorderLayout.CENTER);
        // bus and stops panels
        JScrollPane busScrollPanel = new JScrollPane();
        progressBarPanel = new ProgressBarPanel(bQueue);
        stopsButtonPanel = new BusStopButtonPanel(bQueue);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(stopsButtonPanel, BorderLayout.PAGE_START);
        panel.add(progressBarPanel, BorderLayout.CENTER);
        busScrollPanel.getViewport().add(panel);
        // frame setup
        frame.add(toolbars, BorderLayout.PAGE_START);
        frame.add(busScrollPanel, BorderLayout.CENTER);
        frame.add(passengerInfoPanel, BorderLayout.PAGE_END);
    }

    public void updateBusInfoPanel(Integer i)
    {
        for (MockupBus mb : mockup.getBuses())
        {
            if (mb.getId().equals(i))
            {
                passengerInfoPanel.showForBus(i, mb.getPassengerList());
                return;
            }
        }
        logger.warning("Can't show BusInfoPanel for busId=" + i.toString());
    }

    public void updateBusStopInfoPanel(String s)
    {
        for (MockupBusStop mbs : mockup.getBusStops())
        {
            if (mbs.getName().equals(s))
            {
                passengerInfoPanel.showForBusStop(s, mbs.getPassengerList());
                return;
            }
        }
        logger.warning("Can't show BusStopInfoPanel for busStop=" + s);
    }

    public void refresh(Mockup mockup)
    {
        this.mockup = mockup;
        progressBarPanel.refresh(mockup.getBuses());
        stopsButtonPanel.refresh(mockup.getBusStops());
        currentTime = mockup.getCurrentTime();
        timeLabel.setText("    Time: " + Long.toString(currentTime));
    }

    private void tryChangeConnectionStatus()
    {
        boolean isConnected = this.isConnected.get();
        connectionButton.setText(isConnected ? "Disconnecting..."
                : "Connecting...");
        connectionButton.setBackground(Color.YELLOW);
        setConnectionStatusText("WAITING");
        putInQueue(isConnected ? new DisconnectEvent() : new ConnectEvent());
    }

    public void refreshConnectionStatus()
    {
        boolean isConnected = this.isConnected.get();
        connectionButton.setText(isConnected ? "Disconnect" : "Connect");
        connectionButton.setBackground(isConnected ? Color.RED : Color.GREEN);
        setConnectionStatusText((isConnected ? "OK" : "NO CONNECTION"));
    }

    private void setConnectionStatusText(String status)
    {
        connectedLabel.setText("    Connection status: " + status);
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
            logger.log(Level.WARNING,
                    "Couldn't put event in queue: " + ev.getClass(), err);
        }
    }
}
