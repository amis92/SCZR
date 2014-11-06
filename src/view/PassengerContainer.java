package view;

import mockup.MockupPassenger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerContainer {
    private int cellResolution;
    private ArrayList<PassengerView> passengerViewList;
    private PassengerView moreDetailes = null;
    private int columnsNumber;
    private int x;
    private int y;
    private int rowsNumber;

    public PassengerContainer(int x, int y, int columnsNumber, int rowsNumber, int cellResolution) {
        passengerViewList = new ArrayList<PassengerView>();
        this.x = x;
        this.y = y;
        this.rowsNumber = rowsNumber;
        this.columnsNumber = columnsNumber;
        this.cellResolution = cellResolution;
    }


    public PassengerContainer(PassengerContainer container){
        this(container.x, container.y, container.columnsNumber, container.rowsNumber, container.cellResolution);
    }

    public void addPassengerView(PassengerView passengerView) {
        passengerView.setXY(cellResolution * (passengerViewList.size() % columnsNumber), cellResolution * (passengerViewList.size() / columnsNumber));
        passengerViewList.add(passengerView);
    }

    public void draw(Graphics g) {
        PassengerView passenger;
        for (int i = 0; i < passengerViewList.size(); i++) {

            if(i>=columnsNumber*rowsNumber) break;

            passenger = passengerViewList.get(i);
            passenger.setXY(x + i%columnsNumber*cellResolution, y + i/columnsNumber*cellResolution);
            passenger.setResolution(cellResolution - 2);
            passenger.paint(g);



        }
        if (moreDetailes != null) moreDetailes.paintCloud(g);
    }

    public void onMouseClick(int x, int y) {

        int place = (x - this.x) / cellResolution + ((y -this.y) / cellResolution) * columnsNumber;
        if (place < passengerViewList.size()) {
            moreDetailes = passengerViewList.get(place);
        } else {
            moreDetailes = null;
        }

    }
    public PassengerView findViewOf(MockupPassenger mockupPassenger){
        for(PassengerView passengerView: passengerViewList){
            if(passengerView.isViewOf(mockupPassenger)){
                return passengerView;
            }
        }
        return null;
    }

    public int getPassengersNum() {
        return passengerViewList.size();
    }

    public void setPassengerContainerParameters(int x, int y, int columnsNum, int rowsNum, int resolution) {
        this.x = x;
        this.y = y;
        this.rowsNumber = rowsNum;
        this.columnsNumber = columnsNum;
        this.cellResolution = resolution;
    }

    public PassengerView getMoreDetailes() {
        return moreDetailes;
    }

    public void setMoreDetailes(PassengerView moremoreDetailes) {
        this.moreDetailes = moremoreDetailes;
    }
}
