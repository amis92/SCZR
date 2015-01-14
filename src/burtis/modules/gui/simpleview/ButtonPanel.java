package burtis.modules.gui.simpleview;

import java.util.concurrent.BlockingQueue;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import burtis.modules.gui.events.ProgramEvent;

public class ButtonPanel extends JPanel
{
    private static final long serialVersionUID = 2056234272927438906L;
    private BlockingQueue<ProgramEvent> bQueue;
    private JButton addPassenger = new JButton("Add Passenger");
    private JButton setMinGenTimeButton = new JButton("MinGenTime");
    private JButton setMaxGenTimeButton = new JButton("MaxGenTime");

    public ButtonPanel(BlockingQueue<ProgramEvent> bQueue)
    {
        this.bQueue = bQueue;
        add(addPassenger);
        add(setMinGenTimeButton);
        add(setMaxGenTimeButton);
        addPassenger.addActionListener(e -> addPassengerDialog());
        setMinGenTimeButton.addActionListener(e -> setMinGenTimeDialog());
        setMaxGenTimeButton.addActionListener(e -> setMaxGenTimeDialog());
    }

    private void addPassengerDialog()
    {
        JTextField passengerId = new JTextField(2);
        JTextField passengerOrigin = new JTextField(15);
        JTextField passengerDestin = new JTextField(15);
        Object[] params = { new JLabel("Id pasazera"), passengerId,
                new JLabel("Stacja bazowa"), passengerOrigin,
                new JLabel("Stacja docelowa"), passengerDestin };
        int d = JOptionPane.showConfirmDialog(getParent(), params,
                "Dodaj pasazera", JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            JOptionPane.showMessageDialog(null, "Dodales pasazera");
        }
        // TODO A teraz tak serio - jak dodac tego pasazera???
    }

    private void setMinGenTimeDialog()
    {
        JTextField minGenTime = new JTextField(10);
        Object[] params = { new JLabel("minGenTime"), minGenTime };
        int d = JOptionPane.showConfirmDialog(getParent(), params,
                "Ustaw minGenTime", JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            JOptionPane.showMessageDialog(null, "Ustawiles minGenTime na "
                    + minGenTime.getText());
        }
        // TODO jak to ustawic ???
    }

    private void setMaxGenTimeDialog()
    {
        JTextField maxGenTime = new JTextField(10);
        Object[] params = { new JLabel("maxGenTime"), maxGenTime };
        int d = JOptionPane.showConfirmDialog(getParent(), params,
                "Ustaw maxGenTime", JOptionPane.OK_CANCEL_OPTION);
        if (d == 0)
        {
            JOptionPane.showMessageDialog(null, "Ustawiles maxGenTime na "
                    + maxGenTime.getText());
        }
        // TODO jak to ustawic ???
    }
}
