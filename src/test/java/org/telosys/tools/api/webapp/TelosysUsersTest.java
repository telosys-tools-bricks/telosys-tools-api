package org.telosys.tools.api.webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.telosys.tools.api.webapp.TelosysUsers;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.users.User;
import org.telosys.tools.users.UserType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // No parallel processing
public class TelosysUsersTest {
	
	private final static String USERS_FILE1 = "src/test/resources/users/users3.data" ; // read-only
	private final static String USERS_FILE2 = "target/tests-tmp/users/users3.data" ;
	
	@Before
	public void setUp() throws Exception {
		System.out.println("Before test : file copy");
		File file1 = new File(USERS_FILE1);
		System.out.println("File 1 : " + file1.getAbsolutePath() ) ;
		File file2 = new File(USERS_FILE2);
		System.out.println("File 2 : " + file2.getAbsolutePath() ) ;
		
		FileUtil.copy(file1, file2, true);
		TelosysUsers.setUsersFileName(USERS_FILE2);
		TelosysUsers.loadAllUsers();
	}

	@After
	public void tearDown() {
		System.out.println("After test");
		File file2 = new File(USERS_FILE2);
		file2.delete();
	}

	@Test // A single global test to avoid parallel processing due to file copy
	public void globalTest()  {
		System.out.println("----------");
		
		getUsersCountTest();
		getUserByLoginTest();
		
		updateUserTest();
		updateUserWithPasswordTest();
		
		deleteUserTest() ;
		deleteUserByLoginTest();
		
		createUserTest();
		authenticationTest();
		
		System.out.println("----------");
	}

	//@Test
	public void getUsersCountTest()  {
		System.out.println("--- getUsersCountTest");
		
		int n = TelosysUsers.getUsersCount() ;
		System.out.println("count = " + n);
		assertEquals(3, n);
	}

	//@Test
	public void getUserByLoginTest()  {
		System.out.println("--- getUserByLoginTest");
		
		User user = TelosysUsers.getUserByLogin("bwayne");
		System.out.println("user = " + user);
		assertEquals("bwayne", user.getLogin() );
		assertEquals("Bruce", user.getFirstName() );
		assertEquals("Wayne", user.getLastName() );
		assertEquals("", user.getMail() );
	}

	//@Test
	public void updateUserTest()  {
		System.out.println("--- updateUserTest");
		
		User user1 = TelosysUsers.getUserByLogin("bwayne");
		System.out.println("user1 = " + user1);
		assertNotNull(user1 );
		user1.setFirstName("Brice");
		user1.setLastName("De Nice");
		user1.setMail("bwayne@gmail.com");
		assertEquals("", user1.getEncryptedPassword() );
		
		// UPDATE without password
		TelosysUsers.saveUser(user1);
		
		TelosysUsers.loadAllUsers(); // FORCE RELOAD (just for test)
		User user2 = TelosysUsers.getUserByLogin("bwayne");
		System.out.println("user2 = " + user2);
		
		assertEquals("Brice", user2.getFirstName() );
		assertEquals("De Nice",  user2.getLastName() );
		assertEquals("bwayne@gmail.com",  user2.getMail());
		assertEquals("", user2.getEncryptedPassword() ); // Still void
	}

	public void createUserTest()  {
		System.out.println("--- createUserTest");
		
		User user = TelosysUsers.getUserByLogin("bspringsteen");
		System.out.println("user = " + user);
		assertNull(user );
		// Doesn't exist 
		
		User user1 = new User(UserType.TELOSYS_USER, "bspringsteen");
//		user1.setLogin("bspringsteen");
		user1.setFirstName("Bruce");
		user1.setLastName("Springsteen");
		user1.setMail("bruce.springsteen@gmail.com");
		user1.setCountry("US");
		user1.setLanguage("en");
		user1.setCreationDate(new Date());
		
		// UPDATE with a new password
		TelosysUsers.saveUser(user1, "theriver");
		System.out.println("user1 = " + user1);
		
		TelosysUsers.loadAllUsers(); // FORCE RELOAD (just for test)
		User user2 = TelosysUsers.getUserByLogin("bspringsteen");
		System.out.println("user2 = " + user2);
		
		assertEquals(user1.getFirstName(), user2.getFirstName() );
		assertEquals(user1.getLastName(),  user2.getLastName() );
		assertEquals(user1.getMail(),      user2.getMail() );
		assertEquals(user1.getCountry(),   user2.getCountry() );
		assertEquals(user1.getLanguage(),  user2.getLanguage() );
		assertNotNull( user2.getEncryptedPassword() ); 
	}

