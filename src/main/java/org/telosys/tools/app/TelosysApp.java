package org.telosys.tools.app;

import java.io.File;
import java.util.List;

import org.telosys.tools.api.TelosysProject;
import org.telosys.tools.commons.DirUtil;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.task.GenerationTaskResult;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.nrt.FileComparator;
import org.telosys.tools.nrt.LineComparatorForGeneratedFiles;

public class TelosysApp {
	
	private final static String PROJECT_ROOT_PATH = "D:/workspaces-TELOSYS-TOOLS-NREG-TESTS/wks43-nreg-TT211-TT300" ;

	private static String getProjectAbsolutePath(String projectName) {
		return FileUtil.buildFilePath(PROJECT_ROOT_PATH, projectName);
	}

	private static String getDestinationFolderAbsolutePath(TelosysProject telosysProject) {
		return FileUtil.buildFilePath(telosysProject.getProjectFolder(), "GENERATED_FILES");
	}
	
	private static void compare(String refFileName, String newFileName) {
		LineComparatorForGeneratedFiles lineComparator = new LineComparatorForGeneratedFiles();
		FileComparator fileComparator = new FileComparator(
				new File(refFileName), new File(newFileName), lineComparator);
		StringBuffer sb = new StringBuffer();
		if ( fileComparator.compare(sb) != 0 ) {
			System.out.println("DIFFERENT : ");
			System.out.println(sb);
		}
		else {
			System.out.println("INDENTICAL");
		}
	}
	
	public static void main(String[] args) throws TelosysToolsException, GeneratorException {
		
		TelosysProject telosysProject = new TelosysProject(getProjectAbsolutePath("basic-templates-TT210"));
		
		System.out.println("Updating destination folder...");
		TelosysToolsCfg telosysToolsCfg = telosysProject.loadTelosysToolsCfg();
		telosysToolsCfg.setSpecificDestinationFolder(getDestinationFolderAbsolutePath(telosysProject));
		telosysProject.saveTelosysToolsCfg(telosysToolsCfg);
		System.out.println("Update done : " + telosysToolsCfg.getDestinationFolderAbsolutePath() );
		
		File destinationFolder = new File(telosysToolsCfg.getDestinationFolderAbsolutePath());
		System.out.println("Cleaning destination folder...");
		DirUtil.deleteDirectory( destinationFolder );
		System.out.println("Cleaning done.");
		
		System.out.println("Loadind model...");
		Model model = telosysProject.loadModel("StudentsAndTeachers-H2.dbrep");
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
		
		System.out.println("Launching generation for bundle ");
		GenerationTaskResult result = telosysProject.launchGeneration(model, "basic-templates-TT210");
		System.out.println("Normal end of generation : " + result.getNumberOfFilesGenerated() + " files generated");
		
		List<String> files = DirUtil.getDirectoryFiles(destinationFolder, true);
		System.out.println(files.size() + " file(s) found in the directory ");
		System.out.println("Comparison...");
		for ( String generatedFileName : files ) {
			System.out.println("----- ");
			System.out.println(" . " + generatedFileName );
			String relativeFileName = generatedFileName.substring( telosysToolsCfg.getDestinationFolderAbsolutePath().length() + 1 );
			System.out.println("   ( " + relativeFileName + " )");
			String referenceFileName = FileUtil.buildFilePath(telosysToolsCfg.getProjectAbsolutePath(), relativeFileName);
			System.out.println(" . " + referenceFileName );
			compare(referenceFileName, generatedFileName) ;
		}
	}

}
