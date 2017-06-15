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
package org.telosys.tools.launcher;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.telosys.tools.api.TelosysProject;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.PropertiesManager;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.task.GenerationTaskResult;
import org.telosys.tools.generic.model.Model;

public class TelosysLauncher {
	
	private final static String LAUNCHERS = "launchers" ; // "launchers" folder in "TelosysTools"
	private final static String MODEL  = "model" ; // property name
	private final static String BUNDLE = "bundle" ; // property name
	
	private final String projectDir ;
	private final String launcherName ;
	private final String launchersDir ;
	
	private final TelosysProject  telosysProject ;
	private final TelosysToolsCfg telosysToolsCfg ;

	private final String modelName ;   // for example "cars.model"
	private final String bundleName ;  // for example "basic-templates-samples-T300"
	private final List<String> entities ;
	
	/**
	 * Constructor : creates and initializes a launcher
	 * @param projectDir
	 * @param launcherName
	 * @throws RuntimeException if the launcher cannot be initialized
	 */
	public TelosysLauncher(String projectDir, String launcherName) {
		super();
		this.projectDir = projectDir;
		this.launcherName = launcherName;
		this.telosysProject = new TelosysProject(projectDir);
		
		// Try to load the Telosys project configuration
		try {
			this.telosysToolsCfg = telosysProject.loadTelosysToolsCfg();
		} catch (TelosysToolsException e) {
			throw new RuntimeException("Cannot load Telosys configuration", e);
		}
		
		this.launchersDir = buildLaunchersDir(this.projectDir);
		
		// Try to load the launcher properties
		Properties properties = loadLauncherProperties(this.launchersDir);
		this.modelName   = (String) properties.get(MODEL);
		this.bundleName  = (String) properties.get(BUNDLE);

		// Try to load the launcher entities
		this.entities = loadLauncherEntities(this.launchersDir);
		
	}

	public String getLauncherName() {
		return launcherName;
	}

	public String getLaunchersDir() {
		return launchersDir;
	}

	public String getModelName() {
		return modelName;
	}

	public String getBundleName() {
		return bundleName;
	}

	public TelosysToolsCfg getTelosysToolsCfg() {
		return telosysToolsCfg;
	}

	public List<String> getEntities() {
		return entities;
	}

	private final String buildLaunchersDir(String projectDir) {
		// Build a path like "{project-directory}/TelosysTools/launchers"
		String launchersDir = FileUtil.buildFilePath(telosysToolsCfg.getTelosysToolsFolderAbsolutePath(), LAUNCHERS );
		File dir = new File(launchersDir) ;
		if ( ! dir.exists() ) {
			throw new RuntimeException("'" + dir + "' doesn't exist");
		}
		if ( ! dir.isDirectory() ) {
			throw new RuntimeException("'" + dir + "' is not a directory");
		}
		return launchersDir ;
	}
	
	private final Properties loadLauncherProperties(String launchersDir) {
		
		String shortFileName = launcherName + ".properties" ;
		String fileName = FileUtil.buildFilePath(launchersDir, shortFileName ) ;
		
		File file = new File(fileName);
		if ( ! file.exists() ) {
			throw new RuntimeException("'" + fileName + "' not found");
		}
		
		PropertiesManager pm = new PropertiesManager(file);
		Properties properties = pm.load();
		checkProperty(properties, MODEL, shortFileName);
		checkProperty(properties, BUNDLE, shortFileName);
		return properties;
	}
	
	private final void checkProperty(Properties properties, String name, String fileName) {
		if ( properties.get(name) == null ) {
			throw new RuntimeException("'" + name + "' undefined in '" + fileName + "'");
		}
	}

	/**
	 * Loads the list of entities to be generated, a void list is returned if no ".entities" file
	 * @param launchersDir
	 * @return
	 */
	private final List<String> loadLauncherEntities(String launchersDir) {
		
		String shortFileName = launcherName + ".entities" ;
		String fileName = FileUtil.buildFilePath(launchersDir, shortFileName ) ;
		
		File file = new File(fileName);
		if ( ! file.exists() ) {
			// No ".entities" file => use all entities
			return new LinkedList<String>();
		}
		else {
			EntitiesFileReader reader = new EntitiesFileReader(fileName);
			List<String> entities = reader.loadLines();
			return entities;
		}
	}
	
	public GenerationTaskResult launchGeneration() throws TelosysToolsException, GeneratorException {
				
		// Try to load the model
		Model model = telosysProject.loadModel(modelName);
		
		// Launch the generation
		GenerationTaskResult result ;
		if ( entities.size() > 0 ) {
			// Launch the generation for the selected entities with the given bundle name
			result = telosysProject.launchGeneration(model, entities, bundleName);
		}
		else {
			// Launch the generation for all entities with the given bundle name
			result = telosysProject.launchGeneration(model, bundleName);
		}
		return result;
	}

}
