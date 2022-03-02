package org.telosys.tools.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.generator.task.ErrorReport;
import org.telosys.tools.generator.task.GenerationTaskResult;
import org.telosys.tools.generic.model.Model;

public class GenerationTest {
	
	private final static String TEST_BUNDLE1 = "test-bundle1";
	
	// JUnit RULE with JUnit TemporaryFolder
	// the TemporaryFolder is different fro each test and deleted after each test
	@Rule 
	public TemporaryFolder tmpFolder = new TemporaryFolder(); 

	private String createProjectFolder() throws Exception {
		File createdFolder = tmpFolder.newFolder("myproject");
		System.out.println("createProjectFolder : " + createdFolder.getAbsolutePath() );
		assertTrue(createdFolder.exists());
		assertTrue(createdFolder.isDirectory());
		return createdFolder.getAbsolutePath();
	}
	
	
	@Before
	public void setUp() throws Exception {
		//System.out.println("Before test");
	}

	@After
	public void tearDown() throws Exception {
		//System.out.println("After test");
	}

	private TelosysProject initProject() throws Exception {

		//String projectFolderFullPath = TestsEnv.createProjectFolder("myproject");
		String projectFolderFullPath = createProjectFolder();
		TelosysProject telosysProject = new TelosysProject(projectFolderFullPath);
		
		System.out.println("Init project...");
		String s = telosysProject.initProject();
		System.out.println(s);
		
//		TestsEnv.copyDbModelFile(projectFolderFullPath, "bookstore.dbrep");
		TestsEnv.copyDslModelFiles(projectFolderFullPath, "employees");
		TestsEnv.copyDslModelFiles(projectFolderFullPath, "cars-and-drivers");
//		TestsEnv.copyDslModelFiles(projectFolderFullPath, "employees_invalid");
//		TestsEnv.copyTemplatesFiles(projectFolderFullPath, "basic-templates-TT210");
		TestsEnv.copyTemplatesFiles(projectFolderFullPath, TEST_BUNDLE1 );
		
		return telosysProject ;
	}
	
	@Test
	public void testModelDSL_CarsAndDrivers() throws Exception {
		System.out.println("========== Generation for model 'Cars and Drivers'");
		System.out.println(" --- Init Project ..." );
		TelosysProject telosysProject = initProject() ;
		System.out.println("Project folder : " + telosysProject.getProjectFolder() );
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
		System.out.println(" --- Load Model ..." );
//		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
//		Model model = genericModelLoader.loadModel("cars-and-drivers.model");
//		Model model = TestUtils.loadModelWithGenericModelLoader("cars-and-drivers.model", telosysToolsCfg);
//		Model model = TestUtils.loadModelWithGenericModelLoader("cars-and-drivers", telosysToolsCfg);
		Model model = TestUtils.loadModel(telosysProject, "cars-and-drivers");

		assertNotNull(model);
		//assertNull(genericModelLoader.getErrorMessage());
		System.out.println("Model loaded. Model name = " + model.getName() );

		String bundleName = TEST_BUNDLE1 ;
		System.out.println(" --- Launch code generation for bundle '" + bundleName  + "' ..." );
		GenerationTaskResult result = telosysProject.launchGeneration(model, bundleName);
		
		System.out.println("Error(s) count = " + result.getNumberOfGenerationErrors() ) ;
		if ( result.getNumberOfGenerationErrors() > 0 ) {
			int n = 0 ;
			List<ErrorReport> errors = result.getErrors();
			for ( ErrorReport error : errors ) {
				n++;
				System.out.println("ERROR #" + n);
				System.out.println(" . error message : " + error.getErrorMessage() );
				Throwable ex = error.getException() ;
				System.out.println(" . exception : " + ex.getMessage() );
				Throwable cause = ex;
				while ( ( cause = cause.getCause() ) != null ) {
					System.out.println(" . cause : " + cause.getMessage() );
				}
			}
		}
		assertEquals(0, result.getNumberOfGenerationErrors() );
		
//		assertNotNull(model);
//		assertEquals(3, model.getEntities().size() );
//		assertNull(genericModelLoader.getErrorMessage());
//		assertNotNull(e);
//		assertEquals("Car", e.getClassName());
//		assertEquals("Car", e.getDatabaseTable());

	}

}
