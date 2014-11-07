package view;

import java.awt.*;

/**
 * Created by mateusz on 27.05.14.
 */
public interface MiniView {
    public void drawMiniView(Graphics g);
    public void setMiniViewPosition(int x, int y);
    public void setMiniViewSize(int width, int height);
    public void rotateMiniView(int direct);
}
