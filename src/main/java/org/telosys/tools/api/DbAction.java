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

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.dbcfg.DbConnectionManager;
import org.telosys.tools.commons.dbcfg.DbInfo;

public class DbAction {
	
	private final DbConnectionManager dbConnectionManager ;
	
	public DbAction(TelosysProject telosysProject) throws TelosysToolsException {
		super();
		this.dbConnectionManager = new DbConnectionManager(telosysProject.getTelosysToolsCfg());
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
	
	public final boolean testConnection(Integer id ) throws TelosysToolsException {
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

	public final DbInfo getDatabaseInfo(Integer id ) throws TelosysToolsException {
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

}
