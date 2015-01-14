package burtis.modules.gui.simpleview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowListener;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
    private boolean isConnected = false;
    private final PassengerInfoPanel busStopInfoPanel = new PassengerInfoPanel();
    private final static Logger logger = Logger.getLogger(View.class.getName());
    private final LinkedBlockingQueue<ProgramEvent> bQueue;
    // private List<MockupBusStop> busStops;
    /**
     * Toolbar and buttons
     */
    private final JButton connectionButton = new JButton("Connect");
    private final JLabel timeLabel = new JLabel("    Time: "
            + Long.toString(currentTime));
    private final JLabel connectedLabel = new JLabel("    Connected: "
            + isConnected);
    private ProgressBarPanel progressBarPanel;
    private BusStopButtonPanel busStopButtonPanel;
    private Mockup mockup;

    public SimpleView(LinkedBlockingQueue<ProgramEvent> bQueue,
            WindowListener exitListener)
    {
        this.bQueue = bQueue;
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
        JButton goButton = new JButton("Go");
        JButton stepButton = new JButton("Pause/Step");
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> putInQueue(new StopEvent()));
        goButton.addActionListener(e -> putInQueue(new GoEvent()));
        stepButton.addActionListener(e -> putInQueue(new StepEvent()));
        connectionButton.addActionListener(e -> tryChangeConnectionStatus());
        connectionButton.setBackground(Color.GREEN);
        JToolBar toolbar = new JToolBar();
        toolbar.setLayout(new GridLayout(1, 5));
        toolbar.add(connectionButton);
        toolbar.add(stopButton);
        toolbar.add(goButton);
        toolbar.add(stepButton);
        toolbar.add(timeLabel);
        toolbar.add(connectedLabel);
        toolbar.setRollover(true);
        JScrollPane topScrollPane = new JScrollPane();
        progressBarPanel = new ProgressBarPanel(bQueue);
        busStopButtonPanel = new BusStopButtonPanel(bQueue);
        ButtonPanel buttonPanel = new ButtonPanel(bQueue);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(busStopButtonPanel, BorderLayout.PAGE_START);
        panel.add(progressBarPanel, BorderLayout.CENTER);
        topScrollPane.getViewport().add(panel);
        JSplitPane splitPaneHorizontal = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, topScrollPane, busStopInfoPanel);
        frame.add(toolbar, BorderLayout.PAGE_START);
        frame.add(splitPaneHorizontal, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.PAGE_END);
    }

    public void updateBusInfoPanel(Integer i)
    {
        for (MockupBus mb : mockup.getBuses())
        {
            if (mb.getId().equals(i))
            {
                busStopInfoPanel.showForBus(i, mb.getPassengerList());
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
                busStopInfoPanel.showForBusStop(s, mbs.getPassengerList());
                return;
            }
        }
        logger.warning("Can't show BusStopInfoPanel for busStop=" + s);
    }

    public void refresh(Mockup mockup)
    {
        // doing cleanup
        progressBarPanel.removeAll();
        busStopButtonPanel.removeAll();
        this.mockup = mockup;
        progressBarPanel.refreshProgressBarPanel(mockup.getBuses());
        busStopButtonPanel.refreshBusStopButtonPanel(mockup.getBusStops());
        currentTime = mockup.getCurrentTime();
        timeLabel.setText("    Time: " + Long.toString(currentTime));
    }

    private void tryChangeConnectionStatus()
    {
        putInQueue(isConnected ? new DisconnectEvent() : new ConnectEvent());
    }

    public void setConnectionStatus(boolean isConnected)
    {
        this.isConnected = isConnected;
        connectionButton.setText(isConnected ? "Disconnect" : "Connect");
        connectionButton.setBackground(isConnected ? Color.RED : Color.GREEN);
        connectedLabel.setText("    Connected: " + isConnected);
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
