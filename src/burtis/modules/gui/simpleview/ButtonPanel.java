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

import burtis.modules.gui.View;
import burtis.modules.gui.events.CreatePassengerEvent;
import burtis.modules.gui.events.PassengerGenRateEvent;
import burtis.modules.gui.events.ProgramEvent;

public class ButtonPanel extends JPanel
{
    private static final long serialVersionUID = 2056234272927438906L;
    private final static Logger logger = Logger.getLogger(View.class.getName());
    private BlockingQueue<ProgramEvent> bQueue;
    private JButton addPassenger = new JButton("Add Passenger");
    private JButton setPassengerGenParamsButton = new JButton("MinGenTime");

    public ButtonPanel(BlockingQueue<ProgramEvent> bQueue)
    {
        this.bQueue = bQueue;
        add(addPassenger);
        add(setPassengerGenParamsButton);
        addPassenger.addActionListener(e -> addPassengerDialog());
        setPassengerGenParamsButton
                .addActionListener(e -> setPassengerGenParamsDialog());
    }

    private void addPassengerDialog()
    {
        JTextField originField = new JTextField(15);
        JTextField destinationField = new JTextField(15);
        Object[] params = { new JLabel("Stacja bazowa"), originField,
                new JLabel("Stacja docelowa"), destinationField };
        int d = JOptionPane.showConfirmDialog(getParent(), params,
                "Dodaj pasazera", JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            String origin = originField.getText();
            String destination = destinationField.getText();
            putInQueue(new CreatePassengerEvent(origin, destination));
            String message = String.format("Dodałeś pasażera:\n"
                    + "Stacja docelowa: %s\n" + "Stacja docelowa: %s\n",
                    origin, destination);
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
                "Ustaw parametry generacji pasażerów",
                JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            int gcl = (Integer) gclField.getValue();
            int ppc = (Integer) ppcField.getValue();
            putInQueue(new PassengerGenRateEvent(gcl, ppc));
            String message = String.format("Ustawiłeś parametry:\n"
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
