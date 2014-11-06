package view;

import mockup.MockupBus;

import java.awt.*;

/**
 * Created by mateusz on 28.05.14.
 */
public class BusView extends SimulationObjectView {


    private MockupBus bus;

    public BusView(MockupBus bus){
        super();
        this.bus = bus;
        setPassengers(bus.getPassengerList());
        setPassengerContainerParameters(20, 100, 8, 4, 20);
    }

    @Override
    public void drawDetailView(Graphics g){

        final int firstLine = 80;
        final int secoundLine = 140;

        g.setColor(new Color(230, 230, 180));
        g.fillRect(0,0,getDetailViewWidth(), firstLine);

        g.setColor(new Color(50, 50, 50));
        g.fillRect(0,firstLine,getDetailViewWidth(), getDetailViewHeight());

        g.setColor(new Color(250, 250, 0));
        g.drawLine(0,secoundLine,getDetailViewWidth(), secoundLine);

        g.setColor(new Color(230, 10, 0));
        g.fillRect(16, 96, 200, 88);

        g.setColor(new Color(130, 130, 130));
        g.fillRect(20, 100, 165, 80);

        g.setColor(new Color(130, 180, 250));
        g.fillRect(204, 100, 10, 80);

        g.setColor(Color.BLACK);
        g.drawString("Bus: "+(new Integer(bus.getID()).toString()),10, 10);
        g.drawString("Destination: " + bus.getCurrentBusStop(),10, 26);
        g.drawString("Progress: "
                + (int)(((float)bus.getLengthPassed() / (bus.getLengthPassed() + bus.getBusStopDistace()))*100) + '%',
                10, 42);
        g.drawString("Passenger number: " + getPassengersNum(),10, 58);
        g.drawString("State: " + bus.getState(),10, 74);

        super.drawDetailView(g);
    }



    @Override
    public void drawMiniView(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(getMiniViewXPosition(),getMiniViewYPosition(),getMiniViewWidth(), getMiniViewHeight());
        g.setColor(Color.WHITE);
        g.drawString("Bus: " + bus.getID() +
                     " progress: "+ (int)(((float)bus.getLengthPassed() / (bus.getLengthPassed() + bus.getBusStopDistace()))*100) + '%' +
                     " to " + bus.getCurrentBusStop() , getMiniViewXPosition() + getMiniViewWidth() + 4,
                                            getMiniViewYPosition() + getMiniViewHeight() - 4);
    }


    public void updateView(MockupBus bus) {
        this.bus = bus;
        setPassengers(bus.getPassengerList());
    }

    public boolean isViewOf(MockupBus bus) {
        System.out.println("ID1 : " + this.bus.getID() +"ID : "+bus.getID() + (this.bus.getID() == bus.getID()));
        return (this.bus.getID() == bus.getID());
    }
}
