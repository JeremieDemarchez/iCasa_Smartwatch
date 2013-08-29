ZigBee driver port can be set when launching java:
```
java -Dzigbee.driver.port=/dev/USBtty0 -jar bin/felix.jar
```
Or in the felix config file:
```
zigbee.driver.port=/dev/USBtty0
```

By default, it uses COM3 port for windows.