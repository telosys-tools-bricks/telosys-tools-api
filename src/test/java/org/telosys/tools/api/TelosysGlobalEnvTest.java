package org.telosys.tools.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
