package view;

import mockup.MockupBus;
import mockup.MockupBusStop;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

public class BusStopView extends SimulationObjectView {


    private MockupBusStop busStop;

    public BusStopView(MockupBusStop busStop){
        super();
        this.busStop = busStop;
        setPassengers(busStop.getPassengerQueue());
        setPassengerContainerParameters(20, 90, 12, 6, 24);
    }

    @Override
    public void drawDetailView(Graphics g){
        final int firstLine = 40;
        final int secoundLine = 240;
        g.setColor(new Color(230, 230, 180));
        g.fillRect(0,0,getDetailViewWidth(), firstLine);

        g.setColor(new Color(100, 100, 120));
        g.fillRect(0,firstLine,getDetailViewWidth(), secoundLine);

        g.setColor(new Color(50, 50, 50));
        g.fillRect(0,secoundLine,getDetailViewWidth(), getDetailViewWidth());

        g.setColor(Color.BLACK);
        g.drawString("Bus Stop: "+busStop.getNAME(),10, 15);
        g.drawString("Waiting passengers: " + busStop.getPassengerQueue().size(),10, 30);
        super.drawDetailView(g);
    }

    @Override
    public void drawMiniView(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillRect(getMiniViewXPosition(),getMiniViewYPosition(),getMiniViewWidth(), getMiniViewHeight());
        g.setColor(Color.WHITE);
        g.drawString("Bus Stop: " + busStop.getNAME(), getMiniViewXPosition() + getMiniViewWidth() + 4,
                getMiniViewYPosition() + getMiniViewHeight() - 4);
    }


    public void updateView(MockupBusStop busStop) {
        this.busStop = busStop;
        this.setPassengers(busStop.getPassengerQueue());
    }

    public boolean isViewOf(MockupBusStop mockupBusStop) {
        return this.busStop.getID().equals(mockupBusStop.getID());
    }
}


/*
public class BusStopView2 implements DetailView, MouseListener {

    private final static int columnNumber = 15;
    private final static int rowNumber = 4;
    PassengerContainer passengerContainer;
    private BusStopBackground busStopBackground;
    private BusStopSign busStopSign;

    public BusStopView(int width, int height) {

        busStopBackground = new BusStopBackground(width, height);
        busStopSign = new BusStopSign((int) (0.9 * width), (int) (0.25 * height), 100, (int) (0.3 * height));
        passengerContainer = new PassengerContainer((int) (0.80 * width), (int) (0.49 * height), columnNumber, rowNumber, (int) (0.05 * width));
        
    }

    //@Override
    public void paint(Graphics g) {
        busStopBackground.paint(g);
        busStopSign.paint(g);
        passengerContainer.draw(g);


    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {

        passengerContainer.onMouseClick(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawDetailView(Graphics g) {

    }

    @Override
    public void setDetailViewSize(int width, int height) {

    }

    @Override
    public void setDetailViewPosition(int x, int y) {

    }

    private class BusStopSign {

        private final static int imageWidth = 40;
        private final static int imageHeight = 50;
        private final static int pistilWidth = 8;
        private final Color pistilColor = new Color(180, 20, 10);
        private int width;
        private int height;
        private int x;
        private int y;
        private Image image;

        public BusStopSign(int x, int y, int width, int height) {
            try {
                image = ImageIO.read(new File("res/bus_stop.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void paint(Graphics g) {
            g.drawImage(image, x, y, imageWidth, imageHeight, null);
            g.setColor(pistilColor);
            g.fillRect(x + imageWidth / 2 - pistilWidth / 2, y + imageHeight, pistilWidth, height - imageHeight);
        }

    }

    private class BusStopBackground {

        private final Color roadColor = new Color(80, 80, 80);
        private final Color sidewalkColor = new Color(120, 120, 130);
        private final Color grassColor = new Color(50, 180, 20);
        private final Color curbColor = new Color(30, 30, 30);
        private final Color roadLineColor = new Color(210, 200, 10);
        private int width;
        private int height;

        public BusStopBackground(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public void paint(Graphics g) {
            g.setColor(roadColor);
            g.fillRect(0, (int) (0.6 * height), width, (int) (0.4 * height));
            g.setColor(sidewalkColor);
            g.fillRect(0, (int) (0.2 * height), width, (int) (0.4 * height));
            g.setColor(grassColor);
            g.fillRect(0, 0, width, (int) (0.2 * height));
            g.setColor(curbColor);
            g.drawLine(0, (int) (0.6 * height) - 1, width, (int) (0.6 * height) - 1);
            g.drawLine(0, (int) (0.6 * height), width, (int) (0.6 * height));
            g.drawLine(0, (int) (0.6 * height) + 1, width, (int) (0.6 * height) + 1);
            g.setColor(roadLineColor);
            g.fillRect(0, height - 8, width, 8);
        }
    }

}
*/