package org.telosys.tools.nrt;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.generator.GeneratorException;

public class LineComparatorRunner {

	public static void main(String[] args) throws TelosysToolsException, GeneratorException {
		String line1 = " * Created on 3 sept. 2015 ( Time 15:45:42 )" ;
		String line2 = " * Created on 3 sept. 2015 ( Time 18:53:55 )" ;
		
		testMatches(line1, line2) ;
	}
	
	public static boolean testMatches(String line1, String line2) {
		LineComparator lc = new LineComparatorForGeneratedFiles();
		boolean r = lc.consideredAsIdentical(line1, line2);
		System.out.println("identical ? " + r  );
		return r ;
	}
}
