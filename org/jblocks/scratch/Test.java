package org.jblocks.scratch;

import java.io.FileInputStream;
import java.util.Arrays;

class Test {

    public static void main(String[] args) throws Exception {
        final String testFile = "C:/JTest/test.sb";
        
        ObjReader r = new ObjReader(new FileInputStream(testFile));
        Object[][] objTable = r.readObjects();

        // Or try Arrays.deepToString(objTable)
        System.out.println(Arrays.toString(objTable));
    }
}
