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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.bundles.BundleStatus;
import org.telosys.tools.commons.bundles.BundlesFromGitHub;
import org.telosys.tools.commons.bundles.BundlesManager;
import org.telosys.tools.commons.bundles.BundlesNames;
import org.telosys.tools.commons.bundles.TargetDefinition;
import org.telosys.tools.commons.bundles.TargetsDefinitions;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.dbcfg.DatabasesConfigurations;
import org.telosys.tools.commons.dbcfg.DbConnectionStatus;
import org.telosys.tools.commons.env.EnvironmentManager;
import org.telosys.tools.commons.github.GitHubRateLimitResponse;
import org.telosys.tools.commons.logger.ConsoleLogger;
import org.telosys.tools.db.metadata.DbInfo;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.generator.task.GenerationTask;
import org.telosys.tools.generator.task.GenerationTaskResult;
import org.telosys.tools.generator.task.StandardGenerationTask;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.repository.changelog.ChangeLog;
import org.telosys.tools.stats.services.CounterFileManager;

public class TelosysProject {

	private static final boolean STATS_FLAG = false;
	
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
		StringBuilder sb = new StringBuilder();
		sb.append("Project initialization \n");
		sb.append("Project folder : '" + projectFolderAbsolutePath + "' \n");
		sb.append("\n");
		// Init environment files
		initProject(sb);
		return sb.toString();		
	}
	
	public void initProject(StringBuilder sb) {
		EnvironmentManager environmentManager = new EnvironmentManager( projectFolderAbsolutePath );
		// Init environment files
		environmentManager.initEnvironment(sb);
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
	public BundlesFromGitHub getGitHubBundlesList(String userName) throws TelosysToolsException {
		
		BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
		try {
			return bm.getGitHubBundlesList(userName);
		} catch (Exception e) {
			throw new TelosysToolsException("Cannot get bundles list", e);
		}
	}
	
	/**
	 * Returns GitHub API current rate limit 
	 * @return
	 * @throws TelosysToolsException
	 */
	public GitHubRateLimitResponse getGitHubRateLimit() throws TelosysToolsException {
		
		BundlesManager bundlesManager = new BundlesManager( getTelosysToolsCfg() );
		try {
			return bundlesManager.getGitHubRateLimit();
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
	public BundlesNames getInstalledBundles() throws TelosysToolsException {
		BundlesManager bundlesManager = new BundlesManager( getTelosysToolsCfg() );
		return bundlesManager.getProjectBundlesList();
	}
	
	/**
	 * Returns the 'templates.cfg' File object for the given bundle name <br>
	 * There's no guarantee the returned file exists
	 * @param bundleName
	 * @return
	 */
	public File getBundleConfigFile(String bundleName) throws TelosysToolsException {
		BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
		return bm.getBundleConfigFile(bundleName);
	}
	
	/**
	 * Deletes the given bundle
	 * @param bundleName
	 * @return true if found and deleted, false if not found
	 * @throws TelosysToolsException
	 */
	public boolean deleteBundle(String bundleName) throws TelosysToolsException {
		BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
		return bm.deleteBundle(bundleName);
	}

	/**
	 * Returns a list with all the targets definitions for the given bundle name
	 * @param bundleName
	 * @return
	 * @throws TelosysToolsException
	 */
	public TargetsDefinitions getTargetDefinitions(String bundleName) throws TelosysToolsException {
		BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
		return bm.getTargetsDefinitions(bundleName);	
	}
	
	/**
	 * Returns a list with all the templates for the given bundle name
	 * @param bundleName
	 * @return
	 * @throws TelosysToolsException
	 */
	public List<String> getTemplates(String bundleName) throws TelosysToolsException {
		TargetsDefinitions targetDef = getTargetDefinitions(bundleName);
		List<TargetDefinition> templates = targetDef.getTemplatesTargets();
		List<String> list = new LinkedList<>();
		for ( TargetDefinition t : templates ) {
			list.add( t.getTemplate() );
		}
		return list ;
	}

	//-----------------------------------------------------------------------------------------------------
	// Model loading DSL or Database model 
	//-----------------------------------------------------------------------------------------------------
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
		String modelAbsolutePath = FileUtil.buildFilePath( getTelosysToolsCfg().getModelsFolderAbsolutePath(), modelFileName);
		File modelFile = new File(modelAbsolutePath);
		return loadModel( modelFile);
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
		GenericModelLoader genericModelLoader = new GenericModelLoader() ;
		return genericModelLoader.loadModel(modelFile);
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
	 */
	public GenerationTaskResult launchGeneration(Model model, String bundleName ) 
			throws TelosysToolsException {
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
	 */
	public GenerationTaskResult launchGeneration(Model model, List<String> selectedEntities,
			String bundleName) throws TelosysToolsException {
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
	 */
	public GenerationTaskResult launchGeneration(Model model, List<String> entitiesNames,
			String bundleName, List<TargetDefinition> targetsList, boolean copyResources 
			) throws TelosysToolsException {
		
		//----- ENTITIES TO BE USED FOR CODE GENERATION
		List<String> selectedEntities = entitiesNames ;
		if ( selectedEntities == null ) {
			//--- Not defined => ALL ENTITIES
			selectedEntities = new LinkedList<>();
			for ( Entity entity : model.getEntities() ) {
				selectedEntities.add( entity.getClassName() );
			}
		}

		TargetsDefinitions targetsDefinitions = null ;
		
		//----- TEMPLATES TO BE USED FOR CODE GENERATION
		List<TargetDefinition> selectedTemplatesTargets = targetsList ;
		if ( selectedTemplatesTargets == null ) {
			//--- Not defined => ALL TEMPLATES TARGETS 
			targetsDefinitions = getTargetDefinitions(bundleName);
			selectedTemplatesTargets = targetsDefinitions.getTemplatesTargets();
		}
		
		//----- RESOURCES TO BE COPIED
		List<TargetDefinition> selectedResourcesTargets = null ; // no resources to be copied
		//--- Get all RESOURCES TARGETS defined for this BUNDLE
		if ( copyResources ) {
			if ( targetsDefinitions == null ) {
				targetsDefinitions = getTargetDefinitions(bundleName);
			}
			selectedResourcesTargets = targetsDefinitions.getResourcesTargets(); // ALL resources to be copied
		}
		
		//----- GENERATION TASK CREATION AND LAUNCH
		GenerationTask generationTask = new StandardGenerationTask(
				model, selectedEntities, 
				bundleName, selectedTemplatesTargets, selectedResourcesTargets, 
				getTelosysToolsCfg(), this.telosysToolsLogger );
		
		GenerationTaskResult generationTaskResult = generationTask.launch();
		
		afterGeneration();
		
		return generationTaskResult ;
	}
	
	private void afterGeneration() throws TelosysToolsException {
		if ( STATS_FLAG ) {
			String fileName = FileUtil.buildFilePath( 
					getTelosysToolsCfg().getTelosysToolsFolderAbsolutePath(), 
					"/stats/gen.count" );
			CounterFileManager counterFileManager = new CounterFileManager(fileName, true);
			counterFileManager.incrementCounter() ;
		} 
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

		List<File> list = new LinkedList<>();
		File modelsFolder = new File( getTelosysToolsCfg().getModelsFolderAbsolutePath() );
		if ( modelsFolder.exists() && modelsFolder.isDirectory() ) {
			for ( File file : modelsFolder.listFiles() ) {
				if ( file.isFile() && ApiUtil.isModelFile(file) ) {
					list.add(file);
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
				if ( file.getName().equals(modelName + ApiUtil.DSL_MODEL_FILE_SUFFIX) ) {
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
		return new File( getTelosysToolsCfg().getDslModelFileAbsolutePath(modelFileName));
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
	public final File createNewDslEntity(File modelFile, String entityName) {
		
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
	public final void deleteDslModel(File modelFile) {
		
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
	
	//-----------------------------------------------------------------------------------------------------
	// DATABASE MODELS 
	//-----------------------------------------------------------------------------------------------------
	public final DatabasesConfigurations getDatabasesConfigurations() throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabasesConfigurations();
	}		

	public final List<DatabaseConfiguration> getDatabasesConfigurationsList() throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabasesConfigurationsList();
	}		

	public final DatabaseConfiguration getDatabaseConfiguration(Integer id) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabaseConfiguration(id);
	}
	
	/**
	 * Returns the DB-Model file for the given database ID (or null if no DatabaseConfiguration)
	 * @param id 
	 * @return
	 * @throws TelosysToolsException
	 */
	public final File getDbModelFile(Integer id) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getDbModelFile(id);
	}
	
	//--------------------------------------------------------------------------------------------
	// Check database connection and get database information
	//--------------------------------------------------------------------------------------------
	/**
	 * Basic connection test for the given database id
	 * @param id
	 * @return
	 * @throws TelosysToolsException
	 */
	public final boolean checkDatabaseConnection(Integer id) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.checkDatabaseConnection(id);
	}
	/**
	 * Basic connection test for the given database id
	 * @param databaseConfiguration
	 * @return
	 * @throws TelosysToolsException
	 */
	public final boolean checkDatabaseConnection(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.checkDatabaseConnection(databaseConfiguration);
	}

	/**
	 * Test the connection for the given database id (and get information)
	 * @param id
	 * @param options
	 * @return true if connection is OK, false if cannot connect
	 * @throws TelosysToolsException
	 */
	public final DbConnectionStatus checkDatabaseConnectionWithStatus(Integer id) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.checkDatabaseConnectionWithStatus(id);
	}
	
	/**
	 * Test the connection for the given database configuration (and get information)
	 * @param databaseConfiguration
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbConnectionStatus checkDatabaseConnectionWithStatus(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.checkDatabaseConnectionWithStatus(databaseConfiguration);
	}
	
	/**
	 * Returns a long string containing all the required meta-data 
	 * @param id
	 * @param options
	 * @return
	 * @throws TelosysToolsException
	 */
	public final String getMetaData(Integer id, MetaDataOptions options) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getMetaData(id, options);
	}
	
	/**
	 * Returns a long string containing all the required meta-data 
	 * @param databaseConfiguration
	 * @param options
	 * @return
	 * @throws TelosysToolsException
	 */
	public final String getMetaData(DatabaseConfiguration databaseConfiguration, MetaDataOptions options) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getMetaData(databaseConfiguration, options);
	}
	
	/**
	 * Returns the database information retrieved from meta-data
	 * @param id
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbInfo getDatabaseInfo(Integer id ) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabaseInfo(id);
	}

	/**
	 * Returns the database information retrieved from meta-data
	 * @param databaseConfiguration
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbInfo getDatabaseInfo(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabaseInfo(databaseConfiguration);
	}
	
	//--------------------------------------------------------------------------------------------
	// DB MODEL management
	//--------------------------------------------------------------------------------------------
	/**
	 * Creates a new 'database model' from the given database id
	 * @param id
	 * @throws TelosysToolsException
	 */
	public final void createNewDbModel(Integer id ) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		dbAction.createNewDbModel(id, telosysToolsLogger);
	}
	
	/**
	 * Creates a new 'database model' from the given database configuration
	 * @param databaseConfiguration
	 * @throws TelosysToolsException
	 */
	public final void createNewDbModel(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		dbAction.createNewDbModel(databaseConfiguration, telosysToolsLogger);
	}
	
	/**
	 * Updates a 'database model' from the given database id
	 * @param id
	 * @throws TelosysToolsException
	 */
	public final ChangeLog updateDbModel(Integer id ) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.updateDbModel(id, telosysToolsLogger);
	}

	/**
	 * Updates a 'database model' from the given database configuration
	 * @param databaseConfiguration
	 * @throws TelosysToolsException
	 */
	public final ChangeLog updateDbModel(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.updateDbModel(databaseConfiguration, telosysToolsLogger);
	}
}
