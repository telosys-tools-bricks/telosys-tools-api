package org.telosys.tools.api;

import java.io.File;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.env.TelosysToolsEnv;

public class TestsEnv {
	
//	public final static String REPO_FILENAME = "DERBY-Tests-Jan-2014-10.dbrep" ; 
//	public final static String BUNDLE_NAME   = "basic-templates-TT210" ; 

	public  final static String SRC_TEST_RESOURCES = "src/test/resources/" ;
	
	/***
	private final static String TARGET_TESTS_TMP_DIR = "target/tests-tmp/" ;

	private static File createVoidFolder(String folderName ) {
		File folder = new File(TARGET_TESTS_TMP_DIR + folderName);
		//--- Clean if exists
		if ( folder.exists() ) {
			if ( folder.isDirectory() ) {
				DirUtil.deleteDirectory(folder);
			}
			else {
				throw new RuntimeException("'"+ folder.getName() + "' is not a folder");
			}
		}
		//--- Create
		if ( folder.mkdirs() ) {
			return folder ;
		}
		else {
			throw new RuntimeException("Cannot create folder '"+ folder.getName() + "' ");
		}
	}

	public static String createProjectFolder(String folderName ) {
		File file = createVoidFolder( folderName ); 
		return file.getAbsolutePath();
	}
***/
	
	public static void copyDbModelFile(String projectFolderAbsolutePath, String dbrepFileName) throws Exception {
		System.out.println("initializing 'DB model' in project from file '" + dbrepFileName +"'" );
		
		// original file is stored in "src/test/resources/"
		String originalFileAbsolutePath = buildTestResourceAbsolutePath(FileUtil.buildFilePath("db-models", dbrepFileName ));
		File dbrepInputFile = new File( originalFileAbsolutePath ) ;
		
		// destination folder
		String projectModelsFolder = FileUtil.buildFilePath(projectFolderAbsolutePath, TelosysToolsEnv.getInstance().getModelsFolder() );
		File destDir = new File(projectModelsFolder);
		
		System.out.println(" copy " + dbrepInputFile ); 
		System.out.println("   to " + destDir ); 
		FileUtil.copyToDirectory(dbrepInputFile, destDir, true);
	}

	public static void copyDslModelFiles(String projectFolderAbsolutePath, String dslModelName) throws Exception {
		System.out.println("initializing 'DSL model' mane = '" + dslModelName +"'" );
		//copyDslModelFile(projectFolderAbsolutePath, dslModelName+".model");
		//copyDslModelFolder(projectFolderAbsolutePath, dslModelName+"_model");
		copyDslModelFolder(projectFolderAbsolutePath, dslModelName);
	}
//	private static void copyDslModelFile(String projectFolderAbsolutePath, String dslModelFile) throws Exception {
//		System.out.println("Copy 'DSL model' file '" + dslModelFile +"'" );
//		String originalFileAbsolutePath = buildTestResourceAbsolutePath(FileUtil.buildFilePath("dsl-models", dslModelFile ));
//		File dbrepInputFile = new File( originalFileAbsolutePath ) ;
//		
//		// destination folder
//		String projectModelsFolder = FileUtil.buildFilePath(projectFolderAbsolutePath, TelosysToolsEnv.getInstance().getModelsFolder() );
//		File destDir = new File(projectModelsFolder);
//		
//		System.out.println(" copy " + dbrepInputFile ); 
//		System.out.println("   to " + destDir ); 
//		FileUtil.copyToDirectory(dbrepInputFile, destDir, true);
//	}
	private static void copyDslModelFolder(String projectFolderAbsolutePath, String dslModelFolder) throws Exception {
		System.out.println("Copy DSL model folder '" + dslModelFolder +"'" );
		
		// original file is stored in "src/test/resources/"
		String originalFolderAbsolutePath = buildTestResourceAbsolutePath(FileUtil.buildFilePath("dsl-models", dslModelFolder ));
		File sourceDir = new File( originalFolderAbsolutePath ) ;
		
		// destination folder
		String projectModelsFolder = FileUtil.buildFilePath(projectFolderAbsolutePath, TelosysToolsEnv.getInstance().getModelsFolder() );
		File destDir = new File(FileUtil.buildFilePath(projectModelsFolder, dslModelFolder) );
		
		System.out.println(" copy " + sourceDir ); 
		System.out.println("   to " + destDir ); 
		FileUtil.copyFolder(sourceDir, destDir, true);
		System.out.println("Copy OK." ); 
	}

	public static void copyTemplatesFiles(String projectFolderAbsolutePath, String bundleName) throws Exception {
		System.out.println("initializing 'templates' in project from bundle folder '" + bundleName +"'" );
		
		// original file is stored in "src/test/resources/"
		String originalFolderAbsolutePath = buildTestResourceAbsolutePath(FileUtil.buildFilePath("templates", bundleName ));
		File sourceDir = new File( originalFolderAbsolutePath ) ;
		
		// destination folder
		String projectModelsFolder = FileUtil.buildFilePath(projectFolderAbsolutePath, TelosysToolsEnv.getInstance().getTemplatesFolder() );
		File destDir = new File(FileUtil.buildFilePath(projectModelsFolder, bundleName) );
		
		System.out.println(" copy " + sourceDir ); 
		System.out.println("   to " + destDir ); 
		FileUtil.copyFolder(sourceDir, destDir, true);
	}

	/**
	 * Returns the absolute path for the given test file name without checking existence
	 * @param fileName
	 * @return
	 */
	private final static String buildTestResourceAbsolutePath(String fileName) {
		File file = new File(SRC_TEST_RESOURCES + fileName);
		return file.getAbsolutePath();
	}
	

//	public static void initBundle(TelosysProject telosysProject, String bundleName) throws TelosysToolsException, Exception {
//		System.out.println("initializing bundle in project templates '" + bundleName +"'" );
//		// original file is stored in "src/test/resources/"
//		String originalFolderAbsolutePath = buildTestResourceAbsolutePath(FileUtil.buildFilePath("templates", bundleName ));
//		File sourceFolder = new File( originalFolderAbsolutePath ) ;
//		// destination folder
//		TelosysToolsCfg telosysToolsCfg = telosysProject.loadTelosysToolsCfg();
//		File destFolder = new File(telosysToolsCfg.getTemplatesFolderAbsolutePath(bundleName));
//		
//		
//		//--- Copy 
//		System.out.println(" from " + sourceFolder ); 
//		System.out.println("   to " + destFolder ); 
//		FileUtil.copyFolder(sourceFolder, destFolder, true);
//	}

}
