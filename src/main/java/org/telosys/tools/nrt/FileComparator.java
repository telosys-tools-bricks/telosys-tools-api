package org.telosys.tools.nrt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileComparator {

	private final File file1 ;
	private final File file2 ;
	private final LineComparator lineComparator ;
	
	public FileComparator(File file1, File file2, LineComparator lineComparator ) {
		this.file1 = file1 ;
		this.file2 = file2 ;
		this.lineComparator = lineComparator ;
	}
	
	public int compare(StringBuffer sb) {
		if ( ! file1.exists() ) {
			sb.append("File not found '" + file1.getAbsolutePath() +"'");
			return 1 ; // considered as 1 difference
		}
		if ( ! file2.exists() ) {
			sb.append("File not found '" + file2.getAbsolutePath() +"'");
			return 1 ; // considered as 1 difference
		}
		try {
			return compareLines(sb, file1, file2);
		} catch (IOException e) {
			throw new RuntimeException("IOException", e);
		}
	}
	
	private int compareLines(StringBuffer sb, File file1, File file2) throws IOException {
		int result = 0 ;
		BufferedReader br1 = new BufferedReader(new FileReader(file1));
		BufferedReader br2 = new BufferedReader(new FileReader(file2));
		
		try {
			result = compareLines(sb, br1, br2);
		}
		finally {
			br1.close();
			br2.close();
		}
		return result ;
	}

	private int compareLines(StringBuffer sb, BufferedReader br1, BufferedReader br2) throws IOException {
		int linesCount = 0 ;
		int diffCount = 0 ;
		String line1 ;
		while ( ( line1 = br1.readLine() ) != null ) {
			linesCount++ ;
			String line2 = br2.readLine() ;
			if ( line2 == null ) {
				sb.append("Line #"+linesCount + " not in file2 (end of file) \n");
				return 1 ;
			}
			if ( ! line1.equals(line2) ) {
				if ( ! consideredAsIdentical(line1, line2) ) {
					diffCount++;
					sb.append("Line #"+linesCount + " is different : \n");
					sb.append(" file1 > " + line1 + "\n");
					sb.append(" file2 > " + line2 + "\n");
				}
			}
		}
		if ( br2.readLine() != null ) {
			linesCount++ ;
			diffCount++;
			sb.append("Line #"+linesCount + " not if file1 (end of file) \n");
		}
		return diffCount ;
	}
	
	private boolean consideredAsIdentical(String line1, String line2) {
		return lineComparator.consideredAsIdentical(line1, line2) ;
	}
}
