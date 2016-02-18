/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.users.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class PasswordEncoder {
	
	public String encrypt(String originalPassword) {
		checkString(originalPassword);
		return encryptPassword(originalPassword);
	}
	
	public boolean verify(String originalPassword, String encryptedPassword) {
		checkString(originalPassword);
		checkString(encryptedPassword);
		return encryptedPassword.equals( encryptPassword(originalPassword) ) ;
	}
	
	private void checkString(String s) {
		if ( s == null ) {
			throw new IllegalArgumentException("Invalid string (null)");
		}
		if ( s.length() == 0 ) {
			throw new IllegalArgumentException("Invalid string (void)");
		}
	}
	
	private static final String SALT1 = "%Naoned!44*";
	private static final String SALT2 = "tElOsYs";
	
	protected String encryptPassword(String password) {
		String passwordWithSalt = SALT1 + password + SALT2 ;
	    String sha1 = "";
	    try {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	        crypt.reset();
	        crypt.update(passwordWithSalt.getBytes("UTF-8"));
	        sha1 = byteToHex(crypt.digest());
	    }
	    catch(NoSuchAlgorithmException e) {
	    	throw new RuntimeException("Cannot encrypt (NoSuchAlgorithmException)", e);
	    }
	    catch(UnsupportedEncodingException e) {
	    	throw new RuntimeException("Cannot encrypt (UnsupportedEncodingException)", e);
	    }
	    return sha1;
	}
	
	protected String byteToHex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
}
