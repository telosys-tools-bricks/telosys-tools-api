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
package org.telosys.tools.stats;

import java.util.Date;

public interface ModelStats {

	/**
	 * The project name <br>
	 * Origin : folder name in the filesystem
	 * @return
	 */
	String getProjectName() ;
	
	/**
	 * The model name <br>
	 * Origin : model file name in the filesystem <br>
	 * e.g. : "cars.model"
	 * @return
	 */
	String getModelName() ;
	
	/**
	 * Returns the last modified date of the model file <br> 
	 * Origin : model file OS info 
	 *  see : Files.readAttributes + attr.lastModifiedTime() (OK in Windows and Linux)
	 * @return
	 */
	Date getLastModifiedDate() ;
	
//	/**
//	 * Returns the creation date of the model file <br> 
//	 * Origin : "creation.date"
//	 * @return
//	 */
//	Date getCreationDate() ;
	
}
