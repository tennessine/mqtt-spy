# Top features #

  * support for multiple connections (a tab of each MQTT broker connection)
  * connection management (driven by an XML-based configuration file - for more details see [Configuration](Configuration.md))
  * high availability (multiple server URIs & automatic reconnection)
  * message publication (manual, parametrised and scripted - using JavaScript)
  * remembering and restoring recently published messages
  * multiple subscriptions & subscription summary (a tab for each subscription)
  * summary of all topics received - with search and filtering
  * handling over 1000 messages per second (tried on a standard laptop)
  * browsing, searching and filtering for received messages
  * support for all MQTT message/connection properties (e.g. QoS, Retained, LWT, User Authentication)
  * message content formatting and conversions (e.g. Hex or Base64 decoding)
  * perspectives & customisable pane visibility
  * diagnostic logging, including published and received messages (for more details see [Logging](Logging.md))
  * message log - recording, exporting, replaying and offline browsing
  * checking for updates at start-up

### Features to be added in the future ###

  * SSL/TSL support

# Control panel #

The mqtt-spy's control panel gives you a quick overview of the following items:
  * configuration - when no configuration present click to open the configuration wizard
  * list of configured connections and their status - click on the relevant button to perform the action
  * update status - checks if there are any new versions of mqtt-spy available
  * processing statistics & link to the UNICEF fundraising page
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_control-panel.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_control-panel.png)

# Main menu #

From the main menu you can perform the following actions:
  * reload a configuration file (Configuration -> Open file)
  * run the configuration creator / restore defaults (Configuration -> Restore defaults)
  * create a new connection (Connections -> New connection)
  * manage connections - create, edit, copy, delete (Connections -> Manage connections)
  * open message log (Logger -> Open message log for off-line analysis)
  * change perspective - hides/shows panes or detailed view (Window -> Perspectives)
  * open a Base64 converter (Window -> Converter)
  * open the wiki pages (Help -> Wiki)
  * open the UNICEF fundraising page (Help -> Donate to UNICEF)
  * open the project's page (Help -> About)

### Managing connections / configuration ###

For information about all the various configuration parameters see the [Configuration](Configuration.md) wiki.


![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.1_edit-connections.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.1_edit-connections.png)

# Connection tabs #

Each connection tab divided into multiple panes:
  * Publish message
  * Scripted publications
  * Define new subscription
  * Subscriptions and received messages
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_connection-tab.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_connection-tab.png)

To connect, disconnect or close the tab, use the connection tab's context menu. You can also toggle the visibility of all the panes.
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_connection-tab-menu.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_connection-tab-menu.png)

Additionally, the connection tab's context menu contains a shortcut to broker's statistics. This is compatible with brokers supporting the $SYS topics - e.g. Mosquitto, HiveMQ or RSMB.
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_stats.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_stats.png)

### Scripted publications ###

Apart from publishing individual messages, you can also automate message publications by using scripted publications. For more details see the [Scripting](Scripting.md) wiki.
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_scripted-publications.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_scripted-publications.png)

### Subscriptions ###

After typing in or selecting the subscription topic, click on the Subscribe button or press Enter.

Once you've created a new subscription, a tab will be created. If you right-click on the tab, the subscription's context menu will appear.
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_subscription-menu.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_subscription-menu.png)

### Subscriptions and received messages ###

Subscriptions and received messages are the main focus for mqtt-spy. There are a number of ways to find and browse wanted messages: subscribe to the relevant topics or if you don't know which topics to subscribe, just try with "#" or "/#".
mqtt-spy will create a list of all the topics it receives messages on.

In the 'Received messages summary' pane, each row in the table represents a single topic, together with the payload of the last message, its time and total message count.

When dealing with hundreds of messages per second, and hundreds or thousands of topics, it might be difficult to find the messages you need. To help with that, you can search for topics containing the given text.
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_message-summary-topic-search.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_message-summary-topic-search.png)
Once you know what topics you might be interested in, use the topic filtering functionality (the 'browse' column & context menu). This will allow you to browse only the messages on the selected topics.
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_message-summary-topic-filter.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_message-summary-topic-filter.png)

For all actions see the table's context menu.
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_message-summary-menu.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_message-summary-menu.png)

### Message browsing - mouse actions ###

When hovering over the message index field, the mouse scroll can be used for increasing or decreasing the currently viewed message number.

### Message browsing - keyboard actions ###

Once the focus has been given to the message index field, the following actions can be triggered:

  * Toggle "Show latest on new" by pressing the Space key
  * Show the latest (first) message by pressing the Home key
  * Show the oldest (last) message by pressing the End key
  * Increasing the message index by 1 by pressing the Up Arrow key
  * Increasing the message index by 5 by pressing the Page Up key
  * Decreasing the message index by 1 by pressing the Down Arrow key
  * Decreasing the message index by 5 by pressing the Page down key

### Message publishing - keyboard actions ###

When the focus is on the publication topic field, the following actions can be triggered:

  * Publish message by pressing the Enter key
  * Restore the X most recent message (ALT + X, where X is 1, 2, 3, 4, 5, 6, 7, 8, 9 and 0; 1 is for the most recent message; 2 for the second most recent; 0 for the 10th most recent)

### Message search ###

When you want to find a message with specific content, use the message search functionality. Clicking on the 'Search' button will open a new search window for that subscription tab. Note that any topic filtering will be maintained.
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_message_content_search.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.0_message_content_search.png)

### Message storage ###

To help you with analysing MQTT traffic, mqtt-spy keeps an in-memory history of received messages. These messages are stored in a list, in which new messages are always added at beginning of the list (that is with index of 1). When a new message is added, all previously received messages are moved by one towards the end of the list.

In order to stop this list growing out of control, there is a limit on how many messages that list can contain. When this limit is reached, the oldest messages from the end of the list will be removed. As this is done once a second, you might see the message count going up (over the configured limit), and then down to the configured value. This significantly improves performance when receiving 100s or 1000s messages per second.

This limit ('Max messages stored') can be defined individually for each connection ('Other' tab of your connection settings).

If however, there are topics with lots of repetitive publications, they might force other useful messages out of the list by pushing them towards the end and as a result, getting them removed. To avoid this, there is another configuration parameter called 'Min messages per topic', which says how many messages to keep for each topic, before any message on that topics gets removed.

This means that in environments with 100s or 1000s messages per second, you can still catch and analyse occasional publications on some less used topics.

### Message log ###

For details on creating, replaying or browsing the message log see the [Message Log](MessageLog.md) wiki.

### Exporting data ###

There are a number of ways to export data collected by mqtt-spy:

  * Automatically export messages as [Message Log](MessageLog.md)
  * Export individual topics or message payload - right click the subscription tab or message summary table rows
  * Export browsed messages or topics - use the 'Tools' menu button on the 'Subscriptions and received messages' pane; then select 'Export'

### Pan and zoom in charts ###

To pan around the chart, use right click drag or CTRL/CMD + left click.

For zooming, use the following:

  * Drag mouse (with left click) in plot area to zoom in both axes
  * Drag mouse (with left click) on X axis to zoom in that axis only
  * Drag mouse (with left click) on Y axis to zoom in that axis only
  * Mouse wheel to zoom in/out plot area (X axis and Y axis)
  * Mouse wheel on X axis to zoom in/out that axis only
  * Mouse wheel on Y axis to zoom in/out that axis only

To reset, double click on the chart or use the context menu in Options.