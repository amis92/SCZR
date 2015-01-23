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
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

import burtis.common.mockups.Mockup;
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
        stopsPanel = new StopsPanel(bQueue);
        busProgressPanel = new BusProgressPanel(bQueue);
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
        // settings
        final SettingsToolbar settingsToolbar = new SettingsToolbar(bQueue,
                () -> mockup, isConnected);
        // upper group
        final JPanel toolbars = new JPanel(new GridLayout(0, 1));
        toolbars.add(statusFlowToolbar);
        toolbars.add(settingsToolbar);
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
        final JScrollPane stopsScroll = new JScrollPane(stopsPanel);
        stopsScroll
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        final JSplitPane horizontalSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, true, new JScrollPane(
                        busProgressPanel), passengerInfoPanel);
        horizontalSplit.setDividerLocation(frame.getWidth() / 8 * 5);
        final JSplitPane veritcalSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, true, stopsScroll, horizontalSplit);
        veritcalSplit.setDividerLocation(frame.getHeight() / 8);
        frame.add(veritcalSplit, BorderLayout.CENTER);
    }

    public void updatePassengerInfoPanel(Integer busId)
    {
        passengerInfoPanel.showForBus(busId);
    }

    public void updatePassengerInfoPanel(String stopName)
    {
        passengerInfoPanel.showForBusStop(stopName);
    }

    public void refresh(Mockup mockup)
    {
        this.mockup = mockup;
        busProgressPanel.refresh(mockup.getBuses());
        stopsPanel.refresh(mockup.getBusStops());
        passengerInfoPanel.refresh(mockup);
        currentTime = mockup.getCurrentTime();
        setCurrentTimeLabel(currentTime);
    }

    private void tryChangeConnectionStatus()
    {
        boolean isConnected = this.isConnected.get();
        connectionButton.setText(isConnected ? "Disconnecting..."
                : "Connecting...");
        connectionButton.setForeground(Color.YELLOW);
        setConnectionStatusText("WAITING");
        putInQueue(isConnected ? new DisconnectEvent() : new ConnectEvent());
    }

    public void refreshConnectionStatus()
    {
        boolean isConnected = this.isConnected.get();
        connectionButton.setText(isConnected ? "Disconnect" : "Connect");
        connectionButton.setForeground(isConnected ? Color.RED : new Color(0,
                130, 0)/* dark green */);
        setConnectionStatusText((isConnected ? "OK" : "NO CONNECTION"));
    }

    private void setConnectionStatusText(String status)
    {
        connectedLabel.setText("    Connection status: " + status);
    }

    private void setCurrentTimeLabel(long currentTime)
    {
        timeLabel.setText("    Time: " + Long.toString(currentTime));
    }

    /**
     * Wraps event insertion in try catch. Any exceptions caught will be logged.
     * 
     * @param ev
     *            - the event to be inserted into the queue.
     */
    private void putInQueue(ProgramEvent ev)
    {
        try
        {
            bQueue.put(ev);
        }
        catch (InterruptedException err)
        {
            logger.log(Level.WARNING, "Couldn't put event in queue: "
                    + ev.getClass().getName());
        }
    }
}
