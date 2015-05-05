## Overview ##

mqtt-spy & mqtt-spy-daemon provide a mechanism to script message publications and processing of received messages. This is done by running a script written in JavaScript (that is compliant with the Java Nashorn specification).

## Publishing messages ##

All following sections apply to both mqtt-spy and mqtt-spy-daemon, unless stated otherwise.

#### Configuring publication scripts in mqtt-spy ####

There are three ways to configure mqtt-spy with publications scripts:

<ol type='1'>
<blockquote><li>With auto-discovery (finds all ".js" files in given directory)</li>
<ol type='a'>
<blockquote><li>Using the mqtt-spy's home directory</li>
<li>Using a user-defined directory</li>
</blockquote></ol>
<li>Using a predefined list of scripts (this supports auto-start and repeated execution)</li>
</ol></blockquote>

In the first case (1a), put your script file into the mqtt-spy's home directory (the same where the configuration and statistics files are stored by default) and make sure your script has ".js" suffix/extension.

The second case (1b) involves some configuration, but allows you to specify a custom directory, so that you can separate out your publications scripts from other files. To do that, follow these steps:

  * From the menu, select Connections -> Manage connections
  * Select the connection you want to configure the script for
  * Go to the Publications -> Scripts tab
  * In "Directory with publication scripts" field, put the absolute path to the scripts directory (e.g. /home/kamil/mqtt-spy/scripts)
  * Put the file to the configured directory (make sure your script has ".js" suffix/extension)
  * Click Apply to save the settings
  * Re-open the connection

In the last case (2), you need to configure individual scripts. This has got one main benefit - you can define the exact list of scripts you want to have available, and you can configure each script to auto-start and to repeat its execution. To do all that, follow these steps:

  * From the menu, select Connections -> Manage connections
  * Select the connection you want to configure the script for
  * Go to the Publications -> Scripts tab
  * Click "Add script" (a new entry will appear in the table)
  * Double click on the table row you want to edit
  * Put the exact location of your script (including the full path)
  * Press ENTER (to submit the value to the table)
  * Set auto-start and repeat flags if desired (note: you might want the connection to auto-open and auto-connect - see the Other tab)
  * Click "Apply" button to save your changes to the configuration file
  * Re-open the connection

In all cases, upon (re)opening the connection, you should see the script on the list of scripts in Scripted publications pane.

For scripts that haven't been started, do the following:

  * Right click on the script's table row to bring up the context menu
  * Click Start

If the script is valid, the status of the script should change accordingly (if you see a Failed state, check your mqtt-spy.log to see what could be causing this).

#### Configuring subscription scripts in mqtt-spy ####

Each connection allows you to predefine your subscriptions. One of the parameters that can be defined for a subscription is 'Script'. This is the location of the script to run when a message on the given subscription is received. Please note that the provided location needs to be the full path (e.g. /home/kamil/mqtt-spy/scripts/subscriptions/on\_message\_received.js).

#### Configuring publication scripts mqtt-spy-daemon ####

Publication scripts are defined as 'BackgroundScripts' in mqtt-spy-daemon. For more details, see the [Daemon](Daemon.md) wiki.

#### Configuring publication scripts mqtt-spy-daemon ####

Subscription scripts are defined as 'scriptFile' attribute of a subscription. For more details, see the [Daemon](Daemon.md) wiki.

### Interface between mqtt-spy/mqtt-spy-daemon and scripts ###

Most interaction between a script and mqtt-spy/mqtt-spy-daemon is done via the `mqttspy` object.

#### Publishing messages ####

In order to publish messages, the script must call an appropriate method on the `mqttspy` object. There are two methods available for publishing messages:

  * `mqttspy.publish(topic, messagePayload);`
  * `mqttspy.publish(topic, messagePayload, qos, retained);`

#### Publishing parametrised messages ####

_This feature is available from v0.1.3 of mqtt-spy._

mqtt-spy, apart from supporting standard manual publications, allows you to define your message parameters (i.e. topic, payload, QoS, retained) and then pass it to a chosen script for publication.

This allows you to enrich the message, e.g. by adding a timestamp, sequence number, digital signature, or defining your custom message envelope or encoding.

The defined message is available as a `message` object in your script, so for instance you can do:

`mqttspy.publish(message.getTopic(), message.getPayload() + i, 0, false);`, where 'i' is a loop parameter. For more details see the 'Interacting with message objects' section.

#### Supporting methods ####

The `mqttspy` object exposes additional helper methods:

  * `var result = mqttspy.execute(command);` - executes a command line command (e.g. call a system command)
  * `var success = mqttspy.instantiate(className);` - instantiates a class with the given package name and class name, e.g. by passing `com.test.MyClass`, the following object `com_test_MyClass` becomes available, e.g. `var myResult = com_test_MyClass.myCustomMethod();`

#### Message log replay ####

_This feature is available from v0.0.3 of mqtt-spy-daemon and v0.1.2 of mqtt-spy._

In order to replay messages from a mqtt-spy message log file, use the `messageLog` object. The available methods are:

  * readFromFile (takes the location of the message log file; returns number of messages available)
  * getMessageCount (gets the available number of messages)
  * start (starts the message log time updater and sets the start position to the first message)
  * stop (stops the time updater)
  * setSpeed (sets replay speed, e.g. 1 is the normal speed (as in the message log), 2 is twice the normal, 0.5 is half the normal)
  * isReadyToPublish (checks if a message with the given index is ready to be published; 0 is the first message)
  * getMessage (retrieves message with the given index; 0 is the first message)

See the samples section for a replay script example.

#### Script health reporting ####

