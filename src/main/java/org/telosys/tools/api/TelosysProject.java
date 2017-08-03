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
package org.telosys.tools.api;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.ConsoleLogger;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.bundles.BundleStatus;
import org.telosys.tools.commons.bundles.BundlesManager;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.commons.env.EnvironmentManager;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.target.TargetDefinition;
import org.telosys.tools.generator.target.TargetsDefinitions;
import org.telosys.tools.generator.target.TargetsLoader;
import org.telosys.tools.generator.task.GenerationTask;
import org.telosys.tools.generator.task.GenerationTaskResult;
import org.telosys.tools.generator.task.StandardGenerationTask;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.stats.services.CounterFileManager;

public class TelosysProject {

	private final String projectFolderAbsolutePath ;
	private final TelosysToolsLogger telosysToolsLogger ;
	private TelosysToolsCfg telosysToolsCfg = null ;
	
	/**
	 * Constructor
	 * @param projectFolderAbsolutePath
	 * @param logger
	 */
	public TelosysProject(String projectFolderAbsolutePath, TelosysToolsLogger logger) {
		super();
		this.projectFolderAbsolutePath = projectFolderAbsolutePath;
		this.telosysToolsLogger = logger ;
		this.telosysToolsCfg = null ;
	}

	/**
	 * Constructor
	 * @param projectFolderAbsolutePath
	 */
	public TelosysProject(String projectFolderAbsolutePath) {
		super();
		this.projectFolderAbsolutePath = projectFolderAbsolutePath;
		this.telosysToolsLogger = new ConsoleLogger() ;
		this.telosysToolsCfg = null ;
	}

	/**
	 * Returns the current project folder 
	 * @return
	 */
	public String getProjectFolder() {
		return projectFolderAbsolutePath;
	}

	//-----------------------------------------------------------------------------------------------------
	// Project initialization
	//-----------------------------------------------------------------------------------------------------
	public String initProject() {
		StringBuffer sb = new StringBuffer();
		sb.append("Project initialization \n");
		sb.append("Project folder : '" + projectFolderAbsolutePath + "' \n");
		sb.append("\n");
		// Init environment files
		initProject(sb);
		return sb.toString();		
	}
	
	public void initProject(StringBuffer sb) {
		EnvironmentManager em = new EnvironmentManager( projectFolderAbsolutePath );
		// Init environment files
		em.initEnvironment(sb);
	}
	
	//-----------------------------------------------------------------------------------------------------
	// Project configuration 
	//-----------------------------------------------------------------------------------------------------
	public TelosysToolsCfg loadTelosysToolsCfg() throws TelosysToolsException {
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager( projectFolderAbsolutePath );
		this.telosysToolsCfg = cfgManager.loadTelosysToolsCfg() ;
		return this.telosysToolsCfg;
	}
	
	public void saveTelosysToolsCfg(TelosysToolsCfg telosysToolsCfg) throws TelosysToolsException {
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager( projectFolderAbsolutePath );
		cfgManager.saveTelosysToolsCfg(telosysToolsCfg);
	}
	
	public TelosysToolsCfg getTelosysToolsCfg() throws TelosysToolsException {
		if ( this.telosysToolsCfg == null ) {
			loadTelosysToolsCfg();
		}
		return this.telosysToolsCfg;
	}

	//-----------------------------------------------------------------------------------------------------
	// Project bundles management 
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Returns a list of bundles available on the given user's name (on GitHub)
	 * @param userName the GitHub user name (e.g. "telosys-tools")
	 * @return
	 * @throws TelosysToolsException
	 */
	public List<String> getBundlesList(String userName) throws TelosysToolsException {
		BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
		try {
			return bm.getBundlesList(userName);
		} catch (Exception e) {
			throw new TelosysToolsException("Cannot get bundles list", e);
		}
	}

