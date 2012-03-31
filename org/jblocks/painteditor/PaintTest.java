package org.jblocks.painteditor;

import javax.swing.JFrame;
/**
 * 
 * @author TRocket
 *
 */
public class PaintTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame jf = new JFrame();

PaintEditor pE = new PaintEditor();
pE.setVisible(true);
jf.add(pE);
jf.setVisible(true);
jf.pack();

	}

}
