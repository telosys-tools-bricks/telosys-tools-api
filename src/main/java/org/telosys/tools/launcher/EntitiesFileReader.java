/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.launcher;

import java.util.List;

import org.telosys.tools.commons.TextFileReader;

/**
 * Entities text file reader <br>
 * 
 * @author L. Guerin
 *
 */
public class EntitiesFileReader extends TextFileReader {
	
    /**
     * Constructor 
	 * @param filePath 
	 */
	public EntitiesFileReader(String filePath) {
		super(filePath);
	}

	@Override
	protected void parseLine(String line, List<String> lines) {
		String trimmedLine = line.trim() ;
		if ( trimmedLine.length() > 0 ) {
			// Not a comment line ?
			if ( ! trimmedLine.startsWith("#")) {
				lines.add(trimmedLine);
			}
		}
	}

}
