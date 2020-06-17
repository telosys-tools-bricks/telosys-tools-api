package org.telosys.tools.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.commons.bundles.TargetDefinition;
import org.telosys.tools.commons.bundles.TargetsDefinitions;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.generator.task.ErrorReport;
import org.telosys.tools.generator.task.GenerationTaskResult;
import org.telosys.tools.generic.model.Model;

public class TelosysProjectTest {
	
	@Before
	public void setUp() throws Exception {
		//System.out.println("Before test");
	}

	@After
	public void tearDown() throws Exception {
		//System.out.println("After test");
	}

	private TelosysProject initProject() throws Exception {

		String projectFolderFullPath = TestsEnv.createProjectFolder("myproject");
		TelosysProject telosysProject = new TelosysProject(projectFolderFullPath);
		
		System.out.println("Init project...");
		String s = telosysProject.initProject();
		System.out.println(s);
		
		TestsEnv.copyDbModelFile(projectFolderFullPath, "bookstore.dbrep");
		TestsEnv.copyDslModelFiles(projectFolderFullPath, "employees");
		TestsEnv.copyTemplatesFiles(projectFolderFullPath, "basic-templates-TT210");
		
		return telosysProject ;
	}
	
	@Test
	public void testConfigLoading() throws Exception {
		
		TelosysProject telosysProject = initProject() ;
		
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		System.out.println("Config loaded.");
		System.out.println("Full path : " + telosysToolsCfg.getCfgFileAbsolutePath());
	}

	@Test
	public void testDbModelLoading() throws Exception {
		
		TelosysProject telosysProject = initProject() ;
		
		System.out.println("Loading dbmodel...");
//		Model model = telosysProject.loadDatabaseModel("bookstore.dbrep");
		Model model = telosysProject.loadModel("bookstore.dbrep");
		System.out.println("Model loaded.");
		System.out.println(model.getEntities().size() + " entities");
	}

	@Test
	public void testModelLoading() throws Exception {
		
		TelosysProject telosysProject = initProject() ;
		
		System.out.println("Loading model...");
//		Model model = telosysProject.loadDslModel("employees-model");
//		Model model = telosysProject.loadDslModel("employees");
		Model model = telosysProject.loadModel("employees.model");
		System.out.println("Model loaded.");
		System.out.println("Name = " + model.getName() + " ( " + model.getEntities().size() + " ) entities");
		
	}

	@Test
	public void testDatabaseModelLoadingAndGeneration() throws Exception {
		TelosysProject telosysProject = initProject() ;
		
		//testModelLoadingAndGeneration("employees-model", "basic-templates-TT210" );
		testModelLoadingAndGeneration(telosysProject, 
				"bookstore.dbrep", null,
				"basic-templates-TT210", null, false );
	}
	
	@Test
	public void testDslModelLoadingAndGeneration() throws Exception {
		TelosysProject telosysProject = initProject() ;
		
		List<TargetDefinition> selectedTargets = new LinkedList<TargetDefinition>();
//		TargetsDefinitions targetsDefinitions = telosysProject.loadTargetsDefinitions("basic-templates-TT210") ;
		TargetsDefinitions targetsDefinitions = telosysProject.getTargetDefinitions("basic-templates-TT210") ;
		for ( TargetDefinition td : targetsDefinitions.getTemplatesTargets() ) {
			if ( "python_bean.vm".equals( td.getTemplate() ) ) {
				selectedTargets.add(td);
			}
		}
		testModelLoadingAndGeneration(telosysProject, 
				"employees.model", null,
				"basic-templates-TT210", selectedTargets, false );

		testModelLoadingAndGeneration(telosysProject, 
				"employees.model", null,
				"basic-templates-TT210", null, false ); // All templates
	}
	
//	private GenerationTaskResult testModelLoadingAndGeneration(TelosysProject telosysProject, 
//			String modelName, String bundleName) throws Exception {
//		
//		System.out.println("Loading model...");
//		Model model = telosysProject.loadModel(modelName);
//		System.out.println("Model loaded.");
//		System.out.println("Name = " + model.getName() + " ( " + model.getEntities().size() + " ) entities");
//		
//		System.out.println("Launching code generation...");
//		GenerationTaskResult generationTaskResult = telosysProject.launchGeneration(model, bundleName );
//		System.out.println("End of code generation");
//		System.out.println( generationTaskResult.getNumberOfFilesGenerated() + " file(s) generated ");
//		System.out.println( generationTaskResult.getNumberOfResourcesCopied() + " resources(s) copied ");
//		return generationTaskResult ;
//	}

