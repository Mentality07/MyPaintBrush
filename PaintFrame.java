package paint.brush;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PaintFrame extends JFrame {
    private static final Border DEFAULT_BORDER = UIManager.getBorder("Button.border");
    private PaintCanvas paintCanvas;
    private JButton lastSelectedColorButton = null;
    private JButton lastSelectedShapeButton = null;

    public PaintFrame() {
        setTitle("My Paint Brush");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        paintCanvas = new PaintCanvas();
        add(paintCanvas, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.NORTH);
    }

 private JPanel createControlPanel() {
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    controlPanel.setBackground(Color.LIGHT_GRAY);
    controlPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

    // Add color buttons
    controlPanel.add(createColorButton(Color.RED));
    controlPanel.add(createColorButton(Color.GREEN));
    controlPanel.add(createColorButton(Color.BLUE));

    // Add shape buttons
    controlPanel.add(createShapeButton("Rectangle", PaintCanvas.ShapeType.RECTANGLE));
    controlPanel.add(createShapeButton("Oval", PaintCanvas.ShapeType.OVAL));
    controlPanel.add(createShapeButton("Line", PaintCanvas.ShapeType.LINE));

    // Add action buttons
    controlPanel.add(createActionButton("Free Hand", PaintCanvas.ActionType.FREE_HAND));
    controlPanel.add(createActionButton("Eraser", PaintCanvas.ActionType.ERASER));

    // Add clear button
    JButton clearButton = new JButton("Clear All");
    clearButton.setFocusPainted(false);
    clearButton.addActionListener(e -> paintCanvas.clearAll());
    controlPanel.add(clearButton);

    // Add dotted and filled checkboxes
    JCheckBox dottedCheckbox = new JCheckBox("Dotted");
    dottedCheckbox.setFocusPainted(false);
    dottedCheckbox.setIcon(createCheckBoxIcon(false));
    dottedCheckbox.setSelectedIcon(createCheckBoxIcon(true));
    JCheckBox filledCheckbox = new JCheckBox("Filled");
    filledCheckbox.setFocusPainted(false);
    filledCheckbox.setIcon(createCheckBoxIcon(false));
    filledCheckbox.setSelectedIcon(createCheckBoxIcon(true));

    dottedCheckbox.addActionListener(e -> {
        if (dottedCheckbox.isSelected()) {
            filledCheckbox.setSelected(false);
            paintCanvas.setDotted(true);
        } else {
            paintCanvas.setDotted(false);
        }
    });

    filledCheckbox.addActionListener(e -> {
        if (filledCheckbox.isSelected()) {
            dottedCheckbox.setSelected(false);
            paintCanvas.setFilled(true);
        } else {
            paintCanvas.setFilled(false);
        }
    });

    controlPanel.add(dottedCheckbox);
    controlPanel.add(filledCheckbox);

    // Add undo button
    JButton undoButton = new JButton("Undo");
    undoButton.setFocusPainted(false);
    undoButton.addActionListener(e -> paintCanvas.undo());
    controlPanel.add(undoButton);

    return controlPanel;
}

    private Icon createCheckBoxIcon(boolean selected) {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (selected) {
            g2d.setColor(Color.BLUE);
            g2d.fillOval(0, 0, size, size);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawOval(0, 0, size - 1, size - 1);
        }

        g2d.dispose();
        return new ImageIcon(image);
    }

    private JButton createColorButton(Color color) {
        JButton button = new JButton();
        button.setFocusPainted(false);
        button.setIcon(createColorIcon(color));
        button.addActionListener(e -> {
            paintCanvas.setCurrentColor(color);
            applyColorEffect(button);
        });
        return button;
    }

    private JButton createShapeButton(String text, PaintCanvas.ShapeType shapeType) {
    JButton button = new JButton(text);
    button.setFocusPainted(false);
    button.addActionListener(e -> {
        System.out.println("Shape button clicked: " + shapeType);
        paintCanvas.setCurrentShape(shapeType);
        applyShapeEffect(button);
    });
    return button;
}

    private JButton createActionButton(String text, PaintCanvas.ActionType actionType) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            paintCanvas.setCurrentAction(actionType);
            applyShapeEffect(button);
        });
        return button;
    }

  private void applyColorEffect(JButton button) {
    if (lastSelectedColorButton != null) {
        lastSelectedColorButton.setBorder(DEFAULT_BORDER); // Reset previous button
        lastSelectedColorButton.setBackground(UIManager.getColor("Button.background"));
    }
    button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3)); // Add a border to the selected button
    button.setBackground(Color.LIGHT_GRAY); // Change the background color
    lastSelectedColorButton = button; // Update the last selected button
}
    private void applyShapeEffect(JButton button) {
    if (lastSelectedShapeButton != null) {
        lastSelectedShapeButton.setBorder(DEFAULT_BORDER); // Reset previous button
        lastSelectedShapeButton.setBackground(UIManager.getColor("Button.background"));
    }
    button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3)); // Add a border to the selected button
    button.setBackground(Color.LIGHT_GRAY); // Change the background color
    lastSelectedShapeButton = button; // Update the last selected button
}

    private Icon createColorIcon(Color color) {
        int size = 20;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, size, size);
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    
}