/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.device.bathroomscale.rest.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;


import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;


@Component(name="hl7-file-install")
@Instantiate(name="hl7-file-install-0")
@Provides
public class HL7MessageFileInstaller implements ArtifactInstaller, IHL7MessageFileInstaller {

	private Set<File> files = new HashSet<File>();
	
	@Override
	public boolean canHandle(File file) {
		return file.getName().endsWith(".hl7");
	}

	@Override
	public void install(File file) throws Exception {
		files.add(file);
	}

	@Override
	public void uninstall(File file) throws Exception {
		files.remove(file);
	}

	@Override
	public void update(File file) throws Exception {
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.device.bathroomscale.rest.impl.IHL7MessageFileInstaller#getFileContent(java.lang.String)
	 */
	@Override
	public String getFileContent(String fileName) {
		
		File file = getFile(fileName);
		if (file == null)
			return null;
		
		try {
			return readFile(file);
		} catch (Exception e) {
			return null;
		}
	}
	
	private File getFile(String fileName) {
		for (File file : files)
			if (file.getName().equals(fileName))
				return file;
		return null;
	}
	
	private static String readFile(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			return Charset.forName("UTF-8").decode(buffer).toString();
		}
		finally {
			stream.close();
		}
	}
}
