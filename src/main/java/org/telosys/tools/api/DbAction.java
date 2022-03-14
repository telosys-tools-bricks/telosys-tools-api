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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.dbcfg.DbConnectionStatus;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseConnectionProvider;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseConnectionTool;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinition;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitions;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitionsLoader;
import org.telosys.tools.db.metadata.DbInfo;
import org.telosys.tools.db.metadata.MetaDataManager;

public class DbAction {
	
	private final TelosysToolsCfg     telosysToolsCfg ;
	//private final DbConfigManager     dbConfigManager ;
//	private final DbConnectionManager dbConnectionManager ;
	private final DatabaseConnectionProvider  databaseConnectionProvider ; // v 3.4.0
	
	/**
	 * Constructor
	 * @param telosysProject
	 * @throws TelosysToolsException
	 */
	public DbAction(TelosysProject telosysProject) throws TelosysToolsException {
		super();
		this.telosysToolsCfg = telosysProject.getTelosysToolsCfg() ;
//		this.dbConfigManager = new DbConfigManager(telosysToolsCfg);
//		this.dbConnectionManager = new DbConnectionManager(telosysToolsCfg);
		this.databaseConnectionProvider = new DatabaseConnectionProvider(telosysToolsCfg); // v 3.4.0
	}

//	private final Connection getConnection(Integer id) throws TelosysToolsException {
	private final Connection getConnection(String databaseId) throws TelosysToolsException {
//		if ( id != null ) {
//			return dbConnectionManager.getConnection(id);
//		}
//		else {
//			return dbConnectionManager.getConnection();
//		}
		return databaseConnectionProvider.getConnection(databaseId); // v 3.4.0
	}

//	private final Connection getConnection(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
//		return dbConnectionManager.getConnection(databaseConfiguration);
//	}
	private final Connection getConnection(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		return databaseConnectionProvider.getConnection(databaseDefinition); // v 3.4.0
	}
	
	private final void closeConnection(Connection con) throws TelosysToolsException {
//		dbConnectionManager.closeConnection(con);
		DatabaseConnectionTool.closeConnection(con); // v 3.4.0
	}
	
	/**
	 * Returns the DatabasesConfigurations
	 * @return
	 * @throws TelosysToolsException
	 */
//	public final DatabasesConfigurations getDatabasesConfigurations() throws TelosysToolsException {
//		return dbConfigManager.load() ;
//	}
	public final DatabaseDefinitions getDatabaseDefinitions() {  // v 3.4.0
		DatabaseDefinitionsLoader loader = new DatabaseDefinitionsLoader();
		return loader.load(this.telosysToolsCfg);
	}

	/**
	 * Returns a list containing all the databases configurations 
	 * @return
	 * @throws TelosysToolsException
	 */
//	public final List<DatabaseConfiguration> getDatabasesConfigurationsList() throws TelosysToolsException {
//		DatabasesConfigurations databasesConfigurations = dbConfigManager.load() ;
//		return databasesConfigurations.getDatabaseConfigurationsList();
//	}
	public final List<DatabaseDefinition> getDatabaseDefinitionsList() { // v 3.4.0
		return getDatabaseDefinitions().getDatabases();
	}
	
	/**
	 * Returns the DatabaseConfiguration for the given database ID (or null if none)
	 * @param databaseId the database id (or null to get the default database)
	 * @return
	 * @throws TelosysToolsException
	 */
//	public final DatabaseConfiguration getDatabaseConfiguration(Integer id) throws TelosysToolsException {
//		DatabasesConfigurations databasesConfigurations = dbConfigManager.load() ;
//		if ( id != null ) {
//			return databasesConfigurations.getDatabaseConfiguration(id);
//		}
//		else {
//			return databasesConfigurations.getDatabaseConfiguration();
//		}
//	}
	public final DatabaseDefinition getDatabaseDefinition(String databaseId) throws TelosysToolsException {
		return getDatabaseDefinitions().getDatabaseDefinition(databaseId);
	}
	
//	/**
//	 * Returns the db model file name (absolute path) for the given database configuration
//	 * @param databaseConfiguration
//	 * @return
//	 */
//	public final String getDbModelFileName(DatabaseConfiguration databaseConfiguration) {
//		String databaseName = databaseConfiguration.getDatabaseName();
//		if ( StrUtil.nullOrVoid(databaseName) ) {
//			databaseName = "default-dbmodel" ;
//		}
//        String dir = telosysToolsCfg.getModelsFolderAbsolutePath();
//        return FileUtil.buildFilePath(dir, databaseName+".dbrep" );
//    }
	
