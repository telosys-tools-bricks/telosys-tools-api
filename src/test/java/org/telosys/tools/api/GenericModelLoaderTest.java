package org.telosys.tools.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Hashtable;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
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
		assertNull(genericModelLoader.getErrorMessage());
		assertNull(genericModelLoader.getParsingErrors());
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
	}

	@Test
	public void testModelLoadingDSL_invalid() throws Exception {
		System.out.println("========== Loading .model INVALID");
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
		Model model = genericModelLoader.loadModel("employees_invalid.model");
		assertNull(model);
		Hashtable<String,String> parsingErrors = genericModelLoader.getParsingErrors() ;
		assertNotNull(genericModelLoader.getErrorMessage());
		assertNotNull(parsingErrors);
		System.out.println("Error message : " + genericModelLoader.getErrorMessage() );
		System.out.println( parsingErrors.size() + " error(s)" );
		for ( Map.Entry<String,String> entry : parsingErrors.entrySet() ) {
			System.out.println( "'" + entry.getKey() + "' : " + entry.getValue() );
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
		assertNull(genericModelLoader.getErrorMessage());
		assertNull(genericModelLoader.getParsingErrors());
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
	}

}
