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
	
	private final static File getUsersFile() {
		return new File(USERS_FILE);
	}
	
	//@BeforeClass
	public static void init() {
		System.out.println("--- Init ");
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
	private static User createUser(int i) {
		User user = new User() ;
		user.setFirstName("John"+i);
		user.setLastName("Wayne"+i);
		user.setLogin("johnWayne"+i);
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
		File usersFile = getUsersFile(); 
		System.out.println("getUsersManagerForTests() : " + usersFile.getAbsolutePath() );
		UsersFileName.setSpecificFileName(usersFile.getAbsolutePath()) ;
		return UsersManager.getInstance() ;
	}

	@Test
	public void globalTest() {
		
		init();
		
		// force the SINGLETON to use the new file for this test case
		getUsersManagerForTests().loadAllUsers();
		
		test1GetExistingUser();
		
		try {
			test2UpdateUserNoLogin();
			fail("Exception expected");
		} catch (Exception e) {	}
		
		try {
			test3UpdateUserVoidPassword();
			fail("Exception expected");
		} catch (Exception e) {	}
		
		try {
			test4UpdateUserNullPassword();
			fail("Exception expected");
		} catch (Exception e) {	}
		
		test5UpdateUser();
		test6AddUser();
		test7CheckPassword();
	}
	
	//@Test
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
		User user = new User() ;
		UsersManager usersManager = getUsersManagerForTests(); 
		usersManager.saveUser(user, "abcd"); 
	}

	//@Test(expected=IllegalArgumentException.class)
	public void test3UpdateUserVoidPassword() {
		System.out.println("--- test3UpdateUserVoidPassword");
		
		User user = new User() ;
		user.setLogin("johnWayne1");
		user.setFirstName("John updated");
		user.setLastName("Wayne updated");

		// No password 
		UsersManager usersManager = getUsersManagerForTests(); 
		usersManager.saveUser(user, ""); 
	}

	//@Test(expected=IllegalArgumentException.class)
	public void test4UpdateUserNullPassword() {
		System.out.println("--- test4UpdateUserNullPassword");
		
		User user = new User() ;
		user.setLogin("johnWayne1");
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
		
		User user = new User() ;
		user.setLogin("johnWayne1");
		user.setFirstName("John updated");
		user.setLastName("Wayne updated");
		usersManager.saveUser(user, "secret"); 
		
	}

	//@Test
	public void test6AddUser() {
		System.out.println("--- test6AddUser");
		
		UsersManager usersManager = getUsersManagerForTests(); 
		
		User user = new User() ;
		user.setLogin("foo");
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


		User user = new User() ;
		user.setLogin("foo");
		user.setFirstName("aaaa");
		user.setLastName("bbbbb");
		usersManager.saveUser(user, "secret");
		
		assertTrue( usersManager.checkPassword("foo", "secret"));
		assertFalse( usersManager.checkPassword("foo", "azerty"));

		assertTrue( usersManager.checkPassword(user, "secret"));
		assertFalse( usersManager.checkPassword(user, "azerty"));
	}
}
