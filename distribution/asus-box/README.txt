This distribution includes following components:
- fake application manager
- device manager
- upnp set top box support
- x10 presence sensor support
- tikitag reader support
- application and device dashboard (+ widgets for setopbox, tikitag and x10 presence sensor)
- house simulator

Before building it, you must change com port id in save-conf-files/rose-conf-x10.json file:
1 - Plug your USB to serial cable for LM11 module to any usb port.
2 - Open windows device manager, check com port id (you can see it in the device name, should look like Prolific USB-to-Serial Comm Port (COM3) if it is on COM3 port).
3 - Finally, modify save-conf-files/rose-conf-x10.json file line "x10.module.port" : "COM3" with your corresponding com port.

To build it, execute mvn install.
To launch it, execute iCASA.bat or iCasa file depending on your operating system.
Then launch a web browser on http://localhost:8080/simulator and http://localhost:8080/dashboards