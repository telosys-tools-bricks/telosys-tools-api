package org.telosys.tools.api;

import java.io.File;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.dsl.loader.ModelLoader;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.repository.persistence.PersistenceManager;
import org.telosys.tools.repository.persistence.PersistenceManagerFactory;

public class GenericModelLoader {
	
	private final TelosysToolsCfg   _telosysToolsCfg ;

	public GenericModelLoader(TelosysToolsCfg telosysToolsCfg) {
		this._telosysToolsCfg = telosysToolsCfg ;
	}
	
	//-----------------------------------------------------------------------------------------------------
	// Model loading DSL or Database model 
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Loads a 'model' from the given model file name <br>
	 * The model name can be a 'database model' file name or a 'DSL model' file name <br>
	 * e.g. : 'books.dbrep' or 'books.model'  <br>
	 * 
	 * @param modelFileName the model file name in the current project models 
	 * @return
	 * @throws TelosysToolsException
	 */
	public Model loadModel(final String modelFileName) throws TelosysToolsException {
		String modelAbsolutePath = FileUtil.buildFilePath( _telosysToolsCfg.getModelsFolderAbsolutePath(), modelFileName);
		File file = new File(modelAbsolutePath);
		return loadModel(file);
	}
	
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
				if ( file.getName().endsWith(".dbrep") || file.getName().endsWith(".dbmodel") ) {
					//--- This file is supposed to be a db model file
					return loadDatabaseModel(file) ;
				}
				else if ( file.getName().endsWith(".model") ) {
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
//	/**
//	 * Loads a 'database model' from the given model file name
//	 * @param dbModelFileName the short file name in the current project models ( eg 'mymodel.dbrep' ) 
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	private Model loadDatabaseModel(final String dbModelFileName) throws TelosysToolsException {
//		String dbrepAbsolutePath = FileUtil.buildFilePath( _telosysToolsCfg.getModelsFolderAbsolutePath(), dbModelFileName);
//		File repositoryFile = new File(dbrepAbsolutePath);
//		return loadDatabaseModel( repositoryFile ) ;
//	}
	
	private Model loadDatabaseModel( File repositoryFile ) throws TelosysToolsException {
		//System.out.println("Load repository from file " + repositoryFile.getAbsolutePath());
		PersistenceManager persistenceManager = PersistenceManagerFactory.createPersistenceManager(repositoryFile);
		return persistenceManager.load();
	}

	//-----------------------------------------------------------------------------------------------------
	// DSL model loading 
	//-----------------------------------------------------------------------------------------------------
//	/**
//	 * Loads a 'DSL model' from the given model name
//	 * @param modelName the name of the model to be loaded ( e.g. 'bookstore' or 'employees' ) 
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	private Model loadDslModel(final String modelName) throws TelosysToolsException {
//		String modelFileAbsolutePath = FileUtil.buildFilePath( _telosysToolsCfg.getModelsFolderAbsolutePath(), modelName+".model");
//		return loadDslModel( new File(modelFileAbsolutePath) );
//	}
	
	private Model loadDslModel(final File modelFile) throws TelosysToolsException {
		ModelLoader modelLoader = new ModelLoader();
		return modelLoader.loadModel( modelFile );
	}
	
}
