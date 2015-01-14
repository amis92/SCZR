package burtis.modules.gui.simpleview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.util.List;
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
import burtis.modules.gui.AbstractView;
import burtis.modules.gui.events.GoEvent;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.StepEvent;
import burtis.modules.gui.events.StopEvent;
import burtis.modules.gui.view.View;

public class SimpleView extends AbstractView
{
    private long currentTime = 0;
    private boolean connected = false;
    private final JFrame frame = new JFrame();
    private final BusStopInfoPanel busStopInfoPanel = new BusStopInfoPanel();
    private final ButtonPanel buttonPanel;
    private final static Logger logger = Logger.getLogger(View.class.getName());
    private final LinkedBlockingQueue<ProgramEvent> bQueue;
    private List<MockupBusStop> busStops;
    /**
     * Toolbar and buttons
     */
    private JButton connectionButton = new JButton("Connect");
    private final JToolBar toolbar = new JToolBar();
    private final JButton goButton = new JButton("Go");
    private final JButton stepButton = new JButton("Step");
    private final JButton stopButton = new JButton("Stop");
    private final JLabel timeLabel = new JLabel("    Time: "
            + Long.toString(currentTime));
    private final JLabel connectedLabel = new JLabel("    Connected: "
            + connected);
    private ProgressBarPanel progressBarPanel;
    private BusStopButtonPanel busStopButtonPanel;
    private JScrollPane topScrollPane;
    private Mockup mockup;
    int howManyBuses = 20;

    public SimpleView(LinkedBlockingQueue<ProgramEvent> bQueue,
            WindowListener exitListener)
    {
        this.bQueue = bQueue;
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
        connectionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev)
            {
                if (connected)
                {
                    // putInQueue(new DisconnectEvent());
                    connectionButton.setText("Connect");
                    connectionButton.setBackground(Color.GREEN);
                    connected = false;
                    connectedLabel.setText("    Connected: " + connected);
                }
                else
                {
                    // putInQueue(new ConnectEvent());
                    connectionButton.setText("Disconnect");
                    connectionButton.setBackground(Color.RED);
                    connected = true;
                    connectedLabel.setText("    Connected: " + connected);
                }
            }
        });
        stopButton.addActionListener(e -> putInQueue(new StopEvent()));
        goButton.addActionListener(e -> putInQueue(new GoEvent()));
        stepButton.addActionListener(e -> putInQueue(new StepEvent()));
        connectionButton.setBackground(Color.GREEN);
        toolbar.setLayout(new GridLayout(1, 5));
        toolbar.add(connectionButton);
        toolbar.add(stopButton);
        toolbar.add(goButton);
        toolbar.add(stepButton);
        toolbar.add(timeLabel);
        toolbar.add(connectedLabel);
        toolbar.setRollover(true);
        topScrollPane = new JScrollPane();
        progressBarPanel = new ProgressBarPanel(bQueue);
        busStopButtonPanel = new BusStopButtonPanel(bQueue);
        buttonPanel = new ButtonPanel(bQueue);
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
            if (mb.getId() == i)
            {
                busStopInfoPanel.setCurrentBus(i, mb.getPassengerList());
                return;
            }
        }
        System.out.println("No such bus");
    }

    public void updateBusStopInfoPanel(String s)
    {
        for (MockupBusStop mbs : mockup.getBusStops())
        {
            if (mbs.getName() == s)
            {
                busStopInfoPanel.setCurrentBusStop(s, mbs.getPassengerList());
                return;
            }
        }
        System.out.println("No such bus stop");
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
