## _0.1.9 beta_ ##

Please use with caution and report any issues:

  * Topic summary chart
  * Eclipse Paho Java Client v1.0.2 (Paho 1.1)

## 0.1.8 (released on 01/03/2015) ##

  * Charts
    * Auto-refresh for live data
    * Historical values
    * One or more topics (select which from the summary table)
    * Driven by message payload or its size (could be formatted with subscription scripts; available from the summary table's context menu)
    * Message load charts (available from the subscription tab's context menu)
    * Pan and zoom (see the [Overview](Overview.md) wiki for info)
    * Value tooltips
  * UI improvements
    * Auto-resize for topic search field ([Issue 26](https://code.google.com/p/mqtt-spy/issues/detail?id=26))
    * Remembering 'resizable message pane' flag - see the 'Window' menu ([Issue 27](https://code.google.com/p/mqtt-spy/issues/detail?id=27))
    * Lists of browsed/filtered/all topics can be exported to clipboard ('Tools' -> 'Export' -> 'Topics')
    * New context menu item for the summary table - 'Filtered topics' -> 'Deselect all and select filtered topics'; other items renamed slightly
    * Application icon
  * Text conversions (added Base64 for input and fixed [Issue 29](https://code.google.com/p/mqtt-spy/issues/detail?id=29))

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.8_charts.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.8_charts.png)
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.8_charts-local-load.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.8_charts-local-load.png)
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.8_charts-eclipse-load.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.8_charts-eclipse-load.png)
![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.8_charts-eclipse-clients.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.8_charts-eclipse-clients.png)

## 0.1.7 (released on 15/02/2015) ##

  * Detachable connection and subscription tabs (available from the tab's context menu)
  * Detachable connection panes (e.g. 'Publish message' or 'Scripted publications' - see the connection tab's context menu)
  * Script creator (saving current/recent messages as scripts; see the Publish button's menu)
  * Scripted publications pane improvements
    * Double click to start a script (if not running)
    * New context menu items
      * Copy script location to clipboard
      * Delete script from list
      * Delete script from disk
  * Change color for existing subscription (see the subscription tab's context menu)

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.7_detached-panes.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.7_detached-panes.png)

## 0.1.6 (released on 30/01/2015) ##

  * Creating message log (audit) - see the [Message Log](MessageLog.md) wiki
  * Content-based filtering for browsed messages (see the new button 'Filter')
  * Exporting browsed messages (see the 'Tools' button; this uses the XML-based message log format)
  * Fix to message order when browsing selected topics
  * Fix to change detection in 'Manage connections'
  * Fix to script execution for received messages

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.6_filter-and-export.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.6_filter-and-export.png)

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.6_log-settings.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.6_log-settings.png)

## 0.1.5 (released on 16/01/2015) ##

  * Improved message searching
    * Simple payload search (default)
    * Search with predefined script (see the new configuration parameter in Subscriptions tab)
    * Search with in-line script (e.g. `content.contains("3") && topic.contains("test")`)
  * Improved resizing of the message and summary panes; added new toggle to the connection tab's context menu ([Issue 17](https://code.google.com/p/mqtt-spy/issues/detail?id=17))
  * Simplified saving subscriptions to configuration file ([Issue 22](https://code.google.com/p/mqtt-spy/issues/detail?id=22))
  * Formatting XML files (configuration & stats; [Issue 23](https://code.google.com/p/mqtt-spy/issues/detail?id=23))
  * Predefined publication scripts with auto-start ([Issue 24](https://code.google.com/p/mqtt-spy/issues/detail?id=24))
  * Remembering window size and selected perspective upon application restart

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.5_message-search.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.5_message-search.png)

## 0.1.4 (released on 02/01/2015) ##

  * Remembering and restoring published messages (see the 'Publish' button's menu)
  * Keyboard shortcuts (requires focus on the publication topic field)
    * ENTER - publish message
    * ALT + DIGIT - restores the last X message (e.g. ALT + 1 restores the most recent; ALT + 2 the second most recent; ALT + 0 the 10th most recent)

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.4_recent-messages.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.4_recent-messages.png)

## 0.1.3 (released on 19/12/2014) ##

  * Parametrised publications (see [Scripting](Scripting.md) wiki for details; and the 'Publish' button's menu)
  * Reformatting/modifying content with subscription scripts
  * Using Eclipse Paho Java Client v1.0.1

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.3_connection-tab.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.3_connection-tab.png)

## 0.1.2 (released on 01/12/2014) ##

  * Message log browsing (see the Logger menu)
  * Message log replay with customisable replay speed (via JS scripts; see [Scripting](Scripting.md) wiki for details)
  * 'Repeat' flag in scripted publications
  * Subscription scripts (e.g. for sending auto-reply)
  * Improved formatting

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.2_message-log.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.2_message-log.png)

## 0.1.1 (released on 26/11/2014) ##

  * High availability features
    * Support for multiple server URIs (as provided by Eclipse Paho Java client)
    * Automatic reconnection and resubscription
  * New XML configuration format for connections & new connection parameters (reconnection - see above; auto-subscribe on opening connection, see [Issue 16](https://code.google.com/p/mqtt-spy/issues/detail?id=16))
  * Base64 converter (see Window -> Converter)
  * Received message length now displayed in bytes (B), kilobytes (kB) and megabytes (MB); exact length is now displayed in tooltip

![http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.1_edit-connections.png](http://baczkowicz.pl/mqtt-spy/images/mqtt-spy_v0.1.1_edit-connections.png)

## 0.1.0 (released on 19/10/2014) ##

  * The “new secret feature” is now available on the control panel... ;-)
  * Scripted publications (with JavaScript, using the Nashorn engine; see [Scripting](Scripting.md) wiki for details)
  * Improved topic filtering (new text field on 'Received messages summary')
  * Improved message storage (now keeping last X messages for each topic)
  * Perspectives (Menu -> View -> Perspectives)
  * Customisable pane visibility (Connection tab's context menu -> Show/hide panes)
  * Other UI improvements
  * Bug fixes
  * ControlsFX 8.20.7 & RichTextFX 0.5

### 0.0.11 ###

  * Broker statistics (now available from the connection tab's context menu)
  * Statistics for received messages (shown as averages over 5s, 30s and 5m; see the ‘Received messages summary’ pane’s title)
  * Connection status (now displayed in tab's tooltip; just hover your mouse over to see it; for connection failures this includes the failure reason and time)
  * Subscription status (now also indicated in the tab's tooltip)
  * Subscription can be now created from the ‘Received messages summary’ context menu
  * Performance improvements (handling over 500 messages / second)
  * Minor UI improvements
  * Bug fixes

### 0.0.10 ###

  * MQTT Retained flag added for publications and subscriptions
  * MQTT Last Will and Testament settings
  * Performance improvements (handling over 200 messages / second)
  * Bug fixes
  * Now using “`_`” intead of “`-`” as the client ID timestamp separator (to make it work with IBM® WebSphere MQ)

### 0.0.9 ###

  * Control panel
    1. Configuration file checking
    1. Connection summary
    1. Checking for updates

### 0.0.8 ###

  * Improved connection management
  * Saving connection settings to configuration file
  * Support for default configuration file (stored in user’s home directory)
  * Performance improvements (handling over 100 messages / second)

### 0.0.7 ###

  * User authentication (MQTT username & password)
  * Read-only connection management

### 0.0.6 ###

  * Added message search capability based on formatted content
  * Filtering now applied by clicking on the "Show" column or through the context menu of the summary table
  * Updated context menus
  * Styling improvements
  * Bug fix for message content text blur

### 0.0.5 ###

  * Easy access to broker's statistics by subscribing to $SYS/# (tested with mosquitto and HiveMQ)
  * Topic filtering for received messages (driven from the summary table's context menu)
  * Content of the last message received showed in the summary table
  * New formatter available for encoding individual characters (e.g. # with its hexadecimal value; only using the configuration file)
  * Maximum number of messages stored for each subscription can be now defined in the configuration file
  * Actions triggered from keyboard & mouse (subscribing and message browsing)
  * Bug fix for clearing the summary table

### 0.0.4 ###

  * Auto-loading of the configuration file (command line usage:  --configuration=”file location”) and XSD validation
  * Formatters for incoming messages (basic formatting via GUI, more complex by using the configuration file)
  * Selected tabs have font weight set to bold, so that it's easier to spot which ones are selected
  * Minor UI improvements

## 0.0.3 ##

  * Redesigned content of the connection tab (with collapsible panes)
  * Added publication capability
  * Recording topics used for publications and subscriptions in drop down lists
  * Configuration file now defines whether to create subscription tabs or just populate the drop down list
  * Added optional connection settings to the configuration file (clean session, connection timeout, keepalive)
  * General improvements

## 0.0.2 ##

  * Loading configuration files with predefined connections and subscriptions
  * New context menu item (copy subscription topic) for the subscription tab
  * Changes to the “New connection” window
    1. Removed unused fields
    1. Added auto-connect
    1. Added connection name
  * Minor UI tweaks
  * General improvements

## 0.0.1 ##

  * Initial release