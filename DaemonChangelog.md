## 0.0.4 ##

  * Running mode - continuous or stop when all scripts finished
  * Reformatting/modifying content with subscription scripts
  * Logging messages before or after (default) executing subscription scripts
  * Using Eclipse Paho Java Client v1.0.1
  * Fixed encoding settings ([Issue 18](https://code.google.com/p/mqtt-spy/issues/detail?id=18))

## 0.0.3 ##

  * Added message log replay with customisable replay speed (via background JS scripts)
  * Message log - optional logging for QoS, retained flag, connection name and subscription
  * Background scripts - added 'repeat' flag
  * Console output now also logged to mqtt-spy-daemon.log

## 0.0.2 ##

  * High availability features
    * Support for multiple server URIs (as provided by Eclipse Paho Java client)
    * Automatic reconnection and resubscription
  * Improved logging
  * Improved script execution

## 0.0.1 ##

  * Initial release