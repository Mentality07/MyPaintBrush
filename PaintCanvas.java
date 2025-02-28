package paint.brush;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PaintCanvas extends JPanel {
    private Color currentColor = Color.BLACK;
    private ShapeType currentShape = ShapeType.LINE;
    private ActionType currentAction = ActionType.DRAW;
    private boolean dotted = false;
    private boolean filled = false;
    private Point startPoint;
    private List<DrawingShape> shapes = new ArrayList<>();
    private Stack<List<DrawingShape>> history = new Stack<>();
    private DrawingShape previewShape = null;
    private Cursor drawCursor, eraserCursor, shapeCursor;

    public PaintCanvas() {
        setBackground(Color.WHITE);
        initializeCursors();
        setCursor(drawCursor);

        addMouseListener(new MouseAdapter() {
    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("Mouse pressed at: " + e.getPoint());
        saveToHistory();
        startPoint = e.getPoint();
        previewShape = null;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("Mouse released at: " + e.getPoint());
        if (currentAction == ActionType.DRAW && previewShape != null) {
            shapes.add(previewShape);
            previewShape = null;
        }
        startPoint = null;
        repaint();
    }
});

addMouseMotionListener(new MouseAdapter() {
    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("Mouse dragged to: " + e.getPoint());
        if (null != currentAction) switch (currentAction) {
            case DRAW -> previewShape = createShape(startPoint, e.getPoint());
            case FREE_HAND -> {
                shapes.add(new Line(startPoint.x, startPoint.y, e.getX(), e.getY(), currentColor, dotted));
                startPoint = e.getPoint();
            }
            case ERASER -> {
                shapes.add(new Line(startPoint.x, startPoint.y, e.getX(), e.getY(), Color.WHITE, false, 10));
                startPoint = e.getPoint();
            }
            default -> {
            }
        }
        repaint();
    }
});
    }

    private void initializeCursors() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        // Create a detailed brush cursor
        BufferedImage brushCursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = brushCursorImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the brush shape
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillOval(5, 5, 22, 22);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(5, 5, 22, 22);

        g2d.dispose();
        drawCursor = toolkit.createCustomCursor(brushCursorImage, new Point(16, 16), "Brush");

        // Eraser cursor (unchanged)
        BufferedImage eraserCursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g2d = eraserCursorImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 10, 10);
        g2d.dispose();
        eraserCursor = toolkit.createCustomCursor(eraserCursorImage, new Point(8, 8), "Eraser");

        // Shape cursor (unchanged)
        BufferedImage shapeCursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g2d = shapeCursorImage.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.drawLine(5, 0, 5, 10);
        g2d.drawLine(0, 5, 10, 5);
        g2d.dispose();
        shapeCursor = toolkit.createCustomCursor(shapeCursorImage, new Point(8, 8), "Shape");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (DrawingShape shape : shapes) {
            drawShape(g2d, shape);
        }
        if (previewShape != null) {
            drawShape(g2d, previewShape);
        }
    }

    private void drawShape(Graphics2D g2d, DrawingShape shape) {
        if (shape.isDotted()) {
            float[] dashPattern = {5, 5};
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
        } else {
            g2d.setStroke(new BasicStroke(2));
        }
        shape.draw(g2d);
    }

    public void saveDrawing() {
        try {
            int seqNumber = getNextSequenceNumber();
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            paint(g2d);
            g2d.dispose();

            File output = new File("drawing_" + seqNumber + ".png");
            ImageIO.write(image, "png", output);
            JOptionPane.showMessageDialog(this, "Image saved as " + output.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving image!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getNextSequenceNumber() {
        int maxSeq = 0;
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.matches("drawing_\\d+\\.png"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                int num = Integer.parseInt(name.replaceAll("\\D+", ""));
                maxSeq = Math.max(maxSeq, num);
            }
        }
        return maxSeq + 1;
    }

    public void undo() {
    System.out.println("Undo called. History size: " + history.size());
    if (!history.isEmpty()) {
        shapes = history.pop();
        repaint();
    }
}

private void saveToHistory() {
    System.out.println("Saving to history. Shapes count: " + shapes.size());
    history.push(new ArrayList<>(shapes));
}
    private DrawingShape createShape(Point start, Point end) {
    System.out.println("Creating shape: " + currentShape);
    int x1 = Math.min(start.x, end.x);
    int y1 = Math.min(start.y, end.y);
    int x2 = Math.max(start.x, end.x);
    int y2 = Math.max(start.y, end.y);

    switch (currentShape) {
        case RECTANGLE -> {
            return new Rectangle(x1, y1, x2 - x1, y2 - y1, currentColor, filled, dotted);
        }
        case OVAL -> {
            return new Oval(x1, y1, x2 - x1, y2 - y1, currentColor, filled, dotted);
        }
        case LINE -> {
            return new Line(start.x, start.y, end.x, end.y, currentColor, dotted);
        }
        default -> throw new IllegalArgumentException("Unknown shape type");
    }
}

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setCurrentShape(ShapeType shapeType) {
        this.currentShape = shapeType;
        this.currentAction = ActionType.DRAW;
        setCursor(shapeCursor);
    }

    public void setCurrentAction(ActionType actionType) {
        this.currentAction = actionType;
        switch (actionType) {
            case DRAW -> setCursor(drawCursor);
            case FREE_HAND -> setCursor(drawCursor);
            case ERASER -> setCursor(eraserCursor);
        }
    }

    public void setDotted(boolean dotted) {
        this.dotted = dotted;
        this.filled = false;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
        this.dotted = false;
    }

    public void clearAll() {
        saveToHistory();
        shapes.clear();
        repaint();
    }

    public enum ShapeType {
        RECTANGLE, OVAL, LINE
    }

    public enum ActionType {
        DRAW, FREE_HAND, ERASER
    }
    
    
}