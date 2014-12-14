package burtis.modules.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import burtis.common.mockups.MockupBus;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusEvent;

class AnimationPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private final LinkedBlockingQueue<ProgramEvent> bQueue;
    private ArrayList<Location> locationArray = new ArrayList<Location>();
    // private int squareX;
    private int squareY = 0;
    private int squareW = 70;
    private int squareH = 40;

    public AnimationPanel(LinkedBlockingQueue<ProgramEvent> bQueue)
    {
        this.bQueue = bQueue;
        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {
                clickSquare(e.getX(), e.getY());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e)
            {
                clickSquare(e.getX(), e.getY());
            }
        });
    }

    private void clickSquare(int x, int y)
    {
        Location tmp = null;
        for (Location l : locationArray)
        {
            if ((l.getX() <= x) && (l.getY() <= y) && (l.getX() + squareW >= x)
                    && (l.getY() + squareH >= y))
            {
                tmp = l;
            }
        }
        try
        {
            if (tmp != null)
                bQueue.put(new ShowBusEvent(tmp.getId()));
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(250, 300);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // Draw Text
        // g.drawString("This is my custom Panel!",10,20);
        for (Location l : locationArray)
        {
            g.setColor(Color.BLUE);
            if (l.stop == true)
                g.setColor(Color.RED);
            g.fillRect(l.getX(), l.getY(), squareW, squareH);
            g.setColor(Color.BLACK);
            g.drawRect(l.getX(), l.getY(), squareW, squareH);
            g.drawString("Bus " + l.getId(), l.getX() + 18, l.getY() + 24);
        }
    }

    public void addBus(MockupBus tmp, Integer squareX)
    {
        locationArray.add(new Location(squareX, squareY, tmp.getId(), false));
        squareY += 50;
    }

    class Location
    {
        private int x;
        private int y;
        private Integer Id;
        private Boolean stop;

        Location(int x, int y, Integer Id, Boolean stop)
        {
            this.x = x;
            this.y = y;
            this.Id = Id;
            this.stop = stop;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public Integer getId()
        {
            return Id;
        }
    }
}
