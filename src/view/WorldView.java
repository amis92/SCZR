package view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by mateusz on 28.05.14.
 */
public class WorldView extends Canvas implements MouseListener {

    private ViewModel viewModel;
    private ViewUpdater viewUpdater;

    public WorldView(ViewModel viewModel, ViewUpdater viewUpdater){
        super();
        this.viewModel = viewModel;
        this.viewUpdater = viewUpdater;
        addMouseListener(this);
    }

    public void updateViewModel(ViewModel viewModel){
        this.viewModel = viewModel;
    }

    public void paint(Graphics g){
        g.setColor(new Color(100, 180, 22));
        g.fillRect(0, 0, getWidth(), getHeight());
        for(BusView busView: viewModel.getBusViewList()){
            busView.drawMiniView(g);
        }
        for(BusStopView busStopView: viewModel.getBusStopViewList()){
            busStopView.drawMiniView(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        for(BusView busView: viewModel.getBusViewList()){
            if(busView.isMiniViewPressed(mouseEvent.getX(), mouseEvent.getY())){
                viewModel.setCurrentDetailView(busView);
            };
        }
        for(BusStopView busStopView: viewModel.getBusStopViewList()){
            if(busStopView.isMiniViewPressed(mouseEvent.getX(), mouseEvent.getY())){
                viewModel.setCurrentDetailView(busStopView);
            };
        }
        viewUpdater.updateView();
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
