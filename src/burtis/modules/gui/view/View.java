package burtis.modules.gui.view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.modules.gui.events.ConnectEvent;
import burtis.modules.gui.events.DisconnectEvent;
import burtis.modules.gui.events.GoEvent;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.StepEvent;
import burtis.modules.gui.events.StopEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View {
	/* Kolejka, do ktorej wrzucamy obiekty odpowidajace eventom */
	private LinkedBlockingQueue<ProgramEvent> bQueue;

	private JFrame frame;
	private JSplitPane splitPaneHorizontal;
	private JSplitPane splitPaneVertical;
    private JPanel mainPanel 				= new JPanel(new BorderLayout());
	private JPanel buttonPanel 				= new JPanel(new FlowLayout());
	private JPanel dataPanel				= new JPanel();
	private	JScrollPane scrollPane 			= new JScrollPane();
	
	private long currentTime = 0;
	private BusStationButton tmpBusStationButton;
	
	private JButton goButton 				= new JButton("Go");
	private JButton stopButton 				= new JButton("Stop");
	private JButton stepButton 				= new JButton("Step");
	private JButton connectButton 			= new JButton("Connect");
	
	private JLabel timeLabel 				= new JLabel(Long.toString(currentTime));
	private JToolBar toolbar 				= new JToolBar();

	private List<MockupBus> schedule;
	private List<MockupBusStop> busStops;	
	
	private BusStopInfoPanel busStopInfoPanel = new BusStopInfoPanel();
	private AnimationPanel animationPanel;
	
	public View(LinkedBlockingQueue<ProgramEvent> bQueue) {
		this.bQueue = bQueue;
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setTitle("burtis");
		frame.setVisible(true);
		frame.setSize(1400,400);
		
		connectButton.addActionListener(new ConnectButtonActionListener());
		stopButton.addActionListener(new StopButtonActionListener());
		goButton.addActionListener(new GoButtonActionListener());
		stepButton.addActionListener(new StepButtonActionListener());
		
		toolbar.add(connectButton);
		toolbar.add(stopButton);
		toolbar.add(goButton);
		toolbar.add(stepButton);
		toolbar.add(timeLabel);
		toolbar.setRollover(true);
		
		animationPanel = new AnimationPanel(bQueue);
		
		JScrollPane scrollPaneAnimation = new JScrollPane(animationPanel);
		
		mainPanel.add(buttonPanel, BorderLayout.CENTER);
		mainPanel.add(scrollPaneAnimation, BorderLayout.CENTER);
		
		splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, busStopInfoPanel);
		
		mainPanel.add(buttonPanel, BorderLayout.PAGE_START);
		mainPanel.add(scrollPaneAnimation, BorderLayout.CENTER);
		
		scrollPane.getViewport().add( mainPanel );
		frame.add(splitPaneHorizontal, BorderLayout.CENTER );
		frame.add(toolbar, BorderLayout.PAGE_START);
	}
	
	public void refresh(Mockup mockup) {		
		schedule = mockup.getBuses();
		busStops = mockup.getBusStops();
		
		this.currentTime = mockup.getCurrentTime();
		timeLabel.setText(Long.toString(this.currentTime));
		
		for(MockupBusStop mbs : busStops) {
			String s = mbs.getName();
			tmpBusStationButton = new BusStationButton("Warszawa", bQueue);
			tmpBusStationButton.getButton().addActionListener(new BusStationInfoPanelActionListener());
			buttonPanel.add(tmpBusStationButton);
		}
		
		for(MockupBus bus : schedule) {
		    animationPanel.addBus(new MockupBus(bus.getId()));	
		}
	}
	
	class BusStationInfoPanelActionListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	        String s = ((JButton)e.getSource()).getText();
	        busStopInfoPanel.setCurrentBusStop(s);
	    }
	}
	
    class GoButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                bQueue.put(new GoEvent());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
	
	class StepButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				bQueue.put(new StepEvent());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	class StopButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				bQueue.put(new StopEvent());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	class ConnectButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				bQueue.put(new ConnectEvent());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	class DisconnectButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				bQueue.put(new DisconnectEvent());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void updateBusStopInfoPanel(String s) {	
		for(MockupBusStop busStop : busStops) {
			if(busStop.getName() == s) {
				busStopInfoPanel.setCurrentBusStop(s, busStop.getPassengerList());
				return;
			}
		}
	}
	
	public void updateBusInfoPanel(Integer i) {
	    for(MockupBus bus : schedule) {
			if(bus.getId() == i) {
				busStopInfoPanel.setCurrentBus(i, bus.getPassengerList());
				return;
			}
		}
	}
}
