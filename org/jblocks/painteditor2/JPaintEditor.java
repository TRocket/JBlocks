package org.jblocks.painteditor2;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import org.jblocks.JBlocks;
import org.jblocks.gui.JSmallColorChooser;
import org.jblocks.gui.JZoomChooser;

/**
 * A vector-graphics paint editor. <br />
 * 
 * @author ZeroLuck
 */
public final class JPaintEditor extends JPanel {

    private JToolBar tools;
    private JPaintCanvas paint;
    private List<PaintAction> actions;
    private int actionsOffset = 0;
    private PaintAction preview;
    private PaintTool tool;
    private JSmallColorChooser colorChooser;
    private JCheckBox fill;
    private Color col = Color.BLACK;
    private BufferedImage backImg;
    private int actionsBackImgOffset = 0;

    public JPaintEditor() {
        tools = new JToolBar();
        tools.setFloatable(false);
        colorChooser = new JSmallColorChooser();
        colorChooser.addColorChangedListener(new JSmallColorChooser.ColorChangedListener() {

            @Override
            public void colorChanged(JSmallColorChooser ch, Color c) {
                col = c;
            }
        });
        fill = new JCheckBox("Fill");
        colorChooser.setStyle(JSmallColorChooser.GRADIENT);
        paint = new JPaintCanvas(new BufferedImage(480, 360, BufferedImage.TYPE_INT_ARGB));
        actions = new ArrayList<PaintAction>();

        setLayout(new BorderLayout());
        tools.add(new JButton(new ImageIcon(JBlocks.class.getResource("res/open.png"))));
        tools.add(new JButton(new ImageIcon(JBlocks.class.getResource("res/save.png"))));

        JButton clear = new JButton(new ImageIcon(JBlocks.class.getResource("res/clear.png")));
        clear.setToolTipText("clear");
        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                clear();
            }
        });
        tools.add(clear);
        tools.add(new JSeparator(JSeparator.VERTICAL));
        JButton redo = new JButton(new ImageIcon(JBlocks.class.getResource("res/redo.png")));
        redo.setToolTipText("redo");
        redo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                redo();
            }
        });
        JButton undo = new JButton(new ImageIcon(JBlocks.class.getResource("res/undo.png")));
        undo.setToolTipText("undo");
        undo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                undo();
            }
        });
        tools.add(undo);
        tools.add(redo);


        tools.add(new JSeparator(JSeparator.VERTICAL));

        // <tools>
        ImageIcon pipetteIcon = new ImageIcon(JBlocks.class.getResource("res/pipette.png"));
        addTool(new PipetteTool(this, pipetteIcon.getImage()), pipetteIcon, "pipette");
        addTool(new StampTool(this), new ImageIcon(JBlocks.class.getResource("res/stamp.png")), "stamp");

        ImageIcon fillIcon = new ImageIcon(JBlocks.class.getResource("res/fill.png"));
        addTool(new FillTool(this, fillIcon.getImage()), fillIcon, "flood-fill : can be very slow!");
        // </tools>

        tools.add(new JSeparator(JSeparator.VERTICAL));

        // <tools>
        addTool(new BrushTool(this), new ImageIcon(JBlocks.class.getResource("res/brush.png")), "brush");
        addTool(new LineTool(this), new ImageIcon(JBlocks.class.getResource("res/line.png")), "line");
        addTool(new RectTool(this), new ImageIcon(JBlocks.class.getResource("res/rect.png")), "rect");
        addTool(new CircleTool(this), new ImageIcon(JBlocks.class.getResource("res/circle.png")), "circle");
        // </tools>

        add(tools, BorderLayout.NORTH);

        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        JPanel leftSouth = new JPanel();
        leftSouth.setLayout(new GridLayout(0, 1));
        leftSouth.add(fill);

        left.add(colorChooser, BorderLayout.NORTH);
        left.add(leftSouth, BorderLayout.CENTER);


        add(left, BorderLayout.WEST);

        add(new JScrollPane(paint), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new java.awt.BorderLayout());

        JZoomChooser zoomChg = new JZoomChooser();
        zoomChg.addZoomChangedListener(new JZoomChooser.ZoomChangedListener() {

            @Override
            public void zoomChanged(JZoomChooser ch, int newZoom) {
                paint.setZoom(newZoom + 1);
            }
        });

        JPanel bottomEast = new JPanel();
        bottomEast.add(zoomChg);
        bottomEast.add(new JButton("OK"));
        bottomEast.add(new JButton("Cancel"));
        bottom.add(bottomEast, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Adds a PaintAction. <br />
     * redo() won't work anymore after this call <br />
     * until the next undo() <br />
     * <br />
     * <code>paintUpdate()</code> have to be called after this. <br />
     * 
     * @param a - the action to add.
     * @see #paintUpdate() 
     * @see #redo()
     */
    void addAction(PaintAction a) {
        actions = actions.subList(0, actionsOffset);

        actions.add(a);
        actionsOffset = actions.size();
    }

    private void paintUpdateOn(BufferedImage img, int off, boolean pre, boolean back) {
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        Composite c = g.getComposite();
        g.setComposite(
                AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setComposite(c);

        if (back && backImg != null) {
            g.drawImage(backImg, 0, 0, this);
        }

        for (int i = off; i < actionsOffset; i++) {
            actions.get(i).draw(img, g);
        }
        if (pre) {
            if (preview != null) {
                preview.draw(img, g);
            }
        }
    }

    private BufferedImage deepSubimage(BufferedImage img, int x, int y, int w, int h) {
        BufferedImage n = new BufferedImage(w, h, img.getType());
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                n.setRGB(i - x, j - y, img.getRGB(i, j));
            }
        }
        return n;
    }

    private void removeBackImage() {
        actionsBackImgOffset = 0;
        backImg = null;
    }

    private void createBackImage() {
        BufferedImage img = paint.getImage();
        backImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        paintUpdateOn(backImg, 0, false, false);
        actionsBackImgOffset = actionsOffset;
    }

    /**
     * Updates the vector-graphics on the JPaintCanvas. <br />
     * The preview action is painted too. <br />
     * 
     * @see #preview
     */
    void paintUpdate() {
        paintUpdateOn(paint.getImage(), actionsBackImgOffset, true, true);

        // to slow? create a back-buffer.
        if (actionsOffset - actionsBackImgOffset > 3) {
            createBackImage();
        }

        paint.updateImage();
    }

    /**
     * the name says all... <br />
     */
    void undo() {
        if (actionsOffset > 0) {
            actionsOffset--;
            if (actionsOffset < actionsBackImgOffset) {
                removeBackImage();
            }

            paintUpdate();
        }
    }

    /**
     * the name says all... <br />
     */
    void redo() {
        if (actionsOffset < actions.size()) {
            actionsOffset++;

            paintUpdate();
        }
    }

    /**
     * the name says all... <br />
     */
    void clear() {
        actionsOffset = 0;
        actions.clear();
        removeBackImage();
        paintUpdate();
    }

    /**
     * Adds a PaintTool to the editor's ToolBar. <br />
     * 
     * @param t - the PaintTool
     * @param icon - the icon of this PaintTool
     * @param name - the name of this PaintTool
     */
    void addTool(final PaintTool t, ImageIcon icon, String name) {
        JButton b = new JButton(icon);
        b.setToolTipText(name);
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                setTool(t);
            }
        });

        tools.add(b);
    }

    /**
     * Sets the preview action. <br />
     * paintUpdate() has to be called after this. <br />
     * 
     * @see #paintUpdate() 
     * @param a - the preview action.
     */
    void setPreviewAction(PaintAction a) {
        preview = a;
    }

    /**
     * @param t - the PaintTool which should be used.
     */
    void setTool(PaintTool t) {
        if (tool != null) {
            tool.uninstall(paint);
        }
        preview = null;
        tool = t;
        if (t != null) {
            t.install(paint);
        }
        paintUpdate();
    }

    PaintAction[] getActions() {
        return actions.toArray(new PaintAction[]{});
    }

    Stroke getLineStroke() {
        return new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    Color getColorB() {
        return Color.YELLOW;
    }

    Color getColorA() {
        return col;
    }

    void setColorA(Color c) {
        if (c == null) {
            throw new IllegalArgumentException("'c' is null");
        }
        col = c;
    }

    JPaintCanvas getCanvas() {
        return paint;
    }

    boolean getFill() {
        return fill.isSelected();
    }
}
