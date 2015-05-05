# Editing connection configuration #

To edit connection parameters, go to the 'Connections' menu item and select 'Manage connections'. When a new window pops up, selected the connection you want to edit.

The following tabs are available:

  * Connectivity
  * Authentication
  * Last Will
  * Publications
  * Subscriptions
  * Log
  * Other

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.1_edit-connections.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.1_edit-connections.png)

Each of the tabs is described in sections below.

### Connectivity ###

This tab contains the most important MQTT-related settings:

  * URI(s) of the broker(s)/server(s) to use
  * Client ID (keep it unique to avoid disconnections)
  * Connection timeout (in seconds)
  * Connection keep alive (in seconds)
  * Clean session flag (tick it to start the connection with a clean session)
  * Reconnect on failure (tick it to automatically reconnect when a connection failure occurs)
  * Reconnection interval (in milliseconds; how often to try reconnect)
  * Resubscribe on failure (tick it to automatically resubscribe on regained connection following a failure)

The broker URI(s) can be provided in the following formats:

  * _protocol://host_ (this assumes the default port; e.g. tcp://iot.eclipse.org, tcp://127.0.0.1, ssl://localhost)
  * _protocol://host:port_ (e.g. tcp://iot.eclipse.org:1883 or ssl://127.0.0.1:8883)

Additionally, the GUI allows you to enter the URI without the _tcp://_ string. When the entered URI is detected not to start with _tcp://_ or _ssl://_ then the URI is automatically prefixed with _tcp://_.

To provide more than one URI, separate them with ";".

### Authentication ###

If you want to use an MQTT username and password for your broker, put it here.

### Last Will ###

On this tab you can defined the MQTT Last Will Testament.

### Publications ###

