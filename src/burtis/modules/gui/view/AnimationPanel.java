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
    
    /**
     * W tej metodzie sprawdzamy czy w klikniętym pukncie znajduje się autobus.
     * Robimy poprzez sprawdzenie czy punkt znajduje się w obszarze, w którym na grafice umieszczony jest autobus
     * Jeśli tak dodajemy odpowiednie zdarzenie do kolejki
     * 
     * @param x
     * @param y
     */
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
                bQueue.put(new ShowBusEvent(tmp.getId(), tmp.getCurrentBusStop()));
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
        
        for(Location l : locationArray) {
        	   
        	g.setColor(Color.BLUE);                                    // Jeśli autobus nie zatrzymał się na przystanku ma kolor niebieski
        	if(l.getCurrentBusStop() != "") g.setColor(Color.RED);     // Jeśli nie ma kolor czerwony
        	
        	g.fillRect(l.getX(),l.getY(),squareW,squareH);
        	g.setColor(Color.BLACK);
        	g.drawRect(l.getX(),l.getY(),squareW,squareH);
        	
        	g.drawString("Bus " + l.getId(),l.getX()+18,l.getY()+24);  // numer autobusu
    	}
    }
    
    public void addBus(MockupBus tmp) {
        System.out.println(tmp.getLengthPassed());
    	locationArray.add(new Location(tmp.getLengthPassed() * (getParent().getWidth() / 100), 
    	        squareY, tmp.getId(), tmp.getCurrentBusStop()));
    	squareY += 50;
    }
    
    /**
     * Klasa odpowiadająca lokalizacji autobusu na grafice
     * 
     * @author vanqyard
     *
     */
    class Location {
    	private int x;
    	private int y;
    	private Integer Id;
    	private String currentBusStop = "";
    	
    	Location(int x, int y, Integer Id, String currentBusStop) {
    		this.x = x;
    		this.y = y;
    		this.Id = Id;
    		this.currentBusStop = currentBusStop;
    	}
    	
    	public String getCurrentBusStop()
        {
            return currentBusStop;
        }

        public int getX() { return x;}
    	public int getY() { return y;}
    	public Integer getId() { return Id;}
    	
    }
}

