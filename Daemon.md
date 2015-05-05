# Overview #

mqtt-spy-daemon is a Java-based command line tool. This means you can run it for example on a server that has only SSH access. The main features are:

  * high availability (multiple server URIs and reconnection)
  * driven by an XML configuration file
  * writing received messages to a message log file (for creating an audit of all received messages)
  * running scripts against received messages
  * running background scripts for publishing messages
  * replaying previously recorded messages

## Running mqtt-spy-daemon ##

To start the mqtt-spy-daemon, run the following command (this assumes you've already installed the appropriate JRE/JDK):

`java -jar mqtt-spy-daemon-0.0.3-jar-with-dependencies.jar "/home/kamil/mqtt-spy-daemon-configuration.xml"`

## High availability ##

There are two ways to achieve high availability with mqtt-spy-daemon.

First is to configure multiple server URIs - for detailed description see http://www.eclipse.org/paho/files/javadoc/org/eclipse/paho/client/mqttv3/MqttConnectOptions.html#setServerURIs%28java.lang.String[]%29.

Second is to configure reconnection and resubscription. With reconnection configured, upon connection failure, mqtt-spy-daemon will try to re-establish its broker connection to the defined server URI(s). In some cases, this will require resubscription to all topics. This can be enabled by setting the resubscription flag in the configuration.

## Configuration file ##

The following is a sample configuration file for v0.0.3:
```
<?xml version="1.0" encoding="UTF-8"?>
<mqttspydc:MqttSpyDaemonConfiguration xmlns:mqttspydc="http://baczkowicz.pl/mqtt-spy/daemon/configuration" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <Connection>
  
	<!-- For details on multiple server URIs see http://www.eclipse.org/paho/files/javadoc/org/eclipse/paho/client/mqttv3/MqttConnectOptions.html#setServerURIs%28java.lang.String[]%29 -->
	<ServerURI>tcp://localhost:2000</ServerURI>
	<!-- Server URI can be supplied without the tcp:// prefix or the port number (default is 1883) -->
	<ServerURI>localhost</ServerURI>
  			
	<ClientID>mqtt-spy-daemon</ClientID>
			
	<UserCredentials>
		<Username>test2</Username>
		<!-- Password is base64 encoded -->
		<Password>dGVzdDI=</Password>
	</UserCredentials>
		
	<!-- Payload is base64 encoded -->
	<LastWillAndTestament topic="/ltw/mqtt-spy-daemon" qos="0" retained="false">R29pbmcgb2ZmbGluZS4gQnllIGJ5ZS4uLg==</LastWillAndTestament>
									
	<CleanSession>true</CleanSession>
	<ConnectionTimeout>10</ConnectionTimeout>
	<KeepAliveInterval>10</KeepAliveInterval>
			
	<ReconnectionSettings>
		<!-- How long (in ms) to wait after previous connection attempt before trying to connect again -->
		<RetryInterval>5000</RetryInterval>				
		<!-- Whether to resubscribe to all topics when the connection is regained -->
		<Resubscribe>true</Resubscribe>
	</ReconnectionSettings>
			
	<!-- 
		For binary payloads, change it to XML_WITH_ENCODED_PAYLOAD.
				
		To log QoS, Retained flag, connection name or subscription, use:
		logQos="true" logRetained="true" logConnection="true" logSubscription="true" 
	-->
	<MessageLog>XML_WITH_PLAIN_PAYLOAD</MessageLog>
			
	<Subscription topic="/test/#" qos="0" />
	<!-- Use the script file for things like auto-reply or additional logging -->
	<Subscription topic="/home/#" qos="0" scriptFile="/home/kamil/reply.js"/>
			 
	<!-- Use these for publishing messages -->	
	<BackgroundScript>
		<File>/home/kamil/bedroom.js</File>
	</BackgroundScript>			
	<BackgroundScript repeat="true">
		<File>/home/kamil/replay.js</File>
	</BackgroundScript>
	
  </Connection>
  
</mqttspydc:MqttSpyDaemonConfiguration>
```

## Running mode ##

From v0.0.4, running mode can be specified with the `RunningMode` parameter. The possible values are:

  * `CONTINUOUS` - for running mqtt-spy-daemon until you kill it (e.g. if you want it to log messages over a long period)
  * `SCRIPTS_ONLY` - for running mqtt-spy-daemon as long as any publication scripts are running

The default is CONTINUOUS.

## Message log ##

By default all received messages are logged to a file called `mqtt-spy-daemon.messages` (the file is housekept - max. 5 files, 10MB each).

To change these settings simply edit the log4j.properties file included in the JAR.

To disable message logging, in the configuration file set `MessageLog` to `DISABLED`.

#### Message log format ####

For details on the message log format, see the [Message Log](MessageLog.md) wiki.

To change the format, set the `MessageLog` property to `XML_WITH_ENCODED_PAYLOAD` or `XML_WITH_PLAIN_PAYLOAD` respectively.

You can enable or disable the following parameters: QoS, retained flag, connection name and subscription. By default, they are turned off. The flags are: `logQos="true" logRetained="true" logConnection="true" logSubscription="true"`.

## Running scripts against received messages (e.g. for auto-reply or reformatting) ##

To run a script (works only with Java 8) following a receipt of a message, configure the script location with the subscription, e.g.:

`<Subscription topic="/test/#" qos="0" scriptFile="/home/kamil/reply.js" />`

The following is a sample script that produces a reply to be sent:

```
function publishReply()
{
	mqttspy.publish(
			"/reply", "<simpleReply><topic>" + receivedMessage.getTopic() + "</topic>" 
			+ "<payload><![CDATA[" + receivedMessage.getMessage() + "]]></payload>"
			+ "</simpleReply>", 0, false);

	return true;
}

publishReply();
```

If you want to modify the message payload, but log the original message, set the `MessageLog` element's `logBeforeScripts` flag to true. The default is false.

For more details on scripting see the [Scripting](Scripting.md) wiki.

## Running scripts to publish messages ##

To configure a publication script, add the following to the connection definition:
```
<BackgroundScript><File>/home/kamil/bedroom.js</File></BackgroundScript>
```

To repeat the script once it has completed, use the `repeat="true"` flag on the `BackgroundScript` element.

For more details on scripting see the [Scripting](Scripting.md) wiki.