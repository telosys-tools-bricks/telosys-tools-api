package org.telosys.tools.api;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.dsl.DslModelErrors;
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
	
	private static final String EMPLOYEES_MODEL         = "employees" ;
	private static final String EMPLOYEES_INVALID_MODEL = "employees-invalid" ;
	
	// JUnit RULE with JUnit TemporaryFolder
	// the TemporaryFolder is different fro each test and deleted after each test
	@Rule 
	public TemporaryFolder tmpFolder = new TemporaryFolder(); 

	private String createProjectFolder() throws Exception {
		File createdFolder = tmpFolder.newFolder("myproject");
		println("createProjectFolder : " + createdFolder.getAbsolutePath() );
		assertTrue(createdFolder.exists());
		assertTrue(createdFolder.isDirectory());
		return createdFolder.getAbsolutePath();
	}
	
	@Before
	public void setUp() throws Exception {
		//println("Before test");
	}

	@After
	public void tearDown() throws Exception {
		//println("After test");
	}
	private void println(String msg) {
		System.out.println(msg);
	}
	private TelosysProject initProject() throws Exception {
		String projectFolderFullPath = createProjectFolder();
		TelosysProject telosysProject = new TelosysProject(projectFolderFullPath);
		println("Init project...");
		String s = telosysProject.initProject();
		println(s);
		//TestsEnv.copyDbModelFile(projectFolderFullPath, "bookstore.dbrep");
//		TestsEnv.copyDslModelFiles(projectFolderFullPath, "employees");
		TestsEnv.copyDslModelFiles(projectFolderFullPath, EMPLOYEES_MODEL);
//		TestsEnv.copyDslModelFiles(projectFolderFullPath, "employees_invalid");
		TestsEnv.copyDslModelFiles(projectFolderFullPath, EMPLOYEES_INVALID_MODEL);
		TestsEnv.copyTemplatesFiles(projectFolderFullPath, "basic-templates-TT210");
		return telosysProject ;
	}
	private TelosysToolsCfg getTelosysToolsCfg() throws Exception {
		TelosysProject telosysProject = initProject() ;
		println("getTelosysToolsCfg...");
		return telosysProject.getTelosysToolsCfg();
	}	

//	private void printErrors(String errorMessage, Map<String,List<String>> errors) {
//		println("MODEL ERRORS : ");
//		println(" --> Main message : " + errorMessage);
//		for ( String entityName : errors.keySet() ) {
//			println(" --> Errors for entity '" + entityName + "' : " );
//			for ( String err : errors.get(entityName) ) {
//            	println(" . " + err );
//			}
//		}
//	}
	private void printErrors(String errorMessage, DslModelErrors errors) {
		TestUtils.printErrors(errorMessage, errors);
	}
	
	
	@Test
	public void testModelLoading_DSL_valid() throws Exception {
		println("========== Loading .model ");
//		Model model = TestUtils.loadModelWithGenericModelLoader("employees.model", getTelosysToolsCfg());
//		Model model = TestUtils.loadModelWithGenericModelLoader(EMPLOYEES_MODEL, getTelosysToolsCfg());
		TelosysProject telosysProject = initProject();
		Model model = TestUtils.loadModel(telosysProject, EMPLOYEES_MODEL);
		
		assertNotNull(model);
		println("Model loaded : " + model.getEntities().size() + " entities");
		
		Entity employeeEntity = model.getEntityByClassName("Employee");
		assertNotNull(employeeEntity);
		List<Attribute> employeeAttributes = employeeEntity.getAttributes();
		for ( Attribute attribute : employeeAttributes ) {
			println(" . " + attribute.getName() + " isKeyElement ? " + attribute.isKeyElement());
			if ( attribute.getName().equals("id") ) {
				println("  'id' attribute");
				assertTrue( attribute.isKeyElement()) ;
				assertTrue( attribute.isNotNull()) ;
			}
			if ( attribute.getName().equals("firstName") ) {
				println("  'firstName' attribute");
				assertEquals(Integer.valueOf(40), attribute.getMaxLength()) ;
			}
			if ( attribute.getName().equals("birthDate") ) {
				println("  'birthDate' attribute");
				assertTrue( attribute.isDatePast() ) ;
			}
		}

		assertEquals(3, employeeAttributes.size()); // since v 3.3 only declared attributes

		//LINKS...
//		if ( attribute.getName().equals("country") ) {
//			println(" 'country' attribute");
//			assertEquals(Integer.valueOf(40), attribute.getMaxLength()) ;
//		}
	}
	
	@Test
	public void testDslModelManagerWithInvalidModel() throws Exception {
		
		//String filePath = getTelosysToolsCfg().getDslModelFileAbsolutePath("employees_invalid.model");
		String filePath = getTelosysToolsCfg().getModelFolderAbsolutePath(EMPLOYEES_INVALID_MODEL);

		DslModelManager dslModelManager = new DslModelManager();
		Model model = dslModelManager.loadModel( filePath );
		assertNotNull(dslModelManager.getErrorMessage());
		assertNotNull(dslModelManager.getErrors());
		printErrors(dslModelManager.getErrorMessage(), dslModelManager.getErrors());
		assertTrue(dslModelManager.getErrors().getNumberOfErrors() > 0 );
		assertNull(model);
	}
	
	@Test
	public void testModelLoading_DSL_invalid() throws Exception {
		println("========== Loading .model INVALID");
//		GenericModelLoader genericModelLoader =  getGenericModelLoader();
		Model model = null;
		try {
			// supposed to throw TelosysModelException
			//model = genericModelLoader.loadModel("employees_invalid.model");
//			model = TestUtils.loadModelWithGenericModelLoader("employees_invalid.model", getTelosysToolsCfg());
//			model = TestUtils.loadModelWithGenericModelLoader(EMPLOYEES_INVALID_MODEL, getTelosysToolsCfg());
			TelosysProject telosysProject = initProject();
			model = TestUtils.loadModel(telosysProject, EMPLOYEES_INVALID_MODEL);
			fail();
		} catch (TelosysModelException tme) {
//			printErrors(tme.getMessage(), tme.getParsingErrors());
			printErrors(tme.getMessage(), tme.getDslModelErrors());
			assertNotNull(tme.getMessage());
//			assertNotNull(tme.getParsingErrors());
			assertNotNull(tme.getDslModelErrors());
		}
		assertNull(model);	
	}

//	@Test
//	public void testModelLoading_DBREP_valid() throws Exception {
//		println("========== Loading .dbrep ");
//		
//		TelosysProject telosysProject = initProject() ;
//		println("getTelosysToolsCfg...");
//		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
//		
//		Model model = TestUtils.loadModelWithGenericModelLoader("bookstore.dbrep", telosysToolsCfg);
//		
//		assertNotNull(model);
//		println("Model loaded : " + model.getEntities().size() + " entities");
//	}

}
