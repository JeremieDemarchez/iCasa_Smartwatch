/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.medical.application.device.web.common.util;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.osgi.framework.Bundle;

import nextapp.echo.app.ResourceImageReference;


/**
 * @author Gabriel
 *
 */
public class BundleResourceImageReference extends ResourceImageReference {

	/**
    * 
    */
   private static final long serialVersionUID = 3152621476928787288L;

   /**
    * Bundle that has the image as resource
    */
   private Bundle bundle;
   
   /**
    * Size of buffer used for reading image data from CLASSPATH and writing
    * it to <code>OutputStream</code>s.
    */
   private static final int BUFFER_SIZE = 4096;
   
   public BundleResourceImageReference(String resource, Bundle bundle) {
   	super(resource);
   	this.bundle = bundle;
   }
   
   @Override
   public void render(OutputStream out) throws IOException {
      InputStream in = null;
      byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead = 0;
      String resource = getResource();
      try {
          URL url = bundle.getResource(resource);
          if (url!=null)
         	 in = url.openStream();
          if (in == null) {
              throw new IllegalArgumentException("Specified resource does not exist: " + resource + ".");
          }
          do {
              bytesRead = in.read(buffer);
              if (bytesRead > 0) {
                  out.write(buffer, 0, bytesRead);
              }
          } while (bytesRead > 0);
      } finally {
          if (in != null) { try { in.close(); } catch (IOException ex) { } } 
      }
   }
   
}
