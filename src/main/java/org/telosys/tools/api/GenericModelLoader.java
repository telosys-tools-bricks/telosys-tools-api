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

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.dsl.DslModelManager;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.repository.persistence.PersistenceManager;
import org.telosys.tools.repository.persistence.PersistenceManagerFactory;

/**
 * Generic model loader, usable with any kind of model
 * 
 * @author Laurent GUERIN
 *
 */
public class GenericModelLoader {
	
	/**
	 * Loads a 'model' from the given model file <br>
	 * The model name can be a 'database model' file name or a 'DSL model' file name <br>
	 * e.g. : 'books.dbrep' or 'books.model'  <br>
	 * 
	 * @param file the model file in the current project models 
	 * @return
	 * @throws TelosysToolsException
	 */
	public Model loadModel(final File file) throws TelosysToolsException {	
		if ( file.exists() ) {
			if ( file.isFile() ) {
				if ( ApiUtil.isDbModelFile(file) ) {
					//--- This file is supposed to be a db model file
					return loadDatabaseModel(file) ;
				}
				else if (  ApiUtil.isDslModelFile(file) ) {
					//--- This file is supposed to be a DSL model file
					return loadDslModel(file);
				}
				else {
					throw new TelosysToolsException("Invalid file extension '" + file.getName() + "' ");
				}
			}
			else {
				throw new TelosysToolsException("Not a file '" + file.getName() + "' ");				
			}
		}
		throw new TelosysToolsException("File '" + file.getAbsolutePath() + "' not found");
	}
	
	//-----------------------------------------------------------------------------------------------------
	// Database model loading ('dbrep') 
	//-----------------------------------------------------------------------------------------------------
	private Model loadDatabaseModel( File repositoryFile ) throws TelosysToolsException {
		PersistenceManager persistenceManager = PersistenceManagerFactory.createPersistenceManager(repositoryFile);
		return persistenceManager.load();
	}

	//-----------------------------------------------------------------------------------------------------
	// DSL model loading 
	//-----------------------------------------------------------------------------------------------------
	private Model loadDslModel(final File modelFile) throws TelosysToolsException {
		DslModelManager modelManager = new DslModelManager();
		Model model = modelManager.loadModel( modelFile );
		if ( model == null ) {
			// Cannot load model => Specific Exception with parsing errors
			throw new TelosysModelException(modelFile, modelManager.getErrorMessage(), modelManager.getErrorsMap());
		}
		return model ;
	}
}
