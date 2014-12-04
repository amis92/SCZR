package view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DetailViewer extends Canvas implements MouseListener{

    private static final long serialVersionUID = -7824633740035921796L;
    private ViewModel viewModel;
    private ViewUpdater viewUpdater;

    public DetailViewer(ViewModel viewModel, ViewUpdater viewUpdater) {
        super();
        this.viewModel = viewModel;
        this.viewUpdater = viewUpdater;
        this.addMouseListener(this);
    }

    public void setViewModel(ViewModel viewModel){
        this.viewModel = viewModel;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if(viewModel != null && viewModel.getCurrentDetailView() != null) {
            viewModel.getCurrentDetailView().setDetailViewSize(getWidth(),getHeight());
            viewModel.getCurrentDetailView().drawDetailView(g2);
        }
    }


    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        viewModel.getCurrentDetailView().pressOn(mouseEvent.getX(), mouseEvent.getY());
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
