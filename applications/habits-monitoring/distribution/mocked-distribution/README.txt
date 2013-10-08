====

      Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
      Licensed under the Apache License, Version 2.0 (the "License");
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.
====
For any distribution:

To build it, execute mvn install.
To launch it, execute PRODUCT.bat or PRODUCT file depending on your operating system.
Then launch a web browser on http://localhost:8080/simulator and http://localhost:8080/dashboards

Configuration of actimetry server location :

1 - edit ActimetryRestServiceClient-1.cfg file in load directory and modify ip and port of server url.
2 - rebuild the distribution.
3 - launch the distribution.
4 - connect to http://serverIp:serverPort/actimetryBrick to access to actimetry ihm (you must replace serverIp and serverPort by your server configuration).