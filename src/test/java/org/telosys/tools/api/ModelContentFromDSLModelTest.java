package org.telosys.tools.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.enums.Cardinality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ModelContentFromDSLModelTest {
	
	// JUnit RULE with JUnit TemporaryFolder
	// the TemporaryFolder is different fro each test and deleted after each test
	@Rule 
	public TemporaryFolder tmpFolder = new TemporaryFolder(); 

	private String createProjectFolder() throws IOException {
		File createdFolder = tmpFolder.newFolder("myproject");
		System.out.println("createProjectFolder : " + createdFolder.getAbsolutePath() );
		assertTrue(createdFolder.exists());
		assertTrue(createdFolder.isDirectory());
		return createdFolder.getAbsolutePath();
	}
		
	@Before
	public void setUp() throws Exception {
		// System.out.println("Before test");
	}

	@After
	public void tearDown() throws Exception {
		// System.out.println("After test");
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
		
		return telosysProject ;
	}
	
	@Test
	public void testModelDSL_Employees() throws Exception {
		System.out.println("========== Loading .model ");
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
//		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
//		Model model = genericModelLoader.loadModel("employees.model");
//		Model model = TestUtils.loadModelWithGenericModelLoader("employees.model", telosysToolsCfg);
//		Model model = TestUtils.loadModelWithGenericModelLoader("employees", telosysToolsCfg);
		Model model = TestUtils.loadModel(telosysProject, "employees");

		assertNotNull(model);
//		assertNull(genericModelLoader.getErrorMessage());
//		assertNull(genericModelLoader.getParsingErrors());
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
		
		assertEquals(2, model.getEntities().size() );
		
		Entity e = model.getEntityByClassName("Employee");
		assertNotNull(e);
		assertEquals("Employee", e.getClassName());
		assertEquals("", e.getDatabaseTable()); // no @DbTable(..)

		List<Link> links = e.getLinks() ;
		assertNotNull(links);
		assertEquals(1, links.size() );
		for ( Link link : links ) {
//			assertNotNull(link.getId());
			assertNotNull(link.getFieldName());
			assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality() );
		}
	}

	@Test
	public void testModelDSL_CarsAndDrivers() throws Exception {
		System.out.println("========== Loading .model ");
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
//		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
//		Model model = genericModelLoader.loadModel("cars-and-drivers.model");
//		Model model = TestUtils.loadModelWithGenericModelLoader("cars-and-drivers.model", telosysToolsCfg);
//		Model model = TestUtils.loadModelWithGenericModelLoader("cars-and-drivers", telosysToolsCfg);
		Model model = TestUtils.loadModel(telosysProject, "cars-and-drivers");
		
		System.out.println("Loading result :");
//		System.out.println(" - Error message = " + genericModelLoader.getErrorMessage() );
		assertNotNull(model);
//		assertNull(genericModelLoader.getErrorMessage());
//		assertNull(genericModelLoader.getParsingErrors());
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
		
		assertEquals(3, model.getEntities().size() );
		
		Entity carEntity = model.getEntityByClassName("Car");
		assertNotNull(carEntity);
		assertEquals("Car", carEntity.getClassName());
		assertEquals("CAR", carEntity.getDatabaseTable());

		List<Link> links = carEntity.getLinks() ;
		assertNotNull(links);
		assertEquals(2, links.size() );
		for ( Link link : links ) {
//			System.out.println("Link Id : " + link.getId() );
//			assertNotNull(link.getId());
			System.out.println("getFieldName() : " + link.getFieldName() );
			assertNotNull(link.getFieldName());
			System.out.println("getCardinality() : " + link.getCardinality() );
			assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality() );
		}
		
		for ( Attribute attribute : carEntity.getAttributes() ) {
			if ( attribute.getName().equals("name")) {
				checkAttributeCarName(attribute);
			}
			if ( attribute.getName().equals("driver")) {
				checkAttributeCarDriver(attribute);
			}
		}
	}
	public void checkAttributeCarName(Attribute attribute) throws Exception {
		System.out.println("Check 'name' attribute...");
		assertEquals("name", attribute.getName());

		assertFalse( attribute.isKeyElement() );
		
		assertFalse( attribute.isFK() );
		assertFalse( attribute.isFKSimple() );
		assertFalse( attribute.isFKComposite() );
		assertNull(attribute.getReferencedEntityClassName());

		assertFalse( attribute.isUsedInLinks() );
		assertFalse( attribute.isUsedInSelectedLinks() );
	}
	
	public void checkAttributeCarDriver(Attribute attribute) throws Exception {
		System.out.println("Check 'driver' attribute...");
		assertEquals("driver", attribute.getName());
		
		assertFalse( attribute.isKeyElement() );

		assertTrue( attribute.isFK() );
		assertTrue( attribute.isFKSimple() );
		assertFalse( attribute.isFKComposite() );
		assertEquals("Driver", attribute.getReferencedEntityClassName());

		assertTrue( attribute.isUsedInLinks() );
		assertTrue( attribute.isUsedInSelectedLinks() );
	}
}
