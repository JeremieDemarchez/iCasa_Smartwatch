/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package test.component.handler;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;

public class MyListener implements DeviceListener<GenericDevice> {

	@Override
   public void deviceAdded(GenericDevice arg0) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void deviceEvent(GenericDevice arg0, Object arg1) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void devicePropertyAdded(GenericDevice arg0, String arg1) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void devicePropertyModified(GenericDevice arg0, String arg1, Object arg2, Object arg3) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void deviceRemoved(GenericDevice arg0) {
	   // TODO Auto-generated method stub
	   
   }

}
