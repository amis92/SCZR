package burtis.modules.gui.simpleview;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import burtis.modules.gui.View;
import burtis.modules.gui.events.CreatePassengerEvent;
import burtis.modules.gui.events.PassengerGenRateEvent;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.SetCycleLengthEvent;

public class ButtonPanel extends JPanel
{
    private static final long serialVersionUID = 2056234272927438906L;
    private final static Logger logger = Logger.getLogger(View.class.getName());
    private BlockingQueue<ProgramEvent> bQueue;

    public ButtonPanel(BlockingQueue<ProgramEvent> bQueue)
    {
        this.bQueue = bQueue;
        JButton addPassengerButton = new JButton("Add Passenger");
        JButton setPassengerGenParamsButton = new JButton(
                "Generation Param Setup");
        JButton setSimCycleLengthButton = new JButton("Set");
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
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(new Long(0l));
        model.setMaximum(Long.MAX_VALUE);
        model.setStepSize(new Long(500l));
        JSpinner lengthField = new JSpinner(model);
        Object[] params = { new JLabel("Cycle length (in miliseconds)"),
                lengthField };
        int d = JOptionPane.showConfirmDialog(getParent(), params,
                "Cycle Length Setup", JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            long cycleLength = (Long) lengthField.getValue();
            putInQueue(new SetCycleLengthEvent(cycleLength));
            String message = String.format("New cycle length: %d ms\n",
                    cycleLength);
            JOptionPane.showMessageDialog(null, message);
        }
    }

    private void addPassengerDialog()
    {
        JTextField originField = new JTextField(15);
        JTextField destinationField = new JTextField(15);
        Object[] params = { new JLabel("Origin Stop"), originField,
                new JLabel("Destination Stop"), destinationField };
        int d = JOptionPane.showConfirmDialog(getParent(), params,
                "Add Passenger", JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            String origin = originField.getText();
            String destination = destinationField.getText();
            putInQueue(new CreatePassengerEvent(origin, destination));
            String message = String.format("You've added passenger:\n"
                    + "Origin Stop: %s\n" + "Destination Stop: %s\n", origin,
                    destination);
            JOptionPane.showMessageDialog(null, message);
        }
    }

    private void setPassengerGenParamsDialog()
    {
        JSpinner gclField = new JSpinner();
        JSpinner ppcField = new JSpinner();
        Object[] params = { new JLabel("GenerationCycleLength"), gclField,
                new JLabel("PassengersPerCycle"), ppcField };
        int d = JOptionPane.showConfirmDialog(getParent(), params,
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
