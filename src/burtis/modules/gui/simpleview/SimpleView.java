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
import burtis.modules.gui.events.PauseEvent;
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
    private final BusProgressPanel busProgressPanel;
    private final StopsPanel stopsPanel;
    private Mockup mockup;
    private final Supplier<Boolean> isConnected;

    public SimpleView(LinkedBlockingQueue<ProgramEvent> bQueue,
            WindowListener exitListener, Supplier<Boolean> isConnected)
    {
        this.bQueue = bQueue;
        this.isConnected = isConnected;
        // status flow toolbar
        final JButton stopButton = new JButton("Stop");
        final JButton pauseButton = new JButton("Pause");
        final JButton stepButton = new JButton("Step");
        final JButton goButton = new JButton("Go");
        stopButton.addActionListener(e -> putInQueue(new StopEvent()));
        pauseButton.addActionListener(e -> putInQueue(new PauseEvent()));
        stepButton.addActionListener(e -> putInQueue(new StepEvent()));
        goButton.addActionListener(e -> putInQueue(new GoEvent()));
        connectionButton.addActionListener(e -> tryChangeConnectionStatus());
        refreshConnectionStatus();
        final JToolBar statusFlowToolbar = new JToolBar();
        statusFlowToolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusFlowToolbar.add(connectionButton);
        statusFlowToolbar.add(stopButton);
        statusFlowToolbar.add(pauseButton);
        statusFlowToolbar.add(stepButton);
        statusFlowToolbar.add(goButton);
        statusFlowToolbar.add(timeLabel);
        statusFlowToolbar.add(connectedLabel);
        statusFlowToolbar.setRollover(true);
        // management toolbar
        final SettingsToolbar settingsToolbar = new SettingsToolbar(bQueue, () -> mockup,
                isConnected);
        final JPanel toolbars = new JPanel(new GridLayout(0, 1));
        toolbars.add(statusFlowToolbar, BorderLayout.PAGE_START);
        toolbars.add(settingsToolbar, BorderLayout.CENTER);
        // bus and stops panels
        busProgressPanel = new BusProgressPanel(bQueue);
        stopsPanel = new StopsPanel(bQueue);
        final JPanel busInfoPanel = new JPanel(new BorderLayout());
        busInfoPanel.add(stopsPanel, BorderLayout.PAGE_START);
        busInfoPanel.add(busProgressPanel, BorderLayout.CENTER);
        final JScrollPane busScrollPanel = new JScrollPane();
        busScrollPanel.getViewport().add(busInfoPanel);
        // frame setup
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
        frame.add(toolbars, BorderLayout.PAGE_START);
        frame.add(busScrollPanel, BorderLayout.CENTER);
        frame.add(passengerInfoPanel, BorderLayout.PAGE_END);
    }

    public void updatePassengerInfoPanel(Integer i)
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

    public void updatePassengerInfoPanel(String s)
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
        busProgressPanel.refresh(mockup.getBuses());
        stopsPanel.refresh(mockup.getBusStops());
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
