package org.telosys.tools.api;

import java.io.File;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.env.TelosysToolsEnv;

public class TestsEnv {
	
	public static final  String SRC_TEST_RESOURCES = "src/test/resources/" ;
		
	public static void copyDbModelFile(String projectFolderAbsolutePath, String dbrepFileName) throws Exception {
		System.out.println("initializing 'DB model' in project from file '" + dbrepFileName +"'" );
		
		// original file is stored in "src/test/resources/"
		String originalFileAbsolutePath = buildTestResourceAbsolutePath(FileUtil.buildFilePath("db-models", dbrepFileName ));
		File dbrepInputFile = new File( originalFileAbsolutePath ) ;
		
		// destination folder
		String projectModelsFolder = FileUtil.buildFilePath(projectFolderAbsolutePath, TelosysToolsEnv.getModelsFolder() );
		File destDir = new File(projectModelsFolder);
		
		System.out.println(" copy " + dbrepInputFile ); 
		System.out.println("   to " + destDir ); 
		FileUtil.copyFileInDirectory(dbrepInputFile, destDir, true);
	}

	public static void copyDslModelFiles(String projectFolderAbsolutePath, String dslModelName) throws Exception {
		System.out.println("initializing 'DSL model' mane = '" + dslModelName +"'" );
		copyDslModelFolder(projectFolderAbsolutePath, dslModelName);
	}

	private static void copyDslModelFolder(String projectFolderAbsolutePath, String dslModelFolder) throws Exception {
		System.out.println("Copy DSL model folder '" + dslModelFolder +"'" );
		
		// original file is stored in "src/test/resources/"
		String originalFolderAbsolutePath = buildTestResourceAbsolutePath(FileUtil.buildFilePath("dsl-models", dslModelFolder ));
		File sourceDir = new File( originalFolderAbsolutePath ) ;
		
		// destination folder
		String projectModelsFolder = FileUtil.buildFilePath(projectFolderAbsolutePath, TelosysToolsEnv.getModelsFolder() );
		File destDir = new File(FileUtil.buildFilePath(projectModelsFolder, dslModelFolder) );
		
		System.out.println(" copy " + sourceDir ); 
		System.out.println("   to " + destDir ); 
		FileUtil.copyFolderToFolder(sourceDir, destDir, true);
		System.out.println("Copy OK." ); 
	}

	public static void copyTemplatesFiles(String projectFolderAbsolutePath, String bundleName) throws Exception {
		System.out.println("initializing 'templates' in project from bundle folder '" + bundleName +"'" );
		
		// original file is stored in "src/test/resources/"
		String originalFolderAbsolutePath = buildTestResourceAbsolutePath(FileUtil.buildFilePath("templates", bundleName ));
		File sourceDir = new File( originalFolderAbsolutePath ) ;
		
		// destination folder
		String projectModelsFolder = FileUtil.buildFilePath(projectFolderAbsolutePath, TelosysToolsEnv.getTemplatesFolder() );
		File destDir = new File(FileUtil.buildFilePath(projectModelsFolder, bundleName) );
		
		System.out.println(" copy " + sourceDir ); 
		System.out.println("   to " + destDir ); 
		FileUtil.copyFolderToFolder(sourceDir, destDir, true);
	}

	/**
	 * Returns the absolute path for the given test file name without checking existence
	 * @param fileName
	 * @return
	 */
	private static final String buildTestResourceAbsolutePath(String fileName) {
		File file = new File(SRC_TEST_RESOURCES + fileName);
		return file.getAbsolutePath();
	}

}
