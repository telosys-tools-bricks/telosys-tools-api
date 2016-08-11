package org.telosys.tools.users;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class UsersFileDAOTest {
	
	
	@Test
	public void testLoadUsersNoFile() {
		File file = new File("src/test/resources/users/usersFileInex.data");
		UsersFileDAO dao = new UsersFileDAO(file);
		
		Map<String,User> users = dao.loadAllUsers();
		assertEquals(0, users.size());
	}
	
	@Test
	public void testLoadUsers() {
		File file = new File("src/test/resources/users/users5.data");
		System.out.println("Loading users from file " + file.getPath());
		UsersFileDAO dao = new UsersFileDAO(file);
		
		Map<String,User> users = dao.loadAllUsers();
		System.out.println(users.size() + " user(s) loaded");
		assertEquals(5, users.size());
	}
	
	private User createUser(int i) {
		User user = new User(UserType.TELOSYS_USER, "johnWayne"+i) ;
		user.setFirstName("John"+i);
		user.setLastName("Wayne"+i);
		return user ;
	}
	
	@Test
	public void saveAllUsers() {
		
		Map<String,User> usersMap = new HashMap<String, User>();
		for ( int i = 0 ; i < 5 ; i++ ) {
			User user = createUser(i);
			usersMap.put( user.getLogin(), user) ;
		}
		System.out.println(usersMap.size() + " user(s) in Map");
		
		File file = new File("target/tests-tmp/users/usersFile1.data");
		UsersFileDAO dao = new UsersFileDAO(file);
		int n = dao.saveAllUsers(usersMap);
		System.out.println(n + " user(s) saved");
		assertEquals(usersMap.size(), n);
	}
	
}
