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

import java.io.File;
import java.util.List;

public interface StatsProvider {

	/**
	 * Returns the root folder for the current StatsProvider
	 * @return
	 */
	File getRoot();
	
	/**
	 * Returns the global stats overview
	 * @return
	 */
	GlobalStatsOverview getFilesystemStatsOverview() ;
	
	/**
	 * Returns the user stats for the given user
	 * 
	 * @param userId
	 * @return
	 */
	UserStats getUserStats(String userId) ;
	
	/**
	 * Returns the project stats for the given project name
	 * @param userId
	 * @param projectName
	 * @return
	 */
	ProjectStats getProjectStats(String userId, String projectName) ;
	
	/**
	 * Returns the model stats for the given model name
	 * @param userId
	 * @param projectName
	 * @param modelName
	 * @return
	 */
	ModelStats getModelStats(String userId, String projectName, String modelName) ;
	
	/**
	 * Returns the bundle stats for the given model name
	 * @param userId
	 * @param projectName
	 * @param bundleName
	 * @return
	 */
	BundleStats getBundleStats(String userId, String projectName, String bundleName) ;
	
	//------------------------------------------------------------------------------
	// 
	//------------------------------------------------------------------------------
	List<ProjectStats> getProjectsStats(String userId);
	
	List<ModelStats>   getModelsStats(String userId);
	
	List<BundleStats>  getBundlesStats(String userId);

	
}
