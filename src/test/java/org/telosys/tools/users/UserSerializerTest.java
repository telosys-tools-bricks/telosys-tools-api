package org.telosys.tools.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class UserSerializerTest {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; // ISO 8601 FORMAT
	
	private void checkIdentical(Date date1, Date date2) {
		if ( date1 != null ) {
			assertNotNull(date2);
		}
		if ( date2 != null ) {
			assertNotNull(date1);
		}
		if ( date1 != null && date2 != null ) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			assertEquals( sdf.format(date1), sdf.format(date2) );
		}
	}
	
	private void checkIdentical(String s1, String s2) {
		if ( s1 == null ) {
			assertEquals("", s2) ;
		}
		else {
			assertEquals(s1, s2) ;
		}
	}
	
	private void checkIdentical(User user1, User user2) {
		assertEquals(user1.getType(), user2.getType());
		
//		checkIdentical(user1.getLogin(), user2.getLogin() );
		assertEquals(user1.getLogin(), user2.getLogin() );

		checkIdentical(user1.getEncryptedPassword(), user2.getEncryptedPassword() );
		checkIdentical(user1.getFirstName(), user2.getFirstName() );
		checkIdentical(user1.getLastName(), user2.getLastName() );
		checkIdentical(user1.getMail(), user2.getMail() );
		checkIdentical(user1.getAvatar(), user2.getAvatar() );
		
		checkIdentical(user1.getLastConnectionDate(), user2.getLastConnectionDate() );
		
		checkIdentical(user1.getCountry(), user2.getCountry() );
		checkIdentical(user1.getLanguage(), user2.getLanguage() );
		checkIdentical(user1.getCreationDate(), user2.getCreationDate() );
	}

	private String serialize(User user) {
		System.out.println("----------" );
		System.out.println("User 1 : " + user);
		
		UserSerializer us = new UserSerializer();
		String s = us.serialize(user);
		System.out.println(" -> '" + s + "'");
		return s ;
	}
	
	private void serializeDeserializeAndCheck(User user) {
//		System.out.println("----------" );
//		System.out.println("User 1 : " + user);
//		
//		UserSerializer us = new UserSerializer();
//		String s = us.serialize(user);
//		System.out.println(" -> '" + s + "'");
//		
		String s = serialize(user);

		UserSerializer us = new UserSerializer();
		User user2 = us.deserialize(s);
		System.out.println("User 2 : " + user2);
		checkIdentical(user, user2);
	}
	
	@Test
	public void test1() {
		User user = new User(UserType.TELOSYS_USER, "foo");
		serializeDeserializeAndCheck(user);
	}
	
	@Test
	public void test2() {
		// Test with ";" inside 
		User user = new User(UserType.TELOSYS_USER, "fo;o");
		user.setEncryptedPassword("xyzxyzxyzxyzxyz");
		user.setFirstName("Bart;");
		user.setLastName("Sim;pson");
		user.setLastConnectionDate(new Date());
		user.setMail("bart@simpson;org");
		user.setAvatar("my avatar");
		
		String s = serialize(user);
		UserSerializer us = new UserSerializer();
		User user2 = us.deserialize(s);
		System.out.println("User 2 : " + user2);

		assertEquals(user2.getLogin(), "fo?o");
		assertEquals(user2.getFirstName(), "Bart?");
		assertEquals(user2.getLastName(), "Sim?pson");
	}

	@Test
	public void test4() {
		User user = new User(UserType.TELOSYS_USER, "foo");
		
		user.setEncryptedPassword("xyzxyzxyzxyzxyz");
		user.setFirstName("Bart");
		user.setLastName("Simpson");
		user.setLastConnectionDate(new Date());
		user.setMail("bart@simpson.org");
		user.setAvatar("my avatar");
		
		user.setCountry("FR");
		user.setLanguage("fr");
		user.setCreationDate(new Date());

		serializeDeserializeAndCheck(user);
	}

	@Test
	public void test5() {
		User user = new User(UserType.TELOSYS_USER, "foo");
		
		user.setEncryptedPassword("   ");
		user.setFirstName(" ");
		//user.setLastName(""); 
		user.setLastConnectionDate(new Date());
		user.setMail(null);
		user.setAvatar("  ");
		
		user.setCountry("");
		user.setLanguage(null);
		user.setCreationDate(new Date());

		serializeDeserializeAndCheck(user);
	}
}
