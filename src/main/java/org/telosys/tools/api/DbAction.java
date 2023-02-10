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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.dbcfg.DbConnectionStatus;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseConnectionProvider;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseConnectionTool;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinition;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitions;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitionsLoader;
import org.telosys.tools.commons.exception.TelosysYamlException;
import org.telosys.tools.db.metadata.DbInfo;
import org.telosys.tools.db.metadata.MetaDataManager;

public class DbAction {
	
	private final TelosysToolsCfg     telosysToolsCfg ;
	private final DatabaseConnectionProvider  databaseConnectionProvider ; // v 3.4.0
	
	/**
	 * Constructor
	 * @param telosysProject
	 * @throws TelosysToolsException
	 */
	public DbAction(TelosysProject telosysProject) throws TelosysToolsException {
		super();
		this.telosysToolsCfg = telosysProject.getTelosysToolsCfg() ;
		this.databaseConnectionProvider = new DatabaseConnectionProvider(telosysToolsCfg); // v 3.4.0
	}

	private Connection getConnection(String databaseId) throws TelosysToolsException {
		return databaseConnectionProvider.getConnection(databaseId); // v 3.4.0
	}

	private Connection getConnection(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		return databaseConnectionProvider.getConnection(databaseDefinition); // v 3.4.0
	}
	
	private DbInfo getDbInfoAndClose(Connection con) throws TelosysToolsException {
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
	
	private DbConnectionStatus getConnectionStatusAndClose(Connection con) throws TelosysToolsException {
		return DatabaseConnectionTool.getConnectionStatus(con); // v 3.4.0
	}

	private boolean checkConnectionAndClose(Connection con) throws TelosysToolsException {
		boolean result = false ;
		if ( con != null ) {
			result = true ;
			closeConnection(con);
		}
		return result ;
	}

	private void closeConnection(Connection con) throws TelosysToolsException {
		DatabaseConnectionTool.closeConnection(con); // v 3.4.0
	}
	
	//--------------------------------------------------------------------------------------------
	// Get databases definitions
	//--------------------------------------------------------------------------------------------
	/**
	 * Returns the DatabaseDefinitions
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DatabaseDefinitions getDatabaseDefinitions() throws TelosysToolsException {
		DatabaseDefinitionsLoader loader = new DatabaseDefinitionsLoader();
		try {
			return loader.load(this.telosysToolsCfg);
		} catch (TelosysYamlException e) {
			throw new TelosysToolsException("Cannot load databases config (YAML error)");
		}
	}

	/**
	 * Returns a list containing all the database definitions 
	 * @return
	 * @throws TelosysToolsException
	 */
	public final List<DatabaseDefinition> getDatabaseDefinitionsList() throws TelosysToolsException {
		return getDatabaseDefinitions().getDatabases();
	}
	
	/**
	 * Returns the DatabaseDefinition for the given database ID (or null if none)
	 * @param databaseId 
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DatabaseDefinition getDatabaseDefinition(String databaseId) throws TelosysToolsException {
		return getDatabaseDefinitions().getDatabaseDefinition(databaseId);
	}
	
	//--------------------------------------------------------------------------------------------
	// Check database connection
	//--------------------------------------------------------------------------------------------
	/**
	 * Just check if it's possible to get a connection for the given database id
	 * @param databaseId
	 * @return
	 * @throws TelosysToolsException
	 */
	public final boolean checkDatabaseConnection(String databaseId) throws TelosysToolsException {
		return checkConnectionAndClose( getConnection(databaseId) );
	}
	
	/**
	 * Just check if it's possible to get a connection for the given database
	 * @param databaseDefinition
	 * @return
	 * @throws TelosysToolsException
	 */
	public final boolean checkDatabaseConnection(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		return checkConnectionAndClose( getConnection(databaseDefinition) );
	}
	
	/**
	 * Test the connection for the given database ID 
	 * @param databaseId
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbConnectionStatus checkDatabaseConnectionWithStatus(String databaseId) throws TelosysToolsException {
		return getConnectionStatusAndClose( getConnection(databaseId) );
	}

	/**
	 * Test the connection for the given database configuration 
	 * @param databaseDefinition
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbConnectionStatus checkDatabaseConnectionWithStatus(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		return getConnectionStatusAndClose( getConnection(databaseDefinition) );
	}

	//--------------------------------------------------------------------------------------------
	// Get DbInfo
	//--------------------------------------------------------------------------------------------
	/**
	 * Returns database information for the given database ID 
	 * @param databaseId
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbInfo getDatabaseInfo(String databaseId) throws TelosysToolsException {
		return getDbInfoAndClose(getConnection(databaseId)); 
	}
	
	/**
	 * Returns database information for the given database definition 
	 * @param databaseDefinition
	 * @return
	 * @throws TelosysToolsException
	 */
	public final DbInfo getDatabaseInfo(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		return getDbInfoAndClose(getConnection(databaseDefinition)); 
	}
	
	//--------------------------------------------------------------------------------------------
	// Get MetaData
	//--------------------------------------------------------------------------------------------
	/**
	 * Returns meta-data for the given database id according with the given options
	 * @param databaseId
	 * @param options
	 * @return
	 * @throws TelosysToolsException
	 */
	public final String getMetaData(String databaseId, MetaDataOptions options) throws TelosysToolsException {
		return getMetaData(getDatabaseDefinition(databaseId), options);
    }
	
	/**
	 * Returns meta-data for the given database configuration according with the given options
	 * @param databaseDefinition
	 * @param options
	 * @return
	 * @throws TelosysToolsException
	 */
	public final String getMetaData(DatabaseDefinition databaseDefinition, MetaDataOptions options) throws TelosysToolsException {
		Connection con = getConnection(databaseDefinition);
		try {
			return DbActionMetaData.getMetaData(databaseDefinition, con, options);
		} catch (SQLException e) {
			throw new TelosysToolsException("Cannot get meta-data", e);
		}
		finally {
			closeConnection(con);
		}
    }

}
