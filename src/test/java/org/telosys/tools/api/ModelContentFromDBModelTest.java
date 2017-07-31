package org.telosys.tools.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.Model;

public class ModelContentFromDBModelTest {
	
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
		
		return telosysProject ;
	}
	
	@Test
	public void testModelDBREP_bookstore() throws Exception {
		System.out.println("========== Loading .model ");
		TelosysProject telosysProject = initProject() ;
		System.out.println("getTelosysToolsCfg...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.getTelosysToolsCfg();
		
		GenericModelLoader genericModelLoader =  new GenericModelLoader(telosysToolsCfg);
		Model model = genericModelLoader.loadModel("bookstore.dbrep");
		assertNotNull(model);
//		assertNull(genericModelLoader.getErrorMessage());
//		assertNull(genericModelLoader.getParsingErrors());
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
		
		assertEquals(14, model.getEntities().size() );
		
		Entity e = model.getEntityByClassName("Book");
		assertNotNull(e);
		assertEquals("Book", e.getClassName());
		assertEquals("BOOK", e.getDatabaseTable());

		System.out.println("All links : ");
		List<Link> links = e.getLinks() ;
		assertNotNull(links);
		assertEquals(5, links.size() );
		for ( Link link : links ) {
			System.out.println(" . " + link );
			assertNotNull(link.getId());
			assertNotNull(link.getFieldName());
			assertNotNull(link.getFieldType());
			if ( link.getSourceTableName().equals("BOOK") && link.getTargetTableName().equals("PUBLISHER") ) {
				assertFalse(link.isSelected()); //Link not selected ( used="false" in XML file )
			}
			if ( link.getSourceTableName().equals("BOOK") && link.getTargetTableName().equals("AUTHOR") ) {
				assertTrue(link.isSelected()); //Link is selected ( used="true" in XML file )
			}
		}

		// TODO
//		System.out.println("Selected links : ");
//		List<Link> selectedLinks = e.getSelectedLinks() ; // TODO
//		assertNotNull(selectedLinks);
//		assertEquals(5, selectedLinks.size() );
//		for ( Link link : selectedLinks ) {
//			System.out.println(" . " + link );
//			assertNotNull(link.getId());
//			assertNotNull(link.getFieldName());
//			assertNotNull(link.getFieldType());
//			assertFalse(link.isSelected());
//		}

		List<Attribute> attributes = e.getAttributes();
		assertEquals(11, attributes.size() );
		for ( Attribute attribute : attributes ) {
			System.out.println(" . " + attribute );
			assertNotNull(attribute.getDatabaseName());
			assertNotNull(attribute.getName());
			assertNotNull(attribute.getNeutralType());
			if ( attribute.getName().equals("title") ) {
				checkAttributeBookTitle(attribute);
			}			
			if ( attribute.getName().equals("authorId") ) {
				checkAttributeBookAuthorId(attribute);
			}
			if ( attribute.getName().equals("publisherId") ) {
				checkAttributeBookPublisherId(attribute);
			}
			
		}		
		
	}

	public void checkAttributeBookTitle(Attribute attribute) throws Exception {
		System.out.println("Check 'title' attribute...");
		assertEquals("title", attribute.getName());
		assertEquals("TITLE", attribute.getDatabaseName());

		assertFalse( attribute.isKeyElement() );
		
		assertFalse( attribute.isFK() );
		assertFalse( attribute.isFKSimple() );
		assertFalse( attribute.isFKComposite() );
		assertNull(attribute.getReferencedEntityClassName());

		assertFalse( attribute.isUsedInLinks() );
		assertFalse( attribute.isUsedInSelectedLinks() );
	}
	
	public void checkAttributeBookAuthorId(Attribute attribute) throws Exception {
		System.out.println("Check 'authorId' attribute...");
		assertEquals("authorId", attribute.getName());
		assertEquals("AUTHOR_ID", attribute.getDatabaseName());
		
		assertFalse( attribute.isKeyElement() );

		assertTrue( attribute.isFK() );
		assertTrue( attribute.isFKSimple() );
		assertFalse( attribute.isFKComposite() );
		assertEquals("Author", attribute.getReferencedEntityClassName());

		assertTrue( attribute.isUsedInLinks() );
		assertTrue( attribute.isUsedInSelectedLinks() );
	}
	
	public void checkAttributeBookPublisherId(Attribute attribute) throws Exception {
		System.out.println("Check 'publisherId' attribute...");
		assertEquals("publisherId", attribute.getName());
		assertEquals("PUBLISHER_ID", attribute.getDatabaseName());
		
		assertFalse( attribute.isKeyElement() );

		assertTrue( attribute.isFK() );
		assertTrue( attribute.isFKSimple() );
		assertFalse( attribute.isFKComposite() );
		assertEquals("Publisher", attribute.getReferencedEntityClassName());

		assertTrue( attribute.isUsedInLinks() );
		assertFalse( attribute.isUsedInSelectedLinks() ); //Link not selected ( used="false" in XML file )
	}
	
}
