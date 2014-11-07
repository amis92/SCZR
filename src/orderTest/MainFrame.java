package orderTest;

import event.guievents.ContinuousSimulationEvent;
import event.guievents.NewPassengerEvent;
import event.guievents.PassengerGenerationInterval;
import main.SimulatorConstants;
import network.Client;
import network.Server;
import view.SimulatorEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by tomasz on 24.05.2014.
 */
public class MainFrame extends JFrame {


    final static public int testGuiServerSocket = 8123;
    final static public int testSimulatorSocket = 8124;
    final static private Integer textFieldHeight = 30;
    final static private Integer textFieldWidth = 100;
    private static final String[] stops = {SimulatorConstants.ratuszStopName, SimulatorConstants.centrumStopName,
                                            SimulatorConstants.SGHStopName, /*SimulatorConstants.westStopName,
                                            SimulatorConstants.tomaszKnapik*/
    };

    private JPanel buttonPanel;
    private JPanel newPassengerPanel;
    private JPanel generationIntensityPanel;
    private JPanel contentPanel;

    private JTextField minimalPassengerCreationIntervalTextField;
    private JTextField maximalPassengerCreationIntervalTextField;
    private JList passengerLocationList;
    private JList passengerDestinationList;

    private JLabel fromLabel;
    private JLabel toLabel;
    private JLabel minimumLabel;
    private JLabel maximumLabel;

    private JButton startStepSimulationButton;
    private JButton stopStepSimulationButton;
    private JButton addPassengerButton;
    private JButton changeGenerationIntensityButton;

    private Client<SimulatorEvent> networkClient;

    public static void main (String [] args) {

        MainFrame mainFrame = new MainFrame("Test View");

    }

    public MainFrame(String title) throws HeadlessException {
        super(title);
        Server myServer = new Server(testGuiServerSocket, testSimulatorSocket, 8125);
        try {
            myServer.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setSize(360, 320);
        addContentPanel();
        addButtonPanel();
        addButtons();
        setVisible(true);
        initNetworkClient();
    }

    private void addContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.LINE_AXIS));
        addGenerationIntensityPanel();
        addNewPassengerPanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addButtonPanel() {
        buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addNewPassengerPanel() {
        newPassengerPanel = new JPanel();
        newPassengerPanel.setLayout(new BoxLayout(newPassengerPanel, BoxLayout.PAGE_AXIS));

        passengerLocationList = new JList(stops);
        passengerDestinationList = new JList(stops);
        fromLabel = new JLabel("From");
        toLabel = new JLabel("To");
        fromLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        toLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        newPassengerPanel.add(fromLabel);
        newPassengerPanel.add(passengerLocationList);
        newPassengerPanel.add(toLabel);
        newPassengerPanel.add(passengerDestinationList);

        contentPanel.add(newPassengerPanel);
    }

    private void addGenerationIntensityPanel() {
        generationIntensityPanel = new JPanel();
        generationIntensityPanel.setLayout(new BoxLayout(generationIntensityPanel, BoxLayout.PAGE_AXIS));

        minimalPassengerCreationIntervalTextField = new JTextField();
        maximalPassengerCreationIntervalTextField = new JTextField();

        minimalPassengerCreationIntervalTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        maximalPassengerCreationIntervalTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        minimalPassengerCreationIntervalTextField.setMaximumSize(minimalPassengerCreationIntervalTextField.getPreferredSize());
        maximalPassengerCreationIntervalTextField.setMaximumSize(maximalPassengerCreationIntervalTextField.getPreferredSize());

        minimumLabel = new JLabel("Minimum");
        maximumLabel = new JLabel("Maximum");
        minimumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        maximumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        generationIntensityPanel.add(minimumLabel);
        generationIntensityPanel.add(minimalPassengerCreationIntervalTextField);
        generationIntensityPanel.add(maximumLabel);
        generationIntensityPanel.add(maximalPassengerCreationIntervalTextField);
        contentPanel.add(generationIntensityPanel);
    }

    private void addButtons() {
        startStepSimulationButton = new JButton("Start simulation"); //juz nie robie constantsow skoro to tylko do testow
        stopStepSimulationButton = new JButton("Stop simulation");
        addPassengerButton = new JButton("Add passenger");
        changeGenerationIntensityButton = new JButton("Change generation times");

        startStepSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ContinuousSimulationEvent startStepSimulation = new ContinuousSimulationEvent(false);

                networkClient.send(startStepSimulation);
            }
        });

        stopStepSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ContinuousSimulationEvent stopStepSimulation = new ContinuousSimulationEvent(true);
                networkClient.send(stopStepSimulation);
            }
        });

        addPassengerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String from = (String)MainFrame.this.passengerLocationList.getSelectedValue();
                String to = (String)MainFrame.this.passengerDestinationList.getSelectedValue();
                NewPassengerEvent createPassenger = new NewPassengerEvent(from, to);
                networkClient.send(createPassenger);
            }
        });

        changeGenerationIntensityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int minTime = Integer.parseInt(MainFrame.this.minimalPassengerCreationIntervalTextField.getText());
                int maxTime = Integer.parseInt(MainFrame.this.maximalPassengerCreationIntervalTextField.getText());
                PassengerGenerationInterval config = new PassengerGenerationInterval(minTime, maxTime);
                networkClient.send(config);
            }
        });

        addPassengerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeGenerationIntensityButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(startStepSimulationButton);
        buttonPanel.add(stopStepSimulationButton);
        generationIntensityPanel.add(changeGenerationIntensityButton);
        newPassengerPanel.add(addPassengerButton);
    }

    private void initNetworkClient() {
        networkClient = new Client<SimulatorEvent>("127.0.0.1", testGuiServerSocket);
        networkClient.connect();
    }

}
