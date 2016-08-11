package org.telosys.tools.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserTest {
	
	@Test ( expected=IllegalArgumentException.class)
	public void testInvalidLogin1() {
		new User(UserType.TELOSYS_USER, null);
	}
	@Test ( expected=IllegalArgumentException.class)
	public void testInvalidLogin2() {
		new User(UserType.TELOSYS_USER, "");
	}
	@Test ( expected=IllegalArgumentException.class)
	public void testInvalidLogin3() {
		new User(UserType.TELOSYS_USER, " ");
	}
	@Test ( expected=IllegalArgumentException.class)
	public void testInvalidLogin4() {
		new User(UserType.TELOSYS_USER, "  ");
	}
	@Test ( expected=IllegalArgumentException.class)
	public void testInvalidLogin5() {
		new User(UserType.TELOSYS_USER, "\t");
	}
	@Test ( expected=IllegalArgumentException.class)
	public void testInvalidLogin6() {
		new User(UserType.TELOSYS_USER, " \n\r");
	}

//	@Test
//	public void testEquals1() {
//		User user1 = new User(UserType.TELOSYS_USER, "foo");
//		User user2 = new User(UserType.TELOSYS_USER, "foo");
//		assertTrue(user1.equals(user2));
//	}
//	
//	@Test
//	public void testNotEquals3() {
//		User user1 = new User(UserType.TELOSYS_USER, "foo");
//		User user2 = new User(UserType.TELOSYS_USER, "bar");
//		assertFalse(user1.equals(user2));
//	}

	@Test
	public void testUserType1() {
		UserType userType1 = UserType.TELOSYS_USER ;
		UserType userType2 = UserType.GITHUB_USER ;
		UserType userType3 = UserType.TELOSYS_USER ;
		
		System.out.println("userType1 = " + userType1 + "  value = " + userType1.getValue() );
		System.out.println("userType2 = " + userType2 + "  value = " + userType2.getValue() );
		
		assertEquals(UserType.TELOSYS_USER, userType1);
		assertEquals(0, userType1.getValue());
		
		assertEquals(UserType.GITHUB_USER, userType2);
		assertEquals(1, userType2.getValue());
		
		assertFalse( userType1 == userType2 ) ;
		assertTrue( userType1 == userType3 ) ;
		
	}

}
