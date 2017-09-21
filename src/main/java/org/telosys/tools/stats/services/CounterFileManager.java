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
package org.telosys.tools.stats.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;

/**
 * A counter stored in a file <br>
 * e.g. "generations.counter",  ...
 *
 */
public class CounterFileManager {
	
	private final String  fileName ;
	private final boolean createParentFolder ;
	
	public CounterFileManager(String fileName) {
		super();
		this.fileName = fileName;
		this.createParentFolder = false ;
	}

	public CounterFileManager(String fileName, boolean createParentFolder) {
		super();
		this.fileName = fileName;
		this.createParentFolder = createParentFolder ;
	}

	public String getFileName() {
		return fileName ;
	}
	
	/**
	 * Increments the counter value stored in the file 
	 * @return the counter value after increment
	 */
	public int incrementCounter() {
		int counter = readCounter() ;
		counter++;
		writeCounter(counter); // lock ?
		return counter ;
	}

	/**
	 * Reads the counter value stored in the file 
	 * @return
	 */
	public int readCounter() {
	    FileReader in;
		try {
			in = new FileReader(fileName);
		} catch (FileNotFoundException e) {
			return 0; 
		}

		String line = "0";
	    BufferedReader br = new BufferedReader(in);
	    try {
			line = br.readLine();
		} catch (IOException e) {
			throw new RuntimeException("Cannot read file '" + fileName + "' (IOException)", e);
		}
	    finally {
		    FileUtil.close(br);
		    FileUtil.close(in);
	    }
		return StrUtil.getInt(line, 0);
	}

	protected void writeCounter(int counter) {
		
		//--- Check file existence in order to create parent folder if necessary
		File file = new File(fileName);
		if ( ! file.exists() ) {
			if ( this.createParentFolder ) {
				FileUtil.createParentFolderIfNecessary(file);
			}
		}

		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(fileName);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write in file '" + fileName + "' (IOException)", e);
		}
		
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		try {
			bufferedWriter.write( ""+counter );
		} catch (IOException e) {
			throw new RuntimeException("Cannot write in file '" + fileName + "' (IOException)", e);
		}
	    finally {
		    FileUtil.close(bufferedWriter);
		    FileUtil.close(fileWriter);
	    }
	}
}
