# Overview #

_This feature is available from v0.1.5 of mqtt-spy._

When performing a message search in mqtt-spy, one of the three modes can be used:

  * Searching for given text in message payload (default)
  * Searching with a predefined script (directory for search scripts is configured in connection's Subscriptions tab)
  * Searching with an inline script

For details on scripting see the Scripting wiki.

# Searching with predefined script #

The following is an example of a script for searching for a message that contains "test" in the topic and "temp" in the payload.
```
function search()
{
	if (message.getTopic().contains("test") && message.getPayload().contains("temp"))
	{
		return true;
	}
	
	return false;
}
search();
```

Note: for formatted payload, use getFormattedPayload(); you can still
use the getPayload() for the original (non-formatted) payload.

# Searching with inline script #

Inline scripts are defined in the text field provided. To search for a message containing "test" in the topic and "temp" in the payload use the following:
```
topic.contains("test") && content.contains("temp")
```

or without the helper fields:
```
message.getTopic().contains("test") && message.getPayload().contains("temp")
```

Inline scripts are expected to be a boolean expression, that will be evaluated to either true of false. In fact, they given JavaScript expression is simply wrapped in the following JavaScript code, and then executed:
```
function search()
{
	var payload = message.getPayload();
	var formattedPayload = message.getFormattedPayload();
	var content = formattedPayload;
	var topic = message.getTopic();
	var qos = message.getQoS(); 

	if (INLINE_SCRIPT)
	{
		return true;
	}
	
	return false;
}
search();
```

Note: try to use the `content` field for searching the payload (e.g. `content.contains("test")`) - this will work regardless of any formatting you use and will match what you actually see on the screen. You can still
use the getPayload() for the original (non-formatted) payload.