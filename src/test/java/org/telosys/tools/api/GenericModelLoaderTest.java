package org.telosys.tools.api;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.dsl.DslModelManager;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
	private TelosysToolsCfg getTelosysToolsCfg() throws Exception {
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		return telosysProject.getTelosysToolsCfg();
	}	
//	private GenericModelLoader getGenericModelLoader() throws Exception {
//		TelosysProject telosysProject = initProject() ;
//		System.out.println("getTelosysToolsCfg...");
//		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();		
//		return new GenericModelLoader(telosysToolsCfg);
//	}
//	private void printErrorsOLD(String errorMessage, Map<String,String> parsingErrors) {
//		System.out.println("Error message : " + errorMessage );
//		System.out.println( parsingErrors.size() + " error(s) : " );
//		for ( Map.Entry<String,String> entry : parsingErrors.entrySet() ) {
//			System.out.println( " . '" + entry.getKey() + "' : " + entry.getValue() );
//		}
//		System.out.flush();
//	}
	private void printErrors(String errorMessage, Map<String,List<String>> errors) {
		System.out.println("MODEL ERRORS : ");
		System.out.println(" --> Main message : " + errorMessage);
		for ( String entityName : errors.keySet() ) {
			System.out.println(" --> Errors for entity '" + entityName + "' : " );
			for ( String err : errors.get(entityName) ) {
            	System.out.println(" . " + err );
			}
		}
	}
	
	
	@Test
	public void testModelLoading_DSL_valid() throws Exception {
		System.out.println("========== Loading .model ");
//		TelosysProject telosysProject = initProject() ;
//		System.out.println("getTelosysToolsCfg...");
//		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
//		GenericModelLoader genericModelLoader =  getGenericModelLoader();
//		Model model = genericModelLoader.loadModel("employees.model");
		Model model = TestUtils.loadModelWithGenericModelLoader("employees.model", getTelosysToolsCfg());
		
		
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

		assertEquals(3, employeeAttributes.size()); // since v 3.3 only declared attributes

		//LINKS...
//		if ( attribute.getName().equals("country") ) {
//			System.out.println(" 'country' attribute");
//			assertEquals(Integer.valueOf(40), attribute.getMaxLength()) ;
//		}
	}
	
	@Test
	public void testDslModelManagerWithInvalidModel() throws Exception {
		
		String filePath = getTelosysToolsCfg().getDslModelFileAbsolutePath("employees_invalid.model");

		DslModelManager dslModelManager = new DslModelManager();
		Model model = dslModelManager.loadModel( filePath );
		assertNotNull(dslModelManager.getErrorMessage());
//		assertNotNull(dslModelManager.getParsingErrors());
		assertNotNull(dslModelManager.getErrorsMap());
//		printErrors(dslModelManager.getErrorMessage(), dslModelManager.getParsingErrors());
		printErrors(dslModelManager.getErrorMessage(), dslModelManager.getErrorsMap());
		assertTrue(dslModelManager.getErrorsMap().size() > 0 );
		assertNull(model);
	}
	
	@Test
	public void testModelLoading_DSL_invalid() throws Exception {
		System.out.println("========== Loading .model INVALID");
//		GenericModelLoader genericModelLoader =  getGenericModelLoader();
		Model model = null;
		try {
			// supposed to throw TelosysModelException
			//model = genericModelLoader.loadModel("employees_invalid.model");
			model = TestUtils.loadModelWithGenericModelLoader("employees_invalid.model", getTelosysToolsCfg());
			fail();
		} catch (TelosysModelException tme) {
			printErrors(tme.getMessage(), tme.getParsingErrors());
			assertNotNull(tme.getMessage());
			assertNotNull(tme.getParsingErrors());
		}
		assertNull(model);	
	}

	@Test
	public void testModelLoading_DSL_invalid2() throws Exception {
		System.out.println("========== Loading .model INVALID");
//		GenericModelLoader genericModelLoader =  getGenericModelLoader();
		Model model = null;
		try {
//			model = genericModelLoader.loadModel("employees_invalid.model");
			model = TestUtils.loadModelWithGenericModelLoader("employees_invalid.model", getTelosysToolsCfg());
			fail("Not supposed to reach this part of code.");
		} catch (TelosysToolsException e) {
			// with standard TelosysToolsException + cast
			assertTrue(e instanceof TelosysModelException );
			TelosysModelException tme = (TelosysModelException) e ;
			
			printErrors(tme.getMessage(), tme.getParsingErrors());
			assertNotNull(tme.getMessage());
			assertNotNull(tme.getParsingErrors());
		}
		assertNull(model);	
	}

	@Test
	public void testModelLoading_DBREP_valid() throws Exception {
		System.out.println("========== Loading .dbrep ");
		
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
//		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
//		Model model = genericModelLoader.loadModel("bookstore.dbrep");
		
		Model model = TestUtils.loadModelWithGenericModelLoader("bookstore.dbrep", telosysToolsCfg);
		
		assertNotNull(model);
		//assertNull(genericModelLoader.getErrorMessage());
		//assertNull(genericModelLoader.getParsingErrors());
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
	}

}
