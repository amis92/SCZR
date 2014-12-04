package view;

import javax.swing.*;
import java.awt.event.*;

public class CreatePassengerDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField from;
    private JTextField to;
    private CreatePassengerEvent who;

    public CreatePassengerDialog(CreatePassengerEvent who) {
        this.who=who;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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

        this.pack();
    }

    private void onOK() {
        who.createPassengerActionPerformed();
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public String getFrom(){
        return from.getText();
    }

    public String getTo(){
        return to.getText();
    }

}