mqtt-spy keeps an eye on the running scripts, detecting whether the script is publishing messages or reporting it is not frozen. If the script is to perform regular long sleeps, use the `mqttspy.setScriptTimetout(timeoutInMilliseconds);` method to increase the default value of 5 seconds (5000ms).

If the script is to perform other actions than message publications, and they might take a long time, call `mqttspy.touch();` to inform mqtt-spy that the script is alive and running OK.

### Interacting with the script from the UI (mqtt-spy only) ###

Each script can be started from the UI. Once running, a request to stop the script can be issued. That request is only going to be handled if the script calls methods the react to the interrupted status of a thread they are running in. One of those methods is `Thread.sleep(timeToSleep);`.

### Samples ###

#### Simplest publication script ####

```
mqttspy.publish("/mqtt-spy/script/sample1/", "hello :)");
```

#### Handling stop requests (mqtt-spy only) ####

In order to stop a script after a stop request or an error, follow this example.

```
// Wrap the script in a method, so that you can do "return false;" in case of an error or stop request
function publish()
{
	var Thread = Java.type("java.lang.Thread");

	for (i = 0; i < 10; i++)
	{
		mqttspy.publish("/mqtt-spy/script/sample2/", "hello" + i);

		// Make sure you wrap the Thread.sleep in try/catch, and do return on exception
		try 
		{
			Thread.sleep(1000);
		}
		catch(err) 
		{
			return false;				
		}
	}

	// This means all OK, script has completed without any issues and as expected
	return true;
}

publish();
```

#### Message log replay ####

```
function replay()
{
	// Get the number of available messages (0 when run for the first time)
	var messageCount = messageLog.getMessageCount();
	
	// If repeat = true, only read the message log once
	if (messageCount == 0)
	{
		messageCount = messageLog.readFromFile("/home/kamil/mqtt-spy-daemon.messages");		
		messageLog.setSpeed(2);		
	}
	
	// If there are messages to replay...
	if (messageCount > 0)
	{
		// Start the message log time updater...
		messageLog.start();
			
		var Thread = Java.type("java.lang.Thread");	
	
		// For all messages
		for (i = 0; i < messageCount; i++)
		{
			// Wait until this message is ready to be published
			while (!messageLog.isReadyToPublish(i))		
			{
				try 
				{
					Thread.sleep(10);
				}
				catch(err) 
				{
					return false;				
				}
			}
			
			// When ready, publish the message
			mqttspy.publish(messageLog.getMessage(i).getTopic(), messageLog.getMessage(i).getMessage(), 0, false);				
		}
	}
	else
	{
		logger.warn("No messages available");
	}
	
	return true;
}

replay();

```

#### Publishing current time ####

```
function publishTime()
{
	var Thread = Java.type("java.lang.Thread");
	var Date = Java.type("java.util.Date");
	var SimpleDateFormat = Java.type("java.text.SimpleDateFormat");
	
	var TIME_FORMAT_WITH_SECONDS = "HH:mm:ss";
	var TIME_WITH_SECONDS_SDF = new SimpleDateFormat(TIME_FORMAT_WITH_SECONDS);

	while (true)
	{
		var currentTime = TIME_WITH_SECONDS_SDF.format(new Date());
		
		mqttspy.publish("/time/", currentTime, 0, false);

		// Sleep for 1 second and handle a stop request 
		try 
		{
			Thread.sleep(1000);				
		}
		catch(err) 
		{
			return false;				
		}
		
		// Keep mqtt-spy informed the script is still running
		mqttspy.touch();
	}

	return true;
}

publishTime();
```

## Processing received messages ##

_This feature is available from v0.0.4 of mqtt-spy-daemon and v0.1.3 of mqtt-spy._

There are three main uses for processing received messages with a script:

  * Auto-reply, based on the content of the received message
  * Reformatting the message before displaying it or logging it (e.g. removing custom message envelope or encoding)
  * Triggering other actions (e.g. diagnostic logging, executing system commands)

The script to run is configured per subscription, and passed to the message as `receivedMessage` object.

### Samples ###

#### Auto-reply ####

```
function publishReply()
{
	mqttspy.publish(
			"/reply", "<simpleReply><topic>" + receivedMessage.getTopic() + "</topic>" 
			+ "<payload><![CDATA[" + receivedMessage.getPayload() + "]]></payload>"
			+ "</simpleReply>", 0, false);
		
	return true;
}

publishReply();
```

#### Modify/reformat payload ####

```
function modify()
{
	receivedMessage.setPayload("<wrapped>" + receivedMessage.getPayload() + "- modified :)</wrapped>");
	
	return true;
}

modify();
```

## Search messages ##

For details see the [Message Search](MessageSearch.md) wiki.

## Interacting with message objects ##

_This feature is available from v0.0.4 of mqtt-spy-daemon and v0.1.3 of mqtt-spy._

Whether accessing message parameters before publication (`message` object), or processing a received message (`receivedMessage` object), the following methods are available:

  * `String getTopic();` - retrieves the message topic
  * `String getPayload();` - retrieves the message payload as string
  * `void setPayload(final String payload);` - sets the message payload (so that for instance you can reformat the message before displaying it or logging)
  * `int getQoS();` - gets the quality of service
  * `boolean isRetained();` - gets the retained flag

## Diagnostic logging ##

mqtt-spy & mqtt-spy-daemon also expose their logger (log4j) to their scripts. The object is simply called `logger`, and exposes all standard slf4j/log4j methods, e,g. `logger.info("Reading XML from a file...");`

## Help ##

If you need further help, search for "java nashorn" and you should get plenty of examples. If still struggling, tweet to @mqtt\_spy.