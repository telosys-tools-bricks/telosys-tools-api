package org.telosys.tools.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.commons.bundles.TargetDefinition;
import org.telosys.tools.commons.bundles.TargetsDefinitions;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.generator.task.ErrorReport;
import org.telosys.tools.generator.task.GenerationTaskResult;
import org.telosys.tools.generic.model.Model;

public class TelosysGlobalEnvTest {
	
	@Test
	public void testGitHubUser() throws Exception {

		assertNull( TelosysGlobalEnv.getGitHubUser() );

		TelosysGlobalEnv.setGitHubUser("aaa", "bbb");
		assertEquals("aaa", TelosysGlobalEnv.getGitHubUser());

		TelosysGlobalEnv.clearGitHubUser();
		assertNull(TelosysGlobalEnv.getGitHubUser());
	}
}
