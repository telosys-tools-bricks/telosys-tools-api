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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.telosys.tools.commons.FileUtil;

public class UsersFileDAO {
	
	//private static final String DATE_FORMAT = "dd/MM/YYYY";
//	private static final String DATE_FORMAT = "YYYY-MM-dd"; // ISO 8601 FORMAT
	
	private final File usersFile ;
	
	/**
	 * Constructor
	 * @param usersFileName
	 */
	public UsersFileDAO(String usersFileName) {
		super();
		this.usersFile = new File(usersFileName);
	}

	/**
	 * Constructor
	 * @param usersFile
	 */
	public UsersFileDAO(File usersFile) {
		super();
		this.usersFile = usersFile;
	}

	/**
	 * Returns the absolute path of the users file
	 * @return
	 */
	public final String getFileName() {
		return usersFile.getAbsolutePath() ;
	}
	
	private final File getUsersFile(boolean create) {
		
		// if file doesn't exist, then create it
		if( ! usersFile.exists() ) {
			if ( create ) {
				FileUtil.createParentFolderIfNecessary(usersFile);
				try {
					usersFile.createNewFile();
				} catch (IOException e) {
					throw new IllegalStateException("Cannot create users file", e);
				}
			}
			else {
				return null ;
			}
		}
		return usersFile ;
	}
	
	/**
	 * Loads all the users stored in the 'users file' 
	 * @return the users map (never null)
	 */
	public synchronized Map<String, User> loadAllUsers() {

		Map<String,User> usersMap = new HashMap<String, User>();
		
		File file = getUsersFile(false);
		if ( file == null ) {
			return usersMap ; // void Map
		}
		
		//Set<User> users = new TreeSet<User>();
		
		UserSerializer userSerializer = new UserSerializer();
        try {
    		BufferedReader br = new BufferedReader(new FileReader(file)) ;
            String line;
	        while ( (line = br.readLine()) != null ) {
	            //User user = this.convertStringToUser(line);
	            User user = userSerializer.deserialize(line);
	            if (user != null) {
	            	usersMap.put(user.getLogin(), user);
	            }
	        }
	        br.close();
	    } catch (IOException e) {
			throw new IllegalStateException("Cannot read Users file (IOException)", e);
	    }
	    return usersMap;
	}
	
	/**
	 * Saves all the given users in the 'users file'
	 * @param users
	 * @return the number users saved
	 */
	public synchronized int saveAllUsers(Map<String, User> users) {
		
		File file = getUsersFile(true);
		UserSerializer userSerializer = new UserSerializer();
		int n = 0 ;
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			// for each user defined in the Map
			for ( Map.Entry<String, User> entry : users.entrySet()  ) {
				//String content = convertUserToString(entry.getValue());
				String content = userSerializer.serialize(entry.getValue());
				bw.write(content);
				bw.write("\n");
				n++;
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			throw new IllegalStateException("Cannot write Users file (IOException)", e);
		}
		return n ;
	}
	
//	private User convertStringToUser(String line) {
//		if(line == null || "".equals(line.trim())) {
//			return null;
//		}
//		if(line.charAt(0) == '#') {
//			return null;
//		}
//		String[] splits = splitWithNullIfEmpty(line, ';');
//		User user = new User();
//		int pos = 0;
//		user.setLogin(splits[pos]);
//		pos++;
//		user.setEncryptedPassword(splits[pos]);
//		pos++;
//		user.setMail(splits[pos]);
//		pos++;
//		user.setFirstName(splits[pos]);
//		pos++;
//		user.setLastName(splits[pos]);
//		pos++;
//		user.setAvatar(splits[pos]);
//		pos++;
//		user.setLastConnectionDate(convertStringToDate(splits[pos]));
//		return user;
//	}
//	
//	private String convertUserToString(User user) {
//		StringBuffer buf = new StringBuffer();
//		append(buf, user.getLogin());
//		buf.append(";");
//		append(buf, user.getEncryptedPassword());
//		buf.append(";");
//		append(buf, user.getMail());
//		buf.append(";");
//		append(buf, user.getFirstName());
//		buf.append(";");
//		append(buf, user.getLastName());
//		buf.append(";");
//		append(buf, user.getAvatar());
//		buf.append(";");
//		append(buf, user.getLastConnectionDate());
//		buf.append(";");
//		return buf.toString();
//	}
//	
//	
//	private Date convertStringToDate(String dateAsString) {
//		if(dateAsString == null || "".equals(dateAsString.trim())) {
//			return null;
//		}
//		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//		try {
//			return sdf.parse(dateAsString);
//		} catch (ParseException e) {
//			return null;
//		}
//	}
//	
//	private String convertDateToString(Date date) {
//		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//		return sdf.format(date);
//	}
//	
//	private void append(StringBuffer buf, String str) {
//		if(str == null || "".equals(str.trim())) {
//			return;
//		}
//		buf.append(str.trim());
//	}
//
//	private void append(StringBuffer buf, Date date) {
//		if(date == null) {
//			return;
//		}
//		buf.append(convertDateToString(date));
//	}
//	
//	private String[] splitWithNullIfEmpty(String str, char separator) {
//		if ( str == null || "".equals(str.trim()) ) {
//			return new String[0];
//		}
//		List<String> splitteds = new ArrayList<String>();
//		int lastPos = 0;
//		int pos = -1;
//		while((pos = str.indexOf(separator, lastPos)) != -1) {
//			if(pos == lastPos) {
//				splitteds.add(null);
//			} else {
//				String substring = str.substring(lastPos, pos);
//				splitteds.add(substring);
//			}
//			lastPos = pos + 1;
//		}
//		return splitteds.toArray(new String[] {});
//	}	
}