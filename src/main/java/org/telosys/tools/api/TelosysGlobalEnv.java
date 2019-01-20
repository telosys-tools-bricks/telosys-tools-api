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
package org.telosys.tools.api;

import org.telosys.tools.commons.github.GitHubUser;

/**
 * Telosys global environement values
 *  
 * @author Laurent Guerin
 *
 */
public class TelosysGlobalEnv {

	private TelosysGlobalEnv() {
	}
	
	/**
	 * Set current GitHub user with its password
	 * @param user
	 * @param password
	 */
	public static final void setGitHubUser(String user, String password) {
		GitHubUser.set(user, password);
	}

	/**
	 * Returns the current GitHub user
	 * @return
	 */
	public static final String getGitHubUser() {
		return GitHubUser.getUser();
	}

	/**
	 * Clear current GitHub user (user/password couple is cleared)
	 */
	public static final void clearGitHubUser() {
		GitHubUser.clear();
	}
	

}