	/**
	 * Download and install a bundle (from GitHub repositories) 
	 * 
	 * @param userName the GitHub user name (e.g. "telosys-tools")
	 * @param bundleName the bundle name, in other words the GitHub repository name 
	 * @return
	 * @throws TelosysToolsException
	 */
	public BundleStatus downloadAndInstallBundle(String userName, String bundleName) throws TelosysToolsException {
		BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
		return bm.downloadAndInstallBundle(userName, bundleName);
	}
	
	/**
	 * Returns a list containig all the bundles installed for the current project
	 * @return
	 * @throws TelosysToolsException
	 */
	public List<String> getInstalledBundles() throws TelosysToolsException {
		TargetsLoader targetsLoader = new TargetsLoader(getTelosysToolsCfg()) ;
		return targetsLoader.loadBundlesList();
	}
	
//	//-----------------------------------------------------------------------------------------------------
//	// Database model loading ('dbrep') 
//	//-----------------------------------------------------------------------------------------------------
//	/**
//	 * Loads a 'database model' from the given model file name
//	 * @param dbModelFileName the short file name in the current project models ( eg 'mymodel.dbrep' ) 
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	public Model loadDatabaseModel(final String dbModelFileName) throws TelosysToolsException {
////		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg();
////		String dbrepAbsolutePath = FileUtil.buildFilePath( telosysToolsCfg.getModelsFolderAbsolutePath(), dbModelFileName);
////		File repositoryFile = new File(dbrepAbsolutePath);
////		return loadDatabaseModel( repositoryFile ) ;
//		GenericModelLoader genericModelLoader = new GenericModelLoader(getTelosysToolsCfg());
//		return genericModelLoader.loadDatabaseModel(dbModelFileName);
//	}
	
	//-----------------------------------------------------------------------------------------------------
	// Model loading DSL or Database model 
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Returns a new instance of GenericModelLoader
	 * @return
	 * @throws TelosysToolsException
	 */
	public GenericModelLoader getGenericModelLoader() throws TelosysToolsException {
		return new GenericModelLoader(getTelosysToolsCfg());
	}

	/**
	 * Loads a 'model' from the given model file name <br>
	 * The model name can be a 'database model' file name or a 'DSL model' file name <br>
	 * e.g. : 'books.dbrep' or 'books.model'  <br>
	 * 
	 * @param modelFileName the model file name in the current project models 
	 * @return
	 * @throws TelosysToolsException
	 */
	public Model loadModel(final String modelFileName) throws TelosysToolsException {
		GenericModelLoader genericModelLoader = getGenericModelLoader() ;
		return genericModelLoader.loadModel(modelFileName);
	}
	
	/**
	 * Loads a 'model' from the given model file <br>
	 * The model name can be a 'database model' file name or a 'DSL model' file name <br>
	 * e.g. : 'books.dbrep' or 'books.model'  <br>
	 * 
	 * @param modelFile
	 * @return
	 * @throws TelosysToolsException
	 */
	public Model loadModel(final File modelFile) throws TelosysToolsException {
		GenericModelLoader genericModelLoader = getGenericModelLoader() ;
		return genericModelLoader.loadModel(modelFile);
	}
	
	
//	//-----------------------------------------------------------------------------------------------------
//	// DSL model loading ('modelName/xxx.model') 
//	//-----------------------------------------------------------------------------------------------------
//	/**
//	 * Loads a 'DSL model' from the given model name
//	 * @param modelName the name of the model to be loaded ( e.g. 'bookstore' or 'employees' ) 
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	public Model loadDslModel(final String modelName) throws TelosysToolsException {
////		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg();
////		String modelFileAbsolutePath = FileUtil.buildFilePath( telosysToolsCfg.getModelsFolderAbsolutePath(), modelName+".model");
////		return loadDslModel( new File(modelFileAbsolutePath) );
//		GenericModelLoader genericModelLoader = new GenericModelLoader(getTelosysToolsCfg());
//		return genericModelLoader.loadDslModel(modelName);
//	}
	
	//-----------------------------------------------------------------------------------------------------
	// Targets definitions 
	//-----------------------------------------------------------------------------------------------------
	public TargetsDefinitions loadTargetsDefinitions(final String bundleName) throws TelosysToolsException, GeneratorException {
		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg();
		//TargetsLoader targetsLoader = new TargetsLoader( telosysToolsCfg.getTemplatesFolderAbsolutePath() );
		TargetsLoader targetsLoader = new TargetsLoader(telosysToolsCfg) ;

		TargetsDefinitions targetsDefinitions = targetsLoader.loadTargetsDefinitions(bundleName);
		return targetsDefinitions ;
	}
	
	//-----------------------------------------------------------------------------------------------------
	// Generation 
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Launch a generation task for all the entities of the given model <br>
	 * and all the targets of the given bundle
	 * @param model
	 * @param bundleName
	 * @return
	 * @throws TelosysToolsException
	 * @throws GeneratorException
	 */
	public GenerationTaskResult launchGeneration(Model model, String bundleName ) 
			throws TelosysToolsException, GeneratorException {
		
//		List<String> allEntitiesNames = new LinkedList<String>();
//		for ( Entity entity : model.getEntities() ) {
//			allEntitiesNames.add( entity.getClassName() );
//		}

		return launchGeneration(model, null, bundleName, null, true);
	}
	
	/**
	 * Launch a generation task for the given entities of the given model <br>
	 * and all the targets of the given bundle
	 * @param model
	 * @param selectedEntities
	 * @param bundleName
	 * @return
	 * @throws TelosysToolsException
	 * @throws GeneratorException
	 */
	public GenerationTaskResult launchGeneration(Model model, List<String> selectedEntities,
			String bundleName
			) throws TelosysToolsException, GeneratorException {
		
//		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg();
//
//		//--- Get all TEMPLATES and RESOURCES defined for this BUNDLE
//		TargetsDefinitions targetsDefinitions = loadTargetsDefinitions(bundleName);
//		List<TargetDefinition> allTemplatesTargets = targetsDefinitions.getTemplatesTargets();
//		List<TargetDefinition> allResourcesTargets = targetsDefinitions.getResourcesTargets();
//		
//		GenerationTask generationTask = new StandardGenerationTask(
//				model, selectedEntities, 
//				bundleName, allTemplatesTargets, allResourcesTargets, 
//				telosysToolsCfg, this.telosysToolsLogger );
//		
//		GenerationTaskResult generationTaskResult = generationTask.launch();
//		return generationTaskResult ;
//
		return launchGeneration(model, selectedEntities, bundleName, null, true);
	}
	
	/**
	 * Launch a generation task 
	 * @param model
	 * @param entitiesNames list of entities names to be used (or null for all the entities of the model)
	 * @param bundleName
	 * @param targetsList list of templates targets to be used (or null for all the templates of the bundle)
	 * @param copyResources true to copy all the resources of the bundle 
	 * @return
	 * @throws TelosysToolsException
	 * @throws GeneratorException
	 */
	public GenerationTaskResult launchGeneration(Model model, List<String> entitiesNames,
			String bundleName, List<TargetDefinition> targetsList, boolean copyResources 
			) throws TelosysToolsException, GeneratorException {
		
		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg();

		//----- ENTITIES TO BE USED FOR CODE GENERATION
		List<String> selectedEntities = entitiesNames ;
		if ( selectedEntities == null ) {
			//--- Not defined => ALL ENTITIES
			selectedEntities = new LinkedList<String>();
			for ( Entity entity : model.getEntities() ) {
				selectedEntities.add( entity.getClassName() );
			}
		}

		TargetsDefinitions targetsDefinitions = null ;
		
		//----- TEMPLATES TO BE USED FOR CODE GENERATION
		List<TargetDefinition> selectedTemplatesTargets = targetsList ;
		if ( selectedTemplatesTargets == null ) {
			//--- Not defined => ALL TEMPLATES TARGETS 
			targetsDefinitions = loadTargetsDefinitions(bundleName);
			selectedTemplatesTargets = targetsDefinitions.getTemplatesTargets();
		}
		
		//----- RESOURCES TO BE COPIED
		List<TargetDefinition> selectedResourcesTargets = null ; // no resources to be copied
		//--- Get all RESOURCES TARGETS defined for this BUNDLE
		if ( copyResources ) {
			if ( targetsDefinitions == null ) {
				targetsDefinitions = loadTargetsDefinitions(bundleName);
			}
			selectedResourcesTargets = targetsDefinitions.getResourcesTargets(); // ALL resources to be copied
		}
		
		//----- GENERATION TASK CREATION AND LAUNCH
		GenerationTask generationTask = new StandardGenerationTask(
				model, selectedEntities, 
				bundleName, selectedTemplatesTargets, selectedResourcesTargets, 
				telosysToolsCfg, this.telosysToolsLogger );
		
		GenerationTaskResult generationTaskResult = generationTask.launch();
		
		afterGeneration();
		
		return generationTaskResult ;
	}
	
	private int afterGeneration() throws TelosysToolsException {
		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg();
		
		String fileName = FileUtil.buildFilePath( telosysToolsCfg.getTelosysToolsFolderAbsolutePath(), "/stats/gen.count" );
		CounterFileManager counterFileManager = new CounterFileManager(fileName, true);
		return counterFileManager.incrementCounter() ;
	}

	//-----------------------------------------------------------------------------------------------------
	// MODELS 
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Returns all the models files ( ".dbmodel", ".dbrep" or ".model" files ) for the current project
	 * @return
	 * @throws TelosysToolsException
	 */
	public final List<File> getModels() throws TelosysToolsException {

		List<File> list = new LinkedList<File>();
		File modelsFolder = new File( getTelosysToolsCfg().getModelsFolderAbsolutePath() );
		if ( modelsFolder.exists() && modelsFolder.isDirectory() ) {
			for ( File file : modelsFolder.listFiles() ) {
				if ( file.isFile() ) {
					if ( ApiUtil.isModelFile(file) ) {
						list.add(file);
					}
				}
			}
		}
		else {
			throw new TelosysToolsException("Invalid models folder");
		}
		return list ;
	}
	
	/**
	 * Returns the model file for the given model name
	 * @param modelName the model name with or without suffix (eg 'foo', 'foo.model', 'foo.dbrep', 'foo.dbmodel' )  
	 * @return the model's File if found or null if not found  
	 * @throws TelosysToolsException if the given model name is ambiguous ( eg 'foo' with 2 models 'foo.model' and 'foo.dbrep' )
	 */
	public final File getModelFile(String modelName) throws TelosysToolsException {
		List<File> models = getModels();
		File modelFile = null ;
		int n = 0 ;
		if ( modelName.contains(".") ) {
			// Suffix is in the name ( foo.model, foo.dbrep, foo.dbmodel )
			for ( File file : models ) {
				if ( file.getName().equals(modelName) ) {
					modelFile = file ;
					n++;				
				}
			}
		}
		else {
			// No suffix in the name => try to add all the suffixes 
			for ( File file : models ) {
				if ( file.getName().equals(modelName + ApiUtil.MODEL_SUFFIX) ) {
					modelFile = file ;
					n++;				
				}
				if ( file.getName().equals(modelName + ApiUtil.DBMODEL_SUFFIX) ) {
					modelFile = file ;
					n++;				
				}
				if ( file.getName().equals(modelName + ApiUtil.DBREP_SUFFIX) ) {
					modelFile = file ;
					n++;				
				}
			}
		}
		if ( n == 0 ) {
			return null ; // Not found
		}
		else if ( n == 1 ) {
			return modelFile ; // Found 1 matching file
		}
		else {
			throw new TelosysToolsException("Ambiguous model name '" + modelName + "' (" + n + " files found)");
		}		
	}
	
	//-----------------------------------------------------------------------------------------------------
	// DSL MODELS 
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Returns a File instance for the given DSL model name  
	 * @param modelName
	 * @return
	 * @throws TelosysToolsException
	 */
	public final File getDslModelFile(String modelName) throws TelosysToolsException {
		
		//--- Build the model file 
		String modelFileName = DslModelUtil.getModelShortFileName(modelName);
		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg();
		return new File( telosysToolsCfg.getDslModelFileAbsolutePath(modelFileName));
	}

	/**
	 * Returns a File instance for the given DSL entity name in the given model name <br>
	 * There's no garanty that the file exists 
	 * @param modelName
	 * @param entityName
	 * @return the File instance (never null, even if the entity doesn't exist)
	 * @throws TelosysToolsException
	 */
	public final File buildDslEntityFile(String modelName, String entityName) throws TelosysToolsException {
		
		File modelFile = getDslModelFile(modelName);
		return DslModelUtil.buildEntityFile(modelFile, entityName);
	}

	/**
	 * Creates a new DSL model in the project <br>
	 * Creates the '.model' file (initialized with default values) and the '_model' folder <br>
	 * 
	 * @param modelName the short model name without extension ( e.g. 'mymodel' )
	 * @return the '.model' file created 
	 * @throws TelosysToolsException
	 */
	public final File createNewDslModel(String modelName) throws TelosysToolsException {
		
		//--- Build the model file 		
		File modelFile = getDslModelFile(modelName) ;
		
		//--- Create the model file and model folder 
		DslModelUtil.createNewModel(modelFile);
		
		return modelFile;
	}
	
	/**
	 * Creates a new DSL entity in the given model
	 * @param modelName
	 * @param entityName
	 * @return
	 * @throws TelosysToolsException
	 */
	public final File createNewDslEntity(String modelName, String entityName) throws TelosysToolsException {
		
		return DslModelUtil.createNewEntity(getModelFile(modelName), entityName);
	}
	
	/**
	 * Creates a new DSL entity in the given model
	 * @param modelFile
	 * @param entityName
	 * @return
	 * @throws TelosysToolsException
	 */
	public final File createNewDslEntity(File modelFile, String entityName) throws TelosysToolsException {
		
		return DslModelUtil.createNewEntity(modelFile, entityName);
	}
	
	/**
	 * Deletes the DSL model identified by the given name (deletes the '.model' file and the '_model' folder)
	 * @param modelName the model name ( eg 'mymodel' )
	 * @throws TelosysToolsException
	 */
	public final void deleteDslModel(String modelName) throws TelosysToolsException {
		
		//--- Build the model file 		
		File modelFile = getDslModelFile(modelName) ;
		
		//--- Delete the model file and model folder 
		DslModelUtil.deleteModel(modelFile);
	}
	
	/**
	 * Deletes the DSL model identified by the given file (deletes the '.model' file and the '_model' folder)
	 * @param modelFile the model file ( e.g. a File instance for 'xxx/TelosysTools/mymodel.model' )
	 * @throws TelosysToolsException
	 */
	public final void deleteDslModel(File modelFile) throws TelosysToolsException {
		
		//--- Delete the model file and model folder 
		DslModelUtil.deleteModel(modelFile);
	}
	
	/**
	 * Deletes the DSL entity in the given model name
	 * @param modelName
	 * @param entityName
	 * @return true if deletes, false if not found
	 * @throws TelosysToolsException
	 */
	public final boolean deleteDslEntity(String modelName, String entityName) throws TelosysToolsException {
		
		File entityFile = buildDslEntityFile(modelName, entityName);
		if ( entityFile.exists() ) {
			return entityFile.delete() ;
		}
		else {
			return false ;
		}
	}
	
}
