package burtis.modules.gui.simpleview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBusStop;
import burtis.modules.gui.events.ConnectEvent;
import burtis.modules.gui.events.GoEvent;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.StepEvent;
import burtis.modules.gui.events.StopEvent;
import burtis.modules.gui.view.AbstractView;
import burtis.modules.gui.view.View;

public class SimpleView extends AbstractView {
    private long currentTime = 0;
    
    private final JFrame frame 
                = new JFrame();
    private final BusStopDataPanel busStopInfoPanel 
                = new BusStopDataPanel();
    private final JPanel buttonPanel 
                = new JPanel(new FlowLayout());

    private final static Logger logger = Logger.getLogger(View.class.getName());
    private final LinkedBlockingQueue<ProgramEvent> bQueue;
    private List<MockupBusStop> busStops;

    /**
     * Toolbar and buttons
     */
    private final JToolBar toolbar = new JToolBar();
    private final JButton goButton = new JButton("Go");
    private final JButton stepButton = new JButton("Step");
    private final JButton stopButton = new JButton("Stop");
    private final JButton connectButton = new JButton("Connect");
    private final JLabel timeLabel = new JLabel("    Time: " + Long.toString(currentTime));

    private ProgressBarPanel progressBarPanel;
    private BusStopButtonPanel busStopButtonPanel;
    private JScrollPane topScrollPane;
    
    int howManyBuses = 20;
    
    public SimpleView(LinkedBlockingQueue<ProgramEvent> bQueue) {
        this.bQueue = bQueue;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
        frame.setSize(800, 600);
        frame.setTitle("burtis");
        
        connectButton.addActionListener(e -> putInQueue(new ConnectEvent()));
        stopButton.addActionListener(e -> putInQueue(new StopEvent()));
        goButton.addActionListener(e -> putInQueue(new GoEvent()));
        stepButton.addActionListener(e -> putInQueue(new StepEvent()));
        toolbar.setLayout(new GridLayout(1, 5));
        toolbar.add(connectButton);
        toolbar.add(stopButton);
        toolbar.add(goButton);
        toolbar.add(stepButton);
        toolbar.add(timeLabel);
        toolbar.setRollover(true);
        
        topScrollPane = new JScrollPane();
        progressBarPanel = new ProgressBarPanel(bQueue);
        busStopButtonPanel = new BusStopButtonPanel(bQueue);
        
        
        
        

        
        //topScrollPane.add(toolbar);
        //topScrollPane.add(busStopButtonPanel);
        
        //frame.add(toolbar, BorderLayout.PAGE_START);
        //frame.add(busStopButtonPanel, BorderLayout.CENTER);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(busStopButtonPanel, BorderLayout.PAGE_START);
        panel.add(progressBarPanel, BorderLayout.CENTER);
        
        topScrollPane.getViewport().add(panel);
        
        JSplitPane splitPaneHorizontal = 
                new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                    topScrollPane, 
                                    busStopInfoPanel);
        
        frame.add(toolbar, BorderLayout.PAGE_START);
        frame.add(splitPaneHorizontal, BorderLayout.CENTER);
        //frame.add(busStopInfoPanel, BorderLayout.PAGE_END);
        
        
        
    }
    
    public void updateBusData() {
        
        
    }
    
    public void updateBusStopData() {
        
        
    }
    
    public void refresh(Mockup mockup) {
        // do cleanup
        
        progressBarPanel.refreshProgressBarPanel(mockup.getBuses());
        busStopButtonPanel.refreshBusStopButtonPanel(mockup.getBusStops());
        
    }
    
    /**
     * Metoda pozwalająca na włożenie obiektu odpowiadającego za zdarzenie do kolejki akcji do wykonania
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
