package org.jblocks.blockloader;

import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.jblocks.editor.AbstrBlock;
import org.jblocks.editor.JScriptPane;
/**
 * 
 * @author TRocket
 *
 */
public class BlockLoader {
	/**
	 * 
	 * @param file the blocks block.xml file
	 * 
	 * @return the loaded block
	 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public Constructor load(InputStream file){
	XML xml = new XML(file);
	System.out.println(xml.getClassname());
	System.out.println(xml.getDocRootName());
	System.out.println(xml.getType());
	System.out.println(xml.getBlock());
	System.out.println(xml.getDependicies());
	xml.getDependicies()[0].isAvailable();
	for (int i = 0; i < xml.getDependicies().length; i++) {
		xml.getDependicies()[i].isAvailable();
	}
	Class<? extends AbstrBlock> cl = null;
	try {
		cl = (Class<? extends AbstrBlock>) Class.forName(xml.getClassname());
	} catch (ClassNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
   	Class partype = JScriptPane.class;
   	Constructor ct = null;
	try {
		ct = cl.getConstructor(partype);
	} catch (NoSuchMethodException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      // AbstrBlock blockx = (AbstrBlock) ct.newInstance(pane);
	return ct;
	
}
}
