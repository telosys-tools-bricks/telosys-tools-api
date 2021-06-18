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

/**
 * Telosys API environment configuration 
 * 
 * @author Laurent GUERIN
 *
 */
public class TelosysAPI {

	private static String globalRootDirectory = null ;
	
	private static String usersFileName = null ;
	
	private TelosysAPI() {
	}
	
	/**
	 * Set the global root directory (absolute path) <br>
	 * e.g. '/foo/bar' <br>
	 * Supposed to be called before any other method <br>
	 */
	public static final void setGlobalRootDirectory(String directory) {
		globalRootDirectory = directory ;
	}
	
	/**
	 * Returns the global root directory (absolute path) <br>
	 * e.g. '/foo/bar' <br>
	 * Required for statistics management <br>
	 * Supposed to be called before any other method <br>
	 * @return
	 */
	public static final String getGlobalRootDirectory() {
		return globalRootDirectory ;
	}
	
	//------------------------------------------------------------------------

	/**
	 * Set the users file name (absolute path) <br>
	 * e.g. '/foo/bar/users.data' <br>
	 * Required for users management <br>
	 * Supposed to be called before any other method <br>
	 */
	public static final void setUsersFileName(String fileName) {
		usersFileName = fileName ;
	}
	
	/**
	 * Returns the users file name (absolute path) <br>
	 * e.g. '/foo/bar/users.data' <br>
	 * @return
	 */
	public static final String getUsersFileName() {
		return usersFileName ;
	}
}
