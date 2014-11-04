package pl.rbraun.view;

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
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import pl.rbraun.events.ProgramEvent;

public class View {
	private LinkedBlockingQueue<ProgramEvent> bQueue;
	private JFrame frame = new JFrame();
	private JButton goButton = new JButton("Go");
	private JButton stopButton = new JButton("Stop");
	private JButton stepButton = new JButton("Step");
	private JButton fastButton = new JButton("Fast");
	private JButton resetButton = new JButton("Reset");
	private JButton slowButton = new JButton("Slow");
    private JToolBar toolbar = new JToolBar();
	private	JScrollPane scrollPane = new JScrollPane();
	private JSplitPane splitPane;
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private JPanel buttonPanel = new JPanel(new FlowLayout());
	
	public View(LinkedBlockingQueue<ProgramEvent> bQueue) {
		this.bQueue = bQueue;
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setTitle("SZCZR");
		frame.setVisible(true);
		frame.setSize(1400,400);
		
		toolbar.add(slowButton);
		toolbar.add(resetButton);
		toolbar.add(stopButton);
		toolbar.add(goButton);
		toolbar.add(stepButton);
		toolbar.add(fastButton);
		toolbar.setRollover(true);
		
		
		
		for(int i=0; i<20; i++) {
			BusStationButton b = new BusStationButton("W-wa Centrum");
			buttonPanel.add(b);
		}		
		
		
		
		
		
		
		
		
		
		
		mainPanel.add(buttonPanel, BorderLayout.PAGE_START);
		mainPanel.add(new Animation(182, 62), BorderLayout.CENTER);
		
		scrollPane.getViewport().add( mainPanel );
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, new InfoPanel());

		frame.add( splitPane, BorderLayout.CENTER );
		
		frame.add(toolbar, BorderLayout.PAGE_START);
	}
}
