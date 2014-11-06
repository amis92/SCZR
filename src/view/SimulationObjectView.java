package view;

import mockup.MockupBusStop;
import mockup.MockupPassenger;
import model.Passenger;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by mateusz on 28.05.14.
 */
public abstract class SimulationObjectView implements DoubleView {

    private static final int CELL_RESOLUTION = 28;
    private static final int PASSENGER_ROW_NUM = 6;
    private static final int PASSENGER_COLUMN_NUM = 10;
    private static final int PASSENGER_RESOLUTION = 24;

    private int miniViewXPosition = 0;
    private int miniViewYPosition = 0;
    private int miniViewWidth = 0;
    private int miniViewHeight = 0;
    private int miniViewDirect = 0;
    private int detailViewXPosition = 0;
    private int detailViewYPosition = 0;
    private int detailViewWidth = 0;
    private int detailViewHeight = 0;
    private PassengerContainer passengerContainer;

    protected SimulationObjectView() {

        passengerContainer = new PassengerContainer(0,0,PASSENGER_COLUMN_NUM, PASSENGER_ROW_NUM,CELL_RESOLUTION);
    }

    public void setPassengerContainerParameters(int x, int y, int columnsNum, int rowsNum, int resolution){
        passengerContainer.setPassengerContainerParameters(x, y, columnsNum, rowsNum, resolution);
    }

    @Override
    public void pressOn(int x, int y){
        passengerContainer.onMouseClick(x, y);
    }

    public void setPassengers(Collection<MockupPassenger> passengersList){
        PassengerContainer passengerContainerOld = this.passengerContainer;
        PassengerView passengerView;
        this.passengerContainer = new PassengerContainer(passengerContainerOld);
        for(MockupPassenger passenger: passengersList){
            passengerView = passengerContainerOld.findViewOf(passenger);
            if(passengerView == null){
                passengerView = new PassengerView(passenger, PASSENGER_RESOLUTION);
            }else{
                passengerView.updateView(passenger);
            }
            passengerContainer.addPassengerView(passengerView);
        }
        this.passengerContainer.setMoreDetailes(passengerContainerOld.getMoreDetailes());
    }

    public int getPassengersNum() {
        return passengerContainer.getPassengersNum() ;
    }

    public void setPassengers(List<MockupPassenger> passengersList){
        PassengerContainer passengerContainerOld = this.passengerContainer;
        PassengerView passengerView;
        this.passengerContainer = new PassengerContainer(passengerContainerOld);
        for(MockupPassenger passenger: passengersList){
            passengerView = passengerContainerOld.findViewOf(passenger);
            if(passengerView == null){
                passengerView = new PassengerView(passenger, PASSENGER_RESOLUTION);
            }else{
                passengerView.updateView(passenger);
            }
            passengerContainer.addPassengerView(passengerView);
            this.passengerContainer.setMoreDetailes(passengerContainerOld.getMoreDetailes());
        }

    }

    public int getMiniViewXPosition() {
        return miniViewXPosition;
    }



    public int getMiniViewYPosition() {
        return miniViewYPosition;
    }



    public int getMiniViewWidth() {
        return miniViewWidth;
    }



    public int getMiniViewHeight() {
        return miniViewHeight;
    }


    public int getDetailViewXPosition() {
        return detailViewXPosition;
    }


    public int getMiniViewDirect() {
        return miniViewDirect;
    }



    public int getDetailViewYPosition() {
        return detailViewYPosition;
    }


    public int getDetailViewWidth() {
        return detailViewWidth;
    }


    public int getDetailViewHeight() {
        return detailViewHeight;
    }

    @Override
    public void drawDetailView(Graphics g) {
        passengerContainer.draw(g);
    }

    @Override
    public void setDetailViewSize(int width, int height) {
        this.detailViewWidth = width;
        this.detailViewHeight = height;
    }

    @Override
    public void setDetailViewPosition(int x, int y) {
        this.detailViewXPosition = x;
        this.detailViewYPosition = y;
    }



    @Override
    public void setMiniViewPosition(int x, int y) {
        this.miniViewXPosition = x;
        this.miniViewYPosition = y;
    }

    @Override
    public void setMiniViewSize(int width, int height) {
        this.miniViewWidth = width;
        this.miniViewHeight = height;
    }

    @Override
    public void rotateMiniView(int direct) {
        this.miniViewDirect = direct;
    }

    public boolean isMiniViewPressed(int x, int y) {
        return x >= miniViewXPosition && x <= miniViewXPosition + miniViewWidth && y >= miniViewYPosition && y <= miniViewYPosition + miniViewHeight;
    }

}
