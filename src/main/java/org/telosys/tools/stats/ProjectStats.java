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

import java.util.Date;
import java.util.List;

public interface ProjectStats {

	//------------------------------------------------------------------------------
	// FILESYSTEM
	//------------------------------------------------------------------------------
	/**
	 * The project name <br>
	 * Origin : folder name in the filesystem
	 * @return
	 */
	String getProjectName() ;
	
	/**
	 * The number of bundles installed in the project <br>
	 * Origin : Bundles folders in the filesystem
	 * @return
	 */
	int getBundlesCount();
	
	/**
	 * List of bundles names in the project <br>
	 * @return
	 */
	List<String> getBundlesNames() ;

	/**
	 * The number of models defined in the project <br>
	 * Origin : Models files in the filesystem
	 * @return
	 */
	int getModelsCount();

	/**
	 * List of models names in the project <br>
	 * @return
	 */
	List<String> getModelsNames() ;

	/**
	 * The total disk usage for the project <br>
	 * Origin : all the project files in the filesystem
	 * @return
	 */
	int getDiskUsage();
	
	//------------------------------------------------------------------------------
	// DATES
	//------------------------------------------------------------------------------
	/**
	 * The creation date of the project 
	 * Origin : creation date file ( ".date" file)
	 * @return
	 */
	Date getCreationDate() ;
	
	/**
	 * The max date retrieved from all the ".date" files of the project
	 * @return
	 */
	Date getLastGenerationDate() ;
	
	//------------------------------------------------------------------------------
	// COUNTERS
	//------------------------------------------------------------------------------
	/**
	 * The number of code generations launched for the project <br>
	 * Origin : generations counter file ( ".counter" file )
	 * @return
	 */
	int getGenerationsCount();
	
}