	private GenerationTaskResult testModelLoadingAndGeneration(TelosysProject telosysProject, 
			String modelName, List<String> selectedEntities,
			String bundleName, List<TargetDefinition> selectedTargets, boolean copyResources) throws Exception {
		
		System.out.println("Loading model...");
		Model model = telosysProject.loadModel(modelName);
		System.out.println("Model loaded.");
		System.out.println("Name = " + model.getName() + " ( " + model.getEntities().size() + " ) entities");
		
		System.out.println("Launching code generation...");
		GenerationTaskResult generationTaskResult = telosysProject.launchGeneration(
				model, selectedEntities, 
				bundleName, selectedTargets, copyResources );
		
		System.out.println("End of code generation");
		System.out.println( generationTaskResult.getNumberOfFilesGenerated() + " file(s) generated ");
		System.out.println( generationTaskResult.getNumberOfResourcesCopied() + " resources(s) copied ");
		
		List<ErrorReport> errors = generationTaskResult.getErrors();
		for ( ErrorReport error : errors ) {
			System.out.println( " . " + error.getErrorMessage() );
		}
		return generationTaskResult ;
	}
	
	@Test
	public void testCreateNewModel() throws Exception {
		
		TelosysProject telosysProject = initProject() ;
		
		System.out.println("getTelosysToolsCfg...");
		System.out.println("Create new dsl model 'foo' ...");
		File modelFile = telosysProject.createNewDslModel("foo");
		System.out.println("Model file created : " + modelFile.getAbsolutePath() );
		assertTrue(modelFile.exists());
		File modelFolder = DslModelUtil.getModelFolder(modelFile);
		System.out.println("Model folder created : " + modelFolder.getAbsolutePath() );
		assertTrue(modelFolder.exists());
	}

	@Test
	public void testGetModelFile() throws Exception {
		
		TelosysProject telosysProject = initProject() ;
		File modelFile ; 
		
		modelFile = telosysProject.getModelFile("employees");
		assertNotNull(modelFile);
		modelFile = telosysProject.getModelFile("employees.model");
		assertNotNull(modelFile);

		modelFile = telosysProject.getModelFile("bookstore.dbrep");
		assertNotNull(modelFile);
		modelFile = telosysProject.getModelFile("bookstore");
		assertNotNull(modelFile);
		
		modelFile = telosysProject.getModelFile("foo.dbrep");
		assertNull(modelFile);
		modelFile = telosysProject.getModelFile("foo");
		assertNull(modelFile);
		
		telosysProject.createNewDslModel("bookstore"); // Same name as "bookstore.dbrep"
		modelFile = telosysProject.getModelFile("bookstore.dbrep");
		assertNotNull(modelFile);
		modelFile = telosysProject.getModelFile("bookstore.model");
		assertNotNull(modelFile);
		
		try {
			modelFile = telosysProject.getModelFile("bookstore");
			fail("Exception expected in this case");
		} catch (Exception e) {
			// OK
			System.out.println("Expected exception thrown : " + e.getMessage());
		}
		
	}
	
	@Test
	public void testCreateNewDslEntityWithModelFile() throws Exception {
		TelosysProject telosysProject = initProject() ;
		
		telosysProject.createNewDslModel("newmodel1"); 
		File modelFile = telosysProject.getModelFile("newmodel1.model");
		assertNotNull(modelFile);
		
		System.out.println("Creating entity 'Car'");
		telosysProject.createNewDslEntity(modelFile, "Car");
		File entityFile = telosysProject.buildDslEntityFile("newmodel1", "Car");
		assertNotNull(entityFile);
		assertTrue(entityFile.exists() ); // Must exists
		
		entityFile = telosysProject.buildDslEntityFile("newmodel1", "Driver");
		assertNotNull(entityFile);
		assertFalse(entityFile.exists() ); // Must not exists
		
	}
	
	@Test
	public void testCreateNewDslEntityWithModelName() throws Exception {
		TelosysProject telosysProject = initProject() ;
		
		String modelName = "newmodel1";
		telosysProject.createNewDslModel(modelName); 
		
		System.out.println("Creating entity 'Car'");
		telosysProject.createNewDslEntity(modelName, "Car");
		File entityFile = telosysProject.buildDslEntityFile(modelName, "Car");
		assertNotNull(entityFile); // Must exists
		assertTrue(entityFile.exists() );
		
		entityFile = telosysProject.buildDslEntityFile(modelName, "Driver");
		assertNotNull(entityFile);
		assertFalse(entityFile.exists() ); // Must not exists
		
	}
	
}
