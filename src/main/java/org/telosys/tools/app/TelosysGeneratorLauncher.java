package org.telosys.tools.app;

import java.io.File;

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

public class TelosysGeneratorLauncher {
	
	private final static String PROJECT_ROOT_PATH = "D:/workspaces-TELOSYS-TOOLS-NREG-TESTS/wks43-nreg-TT211-TT300" ;

	private static String getProjectAbsolutePath(String projectName) {
		return FileUtil.buildFilePath(PROJECT_ROOT_PATH, projectName);
	}

	private static String getDestinationFolderAbsolutePath(TelosysProject telosysProject) {
		return FileUtil.buildFilePath(telosysProject.getProjectFolder(), "GENERATED_FILES");
	}
	
	public static void main(String[] args) throws TelosysToolsException, GeneratorException {
		launch(	"front-angularjs-TT210", 
				"StudentsAndTeachers-H2.dbrep",
				"front-angularjs-TT210");
	}
	
	public static void launch(String projectName, String modelName, String bundleName ) throws TelosysToolsException, GeneratorException {
		
		TelosysProject telosysProject = new TelosysProject(getProjectAbsolutePath(projectName));
		
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
		Model model = telosysProject.loadModel(modelName);
		System.out.println("Model loaded : " + model.getEntities().size() + " entities");
		
		System.out.println("Launching generation for bundle ");
		GenerationTaskResult result = telosysProject.launchGeneration(model, bundleName);
		System.out.println("Normal end of generation : " + result.getNumberOfFilesGenerated() + " files generated");
		
	}

}
