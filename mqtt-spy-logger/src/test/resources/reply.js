function publishReply()
{
	mqttspy.publish(
			"/reply", "<simpleReply><topic>" + receivedMessage.getTopic() + "</topic>" 
			+ "<payload><![CDATA[" + receivedMessage.getMessage() + "]]></payload>"
			+ "</simpleReply>", 0, false);

	return true;
}

publishReply();
