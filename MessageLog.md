# Introduction #

mqtt-spy and mqtt-spy-daemon allow you to create message logs. These a dedicated log files that only contain received messages (as opposed to diagnostic log, which contains all sorts of information about the internal workings of the application).

The main benefit of having a message log is that you can then perform analysis of the received messages, even hours or days after being the actual message being received. You can either you mqtt-spy to browse a message log offline, and then use all the filtering and searching functionality, or write your own scripts or tools to analyse them. Each logged message contains an index (or ID), a timestamp and all the usual message properties (e.g. topic, QoS, payload).

Further to that, you can also replay those messages at different speeds by running a replay script in mqtt-spy or mqtt-spy-daemon.

# Creating message log (mqtt-spy) #

There are two ways to create a message log file:

  * record live messages (as they arrive)
  * export received messages

### Recording live messages (mqtt-spy) ###

To enable the recording, go to your connection properties (see below), select the 'Log' tab, and set the logging mode to other than DISABLED. Then in the message log location put the desired location of your message log file (you can use either an absolute or a relative path).

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.6_log-settings.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.6_log-settings.png)

To change the size and number of message logs (default is 10MB x 5 files), edit the log4j.properties file packaged inside the mqtt-spy jar file.

### Exporting already received messages ###

To export already received messages, select the appropriate subscription tab and then click on the 'Tools' button. Select 'Export' and then choose whether you want the current message or all messages, and whether to copy it/them to the clipboard or a message log file (selected in the next step). If you select the "Message log" option, you will be prompted to select the file to save to.

# Message log replay (mqtt-spy & mqtt-spy-daemon) #

For details on how to perform the message log replay see the Scripting wiki.

# Off-line analysis (mqtt-spy) #

To browse a message log file, from the main menu select 'Logger' and then 'Open message log for off-line analysis'. When prompted, selected the message log file (normally with .messages extension). Once the message file has been loaded, a new tab will appear. Logged messages can be now browsed in the same way as during normal broker connection, including filtering and searching.

# Message log format (mqtt-spy & mqtt-spy-daemon) #

Messages are written to a text file, one message per line. Each line is an XML document with `MqttMessage` element. The value of the element is the payload of the message. The attributes describe various properties of the message, e.g. its ID (set to 1 at start-up), timestamp (in milliseconds), topic, quality of service, retained flag, connection name, subscription and encoded flag. Some of these fields are optional and might not be included.

Message payload can be either written as Base64 encoded string (e.g. when binary messages are handled), or as plain text wrapped in XML CDATA tags if necessary.

### Sample message log ###

```
<MqttMessage id="1" timestamp="1415830549180" topic="/home/bedroom/current" qos="0" retained="false" ><![CDATA[<temp>20.5</temp><energy>110</energy>]]></MqttMessage>
<MqttMessage id="2" timestamp="1415830550249" topic="/home/bedroom/current" qos="0" retained="false" ><![CDATA[<temp>20.8</temp><energy>102</energy>]]></MqttMessage>
<MqttMessage id="3" timestamp="1415830551255" topic="/home/bedroom/current" qos="0" retained="false" ><![CDATA[<temp>20.9</temp><energy>104</energy>]]></MqttMessage>
<MqttMessage id="4" timestamp="1415830552256" topic="/home/bedroom/current" qos="0" retained="false" ><![CDATA[<temp>20.6</temp><energy>108</energy>]]></MqttMessage>
```