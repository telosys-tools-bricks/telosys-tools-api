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
package org.telosys.tools.stats.impl;

import java.io.File;
import java.util.List;

import org.telosys.tools.stats.BundleStats;
import org.telosys.tools.stats.GlobalStatsOverview;
import org.telosys.tools.stats.ModelStats;
import org.telosys.tools.stats.ProjectStats;
import org.telosys.tools.stats.StatsProvider;
import org.telosys.tools.stats.UserStats;

public class StatsProviderImpl implements StatsProvider {

	private final File root ;
	
	public StatsProviderImpl(File root) {
		super();
		this.root = root;
	}
	
	@Override
	public File getRoot() {
		return root ;
	}
	
	@Override
	public GlobalStatsOverview getFilesystemStatsOverview() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserStats getUserStats(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProjectStats getProjectStats(String userId, String projectName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModelStats getModelStats(String userId, String projectName, String modelName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BundleStats getBundleStats(String userId, String projectName, String bundleName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public List<ProjectStats> getProjectsStats(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ModelStats> getModelsStats(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BundleStats> getBundlesStats(String userId) {
		// TODO Auto-generated method stub
		return null;
	}


}
