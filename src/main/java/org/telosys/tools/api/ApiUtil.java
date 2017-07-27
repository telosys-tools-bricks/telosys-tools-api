/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
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

public class ApiUtil {
	
	public final static String  MODEL_SUFFIX   = ".model"  ;

	public final static String  DBREP_SUFFIX   = ".dbrep"  ;
	public final static String  DBMODEL_SUFFIX = ".dbmodel"  ;

	/**
	 * Returns true if the given file name ends with ".model" suffix
	 * @param file
	 * @return
	 */
	public final static boolean isDslModelFile(File file) {
		return file.getName().endsWith(MODEL_SUFFIX) ;
	}

	/**
	 * Returns true if the given file name ends with ".dbrep" or ".dbmodel" suffix
	 * @param file
	 * @return
	 */
	public final static boolean isDbModelFile(File file) {
		return file.getName().endsWith(DBREP_SUFFIX) || file.getName().endsWith(DBMODEL_SUFFIX) ;
	}

	/**
	 * Returns true if the given file name ends with ".model" or ".dbrep" or ".dbmodel" suffix
	 * @param file
	 * @return
	 */
	public final static boolean isModelFile(File file) {
		return isDslModelFile(file) || isDbModelFile(file) ;
	}
}
