# Introduction #

To run mqtt-spy you need to have an appropriate version of Java, either installed or present on your operating system.

The latest version of mqtt-spy requires at least Java Runtime Environment (JRE) 8 Update 20. You can download it from the Oracle website. At the point of writing this Wiki page the best place to get it from is:

http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html

You can also use OpenJDK version 8, but you need to remember to install JavaFX packages as well. Otherwise it probably won't work.

To check the version of your installed Java use the following command:

```
java -version
```

If it says the version is 1.8.0\_20 or later, you are ready to go - see the 'Running with the default Java' section.

If you have an older version, and you want to keep it as the default one, or you don't want to install Java, then - see the 'Running with custom Java version' section.

For instructions in French see the 'Espionner des thèmes MQTT' blog post kindly written by Bernard - http://blogs.media-tips.com/bernard.opic/2015/01/25/espionner-des-themes-mqtt/.

# Running with default Java #
### Ways of starting mqtt-spy ###

  * Double click on the jar
  * Start from the command line

### Starting mqtt-spy by double clicking on the jar (Windows/Linux) ###

You can simply **double click** on the mqtt-spy jar file to start it, assuming a double click on a jar file means 'start with Java 8'.

If this doesn't work, try starting it from the command line.

### Starting mqtt-spy from the command line (Windows/Linux) ###

To start mqtt-spy from the command line, using the default version of Java, use the following command:

`java -jar mqtt-spy-0.1.5-jar-with-dependencies.jar`

Note: this assumes your jar file is in your current folder/directory from which you are running this command.

# Running with custom Java version #

If for any reason you don't want to install Java 8 as your default Java, just download the tar.gz distribution from the provided link (see the Introduction).

Unpack it to your chosen location (in the following example it is a JDK unpacked to /usr/local/java/).

Then, run the java executable (found in the bin directory of the JRE) with the mqtt-spy jar, e.g.:

  * `/usr/local/java/jdk1.8.0_25/jre/bin/java -jar mqtt-spy-0.1.5-jar-with-dependencies.jar` (Linux)

# Running with additional JARs on the classpath #

If you need additional packages on the classpath (for instance to do custom encoding/decoding), you can start mqtt-spy with additional JARs (or other resources) on the classpath.

Below are examples of how to do it in Windows and Linux. Make sure you run this from the directory where your mqtt-spy jar file and the lib folder are.

### Windows (with default Java) ###

```
java -cp mqtt-spy-0.1.5-jar-with-dependencies.jar;lib\xmlsec-1.5.5.jar;lib\commons-logging-1.1.1.jar
pl.baczkowicz.mqttspy.Main
```

### Linux (with custom Java) ###

```
/usr/local/java/jre1.8.0_25/bin/java -cp
"mqtt-spy-0.1.5-jar-with-dependencies.jar:lib/xmlsec-1.5.5.jar:lib/commons-logging-1.1.1.jar"
pl.baczkowicz.mqttspy.Main
```

# First steps after start-up #

When you start your mqtt-spy for the first time, you are likely to see a screen similar to this:

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.5_after-start-up.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.5_after-start-up.png)

The first message is indicating that no configuration file has been found - simply click on this message to resolve this. A pop-up will appear asking you what to do:

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.5_config-creator.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.5_config-creator.png)

At this point, either best to create a configuration file with sample content, or an empty one.

If you haven't decided to have sample content, either click on the message saying that you haven't got any connections configured, or from the main menu select 'Connections -> New connection'. Once you have put all the broker details, hit 'Apply' to save this to the configuration file.

Alternatively, from the main menu select 'Connections -> Manage connections' to revise the sample configuration.

When ready, either click on 'Open connection' in the 'Manage connections' window, or on the control panel click the connection name to open/connect.

If all is fine, your connection tab will go green indicating you have successfully connected to an MQTT broker!

# If still having problems... #

If the above doesn't work, get in touch on Twitter (@mqtt\_spy) or put a comment below.