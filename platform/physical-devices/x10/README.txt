====

      Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
      Group Licensed under the Apache License, Version 2.0 (the "License");
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.
====
To be able to use X10 devices, you must:
1 - install prolific PL2303 driver from Prolific web site (http://www.prolific.com.tw/eng/).
2 - add rose bundles (core + json configurator) to your icasa distribution.
3 - Add the following bundles (maven artifacts) to your icasa distribution:

<!-- X10 -->
<dependency>
   <groupId>org.rxtx</groupId>
   <artifactId>org.rxtx</artifactId>
   <version>2.2.7</version>
</dependency>
	
<dependency>
   <groupId>fr.liglab.adele.m2mappbuilder</groupId>
   <artifactId>x10.discovery.rose</artifactId>
   <version>0.0.1-SNAPSHOT</version>
</dependency>
	
<dependency>
   <groupId>fr.liglab.adele.m2mappbuilder</groupId>
   <artifactId>x10.presence.sensor</artifactId>
   <version>0.0.1-SNAPSHOT</version>
</dependency>

<dependency>
   <groupId>fr.liglab.adele.m2mappbuilder</groupId>
   <artifactId>x10.presence.sensor.importer</artifactId>
   <version>0.0.1-SNAPSHOT</version>
</dependency>

<dependency>
   <groupId>fr.orange</groupId>
   <artifactId>fr.orange.x10</artifactId>
   <version>0.0.1-SNAPSHOT</version>
</dependency>

4 - Add a rose configuration file named "rose-conf-x10.json" in the fine le install directory 
(icasa distribution usually use load directory) with following content:

{
	"machine" : {
		"id" : "x10-devices-machine",
		"host" : "localhost",
		
		"connection" : [
				{ "in" : 
					{"endpoint_filter" : "(service.imported.configs=x10)", "importer_filter" : "(rose.protos.configs=x10)"}
				}
		],

		"component" : [
			{ 
				"factory" : "x10.rose.discovery",
				"properties" : { "instance.name" : "x10.rose.discovery-1",
				                 "x10.module.port" : "COM4",
								 "x10.module.type" : "cm11a"
				}
			},
			{ 
				"factory" : "x10.presence.sensor.importer",
				"properties" : { "instance.name" : "x10.presence.sensor.importer-1" }
			}
		],
	}
}

5 - Update com port id into this file accroding to your cable com port.
To find it, go to windows device manager, check com port id (you can see it in the device name, should look like Prolific USB-to-Serial Comm Port (COM3) if it is on COM3 port).
