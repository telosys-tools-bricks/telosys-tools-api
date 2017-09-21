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

import java.util.Map;

import org.telosys.tools.users.crypto.PasswordEncoder;

public class UsersManager {
	
	private static String USERS_FILE_NAME = null ;

	/**
	 * SINGLETON INSTANCE
	 */
	private static final UsersManager singleInstance = new UsersManager();
	
	public final static void setUsersFileName(String fileName) {
		USERS_FILE_NAME = fileName ;
	}
	public final static String getUsersFileName() {
		return USERS_FILE_NAME ;
	}
	
	public static UsersManager getInstance() {
		return singleInstance;
	}
	
	/**
	 * USERS MAP (indexed by login)
	 */
	private       Map<String, User> usersByLogin = null ;
	
	/**
	 * Private constructor 
	 */
	private UsersManager() {
	}
	
	private UsersFileDAO getUsersFileDAO() {
		if ( USERS_FILE_NAME != null ) {
			return new UsersFileDAO( USERS_FILE_NAME );
		}
		else {
			throw new IllegalStateException("Users file name has not been initialized ( setUsersFileName() must be called before )");
		}
	}

	private Map<String, User> getUsersMap() {
		if ( usersByLogin == null ) {
			loadAllUsers();
		}
		return usersByLogin ;
	}
	
	/**
	 * Returns the user having the given login, or null if none <br>
	 * The user is searched only in memory (the users file is not reloaded)
	 * @param login
	 * @return
	 */
	public User getUserByLogin(String login) {
		return getUsersMap().get(login);
	}

	/**
	 * Checks the password for the user identified by the given login<br>
	 * Returns true if OK <br>
	 * @param login the user's login
	 * @param password the password to be checked
	 * @return
	 */
	public boolean checkPassword(String login, String password) {
		if ( login == null || password == null ) {
			return false ;
		}
		User user = getUserByLogin(login);
		return checkPassword(user, password);
	}

	/**
	 * Checks the password for the given User (returns true if OK)
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean checkPassword(User user, String password) {
		if ( user == null || password == null ) {
			return false ;
		}
		// encrypt the given password
		PasswordEncoder passwordEncoder = new PasswordEncoder();
		return passwordEncoder.verify(password, user.getEncryptedPassword() ) ;
	}

	/**
	 * Returns the number of users currently stored in memory
	 * @return
	 */
	public int getUsersCount() {
		return getUsersMap().size();
	}

	/**
	 * Loads all users in the map 
	 */
	public synchronized void loadAllUsers() {
		usersByLogin = getUsersFileDAO().loadAllUsers();
	}
	
	/**
	 * Create or update the given user <br>
	 * ( updates the Map in memory, then all the users are written in the file )
	 * @param user the user ( the login cannot be null or void or blank )
	 * @param password the password ( cannot be null or void or blank )
	 */
	public synchronized void saveUser(User user, String password) {
		
		if(user.getLogin() == null || "".equals(user.getLogin().trim())) {
			throw new IllegalArgumentException("User login is null or void");
		}
		if(password == null || "".equals(password.trim())) {
			throw new IllegalArgumentException("User password is null or void");
		}

		// encrypt the given password
		PasswordEncoder passwordEncoder = new PasswordEncoder();
		user.setEncryptedPassword( passwordEncoder.encrypt(password) );

		// add or replace the user in memory
		getUsersMap().put(user.getLogin(), user);
		
		// persist the map in the users file
		persistUsers();
	}
	
	/**
	 * Create or update the given user without setting or changing his password <br>
	 * ( updates the Map in memory, then all the users are written in the file )
	 * @param user
	 */
	public synchronized void saveUser(User user) {
		
		if(user.getLogin() == null || "".equals(user.getLogin().trim())) {
			throw new IllegalArgumentException("User login is null or void");
		}

		// add or replace the user in memory
		getUsersMap().put(user.getLogin(), user);
		
		// persist the map in the users file
		persistUsers();
	}
	
	/**
	 * Delete the given user <br>
	 * ( removed from the Map in memory, then all the users are written in the file )
	 * @param user
	 * @return
	 */
	public synchronized boolean deleteUser(User user) {
		if ( user == null ) {
			throw new IllegalArgumentException("User is null");
		}
		return deleteUser(user.getLogin());
	}
	
	/**
	 * Delete the given user <br>
	 * ( removed from the Map in memory, then all the users are written in the file )
	 * @param login
	 * @return
	 */
	public synchronized boolean deleteUser(String login) {
		if ( login == null || "".equals( login.trim() ) ) {
			throw new IllegalArgumentException("User login is null or void");
		}
		// remove the user in memory
		User deletedUser = getUsersMap().remove(login);
		if ( deletedUser != null ) {
			// persist the map in the users file
			persistUsers();
			return true ;
		}
		else {
			return false ;
		}
	}
	
	private void persistUsers() {
		if ( usersByLogin != null ) {
			getUsersFileDAO().saveAllUsers(usersByLogin);
		}
		else {
			throw new IllegalStateException("User map is null");
		}
	}
}