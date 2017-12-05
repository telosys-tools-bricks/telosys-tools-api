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

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.dbcfg.DatabasesConfigurations;
import org.telosys.tools.commons.dbcfg.DbConfigManager;
import org.telosys.tools.commons.dbcfg.DbConnectionManager;
import org.telosys.tools.commons.dbcfg.DbConnectionStatus;
import org.telosys.tools.db.metadata.DbInfo;
import org.telosys.tools.db.metadata.MetaDataManager;
import org.telosys.tools.repository.DbModelGenerator;
import org.telosys.tools.repository.DbModelUpdator;
import org.telosys.tools.repository.UpdateLogWriter;
import org.telosys.tools.repository.changelog.ChangeLog;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.PersistenceManager;
import org.telosys.tools.repository.persistence.PersistenceManagerFactory;

public class DbAction {
	
	private final TelosysToolsCfg     telosysToolsCfg ;
	private final DbConfigManager     dbConfigManager ;
	private final DbConnectionManager dbConnectionManager ;
	
	/**
	 * Constructor
	 * @param telosysProject
	 * @throws TelosysToolsException
	 */
	public DbAction(TelosysProject telosysProject) throws TelosysToolsException {
		super();
		this.telosysToolsCfg = telosysProject.getTelosysToolsCfg() ;
		this.dbConfigManager = new DbConfigManager(telosysToolsCfg);
		this.dbConnectionManager = new DbConnectionManager(telosysToolsCfg);
	}

	private final Connection getConnection(Integer id) throws TelosysToolsException {
		if ( id != null ) {
			return dbConnectionManager.getConnection(id);
		}
		else {
			return dbConnectionManager.getConnection();
		}
	}
	
	private final void closeConnection(Connection con) throws TelosysToolsException {
		dbConnectionManager.closeConnection(con);
	}
	
	/**
	 * Returns the DatabasesConfigurations
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DatabasesConfigurations getDatabasesConfigurations() throws TelosysToolsException {
		return dbConfigManager.load() ;
	}
	
	/**
	 * Returns a list containing all the databases configurations 
	 * @return
	 * @throws TelosysToolsException
	 */
	public final List<DatabaseConfiguration> getDatabasesConfigurationsList() throws TelosysToolsException {
		DatabasesConfigurations databasesConfigurations = dbConfigManager.load() ;
		return databasesConfigurations.getDatabaseConfigurationsList();
	}
	
	/**
	 * Returns the DatabaseConfiguration for the given database ID (or null if none)
	 * @param id the database id (or null to get the default database)
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DatabaseConfiguration getDatabaseConfiguration(Integer id) throws TelosysToolsException {
		DatabasesConfigurations databasesConfigurations = dbConfigManager.load() ;
		if ( id != null ) {
			return databasesConfigurations.getDatabaseConfiguration(id);
		}
		else {
			return databasesConfigurations.getDatabaseConfiguration();
		}
	}
	
	/**
	 * Returns the db model file name (absolute path) for the given database configuration
	 * @param databaseConfiguration
	 * @return
	 */
	public final String getDbModelFileName(DatabaseConfiguration databaseConfiguration) {
		String databaseName = databaseConfiguration.getDatabaseName();
		if ( StrUtil.nullOrVoid(databaseName) ) {
			databaseName = "default-dbmodel" ;
		}
        String dir = telosysToolsCfg.getModelsFolderAbsolutePath();
        return FileUtil.buildFilePath(dir, databaseName+".dbrep" );
    }
	
