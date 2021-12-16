package org.telosys.tools.api;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ApiUtilTest {
	
	@Test
	public void testIsDbModelFile() throws Exception {
		assertTrue(  ApiUtil.isDbModelFile(new File("/tmp/foo/bar/students.dbrep") ) );
		assertTrue(  ApiUtil.isDbModelFile(new File("/tmp/foo/bar/students.dbmodel") ) );
		assertTrue(  ApiUtil.isDbModelFile(new File("students.dbrep") ) );
		
		assertFalse( ApiUtil.isDbModelFile(new File("/tmp/foo/bar/students.model") ) );
		assertFalse( ApiUtil.isDbModelFile(new File("/tmp/foo/bar/students.foo") ) );
		assertFalse( ApiUtil.isDbModelFile(new File("/tmp/foo/bar/students") ) );
	}

	@Test
	public void testIsDslModelFile() throws Exception {
		assertTrue(  ApiUtil.isDslModelFile(new File("/tmp/foo/bar/students.model") ) );
		assertTrue(  ApiUtil.isDslModelFile(new File("students.model") ) );
		
		assertFalse( ApiUtil.isDslModelFile(new File("/tmp/foo/bar/students.foo") ) );
		assertFalse( ApiUtil.isDslModelFile(new File("/tmp/foo/bar/students.dbrep") ) );
		assertFalse( ApiUtil.isDslModelFile(new File("/tmp/foo/bar/students") ) );
	}

	@Test
	public void testIsDslModelFile2() throws Exception {
		
		// Not DSL file suffix 
		assertNull(  ApiUtil.getDslModelFolder(new File("/tmp/foo/bar/students"), false ) );
		assertNull(  ApiUtil.getDslModelFolder(new File("/tmp/foo/bar/students.foo"), false ) );
		
		// DSL file suffix but doesn't exist 
		File modelFolder = ApiUtil.getDslModelFolder(new File("/tmp/foo/bar/students.model"), false );
		assertNotNull(modelFolder);
		assertEquals("students_model", modelFolder.getName());
	}
}
