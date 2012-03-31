package org.jblocks.blockloader;
/**
 * All reporter blocks should implement this
 * @author TRocket
 * @version 0.1
 *
 */
public interface ReporterBlock {
	/**
	 * @param args the argument
	 * @return the reported object 
	 */
public Object evaluate(Object[] args);
}
