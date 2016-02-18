package org.telosys.tools.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UserTest {
	
	@Test
	public void testEquals1() {
		User user1 = new User();
		user1.setLogin("foo");
		
		User user2 = new User();
		user2.setLogin("foo");
		
		assertTrue(user1.equals(user2));
	}
	
	@Test
	public void testEquals2() {
		User user1 = new User();		
		User user2 = new User();
		assertTrue(user1.equals(user2));
	}

	@Test
	public void testEquals3() {
		User user1 = new User();
		user1.setLogin("");
		
		User user2 = new User();
		user2.setLogin("");
		
		assertTrue(user1.equals(user2));
	}
	
	@Test
	public void testEquals4() {
		User user1 = new User();
		user1.setLogin(null);
		
		User user2 = new User();
		user2.setLogin(null);
		
		assertTrue(user1.equals(user2));
	}
	
	@Test
	public void testNotEquals1() {
		User user1 = new User();
		user1.setLogin(null);
		
		User user2 = new User();
		user2.setLogin("foo");
		
		assertFalse(user1.equals(user2));
	}

	@Test
	public void testNotEquals2() {
		User user1 = new User();
		user1.setLogin("foo");
		
		User user2 = new User();
		user2.setLogin(null);
		
		assertFalse(user1.equals(user2));
	}

	@Test
	public void testNotEquals3() {
		User user1 = new User();
		user1.setLogin("foo");
		
		User user2 = new User();
		user2.setLogin("bar");
		
		assertFalse(user1.equals(user2));
	}

}
