package org.telosys.tools.api;

//import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

public class GenericModelLoaderTest {
	
	@Before
	public void setUp() throws Exception {
		System.out.println("Before test");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("After test");
	}

	private TelosysProject initProject() throws Exception {

		String projectFolderFullPath = TestsEnv.createProjectFolder("myproject");
		TelosysProject telosysProject = new TelosysProject(projectFolderFullPath);
		
		System.out.println("Init project...");
		String s = telosysProject.initProject();
		System.out.println(s);
		
		TestsEnv.copyDbModelFile(projectFolderFullPath, "bookstore.dbrep");
		TestsEnv.copyDslModelFiles(projectFolderFullPath, "employees");
		TestsEnv.copyDslModelFiles(projectFolderFullPath, "employees_invalid");
		TestsEnv.copyTemplatesFiles(projectFolderFullPath, "basic-templates-TT210");
		
		return telosysProject ;
	}
	
	@Test
	public void testModelLoadingDSL() throws Exception {
		System.out.println("========== Loading .model ");
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
		Model model = genericModelLoader.loadModel("employees.model");
		assertNotNull(model);
		//assertNull(genericModelLoader.getErrorMessage());
		//assertNull(genericModelLoader.getParsingErrors());
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
		
		Entity employeeEntity = model.getEntityByClassName("Employee");
		assertNotNull(employeeEntity);
		List<Attribute> employeeAttributes = employeeEntity.getAttributes();
		for ( Attribute attribute : employeeAttributes ) {
			System.out.println(" . " + attribute.getName() + " isKeyElement ? " + attribute.isKeyElement());
			if ( attribute.getName().equals("id") ) {
				System.out.println("  'id' attribute");
				assertTrue( attribute.isKeyElement()) ;
				assertTrue( attribute.isNotNull()) ;
			}
			if ( attribute.getName().equals("firstName") ) {
				System.out.println("  'firstName' attribute");
				assertEquals(Integer.valueOf(40), attribute.getMaxLength()) ;
			}
			if ( attribute.getName().equals("birthDate") ) {
				System.out.println("  'birthDate' attribute");
				assertTrue( attribute.isDatePast() ) ;
			}
		}
		//assertEquals(3, employeeAttributes.size());
		assertEquals(4, employeeAttributes.size()); // now all attributes are present !
		//employeeEntity.get

		//LINKS...
//		if ( attribute.getName().equals("country") ) {
//			System.out.println(" 'country' attribute");
//			assertEquals(Integer.valueOf(40), attribute.getMaxLength()) ;
//		}
	}

	@Test(expected=TelosysModelException.class)
	public void testModelLoadingDSL_invalid() throws Exception {
		System.out.println("========== Loading .model INVALID");
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
		genericModelLoader.loadModel("employees_invalid.model");
		
//		Model model = genericModelLoader.loadModel("employees_invalid.model");
//		assertNull(model);
//		Hashtable<String,String> parsingErrors = genericModelLoader.getParsingErrors() ;
//		assertNotNull(genericModelLoader.getErrorMessage());
//		assertNotNull(parsingErrors);
//		System.out.println("Error message : " + genericModelLoader.getErrorMessage() );
//		System.out.println( parsingErrors.size() + " error(s)" );
//		for ( Map.Entry<String,String> entry : parsingErrors.entrySet() ) {
//			System.out.println( "'" + entry.getKey() + "' : " + entry.getValue() );
//		}
	}
	public void testModelLoadingDSL_invalid2() throws Exception {
		System.out.println("========== Loading .model INVALID");
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
		try {
			genericModelLoader.loadModel("employees_invalid.model");
			fail("Not supposed to reach this part of code.");
		} catch (TelosysToolsException e) {
			assertTrue(e instanceof TelosysModelException );
			TelosysModelException tme = (TelosysModelException) e ;
			
			assertNotNull(tme.getMessage());
			System.out.println("Error message : " + tme.getMessage() );

			Map<String,String> parsingErrors = tme.getParsingErrors();
			assertNotNull(parsingErrors);
			System.out.println( parsingErrors.size() + " error(s)" );
			for ( Map.Entry<String,String> entry : parsingErrors.entrySet() ) {
				System.out.println( "'" + entry.getKey() + "' : " + entry.getValue() );
			}
			//e.printStackTrace();
		}
	}

	@Test
	public void testModelLoadingDBREP() throws Exception {
		System.out.println("========== Loading .dbrep ");
		
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
		Model model = genericModelLoader.loadModel("bookstore.dbrep");
		assertNotNull(model);
		//assertNull(genericModelLoader.getErrorMessage());
		//assertNull(genericModelLoader.getParsingErrors());
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
	}

}
