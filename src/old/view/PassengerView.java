package view;

import mockup.MockupPassenger;
import model.Passenger;

import java.awt.*;
import java.math.BigDecimal;
import static java.lang.Math.abs;
import java.util.UUID;


public class PassengerView {

    private static final int textCloudWdiht = 100;
    private static final int textCloudHeigth = 70;
    private int x;
    private int y;
    private int passengerSize;
    private TextCloud textCloud;
    private MockupPassenger passenger;

    public PassengerView(MockupPassenger passenger, int passengerSize) {
        this.passenger = passenger;
        this.passengerSize = passengerSize;
        textCloud = createCloud();
    }

    private Color convertIdToColor(int id) {
        return new Color((id*37) & 255 , (id*67) & 255, (id*101) & 255);
    }

    public void paint(Graphics g) {
        g.setColor(convertIdToColor(passenger.getID()));
        g.fillOval(x, y, passengerSize, passengerSize);
        g.setColor(Color.LIGHT_GRAY);
        g.drawOval(x, y, passengerSize, passengerSize);
        g.setColor(Color.RED);
        if (passenger.getTIMESTAMP() > 200) g.drawString("!", x + passengerSize, y + passengerSize / 4);

    }

    public void paintCloud(Graphics g) {
        textCloud.paint(g);
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
        textCloud = createCloud();
    }

    private TextCloud createCloud() {
        return new TextCloud(x - textCloudWdiht / 2 + passengerSize / 2, y - (int) (1.2 * textCloudHeigth), textCloudWdiht, textCloudHeigth) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font("Arial", Font.PLAIN, 10));
                g.drawString("ID:", getX() + 3, getY() + 14);
                g.drawString("Destination:", getX() + 3, getY() + 28);
                g.drawString("Waiting:", getX() + 3, getY() + 56);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(Integer.toString(passenger.getID()), getX() + 20, getY() + 14);
                g.drawString(passenger.getDestination(), getX() + 3, getY() + 42);
                g.drawString(BigDecimal.valueOf(passenger.getTIMESTAMP()).toString(), getX() + 45, getY() + 56);
            }
        };
    }

    public boolean isViewOf(MockupPassenger mockupPassenger) {
        return (this.passenger.getID() == mockupPassenger.getID());
    }

    public void updateView(MockupPassenger passenger) {
        this.passenger = passenger;
    }

    public void setResolution(int resolution) {
        this.passengerSize = resolution;
    }
}
