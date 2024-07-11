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

import org.telosys.tools.commons.VersionProvider;

/**
 * "commons" version provider
 *  
 * New: since ver 4.2.0 version and build id are provided via a properties file
 * updated at each Maven build using resources filtering
 * 
 * @author Laurent Guerin
 *
 */
public class TelosysApiVersion {

	/**
	 * Property name to get the VERSION from properties file
	 */
	private static final VersionProvider versionProvider = new VersionProvider("/telosys-api-build.properties") ;
	
	/**
	 * Private constructor
	 */
	private TelosysApiVersion() {}
	
	/**
	 * Returns the project name 
	 * @return
	 */
	public static final String getName() {
		return versionProvider.getName();
	}

	/**
	 * Returns the project version 
	 * @return
	 */
	public static final String getVersion() {
		return versionProvider.getVersion();
	}

	/**
	 * Returns the project build-id 
	 * @return
	 */
	public static final String getBuildId() {
		return versionProvider.getBuildId();
	}

	/**
	 * Returns the project version with build-id
	 * @return
	 */
	public static final String getVersionWithBuilId() {
		return versionProvider.getVersionWithBuilId();
	}

}
