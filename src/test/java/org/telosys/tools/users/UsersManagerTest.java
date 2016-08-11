package org.telosys.tools.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsersManagerTest {
	
	private final static String USERS_FILE = "target/tests-tmp/users/usersFile2.data" ;
	
	private final File getUsersFile() {
		return new File(USERS_FILE);
	}
	
	//@BeforeClass
	public void init() {
		System.out.println("--- Init users file");
		Map<String,User> usersMap = new HashMap<String, User>();
		for ( int i = 0 ; i < 5 ; i++ ) {
			User user = createUser(i);
			usersMap.put( user.getLogin(), user) ;
		}
		System.out.println(usersMap.size() + " user(s) in Map");
		
		UsersFileDAO dao = new UsersFileDAO( getUsersFile() );
		int n = dao.saveAllUsers(usersMap);
		System.out.println(n + " user(s) saved");
		assertEquals(usersMap.size(), n);
		System.out.println("--- Init OK");

	}
	private User createUser(int i) {
		User user = new User(UserType.TELOSYS_USER, "johnWayne"+i) ;
		user.setFirstName("John"+i);
		user.setLastName("Wayne"+i);
		return user ;
	}

//	@Test
//	public void testLoadUsersNoFile() {
//		
//		UsersManager usersManager = UsersManager.getInstance() ;
//		System.out.println("Users count = " + usersManager.getUsersCount() );
//		assertEquals(0, usersManager.getUsersCount() );
//	}
	
	private UsersManager getUsersManagerForTests() {
		//UsersFileName.setSpecificFileName(USERS_FILE) ;
//		File usersFile = getUsersFile(); 
//		System.out.println("getUsersManagerForTests() : " + usersFile.getAbsolutePath() );
		//UsersFileName.setSpecificFileName(usersFile.getAbsolutePath()) ;
		return UsersManager.getInstance() ;
	}
	
	@Test
	public void globalTest() {
		
		System.out.println("========== TEST 0");
		test0NoFileName();
		
		System.out.println("========== INIT");
		init();

		String usersFile = getUsersFile().getAbsolutePath() ; 
		System.out.println("--- Init users file name ..." );
		System.out.println("setUsersFileName( '" + usersFile + "' ) " );
		UsersManager.setUsersFileName(usersFile);
		System.out.println("--- Init users file name OK" );

		// force the SINGLETON to use the new file for this test case
		getUsersManagerForTests().loadAllUsers();
		
		System.out.println("========== TEST 1");
		test1GetExistingUser();
		
		System.out.println("========== TEST 2");
		try {
			test2UpdateUserNoLogin();
			fail("Exception expected");
		} catch (Exception e) {	}
		
		System.out.println("========== TEST 3");
		try {
			test3UpdateUserVoidPassword();
			fail("Exception expected");
		} catch (Exception e) {	}
		
		System.out.println("========== TEST 4");
		try {
			test4UpdateUserNullPassword();
			fail("Exception expected");
		} catch (Exception e) {	}
		
		System.out.println("========== TEST 5");
		test5UpdateUser();
		System.out.println("========== TEST 6");
		test6AddUser();
		System.out.println("========== TEST 7");
		test7CheckPassword();
	}
	
	public void test0NoFileName() {
		System.out.println("Test without file name");
		// NB : File Name can be already set previously in a full test cycle ( i.e. : mvn install )
		System.out.println("Current file name = " + UsersManager.getUsersFileName()) ; 
		System.out.println("Set file name to null..." ) ; 
		UsersManager.setUsersFileName(null); // No file name
		System.out.println("Current file name = " + UsersManager.getUsersFileName()) ; 
		try {
			// UsersManager.getInstance().getUsersCount(); // Can be already loaded previously
			UsersManager.getInstance().loadAllUsers(); // Force fileName usage 
			fail("IllegalStateException expected");
		} catch (IllegalStateException e) {	
			System.out.println("OK : expected exception thrown");
		}
	}
	
	public void test1GetExistingUser() {
		System.out.println("--- test1GetExistingUser");
		UsersManager usersManager = getUsersManagerForTests(); 
		//usersManager.ge
		System.out.println("Users count = " + usersManager.getUsersCount() );
		assertEquals(5, usersManager.getUsersCount() );
		
		String login = "johnWayne1";
		User user = usersManager.getUserByLogin(login);
		
		assertNotNull(user);
		assertEquals(login, user.getLogin() );
	}

	//@Test(expected=IllegalArgumentException.class)
	public void test2UpdateUserNoLogin() {
		
		System.out.println("--- test2UpdateUserNoLogin");
		// No user login
		User user = new User(UserType.TELOSYS_USER, null) ;
		UsersManager usersManager = getUsersManagerForTests(); 
		usersManager.saveUser(user, "abcd-password"); 
	}

	//@Test(expected=IllegalArgumentException.class)
	public void test3UpdateUserVoidPassword() {
		System.out.println("--- test3UpdateUserVoidPassword");
		
		User user = new User(UserType.TELOSYS_USER, "johnWayne1") ;
		//user.setLogin("johnWayne1");
		user.setFirstName("John updated");
		user.setLastName("Wayne updated");

		// No password 
		UsersManager usersManager = getUsersManagerForTests(); 
		usersManager.saveUser(user, ""); 
	}

	//@Test(expected=IllegalArgumentException.class)
	public void test4UpdateUserNullPassword() {
		System.out.println("--- test4UpdateUserNullPassword");
		
		User user = new User(UserType.TELOSYS_USER, "johnWayne1") ;
		//user.setLogin("johnWayne1");
		user.setFirstName("John updated");
		user.setLastName("Wayne updated");

		// No password 
		UsersManager usersManager = getUsersManagerForTests(); 
		usersManager.saveUser(user, null); 
	}

	//@Test
	public void test5UpdateUser() {
		System.out.println("--- test5UpdateUser");
		
		UsersManager usersManager = getUsersManagerForTests(); 
		//usersManager.ge
		System.out.println("Users count = " + usersManager.getUsersCount() );
		assertEquals(5, usersManager.getUsersCount() );
		
		User user = new User(UserType.TELOSYS_USER, "johnWayne1") ;
		//user.setLogin("johnWayne1");
		user.setFirstName("John updated");
		user.setLastName("Wayne updated");
		usersManager.saveUser(user, "secret"); 
		
	}

	//@Test
	public void test6AddUser() {
		System.out.println("--- test6AddUser");
		
		UsersManager usersManager = getUsersManagerForTests(); 
		
		User user = new User(UserType.TELOSYS_USER, "foo") ;
		//user.setLogin("foo");
		user.setFirstName("aaaa");
		user.setLastName("bbbbb");
		usersManager.saveUser(user, "secret"); 
		
		int n = usersManager.getUsersCount() ;
		System.out.println("Users count after add = " + n);
		assertEquals(6, n );
		
		usersManager.loadAllUsers();
		n = usersManager.getUsersCount();
		System.out.println("Users count after reload = " + n);
		assertEquals(6, n );
	}

	public void test7CheckPassword() {
		System.out.println("--- test7CheckPassword");

		UsersManager usersManager = getUsersManagerForTests(); 
		assertFalse( usersManager.checkPassword((String)null, null) );
		assertFalse( usersManager.checkPassword((String)null, "bbb") );
		assertFalse( usersManager.checkPassword("", "") );
		assertFalse( usersManager.checkPassword("user1", null) );
		assertFalse( usersManager.checkPassword("user1", "") );
		
		assertFalse( usersManager.checkPassword((User)null, null) );
		assertFalse( usersManager.checkPassword((User)null, "bbb") );


		User user = new User(UserType.TELOSYS_USER, "foo") ;
		//user.setLogin("foo");
		user.setFirstName("aaaa");
		user.setLastName("bbbbb");
		usersManager.saveUser(user, "secret");
		
		assertTrue( usersManager.checkPassword("foo", "secret"));
		assertFalse( usersManager.checkPassword("foo", "azerty"));

		assertTrue( usersManager.checkPassword(user, "secret"));
		assertFalse( usersManager.checkPassword(user, "azerty"));
	}
}