	//--------------------------------------------------------------------------------------------
	// Check database connection
	//--------------------------------------------------------------------------------------------
	/**
	 * Just check if it's possible to get a connection for the given database id
	 * @param id
	 * @return
	 * @throws TelosysToolsException
	 */
//	public final boolean checkDatabaseConnection(Integer id) throws TelosysToolsException {
//		return checkConnectionAndClose( getConnection(id) );
//	}
	public final boolean checkDatabaseConnection(String databaseId) throws TelosysToolsException {
		return checkConnectionAndClose( getConnection(databaseId) );
	}
	
//	/**
//	 * Just check if it's possible to get a connection for the given database configuration
//	 * @param databaseConfiguration
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	public final boolean checkDatabaseConnection(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
//		return checkConnectionAndClose( getConnection(databaseConfiguration) );
//	}
	/**
	 * Just check if it's possible to get a connection for the given database
	 * @param databaseDefinition
	 * @return
	 * @throws TelosysToolsException
	 */
	public final boolean checkDatabaseConnection(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		return checkConnectionAndClose( getConnection(databaseDefinition) );
	}
	
	private boolean checkConnectionAndClose(Connection con) throws TelosysToolsException {
		boolean result = false ;
		if ( con != null ) {
			result = true ;
			closeConnection(con);
		}
		return result ;
	}

	/**
	 * Test the connection for the given database ID 
	 * @param databaseId
	 * @return
	 * @throws TelosysToolsException
	 */
//	public final DbConnectionStatus checkDatabaseConnectionWithStatus(Integer id) throws TelosysToolsException {
//		return getConnectionStatusAndClose( getConnection(id) );
//	}
	public final DbConnectionStatus checkDatabaseConnectionWithStatus(String databaseId) throws TelosysToolsException {
		return getConnectionStatusAndClose( getConnection(databaseId) );
	}

	/**
	 * Test the connection for the given database configuration 
	 * @param databaseConfiguration
	 * @return
	 * @throws TelosysToolsException
	 */
//	public final DbConnectionStatus checkDatabaseConnectionWithStatus(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
//		return getConnectionStatusAndClose( getConnection(databaseConfiguration) );
//	}
	public final DbConnectionStatus checkDatabaseConnectionWithStatus(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		return getConnectionStatusAndClose( getConnection(databaseDefinition) );
	}

	private DbConnectionStatus getConnectionStatusAndClose(Connection con) throws TelosysToolsException {
//		try {
//			return dbConnectionManager.getConnectionStatus(con);
//		}
//		finally {
//			closeConnection(con);
//		}
		return DatabaseConnectionTool.getConnectionStatus(con); // v 3.4.0
	}

	/**
	 * Returns database information for the given database ID 
	 * @param databaseId
	 * @return
	 * @throws TelosysToolsException
	 */
//	public final DbInfo getDatabaseInfo(Integer id) throws TelosysToolsException {
//		Connection con = getConnection(id);
//		return getDbInfoAndClose(con); 
//	}
	public final DbInfo getDatabaseInfo(String databaseId) throws TelosysToolsException {
		return getDbInfoAndClose(getConnection(databaseId)); 
	}
	
//	public final DbInfo getDatabaseInfo(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
//		Connection con = getConnection(databaseConfiguration);
//		return getDbInfoAndClose(con); 
//	}
	public final DbInfo getDatabaseInfo(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		return getDbInfoAndClose(getConnection(databaseDefinition)); 
	}
	
	private final DbInfo getDbInfoAndClose(Connection con) throws TelosysToolsException {
		try {
			MetaDataManager metaDataManager = new MetaDataManager();
			return metaDataManager.getDatabaseInfo(con);
		} catch (SQLException e) {
			throw new TelosysToolsException("Cannot get database information", e);
		}
		finally {
			closeConnection(con);
		}
	}
	
	/**
	 * Returns meta-data for the given database id according with the given options
	 * @param id
	 * @param options
	 * @return
	 * @throws TelosysToolsException
	 */
//	public final String getMetaData(Integer id, MetaDataOptions options) throws TelosysToolsException {
	public final String getMetaData(String databaseId, MetaDataOptions options) throws TelosysToolsException {
//		DatabaseConfiguration databaseConfiguration = getDatabaseConfiguration(id);
		return getMetaData(getDatabaseDefinition(databaseId), options);
    }
	
	/**
	 * Returns meta-data for the given database configuration according with the given options
	 * @param databaseConfiguration
	 * @param options
	 * @return
	 * @throws TelosysToolsException
	 */
//	public final String getMetaData(DatabaseConfiguration databaseConfiguration, MetaDataOptions options) throws TelosysToolsException {
	public final String getMetaData(DatabaseDefinition databaseDefinition, MetaDataOptions options) throws TelosysToolsException {
//		Connection con = getConnection(databaseConfiguration);
		Connection con = getConnection(databaseDefinition);
		try {
//			return DbActionMetaData.getMetaData(databaseConfiguration, con, options);
			return DbActionMetaData.getMetaData(databaseDefinition, con, options);
		} catch (SQLException e) {
			throw new TelosysToolsException("Cannot get meta-data", e);
		}
		finally {
			closeConnection(con);
		}
    }

	//--------------------------------------------------------------------------------------------------
	// DB-MODEL MANAGEMENT
	//--------------------------------------------------------------------------------------------------
	/**
	 * Returns the DatabaseConfiguration for the given id <br>
	 * throws a TelosysToolsException if there's no database defined for the id.
	 * @param id
	 * @return
	 * @throws TelosysToolsException
	 */
//	private final DatabaseConfiguration getRequiredDatabaseConfiguration(Integer id) throws TelosysToolsException {
	private final DatabaseDefinition getRequiredDatabaseDefinition(String id) throws TelosysToolsException {
//		DatabaseConfiguration databaseConfiguration = getDatabaseConfiguration(id);
		DatabaseDefinition databaseConfiguration = getDatabaseDefinition(id);
		if ( databaseConfiguration == null ) {
			throw new TelosysToolsException("No configuration for database #"+id );
		}
		return databaseConfiguration; 
	}

//	/**
//	 * Returns the DB-Model file for the given database ID (or null if no DatabaseConfiguration)
//	 * @param id
//	 * @return
//	 * @throws TelosysToolsException
//	 */
////	public final File getDbModelFile(Integer id) throws TelosysToolsException {
//	public final File getDbModelFile(String databaseId) throws TelosysToolsException {
////		DatabaseConfiguration databaseConfiguration = getDatabaseConfiguration(id);
////		return getDbModelFile(databaseConfiguration) ;
//		return getDbModelFile(getDatabaseDefinition(databaseId)) ;
//	}

//	/**
//	 * Returns the DB-Model file for the given database config (or null if no DatabaseConfiguration)
//	 * @param databaseConfiguration
//	 * @return
//	 */
////	public final File getDbModelFile(DatabaseConfiguration databaseConfiguration) {
//		public final File getDbModelFile(DatabaseDefinition databaseDefinition) {
////		if ( databaseConfiguration != null ) {
////			String dbModelFileName = getDbModelFileName(databaseConfiguration);
//		if ( databaseDefinition != null ) {
//			String dbModelFileName = getDbModelFileName(databaseDefinition);
//			return new File(dbModelFileName); 
//		}
//		return null ;
//	}

//	/**
//	 * Creates a new DB-Model for the given database ID 
//	 * @param id
//	 * @param logger
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	public final void createNewDbModel(Integer id, TelosysToolsLogger logger ) throws TelosysToolsException {
//		//--- Get the database configuration for the given database id
//		DatabaseConfiguration databaseConfiguration = getRequiredDatabaseConfiguration(id);
//		//--- Create the new db model
//		createNewDbModel(databaseConfiguration, logger ); 
//	}
	
//	/**
//	 * Creates a new DB-Model for the given database configuartion
//	 * @param databaseConfiguration
//	 * @param logger
//	 * @throws TelosysToolsException
//	 */
//	public final void createNewDbModel(DatabaseConfiguration databaseConfiguration, TelosysToolsLogger logger ) throws TelosysToolsException {
//		
//		//--- 1) Generate the repository in memory
//		logger.info("Creating new db-model from database " + databaseConfiguration.getDatabaseId() );
//		DbModelGenerator generator = new DbModelGenerator(dbConnectionManager, logger) ;
//		RepositoryModel dbModel = generator.generate(databaseConfiguration);
//			
//		//--- 2) Save the repository in the file
//		File dbModelFile = getDbModelFile(databaseConfiguration);
//		logger.info("Saving model in file " + dbModelFile.getAbsolutePath() );
//		PersistenceManager pm = PersistenceManagerFactory.createPersistenceManager(dbModelFile, logger);
//		pm.save(dbModel);
//		logger.info("Repository saved.");
//	}
	
//	/**
//	 * Updates a DB-Model for the given database ID 
//	 * @param id
//	 * @param logger
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	public final ChangeLog updateDbModel(Integer id, TelosysToolsLogger logger ) throws TelosysToolsException {
//		//--- Get the database configuration for the given database id
//		DatabaseConfiguration databaseConfiguration = getRequiredDatabaseConfiguration(id);
//		//--- Update the db-model
//		return updateDbModel(databaseConfiguration, logger );
//	}
	
//	/**
//	 * Updates a DB-Model for the given database configuration
//	 * @param databaseConfiguration
//	 * @param logger
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	public final ChangeLog updateDbModel(DatabaseConfiguration databaseConfiguration , TelosysToolsLogger logger ) throws TelosysToolsException {
//		
//		String dbModelFileName = getDbModelFileName(databaseConfiguration);
//		File dbModelFile = new File(dbModelFileName); 
//		
//		//--- Load the Database Model 
//		PersistenceManager persistenceManager = PersistenceManagerFactory.createPersistenceManager(dbModelFile);
//		RepositoryModel repositoryModel = persistenceManager.load();
//
//		//--- Create the upadte log writer
//		File updateLogFile = getUpdateLogFile( dbModelFile.getAbsolutePath() );
//		UpdateLogWriter updateLogWriter = new UpdateLogWriter( updateLogFile );
//
//		//--- Update the dbModel in memory
//		logger.info("Updating db-model from database " + databaseConfiguration.getDatabaseId() );
//		DbModelUpdator dbModelUpdator = new DbModelUpdator(dbConnectionManager, logger, updateLogWriter) ;			
//		ChangeLog changeLog = dbModelUpdator.updateRepository(databaseConfiguration, repositoryModel);
//			
//		//--- Save the dbModel in the file
//		logger.info("Saving model in file " + dbModelFile.getAbsolutePath() );
//		PersistenceManager pm = PersistenceManagerFactory.createPersistenceManager(dbModelFile, logger);
//		pm.save(repositoryModel);
//		logger.info("Repository saved.");
//		
//		return changeLog ;
//	}
	
    /**
     * Returns the log file name ( built from the repository file name )
     * @param dbModelFile
     * @return
     */
    private String getUpdateLogFileName(String dbModelFile) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
    	Date now = new Date();
    	String suffix = ".update." + dateFormat.format( now ) + ".log";
    	if ( dbModelFile.endsWith(".dbrep") ) {
    		int last = dbModelFile.length() - 6 ; 
    		return dbModelFile.substring(0,last) + suffix ;
    	}
    	else if ( dbModelFile.endsWith(".dbmodel") ) {
    		int last = dbModelFile.length() - 8 ; 
    		return dbModelFile.substring(0,last) + suffix ;
    	}
    	else {
    		return dbModelFile + suffix ;
    	}
    }
    
    private File getUpdateLogFile(String dbModelFile) {
		String sRepositoryFile = getUpdateLogFileName( dbModelFile ) ;
		return new File(sRepositoryFile);
    }
	
}
