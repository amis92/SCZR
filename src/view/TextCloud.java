package view;

import java.awt.*;

public class TextCloud {
    private int x;
    private int y;
    private int width;
    private int height;
    private Color lightColor;
    private Color darkColor;

    public TextCloud(int x, int y, int width, int height, Color lightColor, Color darkColor) {
        this.setX(x);
        this.setY(y);
        this.width = width;
        this.height = height;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    public TextCloud(int x, int y, int width, int height) {
        this(x, y, width, height, new Color(230, 230, 100), new Color(150, 150, 40));
    }


    public void paint(Graphics g2) {
        int[] xPoints = {getX(), getX() + width, getX() + width, getX() + (int) (0.6 * width), getX() + width / 2, getX() + (int) (0.4 * width), getX()};
        int[] yPoints = {getY(), getY(), getY() + height, getY() + height, getY() + (int) (1.15 * height), getY() + height, getY() + height};
        g2.setColor(lightColor);
        g2.fillPolygon(xPoints, yPoints, xPoints.length);
        g2.setColor(darkColor);
        g2.drawPolygon(xPoints, yPoints, xPoints.length);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
