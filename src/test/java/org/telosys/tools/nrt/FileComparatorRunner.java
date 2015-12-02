package org.telosys.tools.nrt;

import java.io.File;

import org.telosys.tools.api.TestsEnv;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.nrt.FileComparator;

public class FileComparatorRunner {

	public static void main(String[] args) throws TelosysToolsException, GeneratorException {
		compare("aaa1.txt", "aaa2.txt");
		compare("bbb1.txt", "bbb2.txt");
		compare("ccc1.txt", "ccc2.txt");
		compare("ddd1.txt", "ddd2.txt");
	}
	
	public static File getResourceFile(String fileName) {
		return new File(TestsEnv.SRC_TEST_RESOURCES + "files/" + fileName );
	}
	
	public static void compare(String fileName1, String fileName2 ) {
		System.out.println("-----------------------------" );
		System.out.println("Comparing " + fileName1 + " and " + fileName2 );
		
		LineComparatorForGeneratedFiles lineComparator = new LineComparatorForGeneratedFiles();
		FileComparator fc = new FileComparator( getResourceFile(fileName1) , getResourceFile(fileName2), lineComparator );

		StringBuffer sb = new StringBuffer();
		int r = fc.compare(sb);
		System.out.println(  r == 0 ? "Identical" : "Different" ) ;
		System.out.println(sb);
	}
	
}
