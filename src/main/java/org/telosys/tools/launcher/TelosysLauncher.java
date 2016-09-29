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
import java.util.Properties;

import org.telosys.tools.api.TelosysProject;
import org.telosys.tools.commons.DirUtil;
import org.telosys.tools.commons.PropertiesManager;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.task.GenerationTaskResult;
import org.telosys.tools.generic.model.Model;

public class TelosysLauncher {
	
	public static void main(String[] args) throws TelosysToolsException, GeneratorException {

		System.out.println("Telosys Code Generator Launcher" );
		System.out.println("Current working directory : " + DirUtil.getUserWorkingDirectory() );
		Properties properties = loadConfig(args);
		
		String projectPath = (String)properties.get("project-path");
		String modelName = (String) properties.get("model");
		String bundleName = (String) properties.get("bundle");
		
		System.out.println(" . project-path = " + projectPath );
		System.out.println(" . model        = " + modelName );
		System.out.println(" . bundle       = " + bundleName );
		
		System.out.println("Lauching generation..." );
		launch(projectPath, modelName, bundleName );
	}
	
	private static Properties loadConfig(String[] args)  {
		System.out.println(args.length + " argument(s) ");
		for( String arg : args ) {
			System.out.println(" . " + arg );
		}

		String launcherProperties = "launcher.properties" ;
		if ( args.length > 0 ) {
			launcherProperties = args[0];
		}
		System.out.println("Launcher properties file = " +launcherProperties );
		
		File file = new File(launcherProperties);
		if ( ! file.exists() ) {
			System.out.println("File = '" +launcherProperties + "' not found!");
			System.exit(1);
		}
		
		PropertiesManager pm = new PropertiesManager(file);
		Properties properties = pm.load();
		return properties;
	}
	
	private static void launch(String projectPath, String modelName, String bundleName ) throws TelosysToolsException, GeneratorException {
		
		TelosysProject telosysProject = new TelosysProject(projectPath);
		
		TelosysToolsCfg telosysToolsCfg = telosysProject.loadTelosysToolsCfg();

//		System.out.println("Cleaning destination folder...");
//		File destinationFolder = new File(telosysToolsCfg.getDestinationFolderAbsolutePath());
//		DirUtil.deleteDirectory( destinationFolder );
//		System.out.println("Cleaning done.");

		System.out.println("Telosys configuration :");
		System.out.println(" . models folder      : " + telosysToolsCfg.getModelsFolderAbsolutePath() );
		System.out.println(" . templates folder   : " + telosysToolsCfg.getTemplatesFolderAbsolutePath() );
		System.out.println(" . destination folder : " + telosysToolsCfg.getDestinationFolderAbsolutePath() );
		
		System.out.println("Loadind model '" + modelName + "' ...");
		Model model = telosysProject.loadModel(modelName);
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
		
		System.out.println("Launching generation for bundle '" + bundleName + "' ...");
		GenerationTaskResult result = telosysProject.launchGeneration(model, bundleName);
		System.out.println("Normal end of generation : " + result.getNumberOfFilesGenerated() + " files generated");
		
	}

}
