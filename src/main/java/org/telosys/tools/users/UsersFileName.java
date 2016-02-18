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
package org.telosys.tools.users;

public class UsersFileName {
	
	/*
	 * The default users file name to be used in the web application
	 */
	private final static String DEFAULT_USERS_FILE_NAME = "fs/users.csv";

	private static String SPECIFIC_USERS_FILE_NAME = null ;

	public final static String getFileName() {
		if ( SPECIFIC_USERS_FILE_NAME != null ) {
			return SPECIFIC_USERS_FILE_NAME ;
		}
		return DEFAULT_USERS_FILE_NAME ;
	}

	public final static void setSpecificFileName(String fileName) {
		SPECIFIC_USERS_FILE_NAME = fileName ;
	}
	
}
