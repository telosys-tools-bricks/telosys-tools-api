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
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.db.metadata.ColumnMetaData;
import org.telosys.tools.db.metadata.DbInfo;
import org.telosys.tools.db.metadata.ForeignKeyColumnMetaData;
import org.telosys.tools.db.metadata.MetaDataManager;
import org.telosys.tools.db.metadata.PrimaryKeyColumnMetaData;
import org.telosys.tools.db.metadata.SchemaMetaData;
import org.telosys.tools.db.metadata.TableMetaData;

public class DbActionMetaData {
	
	/**
	 * Private constructor
	 */
	private DbActionMetaData() {
	}
	
	public static final String getMetaData(DatabaseConfiguration dbConfig, Connection con, MetaDataOptions options) throws SQLException
    {
    	StringBuilder sb = new StringBuilder();
		//--- Get the database Meta-Data
		DatabaseMetaData dbmd = con.getMetaData();		

		MetaDataManager metaDataManager = new MetaDataManager();
		
		if ( options.isInfo() ) {
			reportDatabaseInfo(sb, metaDataManager.getDatabaseInfo(dbmd) );
		}
		if ( options.isCatalogs() ) {
			getMetaDataCatalogs(sb, metaDataManager, dbmd);
		}
		if ( options.isSchemas() ) {
			getMetaDataSchemas(sb, metaDataManager, dbmd);
		}
		
		if ( options.isTables() || options.isColumns() || options.isPrimaryKeys() || options.isForeignKeys() ) {
			List<TableMetaData> tables = metaDataManager.getTables(dbmd,
					dbConfig.getMetadataCatalog(),
					dbConfig.getMetadataSchema(),
					dbConfig.getMetadataTableNamePattern(),
					dbConfig.getMetadataTableTypesArray(),
					dbConfig.getMetadataTableNameInclude(),
					dbConfig.getMetadataTableNameExclude());
	
			if ( options.isTables() ) {
				reportTables(sb, tables);
			}
			if ( options.isColumns() ) {
				getMetaDataColumns(sb, metaDataManager, dbmd, tables, dbConfig.getMetadataCatalog(), dbConfig.getMetadataSchema());
			}
			if ( options.isPrimaryKeys() ) {
				getMetaDataPrimaryKeys(sb, metaDataManager, dbmd, tables, dbConfig.getMetadataCatalog(), dbConfig.getMetadataSchema());
			}
			if ( options.isForeignKeys() ) {
				getMetaDataForeignKeys(sb, metaDataManager, dbmd, tables, dbConfig.getMetadataCatalog(), dbConfig.getMetadataSchema());
			}
		}
		return sb.toString();
    }
	
    private static final void getMetaDataCatalogs(StringBuilder sb, MetaDataManager metaDataManager, DatabaseMetaData dbmd ) throws SQLException {
    	reportCatalogs(sb, metaDataManager.getCatalogs(dbmd));
    }

    private static final void getMetaDataSchemas(StringBuilder sb, MetaDataManager metaDataManager, DatabaseMetaData dbmd ) throws SQLException {
    	reportSchemas(sb, metaDataManager.getSchemas(dbmd));
    }

    /**
     * Prints the columns
     * @param sb
     * @param metaDataManager
     * @param dbmd
     * @param tables
     * @param catalog
     * @param schema
     * @throws SQLException
     */
    private static final void getMetaDataColumns(StringBuilder sb, MetaDataManager metaDataManager, DatabaseMetaData dbmd, 
    		List<TableMetaData> tables, String catalog, String schema) throws SQLException {
    	
    	if ( tables.isEmpty() ) {    		
    		sb.append("No table => no column.\n");
    	}
    	else {
			//--- Get the columns for each table
			for ( TableMetaData t : tables ) {
				String tableName = t.getTableName();
				List<ColumnMetaData> columns = metaDataManager.getColumns(dbmd, catalog, schema, tableName );
				reportColumns(sb, tableName, columns);
			}
    	}
    }
    
    /**
     * Prints the primary keys
     * @param sb
     * @param metaDataManager
     * @param dbmd
     * @param tables
     * @param catalog
     * @param schema
     * @throws SQLException
     */
    private static final void getMetaDataPrimaryKeys(StringBuilder sb, MetaDataManager metaDataManager, DatabaseMetaData dbmd, 
    		List<TableMetaData> tables, String catalog, String schema) throws SQLException {

    	if ( tables.isEmpty()  ) {
			sb.append("No table => no primary key.\n");
		}
		else {
			//--- Get the columns for each table
			for ( TableMetaData t : tables ) {
				String tableName = t.getTableName();
				List<PrimaryKeyColumnMetaData> pkColumns = metaDataManager.getPKColumns(dbmd, catalog, schema, tableName);
				reportPrimaryKeys(sb, tableName, pkColumns);
			}
		}		
    }
    
    /**
     * Prints the foreign keys
     * @param sb
     * @param metaDataManager
     * @param dbmd
     * @param tables
     * @param catalog
     * @param schema
     * @throws SQLException
     */
    private static final void getMetaDataForeignKeys(StringBuilder sb, MetaDataManager metaDataManager, DatabaseMetaData dbmd, 
    		List<TableMetaData> tables, String catalog, String schema) throws SQLException {

    	if ( tables.isEmpty()  ) {
			sb.append("No table => no foreign key.\n");
		}
		else {
			//--- Get the columns for each table
			for ( TableMetaData t : tables ) {
				String tableName = t.getTableName();
				List<ForeignKeyColumnMetaData> fkColumns = metaDataManager.getFKColumns(dbmd, catalog, schema, tableName);
				reportForeignKeys(sb, tableName, fkColumns);
			}
		}		
    }
    
