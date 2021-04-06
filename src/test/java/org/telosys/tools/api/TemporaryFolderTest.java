package org.telosys.tools.api;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertTrue;

public class TemporaryFolderTest {

	@Rule // JUnit RULE
	public TemporaryFolder tmpFolder = new TemporaryFolder(); // // JUnit
																// TemporaryFolder

	@Before
	public void setUp() throws Exception {
		System.out.println("---" );
		System.out.println("Before test : tmpFolder root = " + tmpFolder.getRoot().getAbsolutePath() );
		// examples :
		// C:\Users\xxxxx\AppData\Local\Temp\junit2616807284304745467
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("After test  : tmpFolder root = " + tmpFolder.getRoot().getAbsolutePath() );
		System.out.println("---" );
	}

	@Test
	public void testUsingTempFolder() throws IOException {
		
		File createdFile = tmpFolder.newFile("myfile.txt");
		System.out.println("createdFile : " + createdFile.getAbsolutePath() );
		assertTrue(createdFile.exists());
		assertTrue(createdFile.isFile());
		
		File createdFolder = tmpFolder.newFolder("subfolder");
		System.out.println("createdFolder : " + createdFolder.getAbsolutePath() );
		assertTrue(createdFolder.exists());
		assertTrue(createdFolder.isDirectory());
	}

	@Test
	public void test2() throws IOException {		
		File createdFile = tmpFolder.newFile("myfile2.txt");
		System.out.println("createdFile : " + createdFile.getAbsolutePath() );
	}
}
