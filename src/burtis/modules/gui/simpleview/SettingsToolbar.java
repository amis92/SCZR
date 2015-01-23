package burtis.modules.gui.simpleview;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;

import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBusStop;
import burtis.modules.gui.View;
import burtis.modules.gui.events.CreatePassengerEvent;
import burtis.modules.gui.events.PassengerGenRateEvent;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.SetCycleLengthEvent;

public class SettingsToolbar extends JToolBar
{
    private static final long serialVersionUID = 2056234272927438906L;
    private final static Logger logger = Logger.getLogger(View.class.getName());
    private final BlockingQueue<ProgramEvent> bQueue;
    private final Supplier<Mockup> mockup;
    private final Supplier<Boolean> isConnected;

    public SettingsToolbar(BlockingQueue<ProgramEvent> bQueue,
            Supplier<Mockup> mockup, Supplier<Boolean> isConnected)
    {
        this.mockup = mockup;
        this.bQueue = bQueue;
        this.isConnected = isConnected;
        JButton addPassengerButton = new JButton("Add Passenger");
        JButton setPassengerGenParamsButton = new JButton(
                "Generation Param Setup");
        JButton setSimCycleLengthButton = new JButton("Set Cycle Length");
        add(addPassengerButton);
        add(setPassengerGenParamsButton);
        add(setSimCycleLengthButton);
        addPassengerButton.addActionListener(e -> addPassengerDialog());
        setPassengerGenParamsButton
                .addActionListener(e -> setPassengerGenParamsDialog());
        setSimCycleLengthButton
                .addActionListener(e -> setSimulationCycleLength());
    }

    private void setSimulationCycleLength()
    {
        if (!isConnected.get())
        {
            JOptionPane.showMessageDialog(null, "Not connected!");
            return;
        }
        final SpinnerNumberModel model = new SpinnerNumberModel(0, 0,
                Integer.MAX_VALUE, 250);
        final JSpinner lengthField = new JSpinner(model);
        final Object[] params = { new JLabel("Cycle length (in miliseconds)"),
                lengthField };
        final int d = JOptionPane.showConfirmDialog(getParent(), params,
                "Cycle Length Setup", JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            long cycleLength = (Integer) lengthField.getValue();
            putInQueue(new SetCycleLengthEvent(cycleLength));
            String message = String.format("New cycle length: %d ms\n",
                    cycleLength);
            JOptionPane.showMessageDialog(null, message);
        }
    }

    private void addPassengerDialog()
    {
        final Mockup mockup = this.mockup.get();
        if (mockup == null || !isConnected.get())
        {
            JOptionPane.showMessageDialog(null, "Not connected!");
            return;
        }
        // prepare combo boxes
        final List<MockupBusStop> stops = mockup.getBusStops();
        final int stopCount = stops.size();
        final String[] stopNames = new String[stopCount];
        for (int i = 0; i < stopCount; ++i)
        {
            stopNames[i] = stops.get(i).getName();
        }
        final JComboBox<String> origins = new JComboBox<>(stopNames);
        final JComboBox<String> destinations = new JComboBox<>(stopNames);
        destinations.setSelectedIndex(1);
        origins.addActionListener(e ->
        {
            int origIndex = origins.getSelectedIndex();
            origins.setSelectedIndex(origIndex < stopCount - 1 ? origIndex
                    : origIndex - 1);
            int destIndex = destinations.getSelectedIndex();
            destinations.setSelectedIndex(origIndex < destIndex ? destIndex
                    : origIndex + 1);
        });
        // prepare dialog
        Object[] params = { new JLabel("Origin Stop"), origins,
                new JLabel("Destination Stop"), destinations };
        int d = JOptionPane.showConfirmDialog(getParent(), params,
                "Add Passenger", JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            // confirm choice
            String origin = (String) origins.getSelectedItem();
            String destination = (String) destinations.getSelectedItem();
            putInQueue(new CreatePassengerEvent(origin, destination));
            String message = String.format("You've added passenger:\n"
                    + "Origin Stop: %s\n" + "Destination Stop: %s\n", origin,
                    destination);
            JOptionPane.showMessageDialog(null, message);
        }
    }

    private void setPassengerGenParamsDialog()
    {
        if (!isConnected.get())
        {
            JOptionPane.showMessageDialog(null, "Not connected!");
            return;
        }
        final JSpinner gclField = new JSpinner(new SpinnerNumberModel(1, -1,
                Integer.MAX_VALUE, 1));
        final JSpinner ppcField = new JSpinner(new SpinnerNumberModel(5, 0,
                Integer.MAX_VALUE, 1));
        final Object[] params = { new JLabel("GenerationCycleLength"),
                gclField, new JLabel("PassengersPerCycle"), ppcField };
        final int d = JOptionPane.showConfirmDialog(getParent(), params,
                "Generation Param Setup", JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            int gcl = (Integer) gclField.getValue();
            int ppc = (Integer) ppcField.getValue();
            putInQueue(new PassengerGenRateEvent(gcl, ppc));
            String message = String.format("New parameters:\n"
                    + "GenerationCycleLength = %s\n"
                    + "PassengersPerCycle = %s\n", gcl, ppc);
            JOptionPane.showMessageDialog(null, message);
        }
    }

    /**
     * Wraps event insertion to not throw exceptions. Any thrown exceptions are
     * logged.
     * 
     * @param ev
     *            - the event to be inserted into queue.
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
