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

public interface MetaDataOptions  {

	/**
	 * Return true if contains at least one option 
	 * @return
	 */
	public boolean hasOptions();
	
	/**
	 * Returns true if 'info' option is 'ON' 
	 * @return
	 */
	boolean isInfo() ;
	
	/**
	 * Returns true if 'tables' option is 'ON' 
	 * @return
	 */
	boolean isTables() ;
	
	/**
	 * Returns true if 'columns' option is 'ON' 
	 * @return
	 */
	boolean isColumns() ;
	
	/**
	 * Returns true if 'primary keys' option is 'ON' 
	 * @return
	 */
	boolean isPrimaryKeys() ;
	
	/**
	 * Returns true if 'foreign keys' option is 'ON' 
	 * @return
	 */
	boolean isForeignKeys() ;
	
	/**
	 * Returns true if 'catalogs' option is 'ON' 
	 * @return
	 */
	boolean isCatalogs() ;
	
	/**
	 * Returns true if 'schemas' option is 'ON' 
	 * @return
	 */
	boolean isSchemas() ;
}
