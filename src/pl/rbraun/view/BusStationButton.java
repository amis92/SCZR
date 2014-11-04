package pl.rbraun.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

class BusStationButton extends JPanel {
	private final String stationName;

	public BusStationButton(final String stationName) {
		super(new FlowLayout());
		this.stationName = stationName;
	    ImageIcon icon = new ImageIcon("bus_stop.png");		// try / catch
		final JButton b = new JButton(stationName, icon);
		
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(stationName + ": " + b.getSize()); 		//b2.getSize());	
			}
		});
		
	 	add(b);
	    b.setToolTipText("Oczekuje: 24");
	}
}