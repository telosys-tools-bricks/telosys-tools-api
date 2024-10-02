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

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.github.GitHubToken;

/**
 * Telosys API for 'global actions' (actions independent of the project, no current project required) 
 * 
 * @author Laurent Guerin
 *
 */
public class TelosysGlobal {

	/**
	 * Private constructor
	 */
	private TelosysGlobal() {}
	
	/**
	 * Returns true if a token is defined for the current user
	 * @return
	 * @throws TelosysToolsException
	 */
	public static final boolean isGitHubTokenSet() throws TelosysToolsException {
		return GitHubToken.isSet();
	}

	/**
	 * Set (define or replace) a token for the current user
	 * @param token
	 * @throws TelosysToolsException
	 */
	public static final void setGitHubToken(String token) throws TelosysToolsException {
		GitHubToken.set(token);
	}
	
	/**
	 * Remove the current user token if any
	 * @throws TelosysToolsException
	 */
	public static final void removeGitHubToken() throws TelosysToolsException {
		GitHubToken.clear();
	}
}
