package org.jblocks.editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.jblocks.gui.JDragPane;

/**
 * Helper class for block/script dragging. <br />
 * 
 * @see org.jblocks.gui.JDragPane
 * @author ZeroLuck
 */
class Drag {

    private static void getDragTargets0(List<JScriptPane> panes, Container cont) {
        for (Component c : cont.getComponents()) {
            if (c instanceof JScriptPane) {
                panes.add((JScriptPane) c);
            }
            if (c instanceof Container) {
                getDragTargets0(panes, (Container) c);
            }
        }
    }

    /**
     * Returns all JScriptPanes on the specified JDragPane. <br />
     */
    static JScriptPane[] getDropTargets(JDragPane root) {
        List<JScriptPane> panes = new ArrayList<JScriptPane>();
        getDragTargets0(panes, root);
        return panes.toArray(new JScriptPane[]{});
    }

    /**
     * Tries to find a drop target for a specified component. <br />
     * 
     * @param root the JDragPane of the specified component
     * @param comp the component
     * @param dragRect the bounds of the component
     * @return a JScriptPane drop target or null.
     */
    static JScriptPane getTarget(final JDragPane root, final Container cont, final Rectangle dragRect) {
        JScriptPane[] allTargets = getDropTargets(root);
        JScriptPane target = null;
        for (JScriptPane p : allTargets) {
            Rectangle rect = SwingUtilities.convertRectangle(p, p.getVisibleRect(), root);
            if (p.isDragEnabled() && rect.intersects(dragRect)) {
                target = p;
                break;
            }
        }
        return target;
    }

    static void dragPuzzle(JDragPane root, final Container cont, Point p, final AbstrBlock puzzle) {
        JDragPane.DragFinishedHandler handler = new JDragPane.DragFinishedHandler() {

            @Override
            public void dragFinished(JDragPane jdrag, Component c, Point location) {
                JScriptPane target = getTarget(jdrag, cont, new Rectangle(location, c.getSize()));
                if (target != null) {
                    Point p = SwingUtilities.convertPoint(jdrag, location, target);

                    AbstrBlock dragBlock = (AbstrBlock) puzzle;
                    dragBlock.setLocation(p);

                    AbstrBlock[] blocks = JBlockSequence.getPuzzlePieces((Puzzle) puzzle, PuzzleAdapter.TYPE_DOWN);
                    for (AbstrBlock b : blocks) {
                        Container parent = b.getParent();
                        if (parent != null) {
                            parent.remove(b);
                        }
                        target.add(b, 0);
                    }
                    ((Puzzle) puzzle).layoutPuzzle();

                    target.invalidate();
                    target.validate();
                    target.repaint();

                    // this can be a problem in future.
                    dragBlock.releasedEvent(null);
                } else {
                    AbstrBlock[] blocks = JBlockSequence.getPuzzlePieces((Puzzle) puzzle, PuzzleAdapter.TYPE_DOWN);
                    for (AbstrBlock b : blocks) {
                        Container parent = b.getParent();
                        if (parent != null) {
                            parent.remove(b);
                        }
                    }
                }
            }
        };
        root.setDrag(puzzle, cont, puzzle.getLocation(), p, handler);
        AbstrBlock[] blocks = JBlockSequence.getPuzzlePieces((Puzzle) puzzle, PuzzleAdapter.TYPE_DOWN);
        for (AbstrBlock b : blocks) {
            b.getParent().remove(b);
            root.add(b, 0);
        }
        ((Puzzle) puzzle).layoutPuzzle();
    }

    static void drag(final JDragPane root, final Container cont, Point p, AbstrBlock block) {
        JDragPane.DragFinishedHandler handler = new JDragPane.DragFinishedHandler() {

            @Override
            public void dragFinished(JDragPane jdrag, Component c, Point location) {
                JScriptPane target = getTarget(jdrag, cont, new Rectangle(location, c.getSize()));
                if (target != null) {
                    Point p = SwingUtilities.convertPoint(jdrag, location, target);

                    AbstrBlock dragBlock = (AbstrBlock) c;

                    target.add(c);
                    c.setLocation(p);
                    target.invalidate();
                    target.validate();
                    target.repaint();

                    // this can be a problem in future.
                    dragBlock.releasedEvent(null);
                    dragBlock.toFront();
                }
            }
        };
        root.setDrag(block, cont, block.getLocation(), p, handler);
    }
}
