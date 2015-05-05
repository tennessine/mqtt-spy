# Outputs #

By default, there are two outputs: console (standard output) and a log file.

## Console (standard output) ##

To be able to see the console output, you need to run your mqtt-spy from a command line.

## Log file ##

The log file used by mqtt-spy is called mqtt-spy.log and is housekept (max. 2 files, 10MB each).

# Logging received and published messages #

For performance reasons (e.g. when 100s or 1000s messages per second are received), received messages are not logged. To change that, simply open the jar file with an archive editor, and change the "connectivity.handlers" logging level from INFO to DEBUG in log4j.properties, i.e. `log4j.logger.pl.baczkowicz.mqttspy.connectivity.handlers=DEBUG`

Published messages are logged by default.

# Implementation #

mqtt-spy uses log4j as its slf4j implementation.

# Changing logging configuration #

To change the logging settings you can either edit the log4j.properties file embedded in the jar or point at a different one.