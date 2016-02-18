package org.telosys.tools.users.crypto;

import org.junit.Test;
import static org.junit.Assert.assertEquals ;

public class CryptoTest {

	@Test
	public void testByteToHex() {
		
		PasswordEncoder passwordEncoder = new PasswordEncoder();
		
		byte[] hash ;
		String s ;
		
		hash = new byte[] { (byte)1, (byte)'a', (byte)'z' };
		s = passwordEncoder.byteToHex(hash);
		System.out.println("s = " + s);
		assertEquals("01617a", s);
		
		hash = new byte[] { (byte)9, (byte)'A', (byte)'Z' };
		s = passwordEncoder.byteToHex(hash);
		System.out.println("s = " + s);
		assertEquals("09415a", s);		
	}

	@Test
	public void testEncrypt1() {
		
		String[] passwords = { "secret", "AzErT-67*/45!", "Aqw Zsx :;,?Z", "A" };
		for ( String pwd : passwords ) {
			PasswordEncoder pe1 = new PasswordEncoder();
			String encrypted1 = pe1.encrypt(pwd);
			
			PasswordEncoder pe2 = new PasswordEncoder();
			String encrypted2 = pe2.encrypt(pwd);
			
			System.out.println(pwd + " : '" + encrypted1 + "'  |  '" + encrypted2 + "'");
			assertEquals(encrypted1, encrypted2);
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEncryptNull() {
		PasswordEncoder pe = new PasswordEncoder();
		pe.encrypt(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEncryptVoid() {
		PasswordEncoder pe = new PasswordEncoder();
		pe.encrypt(null);
	}		
}
