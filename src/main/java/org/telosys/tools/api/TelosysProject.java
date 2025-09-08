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
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.bundles.BundlesManager;
import org.telosys.tools.commons.bundles.TargetDefinition;
import org.telosys.tools.commons.bundles.TargetsDefinitions;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.commons.dbcfg.DbConnectionStatus;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinition;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitions;
import org.telosys.tools.commons.depot.Depot;
import org.telosys.tools.commons.depot.DepotResponse;
import org.telosys.tools.commons.env.EnvironmentManager;
import org.telosys.tools.commons.github.GitHubClient;
import org.telosys.tools.commons.github.GitHubRateLimitResponse;
import org.telosys.tools.commons.logger.ConsoleLogger;
import org.telosys.tools.commons.models.ModelsManager;
import org.telosys.tools.db.metadata.DbInfo;
import org.telosys.tools.dsl.DslModelManager;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.model.dbmodel.DbToModelManager;
import org.telosys.tools.generator.task.GenerationTask;
import org.telosys.tools.generator.task.GenerationTaskResult;
import org.telosys.tools.generator.task.StandardGenerationTask;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

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

	private void checkArgumentNotNull(Object arg, String argName) {
		if (arg == null) {
			throw new IllegalArgumentException("Argument " + argName + " is null");
		}
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
	public TelosysToolsCfg loadTelosysToolsCfg() {
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager( projectFolderAbsolutePath );
		this.telosysToolsCfg = cfgManager.loadTelosysToolsCfg() ;
		return this.telosysToolsCfg;
	}

	public TelosysToolsCfg getTelosysToolsCfg() {
		if ( this.telosysToolsCfg == null ) {
			loadTelosysToolsCfg();
		}
		return this.telosysToolsCfg;
	}

	//-----------------------------------------------------------------------------------------------------
	// BUNDLES OF TEMPLATES
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Returns the bundles folder for the current project
	 * @return
	 * @since 3.4.0
	 */
	public final File getBundlesFolder() {
		return new File(getTelosysToolsCfg().getTemplatesFolderAbsolutePath() );
	}

	/**
	 * Returns the bundle folder for the given bundle name
	 * @param modelName
	 * @return
	 * @since 3.4.0
	 */
	public final File getBundleFolder(String bundleName) {
		return new File(getTelosysToolsCfg().getTemplatesFolderAbsolutePath(bundleName));
	}

	/**
	 * Returns all the bundles folders for the current project 
	 * @return
	 * @since 4.1.0
	 */
	public final List<File> getBundles() {
		BundlesManager bundlesManager = new BundlesManager( getTelosysToolsCfg() );
		return bundlesManager.getBundles(); 
	}
	
	/**
	 * Returns all the names of existing bunles for the current project 
	 * @return
	 * @since 4.1.0
	 */
	public final List<String> getBundleNames() {
		BundlesManager bundlesManager = new BundlesManager( getTelosysToolsCfg() );
		List<String> bundleNames = new LinkedList<>();
		for (File file : bundlesManager.getBundles() ) {
			bundleNames.add(file.getName());
		}
		return bundleNames;
	}
	
	/**
	 * Returns true if the bundle folder exists for the given bundle name
	 * @param bundleName
	 * @return
	 * @since 4.1.0
	 */
	public final boolean bundleFolderExists(String bundleName) {
		checkArgumentNotNull(bundleName, BUNDLE_NAME);
		File file = getBundleFolder(bundleName);
		return file.exists() && file.isDirectory();
	}

	/**
	 * Returns a list of bundles available on the given user's name (on GitHub)
	 * @param depot the depot name (e.g. GitHub user-name like "telosys-templates" )
	 * @return
	 * @throws TelosysToolsException
	 */
	public DepotResponse getBundlesAvailableInDepot(String depot) throws TelosysToolsException { // v 4.2.0
		BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
		return bm.getBundlesFromDepot(new Depot(depot)); // v 4.2.0
	}
	
	/**
	 * Returns a list of models available on the given user's name (on GitHub)
	 * @param depot the depot name (e.g. GitHub user-name like "telosys-models" )
	 * @return
	 * @throws TelosysToolsException
	 * @since 4.2.0
	 */
	public DepotResponse getModelsAvailableInDepot(String depot) throws TelosysToolsException { 
		ModelsManager m = new ModelsManager( getTelosysToolsCfg() );
		return m.getModelsFromDepot(new Depot(depot));
	}
	
	/**
	 * Check GitHub "rate limit" endpoint is responding for the server defined in the given depot
	 * @param depot
	 * @return
	 * @throws TelosysToolsException
	 */
	public GitHubRateLimitResponse checkGitHub(String depot) throws TelosysToolsException { // v 4.2.0
		GitHubClient gitHubClient = new GitHubClient( getTelosysToolsCfg().getCfgFileAbsolutePath() ) ;
		return gitHubClient.getRateLimit(new Depot(depot));
	}

	/**
	 * Download and install a bundle or a model from the given depot
	 * @param depot
	 * @param elementName name of the element to download (bundle name or model name)
	 * @param branch 
	 * @param installationType
	 * @return
	 * @throws TelosysToolsException
	 */
	public boolean downloadAndInstallBranch(String depot, String elementName, String branch, InstallationType installationType) throws TelosysToolsException {
		switch(installationType) {
		case BUNDLE:
			BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
			return bm.downloadAndInstallBundleBranch(new Depot(depot), elementName, branch);
		case MODEL:
			ModelsManager m = new ModelsManager( getTelosysToolsCfg() );
			return m.downloadAndInstallModelBranch(new Depot(depot), elementName, branch);
		default:
			throw new TelosysToolsException("Unexpected InstallationType");
		}
	}
	
	/**
	 * Returns the 'templates.cfg' file for the given bundle name <br>
	 * There's no guarantee the returned file exists
	 * @param bundleName
	 * @return
	 */
	public File getBundleConfigFile(String bundleName) {
		BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
		return bm.getBundleConfigFile(bundleName);
	}
	
	/**
	 * Returns the 'templates.cfg' file for the given bundle folder <br>
	 * There's no guarantee the returned file exists
	 * @param bundleFolder
	 * @return
	 */
	public File getBundleConfigFile(File bundleFolder) {
		BundlesManager bm = new BundlesManager( getTelosysToolsCfg() );
		return bm.getBundleConfigFile(bundleFolder);
	}
	
	/**
	 * Deletes the given bundle
	 * @param bundleName
	 * @return true if found and deleted, false if not found
	 * @throws TelosysToolsException 
	 */
	public boolean deleteBundle(String bundleName) throws TelosysToolsException {
		checkArgumentNotNull(bundleName, BUNDLE_NAME);
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
	// Model loading 
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Loads the given model name
	 * @param modelName
	 * @return
	 * @throws TelosysModelException
	 */
	public Model loadModel(String modelName) throws TelosysModelException {
		String modelAbsolutePath = FileUtil.buildFilePath(getTelosysToolsCfg().getModelsFolderAbsolutePath(), modelName);
		return loadModel(new File(modelAbsolutePath));
	}
	
	/**
	 * Loads the model located in the given model folder
	 * @param modelFolder
	 * @return
	 * @throws TelosysModelException
	 */
	public Model loadModel(File modelFolder) throws TelosysModelException {
		DslModelManager modelManager = new DslModelManager();
		Model model = modelManager.loadModel(modelFolder);
		if ( model == null ) {
			// Cannot load model => Specific Exception with parsing errors
			throw new TelosysModelException(modelFolder, modelManager.getErrorMessage(), modelManager.getErrors());
		}
		return model ;
	}

	//-----------------------------------------------------------------------------------------------------
	// Generation 
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Launch a generation task for all the entities of the given model and all the targets of the given bundle
	 * @param model
	 * @param bundleName
	 * @param copyResources
	 * @return
	 * @throws TelosysToolsException
	 */
	public GenerationTaskResult launchGeneration(Model model, String bundleName, boolean copyResources ) throws TelosysToolsException {
		return launchGeneration(model, null, bundleName, null, copyResources);
	}
	
	/**
	 * Launch a generation task for the given entities of the given model and all the targets of the given bundle
	 * @param model
	 * @param selectedEntities
	 * @param bundleName
	 * @param copyResources
	 * @return
	 * @throws TelosysToolsException
	 */
	public GenerationTaskResult launchGeneration(Model model, List<String> selectedEntities, String bundleName, boolean copyResources) throws TelosysToolsException {
		return launchGeneration(model, selectedEntities, bundleName, null, copyResources);
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
		
		return  generationTask.launch();
	}
	
	//-----------------------------------------------------------------------------------------------------
	// MODELS 
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Returns the models folder in the current project
	 * @return
	 */
	public final File getModelsFolder() {
		return new File(getTelosysToolsCfg().getModelsFolderAbsolutePath());
	}

	/**
	 * Returns the model folder for the given model name
	 * @param modelName
	 * @return
	 */
	public final File getModelFolder(String modelName) {
		// Current project 'models' folder 
		String modelFolderAbsolutePath = getTelosysToolsCfg().getModelFolderAbsolutePath(modelName);
		return new File(modelFolderAbsolutePath);
	}
	
	/**
	 * Returns all the model folders for the current project
	 * @return
	 */
	public final List<File> getModels() {
		return DslModelUtil.getModelsInFolder(getModelsFolder());
	}
	
	/**
	 * Returns all the names of existing models for the current project 
	 * @return
	 * @since 4.1.0
	 * @return
	 */
	public final List<String> getModelNames() { 
		List<String> modelNames = new LinkedList<>();
		// Convert files to file names (strings)
		for ( File f : getModels() ) {
			modelNames.add(f.getName());
		}
		return modelNames;
	}
	
	private static final String BUNDLE_NAME  = "bundleName";
	private static final String MODEL_NAME   = "modelName";
	private static final String MODEL_FOLDER = "modelFolder";
	private static final String ENTITY_NAME  = "entityName";
	private static final String DATABASE_ID  = "databaseId";
	private static final String DATABASE_DEFINITION  = "databaseDefinition";

	/**
	 * Returns the model info file for the given model name
	 * @param modelName
	 * @return
	 */
	public final File getModelInfoFile(String modelName) { // v 3.4.0
		checkArgumentNotNull(modelName, MODEL_NAME);
		return DslModelUtil.getModelFileFromModelFolder(getModelFolder(modelName));
	}
	
	public final File getModelInfoFile(File modelFolder) { // v 3.4.0
		checkArgumentNotNull(modelFolder, MODEL_FOLDER);
		return DslModelUtil.getModelFileFromModelFolder(modelFolder);
	}
	
	//-----------------------------------------------------------------------------------------------------
	// DSL MODELS 
	//-----------------------------------------------------------------------------------------------------

	/**
	 * Creates a new DSL model in the project <br>
	 * Creates the 'model-name' folder and the 'model info' file <br>
	 * @param modelName
	 * @return the model folder created
	 */
	public final File createNewDslModel(String modelName) {
		// Create the model in the 'models' folder 
		File modelFolder = getModelFolder(modelName);
		DslModelUtil.createNewModel(modelFolder);
		return modelFolder;
	}
	
    /**
     * Creates a new DSL model from the given database 
     * @param modelName
     * @param databaseId
     * @throws TelosysToolsException
     */
    public void createNewDslModelFromDatabase(String modelName, String databaseId) throws TelosysToolsException {
		DbToModelManager manager = new DbToModelManager(getTelosysToolsCfg(), telosysToolsLogger);
		manager.createModelFromDatabase(databaseId, modelName);    	
    }
    
	/**
	 * Returns the entity file for the given entity name in the given model 
	 * @param modelName
	 * @param entityName
	 * @return
	 */
	public final File getDslEntityFile(String modelName, String entityName) {
		checkArgumentNotNull(modelName, MODEL_NAME);
		checkArgumentNotNull(entityName, ENTITY_NAME);
		return DslModelUtil.getEntityFile(getModelFolder(modelName), entityName);
	}
	
	/**
	 * Creates a new DSL entity in the given model
	 * @param modelName
	 * @param entityName
	 * @return
	 * @throws TelosysToolsException
	 */
	public final File createNewDslEntity(String modelName, String entityName) {
		checkArgumentNotNull(modelName, MODEL_NAME);
		checkArgumentNotNull(entityName, ENTITY_NAME);
		return DslModelUtil.createNewEntity(getModelFolder(modelName), entityName);
	}
	
	/**
	 * Deletes the given DSL model  
	 * @param modelName the model name ( eg 'mymodel' )
	 * @throws TelosysToolsException 
	 */
	public final boolean deleteModel(String modelName) throws TelosysToolsException {		
		checkArgumentNotNull(modelName, MODEL_NAME);
		ModelsManager mm = new ModelsManager( getTelosysToolsCfg() );
		return mm.deleteModel(modelName);
	}
	
//	/**
//	 * Deletes the given DSL model  
//	 * @param modelFolder 
//	 * @throws TelosysToolsException 
//	 */
//	public final void deleteModel(File modelFolder) throws TelosysToolsException {		
//		checkArgumentNotNull(modelFolder, MODEL_FOLDER);
//		//--- Delete the model file and model folder 
//		DslModelUtil.deleteModel(modelFolder);
//	}
	
	/**
	 * Returns true if the model folder exists for the given model name
	 * @param modelName
	 * @return
	 */
	public final boolean modelFolderExists(String modelName) {
		checkArgumentNotNull(modelName, MODEL_NAME);
		File file = getModelFolder(modelName);
		return file.exists() && file.isDirectory();
	}
	
	/**
	 * Deletes the given entity in the given model name
	 * @param modelName
	 * @param entityName
	 * @return true if deletes, false if not found
	 */
	public final boolean deleteEntity(String modelName, String entityName) {		
		checkArgumentNotNull(modelName, MODEL_NAME);
		checkArgumentNotNull(entityName, ENTITY_NAME);
		return DslModelUtil.deleteEntity(getModelFolder(modelName), entityName);
	}
	
	//-----------------------------------------------------------------------------------------------------
	// DATABASES DEFINITIONS 
	//-----------------------------------------------------------------------------------------------------
	public final DatabaseDefinitions getDatabaseDefinitions() throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabaseDefinitions();
	}		

	public final List<DatabaseDefinition> getDatabaseDefinitionsList() throws TelosysToolsException {
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabaseDefinitionsList();
	}		

	public final DatabaseDefinition getDatabaseDefinition(String databaseId) throws TelosysToolsException {
		checkArgumentNotNull(databaseId, DATABASE_ID);
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabaseDefinition(databaseId);
	}
	
	public final Connection getDatabaseConnection(String databaseId) throws TelosysToolsException {
		checkArgumentNotNull(databaseId, DATABASE_ID);
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabaseConnection(databaseId);
	}
	
	public final boolean databaseIsDefined(String databaseId) throws TelosysToolsException {
		checkArgumentNotNull(databaseId, DATABASE_ID);
		DbAction dbAction = new DbAction(this);
		DatabaseDefinitions databaseDefinitions = dbAction.getDatabaseDefinitions();
		return databaseDefinitions.containsDatabase(databaseId);
	}
	
	//--------------------------------------------------------------------------------------------
	// Check database connection and get database information
	//--------------------------------------------------------------------------------------------
	/**
	 * Basic connection test for the given database id
	 * @param databaseId
	 * @return
	 * @throws TelosysToolsException
	 */
	public final boolean checkDatabaseConnection(String databaseId) throws TelosysToolsException {
		checkArgumentNotNull(databaseId, DATABASE_ID);
		DbAction dbAction = new DbAction(this);
		return dbAction.checkDatabaseConnection(databaseId);
	}
	/**
	 * Basic connection test for the given database 
	 * @param databaseDefinition
	 * @return
	 * @throws TelosysToolsException
	 */
	public final boolean checkDatabaseConnection(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		checkArgumentNotNull(databaseDefinition, DATABASE_DEFINITION);
		DbAction dbAction = new DbAction(this);
		return dbAction.checkDatabaseConnection(databaseDefinition);
	}

	/**
	 * Test the connection for the given database id (and get information)
	 * @param databaseId
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbConnectionStatus checkDatabaseConnectionWithStatus(String databaseId) throws TelosysToolsException {
		checkArgumentNotNull(databaseId, DATABASE_ID);
		DbAction dbAction = new DbAction(this);
		return dbAction.checkDatabaseConnectionWithStatus(databaseId);
	}
	
	/**
	 * Test the connection for the given database configuration (and get information)
	 * @param databaseDefinition
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbConnectionStatus checkDatabaseConnectionWithStatus(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		checkArgumentNotNull(databaseDefinition, DATABASE_DEFINITION);
		DbAction dbAction = new DbAction(this);
		return dbAction.checkDatabaseConnectionWithStatus(databaseDefinition);
	}
	
	/**
	 * Returns a long string containing all the required meta-data 
	 * @param databaseId
	 * @param options
	 * @return
	 * @throws TelosysToolsException
	 */
	public final String getMetaData(String databaseId, MetaDataOptions options) throws TelosysToolsException {
		checkArgumentNotNull(databaseId, DATABASE_ID);
		checkArgumentNotNull(options, "options");
		DbAction dbAction = new DbAction(this);
		return dbAction.getMetaData(databaseId, options);
	}
	
	/**
	 * Returns a long string containing all the required meta-data 
	 * @param databaseDefinition
	 * @param options
	 * @return
	 * @throws TelosysToolsException
	 */
	public final String getMetaData(DatabaseDefinition databaseDefinition, MetaDataOptions options) throws TelosysToolsException {
		checkArgumentNotNull(databaseDefinition, DATABASE_DEFINITION);
		checkArgumentNotNull(options, "options");
		DbAction dbAction = new DbAction(this);
		return dbAction.getMetaData(databaseDefinition, options);
	}
	
	/**
	 * Returns the database information retrieved from meta-data
	 * @param databaseId
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbInfo getDatabaseInfo(String databaseId) throws TelosysToolsException {
		checkArgumentNotNull(databaseId, DATABASE_ID);
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabaseInfo(databaseId);
	}

	/**
	 * Returns the database information retrieved from meta-data
	 * @param databaseDefinition
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbInfo getDatabaseInfo(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		checkArgumentNotNull(databaseDefinition, DATABASE_DEFINITION);
		DbAction dbAction = new DbAction(this);
		return dbAction.getDatabaseInfo(databaseDefinition);
	}
	
}
