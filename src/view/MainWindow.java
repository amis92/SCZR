package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class MainWindow extends JFrame implements ViewUpdater{

    /**
     *
     */
    private static final long serialVersionUID = -3543647902381142115L;
    private static final int minWindowWidth = 1200;
    private JPanel contentPane;
    private WorldView worldCanvas;
    private DetailViewer detailCanvas;
    private JPanel buttonsPanel;
    private ClientWrapper clientWrapper;
    private ViewModel viewModel;

    /**
     * Create the frame.
     * @param clientWrapper
     */
    public MainWindow(ClientWrapper clientWrapper, ViewModel viewModel) {
        this.clientWrapper = clientWrapper;
        this.viewModel = viewModel;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, minWindowWidth, minWindowWidth / 2);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        worldCanvas = new WorldView(viewModel, this);
        worldCanvas.setBounds(0,0,0,0);
        contentPane.add(worldCanvas);


        detailCanvas = new DetailViewer(viewModel, this);
        worldCanvas.setBounds(0,0,0,0);
        contentPane.add(detailCanvas);

        buttonsPanel = new ControlButtonsPanel(300, 10, 400, 250, clientWrapper);
        contentPane.add(buttonsPanel);


        this.addComponentListener(new ComponentListener() {

            @Override
            public void componentShown(ComponentEvent arg0) {

            }

            @Override
            public void componentResized(ComponentEvent arg0) {
                resize();

            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void componentHidden(ComponentEvent arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void resize() {

        if (getWidth() < minWindowWidth) {
            setSize(minWindowWidth, minWindowWidth / 2);
        } else if (getHeight() < getWidth() / 2) {
            setSize(getWidth(), getWidth() / 2);
        } else {
            worldCanvas.setBounds(10, 10, (int) (0.6 * getWidth()), (int) (0.45 * getWidth()));

            detailCanvas.setBounds(worldCanvas.getWidth() + 20, 10, (int) (0.64 * getHeight()), (int) (0.48 * getHeight()));

            buttonsPanel.setBounds(worldCanvas.getWidth() + 20, detailCanvas.getHeight() + 40, buttonsPanel.getWidth(), buttonsPanel.getHeight());
        }
    }

    public void updateViewModel(ViewModel viewModel) {
        detailCanvas.setViewModel(viewModel);
        worldCanvas.updateViewModel(viewModel);
        updateView();
    }

    @Override
    public void updateView() {
        revalidate();
        detailCanvas.repaint();
        worldCanvas.repaint();
    }
}