This tab allows you to:

  * ('Topics' sub-tab) Define publication topics that are going to appear in the topic combo box on the 'Publish message' pane.
  * ('Scripts' sub-tab) Define a directory with publication scripts (leave empty to use the mqtt-spy's home directory)
  * ('Scripts' sub-tab) Define a list of specific scripts (this is where you can set the script to auto-start and repeat itself if necessary)

### Subscriptions ###

This tab allows you to predefine your subscriptions. On the 'Topics' sub-tab define your subscriptions, associated scripts (which run when a message is received), the Quality of Service, and whether to create a tab when opening the connection. The 'Search' sub-tab allows you to specify a directory for search scripts.

### Predefining publication and subscription topics ###

In order to predefine publication or subscription topics, selected the connection you want to edit, and then go to the 'Publications' or 'Subscriptions' tab.

To create a new topic, click "Add topic" button. This will create a new entry in the table (e.g. "/samplePublication/"). Double click on this new table row. An edit box should appear, in which you can type in the topic string. When happy with the typed in value, hit Enter on the keyboard - this is required by the table to submit this new value. Once submitted, mqtt-spy will check if this is any different than the previous value, and if it is, the "Apply" button should become enabled. Click on it to save the changes to the configuration file.

### Log ###

This is where you configure message logging. For more information see the [Message Log](MessageLog.md) wiki.

### Other ###

This tab contains mostly usability-related settings. These are:

  * Auto-open at start-up (whether to open the connection tab at start-up)
  * Auto-connect when opened (whether to connect to broker when the connection tab is opened)
  * Auto-subscribe when opened (whether to subscribe to all subscriptions with 'Create tab' flag set when the connection tab is opened)
  * Message content formatter (whether to use a predefined message content formatter for all received messages)
  * Min messages per topic (when deleting old messages, what's the minimum to keep per topic)
  * Max messages stored (the maximum number of messages to keep in memory before deleting the old ones)

# Other configuration-related information #

### XML structure (version 0.0.5) ###

The configuration file used by mqtt-spy is XML-based. It has the following structure:

  * Connectivity
    * Connection (0 or more)
      * Name (name of the connection)
      * Server URI (connection URI for the MQTT broker)
      * Client ID (the client ID to be used when connecting to the broker)
      * Auto connect (whether to automatically connect once the configuration file has been loaded)
      * Clean session (true or false; optional; if not specified, the default is used)
      * Connection timeout (in seconds; optional; if not specified, the default is used)
      * Keep alive interval (in seconds; optional; if not specified, the default is used)
      * Formatter ID (optional)
      * Maximum messages stored (optional; default is 5000)
      * Publication (0 or more)
        * Topic (publication topic)
      * Subscription (0 or more)
        * Topic (subscription topic)
        * QoS (quality of service; optional; default = 0)
        * Create tab (true or false; optional; default = false; whether to create a tab for this subscription topic)
  * Formatting (optional)
    * Formatter (0 or more)
      * Name (descriptive name of the formatter)
      * ID (unique identifier, referenced from the connection definition)
      * Function (1 or more)
        * Conversion
        * Substring conversion
        * Substring replace
        * Substring extract
        * Character replace

Where not specified, the default values for connection properties are in line with http://www.eclipse.org/paho/files/javadoc/org/eclipse/paho/client/mqttv3/MqttConnectOptions.html.

### Sample configuration file (version 0.0.5) ###

```
<?xml version="1.0" encoding="UTF-8"?>
<config:MqttSpyConfiguration
   xmlns:config="http://baczkowicz.pl/mqtt-spy-configuration"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

   <Connectivity>

 	<Connection>
 		<Name>user1@localhost</Name>
		<ServerURI>tcp://127.0.0.1:1883</ServerURI>
		<ClientID>user1</ClientID>
		<AutoConnect>false</AutoConnect>
 	</Connection>

	<Connection>
 		<Name>user2@localhost</Name>
		<ServerURI>tcp://localhost</ServerURI>
 		<ClientID>user2</ClientID>
		<AutoConnect>true</AutoConnect>
		<CleanSession>true</CleanSession>
		<ConnectionTimeout>5</ConnectionTimeout>
		<KeepAliveInterval>10</KeepAliveInterval>

		<!-- This is an optional field; put the ID of the decoder -->
		<Formatter>base64-body-decoder</Formatter>

		<!-- Default value is 5000 -->
		<MaxMessagesStored>1000</MaxMessagesStored>

		<Publication topic="/pubtest1/" />
		<Publication topic="/pubtest2/"/>
		<Publication topic="/pubtest3/"/>
		
		<Subscription topic="/test1/" />
		<Subscription topic="/test2/" qos="1" />
		<Subscription topic="/test3/" qos="0" createTab="true" />
	</Connection>

   </Connectivity>

   <Formatting>
	<Formatter>
		<!-- Descriptive name of the formatter -->
		<Name>White space to hex</Name>

   		<!-- This should be unique within the file; referenced from the connection definition -->
   		<ID>whitespace-to-hex</ID>
   			
   		<Function>
   			<CharacterReplace>
   				<Format>HexEncode</Format>			
   				<CharacterRangeFrom>0</CharacterRangeFrom>
   				<CharacterRangeTo>32</CharacterRangeTo>
   				<WrapCharacter>-</WrapCharacter>
   			</CharacterReplace>
   		</Function>
   			
	</Formatter>

	<Formatter>
   		<!-- Descriptive name of the formatter -->
   		<Name>Base64 body decoder</Name>
   			
		<!-- This should be unique within the file; referenced from the connection definition -->
   		<ID>base64-body-decoder</ID>
   			
   		<!-- Convert the base64 content to plain text -->
   		<Function>
   			<SubstringConversion>
   				<StartTag><![CDATA[<Body>]]></StartTag>
   				<EndTag><![CDATA[</Body>]]></EndTag>
   				<KeepTags>true</KeepTags>
   				<Format>Base64Decode</Format>
   			</SubstringConversion>   				
   		</Function>
   			
   		<!-- Ignore anything else but the Body tags and their content, then remove these tags, leaving only the content of the Body -->
   		<Function>
   			<SubstringExtract>
   				<StartTag><![CDATA[<Body>]]></StartTag>
   				<EndTag><![CDATA[</Body>]]></EndTag>
   				<KeepTags>false</KeepTags>
   			</SubstringExtract>
   		</Function>
   			
	</Formatter>
   </Formatting>

</config:MqttSpyConfiguration>
```