package paint.brush;

import java.awt.*;

public abstract class DrawingShape {
    protected int x, y;
    protected Color color;
    protected boolean filled;
    protected boolean dotted;

    public DrawingShape(int x, int y, Color color, boolean filled, boolean dotted) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.filled = filled;
        this.dotted = dotted;
    }

    public boolean isDotted() {
        return dotted;
    }

    public abstract void draw(Graphics2D g2d);
}