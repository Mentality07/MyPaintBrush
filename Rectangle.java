package paint.brush;

import java.awt.*;

public class Rectangle extends DrawingShape {
    private int width, height;

    public Rectangle(int x, int y, int width, int height, Color color, boolean filled, boolean dotted) {
        super(x, y, color, filled, dotted);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        if (filled) {
            g2d.fillRect(x, y, width, height);
        } else {
            g2d.drawRect(x, y, width, height);
        }
    }
}