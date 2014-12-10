package burtis.modules.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class BusStopInfoPanel extends JPanel {
	private JTable table;
	private JLabel title = new JLabel();
	
    String[] columnNames = {"Id",
            				"Depot",
            				"Destination"};
    Object[][] data = {
    	    {"Kathy", "Smith", "Snowboarding", new Integer(5), new Boolean(false)}};

	public BusStopInfoPanel() {
		setLayout(new BorderLayout());	

		add(title, BorderLayout.PAGE_START);
		table = new JTable(data, columnNames);
		table.setEnabled(false);
		JScrollPane scrollPane = new JScrollPane(table);
	    add(scrollPane, BorderLayout.CENTER);
	}

	public void setCurrentBusStop(String s) {
		title.setText(s);
	}
	
	public void setCurrentBus(String s) {
		title.setText(s);		
	}
}
