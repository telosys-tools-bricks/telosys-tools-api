/**
 *  Copyright (C) 2008-2017  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.users;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.telosys.tools.commons.StrUtil;

public class UserSerializer {
	
	private static final char SEPARATOR = ';' ; 
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; // ISO 8601 FORMAT
	
	/**
	 * Serializes the given User in a String <br>
	 * Each ';' character is converted to '?' <br>
	 * Each 'null' string is converted to a 'void' string<br>
	 * @param user
	 * @return
	 */
	public String serialize(User user) {
		StringBuffer buf = new StringBuffer();
		
		append(buf, user.getLogin());
		append(buf, user.getEncryptedPassword());
		
		append(buf, user.getMail());
		append(buf, user.getFirstName());
		append(buf, user.getLastName());
		append(buf, user.getAvatar());

		append(buf, user.getCountry());
		append(buf, user.getLanguage());
		
		append(buf, user.getCreationDate());
		append(buf, user.getLastConnectionDate());
		return buf.toString();
	}
	
	/**
	 * Deserializes the given String in a User object
	 * @param line
	 * @return
	 */
	public User deserialize(String line) {
		if(line == null || "".equals(line.trim())) {
			return null;
		}

		//String[] splits = splitWithNullIfEmpty(line, ';');
		String[] splits = StrUtil.split(line, ';');
		if ( splits.length < 10 ) {
			throw new RuntimeException("invalid user record " + splits.length + " fields (10 expected)");
		}
		
		String login = splits[0];
		User user = new User(UserType.TELOSYS_USER, login);
		
		int pos = 1; // 1 = 2nd position
		//user.setLogin(splits[pos++]);
		user.setEncryptedPassword(splits[pos++]);
		
		user.setMail(splits[pos++]);
		user.setFirstName(splits[pos++]);
		user.setLastName(splits[pos++]);
		user.setAvatar(splits[pos++]);

		user.setCountry(splits[pos++]);
		user.setLanguage(splits[pos++]);
		
		user.setCreationDate(convertStringToDate(splits[pos++]));
		user.setLastConnectionDate(convertStringToDate(splits[pos++]));
		return user;
	}
	
	private Date convertStringToDate(String dateAsString) {
		if(dateAsString == null || "".equals(dateAsString.trim())) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try {
			return sdf.parse(dateAsString);
		} catch (ParseException e) {
			return null;
		}
	}
	
	private String convertDateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(date);
	}
	
	private void append(StringBuffer buf, String str) {
		if (str != null ) {
			//buf.append(str.trim());
			//buf.append(str);
			//byte[] bytes = str.getBytes() ;
			char[] chars = str.toCharArray();
			for ( char b : chars ) {
				if ( b != ';' ) {
					buf.append(b) ;
				}
				else {
					buf.append('?');
				}
			}
		}
		buf.append(SEPARATOR);
	}

	private void append(StringBuffer buf, Date date) {
		if (date != null) {
			buf.append(convertDateToString(date));
		}
		buf.append(SEPARATOR);
	}
	
}