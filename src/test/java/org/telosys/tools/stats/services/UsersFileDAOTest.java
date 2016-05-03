package org.telosys.tools.stats.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.telosys.tools.commons.DirUtil;
import org.telosys.tools.commons.FileUtil;

public class UsersFileDAOTest {
	
	
	@Test
	public void testRead1() {
		CounterFileManager cfm = new CounterFileManager("src/test/resources/stats/no-file.data");
		int c = cfm.readCounter();
		assertEquals(0, c);
	}

	@Test
	public void testRead2() {
		CounterFileManager cfm = new CounterFileManager("src/test/resources/stats/counter123.data");
		int c = cfm.readCounter();
		assertEquals(123, c);
	}
	
	@Test
	public void testRead3() {
		CounterFileManager cfm = new CounterFileManager("src/test/resources/stats/counterABC.data");
		int c = cfm.readCounter();
		assertEquals(0, c);
	}
	
	@Test
	public void testRead4() {
		CounterFileManager cfm = new CounterFileManager("src/test/resources/stats/counter789.data");
		int c = cfm.readCounter();
		assertEquals(789, c);
	}
	
	@Test
	public void testWrite1() {
		String fileName = "target/tests-tmp/stats/file1.data" ;
		File file = new File(fileName);
		FileUtil.createParentFolderIfNecessary(file);
		CounterFileManager cfm = new CounterFileManager(fileName);
		cfm.writeCounter(456);
		assertTrue(file.exists());		
		assertEquals(456, cfm.readCounter() );
	}

	@Test
	public void testWrite2() {
		String fileName = "target/tests-tmp/stats/file2.data" ;
		File file = new File(fileName);
		FileUtil.createParentFolderIfNecessary(file);
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException("Cannot create file for test",e);
		}
		
		CounterFileManager cfm = new CounterFileManager(fileName);
		cfm.writeCounter(789);
		assertTrue(file.exists());
		assertEquals(789, cfm.readCounter() );
	}

	@Test
	public void testincrement1() {
		String fileName = "target/tests-tmp/stats/fileIncr1.data" ;
		File file = new File(fileName);
		file.delete();
		FileUtil.createParentFolderIfNecessary(file);
		CounterFileManager cfm = new CounterFileManager(fileName);
		int c = 0 ;
		c = cfm.incrementCounter();
		System.out.println("counter = " + c );
		assertEquals(1, c );
		c = cfm.incrementCounter();
		System.out.println("counter = " + c );
		assertEquals(2, c );
		c = cfm.incrementCounter();
		System.out.println("counter = " + c );
		assertEquals(3, c );
	}

	@Test
	public void testFileCreationWithSubFolders() {
		String fileName = "target/tests-tmp/stats/foo/bar/fileIncr1.count" ;
		DirUtil.deleteDirectory( new File("target/tests-tmp/stats/foo/") );
		CounterFileManager cfm = new CounterFileManager(fileName, true);
		int c = 0 ;
		c = cfm.incrementCounter();
		System.out.println("counter = " + c );
		assertEquals(1, c );
		c = cfm.incrementCounter();
		System.out.println("counter = " + c );
		assertEquals(2, c );
		c = cfm.incrementCounter();
		System.out.println("counter = " + c );
		assertEquals(3, c );
	}

}
