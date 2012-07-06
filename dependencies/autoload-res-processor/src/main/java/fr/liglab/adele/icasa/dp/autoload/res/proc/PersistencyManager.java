/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fr.liglab.adele.icasa.dp.autoload.res.proc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistencyManager {
	
	private final File _root;

	public PersistencyManager(File root) {
		_root = root;
	}

	/**
	 * Stores a resource content.
	 * 
	 * @param name Name of the resource.
	 * @param configs List of <code>AutoConfResource</code>s representing the specified resource.
	 * @throws IOException If the resource could not be stored.
	 */
	public void store(String name, InputStream is) throws IOException {
		
		File targetDir = _root;
		name = name.replaceAll("/", File.separator);
		if (name.startsWith(File.separator)) {
			name = name.substring(1);
		}
		int lastSeparator = name.lastIndexOf(File.separator);
		File target = null;
		if (lastSeparator != -1) {
			targetDir = new File(targetDir, name.substring(0, lastSeparator));
			targetDir.mkdirs();
		}
		target = new File(targetDir, name.substring(lastSeparator + 1));

		FileUtil.copyContent(is, target);
	}

	/**
	 * Deletes a resource.
	 * 
	 * @param name Name of the resource.
	 * @throws IOException If the resource could not be deleted.
	 */
	public void delete(String name) throws IOException {
		name = name.replace('/', File.separatorChar);
		File target = new File(_root, name);
		if (!target.delete()) {
			throw new IOException("Unable to delete file: " + target.getAbsolutePath());
		}
		while (target.getParentFile().list().length == 0 && !target.getParentFile().getAbsolutePath().equals(_root.getAbsolutePath())) {
			target = target.getParentFile();
			target.delete();
		}
	}

	/**
	 * Loads a stored resource.
	 * 
	 * @param name Name of the resource.
	 * @return List of <code>AutoConfResource</code>s representing the specified resource, if the resource is unknown an empty list is returned.
	 * @throws IOException If the resource could not be properly read.
	 */
	public InputStream load(String name) throws IOException {
		name = name.replaceAll("/", File.separator);
		File resourcesFile = new File(_root, name);
		if (resourcesFile.exists()) {
			return new FileInputStream(resourcesFile);
		}
		
		return null;
	}
	
	/**
	 * Deletes all stored resources.
	 * 
	 * @throws IOException If not all resources could be deleted.
	 */
	public void deleteAll() throws IOException {
		FileUtil.deleteDir(_root);
		_root.mkdir();
	}
}
