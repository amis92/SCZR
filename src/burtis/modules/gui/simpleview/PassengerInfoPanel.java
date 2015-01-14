package burtis.modules.gui.simpleview;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import burtis.common.mockups.MockupPassenger;
import burtis.modules.gui.View;

class PassengerInfoPanel extends JToolBar
{
    private static final long serialVersionUID = 1L;
    private final static Logger logger = Logger.getLogger(View.class.getName());
    public static final int TABLE_ROWS = 30;
    private JTable table;
    private JLabel title = new JLabel();
    // private JLabel busStop = new JLabel();
    private final String[] columnNames = { "Id", "Depot", "Destination" };
    private final Object[][] data = new Object[TABLE_ROWS][columnNames.length];

    public PassengerInfoPanel()
    {
        setLayout(new BorderLayout());
        add(title, BorderLayout.PAGE_START);
        table = new JTable(data, columnNames);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void showForBusStop(String s,
            ArrayList<MockupPassenger> passengerList)
    {
        title.setText("Bus Stop Name: " + s);
        int count = 0;
        clearTable();
        if (passengerList == null)
        {
            logger.warning("passengerList is null");
            return;
        }
        for (MockupPassenger mp : passengerList)
        {
            table.setValueAt(mp.getId(), count, 0);
            table.setValueAt(mp.getDepot(), count, 1);
            table.setValueAt(mp.getDestination(), count, 2);
            count++;
        }
    }

    public void showForBus(Integer i,
            ArrayList<MockupPassenger> passengerList)
    {
        title.setText("Bus Id: " + i.toString());
        int count = 0;
        clearTable();
        if (passengerList == null)
        {
            logger.warning("passengerList is null");
            return;
        }
        for (MockupPassenger mp : passengerList)
        {
            table.setValueAt(mp.getId(), count, 0);
            table.setValueAt(mp.getDepot(), count, 1);
            table.setValueAt(mp.getDestination(), count, 2);
            count++;
        }
    }

    private void clearTable()
    {
        for (int row = 0; row < data.length; ++row)
        {
            for (int column = 0; column < columnNames.length; ++column)
            {
                table.setValueAt("", row, column);
            }
        }
    }
}
