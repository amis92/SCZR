package burtis.modules.gui.simpleview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.common.mockups.MockupPassenger;
import burtis.modules.gui.View;

class PassengerInfoPanel extends JToolBar
{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(View.class.getName());
    public static final int TABLE_ROWS = 30;
    private final JTable table;
    private final JLabel title = new JLabel();
    private final String[] columnNames = { "Id", "Depot", "Destination" };
    private Mockup mockup;
    private List<MockupPassenger> currentPassengers;
    private boolean isCurrentListFromBus = false;
    private int currentBusId;
    private boolean isCurrentListFromStop = false;
    private String currentStopName;
    private int lastOldRow = -1;
    private int firstNewRow = Integer.MAX_VALUE;

    public PassengerInfoPanel()
    {
        setLayout(new BorderLayout());
        add(title, BorderLayout.PAGE_START);
        table = new JTable(new Object[TABLE_ROWS][columnNames.length],
                columnNames) {
            private static final long serialVersionUID = 1L;
            private final Color white = Color.WHITE;
            private final Color lightBlue = new Color(240, 240, 255);

            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                    int row, int column)
            {
                Component c = super.prepareRenderer(renderer, row, column);
                JComponent jc = (JComponent) c;
                jc.setForeground(Color.BLACK);
                jc.setBackground(row % 2 == 0 ? white : lightBlue);
                final Map attributes = (new Font("Serif", Font.PLAIN, 12))
                        .getAttributes();
                if (row <= lastOldRow)
                {
                    attributes.put(TextAttribute.STRIKETHROUGH,
                            TextAttribute.STRIKETHROUGH_ON);
                    jc.setForeground(Color.RED);
                }
                else if (row >= firstNewRow)
                {
                    jc.setForeground(Color.GREEN);
                }
                jc.setFont(new Font(attributes));
                return jc;
            };
        };
        table.setEnabled(false);
        final JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        final JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Reloads information from the new provided mockup.
     * 
     * @param mockup
     */
    public void refresh(Mockup mockup)
    {
        this.mockup = mockup;
        if (isCurrentListFromBus)
        {
            showForBus(currentBusId);
        }
        else if (isCurrentListFromStop)
        {
            showForBusStop(currentStopName);
        }
    }

    /**
     * Changes displayed list to the list of provided bus stop. The bus is found
     * in the last received mockup.
     * 
     * @param stopName
     *            - name of the bus stop to display passengers from.
     */
    public void showForBusStop(String stopName)
    {
        fillTable(() -> findOnStop(stopName),
                () -> String.format("Bus Stop Name: '%s'", stopName),
                !(isCurrentListFromStop && stopName.equals(currentStopName)));
    }

    /**
     * Changes displayed list to the list of provided bus. The bus is found in
     * the last received mockup.
     * 
     * @param busId
     *            - id of the bus to display passengers from.
     */
    public void showForBus(Integer busId)
    {
        fillTable(() -> findInBus(busId),
                () -> String.format("Bus ID: %d     ", busId),
                !(isCurrentListFromBus && busId.equals(currentBusId)));
    }

    /**
     * Fills the table with provided data. If data was updated for the displayed
     * object, but the object is the same, visual strikethrough is activated on
     * all old rows (which did not show in new list).
     * 
     * @param freshPassengers
     *            - provides new list of passengers.
     * @param tableTitle
     *            - provides title for the table.
     * @param isSourceChanged
     *            - informs whether the source object itself changed.
     */
    private void fillTable(Supplier<List<MockupPassenger>> freshPassengers,
            Supplier<String> tableTitle, boolean isSourceChanged)
    {
        title.setText(tableTitle.get());
        clearTable();
        List<MockupPassenger> passengers = freshPassengers.get();
        if (passengers == null)
        {
            // table cleared, nothing to show, it's error
            logger.warning("passengerList is null");
            return;
        }
        if (isSourceChanged || currentPassengers == null)
        {
            // just create new content
            lastOldRow = -1; // no strikethrough
            fillRowsFromTo(0, passengers.size(), passengers);
        }
        else
        {
            final int listSize = currentPassengers.size();
            int oldCount = 0;
            // currentPassengers & passengers != null
            if (!currentPassengers.isEmpty())
            {
                // count passengers which are no longer on the stop
                final MockupPassenger firstNewPassenger = passengers.isEmpty() ? null
                        : passengers.get(0);
                while (oldCount < listSize
                        && !currentPassengers.get(oldCount).equals(
                                firstNewPassenger))
                {
                    ++oldCount;
                }
            }
            lastOldRow = oldCount - 1;
            int notNewCount = 0;
            if (!passengers.isEmpty())
            {
                final int newSize = passengers.size();
                final MockupPassenger lastOldPassenger = currentPassengers
                        .isEmpty() ? null : currentPassengers.get(listSize - 1);
                while (notNewCount < newSize
                        && !passengers.get(notNewCount)
                                .equals(lastOldPassenger))
                {
                    ++notNewCount;
                }
            }
            ++notNewCount;
            firstNewRow = oldCount + notNewCount;
            fillRowsFromTo(0, lastOldRow + 1, currentPassengers);
            fillRowsFromTo(lastOldRow + 1, firstNewRow, passengers);
            fillRowsFromTo(firstNewRow, oldCount + passengers.size(),
                    passengers);
        }
        currentPassengers = passengers;
    }

    /**
     * Fills table from start row to end row (exclusive) with values passengers
     * from the list starting at index 0.
     */
    private void fillRowsFromTo(int startRow, int endRow,
            List<MockupPassenger> passengerList)
    {
        int i = 0;
        for (int row = startRow; row < endRow; ++row)
        {
            fillRow(row, passengerList.get(i++));
        }
    }

    private void fillRow(int row, MockupPassenger passenger)
    {
        table.setValueAt(passenger.getId(), row, 0);
        table.setValueAt(passenger.getDepot(), row, 1);
        table.setValueAt(passenger.getDestination(), row, 2);
    }

    /**
     * Finds passengers from given bus. The bus is found in the last received
     * mockup.
     * 
     * @param busId
     *            - searches for bus with that id.
     * @return list of passengers in that bus.
     */
    private List<MockupPassenger> findInBus(Integer busId)
    {
        for (MockupBus bus : mockup.getBuses())
        {
            if (bus.getId().equals(busId))
            {
                isCurrentListFromBus = true;
                isCurrentListFromStop = false;
                currentBusId = busId;
                return bus.getPassengerList();
            }
        }
        logger.warning("Can't show BusInfoPanel for busId=" + busId.toString());
        return null;
    }

    /**
     * Finds passengers on given bus stop. The stop is found in the last
     * received mockup.
     * 
     * @param stopName
     *            - searches for stop with that name.
     * @return list of passengers on that stop.
     */
    private List<MockupPassenger> findOnStop(String stopName)
    {
        for (MockupBusStop stop : mockup.getBusStops())
        {
            if (stop.getName().equals(stopName))
            {
                isCurrentListFromStop = true;
                isCurrentListFromBus = false;
                currentStopName = stopName;
                return stop.getPassengerList();
            }
        }
        logger.warning("Can't show BusStopInfoPanel for busStop=" + stopName);
        return null;
    }

    /**
     * Sets all cell values to empty string "".
     */
    private void clearTable()
    {
        for (int row = 0; row < TABLE_ROWS; ++row)
        {
            for (int column = 0; column < columnNames.length; ++column)
            {
                table.setValueAt("", row, column);
            }
        }
    }
}
