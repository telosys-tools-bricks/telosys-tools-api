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

import org.telosys.tools.dsl.DslModelErrors;

/**
 * @author Laurent GUERIN
 * 
 */
public class TelosysModelException extends Exception { // TelosysToolsException {
	
	private static final long serialVersionUID = 1L;
	
	private final File modelFile ;
	
//    private final Map<String,List<String>> parsingErrors ;
    private final transient DslModelErrors dslModelErrors ;

    /**
     * Constructor
     * @param modelFile
     * @param message
     * @param dslModelErrors
     */
//    public TelosysModelException(File modelFile, String message, Map<String,List<String>> parsingErrors) {
    public TelosysModelException(File modelFile, String message, DslModelErrors dslModelErrors) {
        super(message);
        this.modelFile = modelFile ;
        this.dslModelErrors = dslModelErrors ;
    }

    public TelosysModelException(File modelFile, String message) {
        super(message);
        this.modelFile = modelFile ;
        this.dslModelErrors = new DslModelErrors();
    }
    
    public File getModelFile() {
    	return modelFile ;
    }
    
    public boolean hasParsingErrors() {
    	return dslModelErrors != null && dslModelErrors.getNumberOfErrors() > 0 ;
    }
//    public Map<String, List<String>> getParsingErrors() {
//    	return parsingErrors ;
//    }
    public DslModelErrors getDslModelErrors() {
    	return dslModelErrors ;
    }
}