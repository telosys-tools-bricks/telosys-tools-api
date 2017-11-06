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

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.dbcfg.DatabasesConfigurations;
import org.telosys.tools.commons.dbcfg.DbConfigManager;
import org.telosys.tools.commons.dbcfg.DbConnectionManager;
import org.telosys.tools.commons.dbcfg.DbInfo;
import org.telosys.tools.repository.DbModelGenerator;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.PersistenceManager;
import org.telosys.tools.repository.persistence.PersistenceManagerFactory;
import org.telosys.tools.repository.rules.RepositoryRulesProvider;

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

	private final Connection getConnection(Integer id ) throws TelosysToolsException {
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
	 * Returns the DatabaseConfiguration for the given database ID (or null if none)
	 * @param id the database id (or null to get the default database)
	 * @return
	 * @throws TelosysToolsException
	 */
	public DatabaseConfiguration getDatabaseConfiguration(Integer id) throws TelosysToolsException {
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
        //String dir = telosysToolsCfg.getModelsFolder();
        String dir = telosysToolsCfg.getModelsFolderAbsolutePath();
        return FileUtil.buildFilePath(dir, databaseName+".dbrep" );
    }
	
	/**
	 * Test the connection for the given database ID 
	 * @param id
	 * @return
	 * @throws TelosysToolsException
	 */
	public final boolean testConnection(Integer id) throws TelosysToolsException {
		Connection con = getConnection(id );
		try {
			dbConnectionManager.testConnection(con);
			return true ;
		} catch (Exception e) {
			return false ;
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
			return dbConnectionManager.getDatabaseInfo(con);
		} catch (Exception e) {
			return null ;
		}
		finally {
			closeConnection(con);
		}
	}

	/**
	 * Creates a new DB-Model for the given database ID 
	 * @param id
	 * @param logger
	 * @return
	 * @throws TelosysToolsException
	 */
	public final void createNewDbModel(Integer id, TelosysToolsLogger logger ) throws TelosysToolsException {
		
		DatabaseConfiguration databaseConfiguration = getDatabaseConfiguration(id);
		if ( databaseConfiguration == null ) {
			throw new TelosysToolsException("No configuration for database #"+id );
		}
		
		String dbModelFileName = getDbModelFileName(databaseConfiguration);
		File dbModelFile = new File(dbModelFileName); 
		
		RepositoryModel dbModel = null ;
		//--- 1) Generate the repository in memory
		//RepositoryGenerator generator = new RepositoryGenerator(null, RepositoryRulesProvider.getRepositoryRules(), logger) ;	
		DbModelGenerator generator = new DbModelGenerator(dbConnectionManager, RepositoryRulesProvider.getRepositoryRules(), logger) ;			
		dbModel = generator.generate(databaseConfiguration);
			
		//--- 2) Save the repository in the file
		logger.info("Saving model in file " + dbModelFile.getAbsolutePath() );
		PersistenceManager pm = PersistenceManagerFactory.createPersistenceManager(dbModelFile, logger);
		pm.save(dbModel);
		logger.info("Repository saved.");
	}
	
}