	public void authenticationTest()  {
		System.out.println("--- authenticationTest");
		assertFalse( TelosysUsers.isValidLogin("bspringsteen", "azerty") ) ;
		
		try {
			assertFalse( TelosysUsers.isValidLogin("bspringsteen", "") ) ;
			fail("Not supposed to reach this line");
		} catch (IllegalArgumentException e) {
			System.out.println("Expected exception ");
		}

		try {
			assertFalse( TelosysUsers.isValidLogin("bspringsteen", null) ) ;
			fail("Not supposed to reach this line");
		} catch (IllegalArgumentException e) {
			System.out.println("Expected exception ");
		}

		assertTrue( TelosysUsers.isValidLogin("bspringsteen", "theriver") ) ;
	}

	// @Test
	public void updateUserWithPasswordTest()  {
		System.out.println("--- updateUserWithPasswordTest");
		
		User user1 = TelosysUsers.getUserByLogin("bwayne");
		System.out.println("user1 = " + user1);
		assertNotNull(user1 );
		assertEquals("",user1.getEncryptedPassword() );
		user1.setMail("bwayne@gmail.com");
		
		// UPDATE with a new password
		TelosysUsers.saveUser(user1, "newPassword");
		
		TelosysUsers.loadAllUsers(); // FORCE RELOAD (just for test)
		User user2 = TelosysUsers.getUserByLogin("bwayne");
		System.out.println("user2 = " + user2);
		
		assertEquals(user1.getFirstName(), user2.getFirstName() );
		assertEquals(user1.getLastName(),  user2.getLastName() );
		assertEquals("bwayne@gmail.com",  user2.getMail());
		assertNotNull(user2.getEncryptedPassword() );
	}

	//@Test
	public void deleteUserTest()  {
		System.out.println("--- deleteUserTest");
		
		User user1 = TelosysUsers.getUserByLogin("bwayne");
		System.out.println("user1 = " + user1);
		assertNotNull(user1 );
		assertEquals(3, TelosysUsers.getUsersCount() );
		
		// UPDATE with a new password
		boolean deleted = TelosysUsers.deleteUser(user1);
		assertTrue(deleted);
		assertEquals(2, TelosysUsers.getUsersCount() );
		
		TelosysUsers.loadAllUsers(); // FORCE RELOAD (just for test)
		assertEquals(2, TelosysUsers.getUsersCount() );

		User user2 = TelosysUsers.getUserByLogin("bwayne");
		System.out.println("user2 = " + user2);
		assertNull(user2);
		
	}

	public void deleteUserByLoginTest()  {
		System.out.println("--- deleteUserByLoginTest");
		
		User user1 = TelosysUsers.getUserByLogin("bsimpson");
		System.out.println("user1 = " + user1);
		assertNotNull(user1 );
		assertEquals(2, TelosysUsers.getUsersCount() );
		
		// UPDATE with a new password
		boolean deleted = TelosysUsers.deleteUser("bsimpson");
		assertTrue(deleted);
		assertEquals(1, TelosysUsers.getUsersCount() );
		
		TelosysUsers.loadAllUsers(); // FORCE RELOAD (just for test)
		assertEquals(1, TelosysUsers.getUsersCount() );

		User user2 = TelosysUsers.getUserByLogin("bsimpson");
		System.out.println("user2 = " + user2);
		assertNull(user2);
		
	}

}
