package org.telosys.tools.api;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TelosysProjectDslModelTest {
	
	// JUnit RULE with JUnit TemporaryFolder
	// the TemporaryFolder is different fro each test and deleted after each test
	@Rule 
	public TemporaryFolder tmpFolder = new TemporaryFolder(); 

	private void print(String s) {
		System.out.println(s);
	}
	
	private String createProjectFolder() throws IOException {
		File createdFolder = tmpFolder.newFolder("myproject");
		print("createProjectFolder : " + createdFolder.getAbsolutePath() );
		assertTrue(createdFolder.exists());
		assertTrue(createdFolder.isDirectory());
		return createdFolder.getAbsolutePath();
	}
	
	private TelosysProject initProject() throws Exception {

		String projectFolderFullPath = createProjectFolder();
		TelosysProject telosysProject = new TelosysProject(projectFolderFullPath);
		
		print("Init project...");
		String s = telosysProject.initProject();
		print(s);
		
		TestsEnv.copyDbModelFile(projectFolderFullPath, "bookstore.dbrep");
		TestsEnv.copyDslModelFiles(projectFolderFullPath, "employees");
		TestsEnv.copyTemplatesFiles(projectFolderFullPath, "basic-templates-TT210");
		
		return telosysProject ;
	}
	
	private Model loadModel(String filePath) throws Exception {
		TelosysProject telosysProject = initProject();
		print("Loading model...");
		Model model = telosysProject.loadModel(filePath);
		print("Model loaded.");
		print("Name = " + model.getName() + " ( " + model.getEntities().size() + " ) entities");
		return model;
	}
	
	@Test
	public void testEmployeesModelLoading() throws Exception {
//		Model model = loadModel("employees.model");
		Model model = loadModel("employees");
		Entity entity = model.getEntityByClassName("Employee");
		assertEquals(3, entity.getAttributes().size() );
		assertEquals(1, entity.getLinks().size() );
		assertEquals(1, entity.getTagContainer().size() );
		assertTrue(entity.getTagContainer().containsTag("Foo") );
	}

}
