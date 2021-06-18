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
package org.telosys.tools.api.webapp;

import org.telosys.tools.users.User;
import org.telosys.tools.users.UsersManager;
import org.telosys.tools.users.crypto.PasswordEncoder;


public class TelosysUsers {

	private TelosysUsers() {
	}
	
	/**
	 * Initializes the users file name. <br>
	 * Supposed to be called before any other method
	 */
	public static final void setUsersFileName(String fileName) {
		UsersManager.setUsersFileName(fileName);
	}
	
	/**
	 * Loads all users in memory
	 */
	public static final void loadAllUsers() {
		UsersManager.getInstance().loadAllUsers();
	}

	/**
	 * Returns the user having the given login, or null if none <br>
	 * The user is searched only in memory (the users file is not reloaded)
	 * @param login
	 * @return
	 */
	public static final User getUserByLogin(String login) {
		return UsersManager.getInstance().getUserByLogin(login);
	}

	/**
	 * Returns true if the given login/password pair is valid
	 * @param login
	 * @param password
	 * @return
	 */
	public static final boolean isValidLogin(String login, String password) {
		User user = getUserByLogin(login);
		if ( user != null ) {
			// encrypt the given password
			PasswordEncoder passwordEncoder = new PasswordEncoder();
			String encryptedPassword = passwordEncoder.encrypt(password) ;
			return encryptedPassword.equals( user.getEncryptedPassword() ) ;
		}
		return false ;
	}
	
	/**
	 * Returns the number of users currently stored in memory
	 * @return
	 */
	public static final int getUsersCount() {
		return UsersManager.getInstance().getUsersCount();
	}

	/**
	 * Create or update the given user <br>
	 * ( updates the Map in memory, then all the users are written in the file )
	 * @param user the user ( the login cannot be null or void or blank )
	 * @param password the password ( cannot be null or void or blank )
	 */
	public static final void saveUser(User user, String password) {
		UsersManager.getInstance().saveUser(user, password);
	}

	/**
	 * Create or update the given user without setting or changing his password <br>
	 * ( updates the Map in memory, then all the users are written in the file )
	 * @param user
	 */
	public static final void saveUser(User user) {
		UsersManager.getInstance().saveUser(user);
	}
	
	/**
	 * Delete the given user <br>
	 * ( removed from the Map in memory, then all the users are written in the file )
	 * @param user
	 * @return
	 */
	public static final boolean deleteUser(User user) {
		return UsersManager.getInstance().deleteUser(user);
	}

	/**
	 * Delete the given user <br>
	 * ( removed from the Map in memory, then all the users are written in the file )
	 * @param login
	 * @return
	 */
	public static final boolean deleteUser(String login) {
		return UsersManager.getInstance().deleteUser(login);
	}
	
}
