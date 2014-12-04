package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by mateusz on 28.05.14.
 */
public class ControlButtonsPanel extends JPanel implements CreatePassengerEvent{

    private JButton stepButton;
    private JToggleButton switchSimulationButton;
    private JButton newPassengerButton;
    private JButton changeInterval;
    private ClientWrapper clientWrapper;


    public ControlButtonsPanel(int x, int y, int width, int height, ClientWrapper clientWrapper){
        super();
        setBounds(x, y, width, height);
        setLayout(null);
        this.clientWrapper = clientWrapper;
        initStepButton();
        initSwitchSimulationButton();
        initNewPassengerButton();
        initChangeIntervalButton();

    }

    private void initChangeIntervalButton() {
        changeInterval = new JButton("Change interval");
        changeInterval.setBounds(210, 120, 160, 25);
        add(changeInterval);
        changeInterval.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ChooseGenerationInterval dialog = new ChooseGenerationInterval();
                dialog.setClientWrapper(clientWrapper);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
    }

    private void initStepButton(){
        stepButton = new JButton("Next step");
        stepButton.setBounds(120, 30, 160, 80);
        stepButton.setEnabled(false);
        add(stepButton);
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clientWrapper.nextStep();
            }
        });
    }

    private void initSwitchSimulationButton(){
        switchSimulationButton = new JToggleButton("Auto simulation");
        switchSimulationButton.setSelected(true);
        switchSimulationButton.setBounds(10, 120, 180, 25);
        add(switchSimulationButton);
        switchSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean sel = switchSimulationButton.isSelected();
                switchSimulationButton.setText(sel?"Auto simulation":"Manual simulation");
                clientWrapper.setContinuous(sel);
                stepButton.setEnabled(!sel);
            }
        });
    }

    private CreatePassengerDialog oTemporaOMores = null;
    private void initNewPassengerButton(){
        newPassengerButton = new JButton("New passenger");
        newPassengerButton.setBounds(10, 150, 180, 45);
        add(newPassengerButton);
        newPassengerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                oTemporaOMores=new CreatePassengerDialog(ControlButtonsPanel.this);
                oTemporaOMores.setVisible(true);
            }
        });
    }


    @Override
    public void createPassengerActionPerformed() {
        clientWrapper.createPassenger(oTemporaOMores.getFrom(), oTemporaOMores.getTo());
        oTemporaOMores.dispose();
        oTemporaOMores = null;
    }
}
