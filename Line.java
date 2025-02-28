package paint.brush;

import java.awt.*;

public class Line extends DrawingShape {
    private int endX, endY;
    private int thickness;

    public Line(int x, int y, int endX, int endY, Color color, boolean dotted) {
        this(x, y, endX, endY, color, dotted, 2);
    }

    public Line(int x, int y, int endX, int endY, Color color, boolean dotted, int thickness) {
        super(x, y, color, false, dotted);
        this.endX = endX;
        this.endY = endY;
        this.thickness = thickness;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        if (dotted) {
            float[] dashPattern = {5, 5};
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
        } else {
            g2d.setStroke(new BasicStroke(thickness));
        }
        g2d.drawLine(x, y, endX, endY);
    }
}
