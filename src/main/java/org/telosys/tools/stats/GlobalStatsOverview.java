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
package org.telosys.tools.stats;

/**
 * Global filesystem information for all users <br>
 * 
 * Information is based on the files and folders in the filesystem
 *
 */
public interface GlobalStatsOverview {

	/**
	 * Returns the total number of users folders
	 * @return
	 */
	public int getUsersCount() ;
	
	/**
	 * Returns the total number of projects folders for all the users
	 * @return
	 */
	public int getProjectsCount() ;
	
	/**
	 * Return the total number of ".model" files for all the users
	 * @return
	 */
	public int getModelsCount() ;
	
	/**
	 * Return the total disk usage for all the users
	 * @return
	 */
	public int getDiskUsage() ;
	
}
