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
import java.util.List;
import java.util.Map;

import org.telosys.tools.commons.TelosysToolsException;

/**
 * @author Laurent GUERIN
 * 
 */
public class TelosysModelException extends TelosysToolsException // Exception 
{
	private static final long serialVersionUID = 1L;
	
	private final File modelFile ;
	
//    private final Map<String,String> parsingErrors ;
    private final Map<String,List<String>> parsingErrors ; // v 3.2.2

    public TelosysModelException(File modelFile, String message) {
        super(message);
        this.modelFile = modelFile ;
        this.parsingErrors = null ;
    }

//    public TelosysModelException(File modelFile, String message, Map<String,String> parsingErrors) {
    public TelosysModelException(File modelFile, String message, Map<String,List<String>> parsingErrors) { // v 3.2.2
        super(message);
        this.modelFile = modelFile ;
        this.parsingErrors = parsingErrors ;
    }
    
    public File getModelFile() {
    	return modelFile ;
    }
    
    public boolean hasParsingErrors() {
    	return parsingErrors != null ;
    }
//    public Map<String, String> getParsingErrors() {
    public Map<String, List<String>> getParsingErrors() { // v 3.2.2
    	return parsingErrors ;
    }
}