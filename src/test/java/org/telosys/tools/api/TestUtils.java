package org.telosys.tools.api;

import java.io.File;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.generic.model.Model;

public class TestUtils {

	/**
	 * @param modelShortPath file path in "src/test/resources" ( eg "db-models/bookstore.dbrep" )
	 * @return
	 * @throws TelosysToolsException
	 */
	public static final Model loadModelWithGenericModelLoader(String modelShortPath) throws TelosysToolsException {
		File modelFile = new File(TestsEnv.SRC_TEST_RESOURCES + modelShortPath);

		GenericModelLoader loader = new GenericModelLoader();
		return loader.loadModel(modelFile);
	}

	/**
	 * @param modelFileName just the short file name ( eg "foo.model" or "foo.dbrep" )
	 * @param telosysToolsCfg
	 * @return
	 * @throws TelosysToolsException
	 */
	public static final Model loadModelWithGenericModelLoader(String modelFileName, TelosysToolsCfg telosysToolsCfg) throws TelosysToolsException {
		
		String modelAbsolutePath = FileUtil.buildFilePath( telosysToolsCfg.getModelsFolderAbsolutePath(), modelFileName);
		File modelFile = new File(modelAbsolutePath);

		GenericModelLoader loader = new GenericModelLoader();
		return loader.loadModel(modelFile);
	}

}
