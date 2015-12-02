package org.telosys.tools.nrt;

import org.telosys.tools.api.TelosysProject;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.generator.GeneratorException;

public class TelosysGeneratorNRTRunner {
	
	private final static String WORKSPACE_FULL_PATH = "D:/workspaces-TELOSYS-TOOLS-NREG-TESTS/wks43-nreg-TT211-TT300" ;

	private static TelosysProject getTelosysProject(String projectName) {
		String projectFullPath = FileUtil.buildFilePath(WORKSPACE_FULL_PATH, projectName);
		return new TelosysProject(projectFullPath);
	}

	public static void main(String[] args) {
		
		TelosysGeneratorNRT telosysGeneratorNRT = new TelosysGeneratorNRT("GENERATED_FILES") ;

		int differencesCount = 0 ;
		
		try {
//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "basic-templates-TT210");
//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "database-doc-bundle-TT210"); // difference in .js files
			
//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "persistence-jpa-TT210-R2");
//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "persistence-native-jdbc-TT211");
//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "persistence-spring-jdbc-TT211");
//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "persistence-springdatajpa-TT210-R2");
			
//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "service-jpa-TT210-R2");
//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "service-springdatajpa-TT210-R2");

//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "spring-data-rest-templates-TT210");

			differencesCount += generateAndCompare(	telosysGeneratorNRT, "front-angularjs-TT210");
//			differencesCount += generateAndCompare(	telosysGeneratorNRT, "front-springmvc-TT210-R2");
			
		} catch (GeneratorException e) {
			System.out.println("----- ((( ERROR ))) -----");
			System.out.println("GeneratorException : " + e.getMessage());
		} catch (TelosysToolsException e) {
			System.out.println("----- ((( ERROR ))) -----");
			System.out.println("TelosysToolsException : " + e.getMessage());
		} catch (Throwable e) {
			System.out.println("----- ((( ERROR ))) -----");
			System.out.println("Exception : " + e.getMessage());
		}
		
		System.out.println("-------------------------------");
		System.out.println("Total count of differences : " + differencesCount );
		System.out.println("-------------------------------");
	}

	private static int generateAndCompare(TelosysGeneratorNRT telosysGeneratorNRT, String bundleName) 
			throws TelosysToolsException, GeneratorException {
		int differencesCount = 0 ;
		// the project name is the bundle name
//		differencesCount += telosysGeneratorNRT.generateAndCompare(	getTelosysProject(bundleName),
//				"StudentsAndTeachers-H2.dbrep", bundleName);
		differencesCount += telosysGeneratorNRT.generateAndCompare(	getTelosysProject(bundleName),
				"Bookstore-Derby.dbrep", bundleName);
		return differencesCount ;
	}
}