	/**
	 * Test the connection for the given database ID 
	 * @param id
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbConnectionStatus testConnection(Integer id) throws TelosysToolsException {
		Connection con = getConnection(id);
		try {
			return dbConnectionManager.testConnection(con);
		}
		finally {
			closeConnection(con);
		}
	}

	/**
	 * Returns database information for the given database ID 
	 * @param id
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbInfo getDatabaseInfo(Integer id) throws TelosysToolsException {
		Connection con = getConnection(id );
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
	
	public final String getMetaData(Integer id, MetaDataOptions options) throws TelosysToolsException
    {
		DatabaseConfiguration dbConfig = getDatabaseConfiguration(id);
		Connection con = getConnection(id);
		try {
			return DbActionMetaData.getMetaData(dbConfig, con, options);
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
	private final DatabaseConfiguration getRequiredDatabaseConfiguration(Integer id) throws TelosysToolsException {
		DatabaseConfiguration databaseConfiguration = getDatabaseConfiguration(id);
		if ( databaseConfiguration == null ) {
			throw new TelosysToolsException("No configuration for database #"+id );
		}
		return databaseConfiguration; 
	}

	/**
	 * Returns the DB-Model file for the given database ID
	 * @param id
	 * @return
	 * @throws TelosysToolsException
	 */
	public final File getDbModelFile(Integer id) throws TelosysToolsException {
		DatabaseConfiguration databaseConfiguration = getRequiredDatabaseConfiguration(id);
		String dbModelFileName = getDbModelFileName(databaseConfiguration);
		return new File(dbModelFileName); 
	}

	/**
	 * Creates a new DB-Model for the given database ID 
	 * @param id
	 * @param logger
	 * @return
	 * @throws TelosysToolsException
	 */
	public final void createNewDbModel(Integer id, TelosysToolsLogger logger ) throws TelosysToolsException {
		
		DatabaseConfiguration databaseConfiguration = getRequiredDatabaseConfiguration(id);
		
		String dbModelFileName = getDbModelFileName(databaseConfiguration);
		File dbModelFile = new File(dbModelFileName);
		if ( dbModelFile.exists() ) {
			throw new TelosysToolsException("Model file '" + dbModelFile.getName() + "' already exists");
		}
		
		//--- 1) Generate the repository in memory
		logger.info("Creating new db-model from database " + databaseConfiguration.getDatabaseId() );
		DbModelGenerator generator = new DbModelGenerator(dbConnectionManager, logger) ;
		RepositoryModel dbModel = generator.generate(databaseConfiguration);
			
		//--- 2) Save the repository in the file
		logger.info("Saving model in file " + dbModelFile.getAbsolutePath() );
		PersistenceManager pm = PersistenceManagerFactory.createPersistenceManager(dbModelFile, logger);
		pm.save(dbModel);
		logger.info("Repository saved.");
	}
	
	/**
	 * Updates a new DB-Model for the given database ID 
	 * @param id
	 * @param logger
	 * @return
	 * @throws TelosysToolsException
	 */
	public final ChangeLog updateDbModel(Integer id, TelosysToolsLogger logger ) throws TelosysToolsException {
		
		DatabaseConfiguration databaseConfiguration = getRequiredDatabaseConfiguration(id);
		
		String dbModelFileName = getDbModelFileName(databaseConfiguration);
		File dbModelFile = new File(dbModelFileName); 
		
		//--- Load the Database Model 
		PersistenceManager persistenceManager = PersistenceManagerFactory.createPersistenceManager(dbModelFile);
		RepositoryModel repositoryModel = persistenceManager.load();

		//--- Create the upadte log writer
		File updateLogFile = getUpdateLogFile( dbModelFile.getAbsolutePath() );
		UpdateLogWriter updateLogWriter = new UpdateLogWriter( updateLogFile );

		//--- Update the dbModel in memory
		logger.info("Updating db-model from database " + databaseConfiguration.getDatabaseId() );
		DbModelUpdator dbModelUpdator = new DbModelUpdator(dbConnectionManager, logger, updateLogWriter) ;			
		ChangeLog changeLog = dbModelUpdator.updateRepository(databaseConfiguration, repositoryModel);
			
		//--- Save the dbModel in the file
		logger.info("Saving model in file " + dbModelFile.getAbsolutePath() );
		PersistenceManager pm = PersistenceManagerFactory.createPersistenceManager(dbModelFile, logger);
		pm.save(repositoryModel);
		logger.info("Repository saved.");
		
		return changeLog ;
	}
	
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
