package view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class ChooseGenerationInterval extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private ClientWrapper clientWrapper;

    public ChooseGenerationInterval() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        spinner1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int value = ((Integer)spinner1.getValue()).intValue();
                if(value < 0){
                    spinner1.setValue(Integer.valueOf(0));
                }else if( value > ((Integer)spinner2.getValue()).intValue()){
                    spinner2.setValue(spinner1.getValue());
                };
            }
        });

        spinner2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int value = ((Integer)spinner2.getValue()).intValue();
                if(value < 0){
                    spinner2.setValue(Integer.valueOf(0));
                }else if( value < ((Integer)spinner1.getValue()).intValue()){
                    spinner1.setValue(spinner2.getValue());
                };
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void setClientWrapper(ClientWrapper clientWrapper){
        this.clientWrapper = clientWrapper;
    }

    private void onOK() {
// add your code here
        int value1 = ((Integer)spinner1.getValue()).intValue();
        int value2 = ((Integer)spinner2.getValue()).intValue();
        clientWrapper.setPassengerGenerationIntervals(value1, value1);
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }


}
