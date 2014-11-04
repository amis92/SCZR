package pl.rbraun.view;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JComponent;

public class Animation extends JComponent {
	private BusAnimation bus1;
	private BusAnimation bus2;
	
	public Animation(int x, int y) {
		bus1 = new BusAnimation(10, 10);
		bus2 = new BusAnimation(500, 10);
	}
	
	public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        bus1.print(g);
        bus2.print(g);
        
        //g.drawString("Linia 158", 20, 20);
        //g.drawRect(xPos, yPos, 100, 100);
	}
	
	class BusAnimation extends JComponent {
		private int xPos;
		private int yPos;
		
		BusAnimation(int x, int y) {
			xPos = x;
			yPos = y;
		}
		
		public void paint(Graphics g) {
	        Graphics2D g2 = (Graphics2D) g;
	        
	        g.drawString("Linia 158", 20, 20);
	        g.drawRect(xPos, yPos, 100, 100);
		}
		
		void moveX(int x) {
			xPos+=x;
		}
		
		void moveY(int y) {
			yPos+=y;
		}
	}
}