    //---------------------------------------------------------------------------------------------
    // REPORTING
    //---------------------------------------------------------------------------------------------
    private static final void reportDatabaseInfo(StringBuilder sb, DbInfo dbInfo) {
		sb.append("DATABASE INFORMATION : \n");
		sb.append(" Product name    : " + dbInfo.getDatabaseProductName() + "\n");
		sb.append(" Product version : " + dbInfo.getDatabaseProductVersion() + "\n");
		sb.append(" \n");
		sb.append(" Driver name     : " + dbInfo.getDriverName() + "\n");
		sb.append(" Driver version  : " + dbInfo.getDriverVersion() + "\n");
		sb.append(" URL             : " + dbInfo.getUrl() + "\n");
		sb.append(" User name       : " + dbInfo.getUserName() + "\n");
		sb.append(" Max connections : " + dbInfo.getMaxConnections()+ "\n");
		sb.append(" \n");
		sb.append(" Catalog term          : " + dbInfo.getCatalogTerm() + "\n");
		sb.append(" Catalog separator     : " + dbInfo.getCatalogSeparator() + "\n");
		sb.append(" Schema term           : " + dbInfo.getSchemaTerm() + "\n");
		sb.append(" Search string escape  : " + dbInfo.getSearchStringEscape() + "\n");
		sb.append(" Transaction isolation : " + dbInfo.getDefaultTransactionIsolation() + "\n");
    }
    
    private static final void reportCatalogs(StringBuilder sb, List<String> catalogs) {
    	if ( catalogs.isEmpty() ) {
			sb.append("No catalog.\n");
    	}
    	else {
			sb.append("Database catalogs : \n");
    		for ( String s : catalogs ) {
    			sb.append(" . " + s + " \n");
    		}
    	}
    }
    private static final void reportSchemas(StringBuilder sb, List<SchemaMetaData> schemas) {
    	if ( schemas.isEmpty() ) {
			sb.append("No schema.\n");
    	}
    	else {
			sb.append("Database schemas : \n");
    		for ( SchemaMetaData schema : schemas ) {
				sb.append(" . " + schema.getSchemaName() + " ( catalog : "+ schema.getSchemaName() + " ) \n");
    		}
    	}
    }
    private static final void reportTables(StringBuilder sb, List<TableMetaData> tables) {
    	if ( tables.isEmpty() ) {
			sb.append("No table.\n");
    	}
    	else {
			sb.append("Tables : \n");
			for ( TableMetaData t : tables ) {
				sb.append(" . " + t.getTableName() + " (" + t.getTableType() + ") "
					+ " catalog = '" + t.getCatalogName() + "'"
					+ " schema = '" + t.getSchemaName() + "'" 
					+ "\n");
			}
    	}
    }
    
    private static final void reportColumns(StringBuilder sb, String tableName, List<ColumnMetaData> columns) {
    	sb.append("Table '" + tableName + "' columns : \n");
		for ( ColumnMetaData c : columns ) {
			
			sb.append(" . ");
			sb.append("[" + c.getOrdinalPosition() + "]" );
			sb.append(" " );
			sb.append(c.getColumnName() );
			sb.append(" : " );
			sb.append(c.getDbTypeName() );
			sb.append(" " );
			sb.append("(jdbc:" + c.getJdbcTypeCode() + ")" );
			sb.append(" " );
			sb.append("size=" + c.getSize() );
			sb.append(" " );
			sb.append(c.isNotNull() ? "NOT NULL" : "" );
			sb.append("\n   " );
			
			if ( c.getCharOctetLength() != 0 ) {
				sb.append("char-length=" + c.getCharOctetLength() );
				sb.append(" " );
			}			
			if ( c.getDecimalDigits() != 0 ) {
				sb.append("decimal-digits=" + c.getDecimalDigits() );
				sb.append(" " );
			}			
			if ( c.getNumPrecRadix() != 0 ) {
				sb.append("radix=" + c.getNumPrecRadix() ); // Base 2 or 10
				sb.append(" " );
			}
			sb.append("defaultValue=" + c.getDefaultValue() );
			sb.append("\n");
		}
    	sb.append("\n");
    }

    private static final void reportPrimaryKeys(StringBuilder sb, String tableName, List<PrimaryKeyColumnMetaData> pkColumns) {
    	sb.append("Table '" + tableName + "' primary keys : \n");
		for ( PrimaryKeyColumnMetaData c : pkColumns ) {
			sb.append(" . PK '" + c.getPkName() + "' : " );
			sb.append( " [" + c.getPkSequence() + "]");
			sb.append( "  " + c.getColumnName() );
			sb.append(" \n");
		}
    	sb.append("\n");
    }

    private static final void reportForeignKeys(StringBuilder sb, String tableName, List<ForeignKeyColumnMetaData> fkColumns) {
    	sb.append("Table '" + tableName + "' foreign keys : \n");
		for ( ForeignKeyColumnMetaData c : fkColumns ) {
			sb.append( " . FK '" + c.getFkName() + "' : ");
			sb.append( c.getFkTableName() + "." + c.getFkColumnName() );
			sb.append( " --> " );
			sb.append( c.getPkTableName()  + "." + c.getPkColumnName() );
			sb.append( "  ( PK : '" + c.getPkName() + "' )" );
			sb.append( " \n");
		}
    	sb.append("\n");
    }
}
