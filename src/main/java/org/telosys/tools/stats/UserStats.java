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

public interface UserStats {

	//------------------------------------------------------------------------------
	// From User object
	// see :
	// org.telosys.saas.domain.User
	// org.telosys.saas.security.user.UsersManager
	//------------------------------------------------------------------------------
	
	String getLogin();
	
	String getMail();
	
	Date getCreationDate(); // to be added in User
	
	String getCountry(); // to be added in User
	
	String getLanguage(); // to be added in User
	
	//------------------------------------------------------------------------------
	// From the filesystem
	//------------------------------------------------------------------------------
	/**
	 * The number of projects  <br>
	 * Origin : projects folders found in the filesystem
	 * @return
	 */
	int getProjectsCount() ;
	
	/**
	 * Returns all the projects names for the user
	 * @return
	 */
	List<String> getProjectsNames() ;
	
	/**
	 * The number of models <br>
	 * Origin : models files found in the filesystem
	 * @return
	 */
	int getModelsCount() ;
	
	/**
	 * The number of bundles <br>
	 * Origin : bundles folders found in the filesystem
	 * @return
	 */
	int getBundlesCount() ;
	
	/**
	 * Returns all the bundles names for the user
	 * @return
	 */
	List<String> getBundlesNames() ;
	
	/**
	 * The total disk usage <br>
	 * Origin : disk usage for all the user's files in the filesystem
	 * @return
	 */
	int getDiskUsage();
	
	//------------------------------------------------------------------------------
	// From ".counter" files
	//------------------------------------------------------------------------------
	/**
	 * The number of code generations launched for all the projects <br>
	 * Origin : generations counter files ( ".counter" files )
	 * @return
	 */
	int getGenerationsCount();
	
}
