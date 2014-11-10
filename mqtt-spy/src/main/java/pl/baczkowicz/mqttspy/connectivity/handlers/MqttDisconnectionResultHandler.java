package pl.baczkowicz.mqttspy.connectivity.handlers;

import javafx.application.Platform;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;
import pl.baczkowicz.mqttspy.events.connectivity.MqttConnectionAttemptFailureEvent;
import pl.baczkowicz.mqttspy.events.connectivity.MqttDisconnectionAttemptSuccessEvent;

public class MqttDisconnectionResultHandler implements IMqttActionListener
{
	private final static Logger logger = LoggerFactory.getLogger(MqttDisconnectionResultHandler.class);

	public void onSuccess(IMqttToken asyncActionToken)
	{
		final MqttAsyncConnection connection = (MqttAsyncConnection) asyncActionToken.getUserContext();
		try
		{
			logger.info(connection.getProperties().getName() + " disconnected");
			Platform.runLater(new MqttEventHandler(new MqttDisconnectionAttemptSuccessEvent(connection)));
		}
		catch (IllegalStateException e)
		{
			logger.debug("Application about to close");
		}
	}

	public void onFailure(IMqttToken asyncActionToken, Throwable exception)
	{
		final MqttAsyncConnection connection = (MqttAsyncConnection) asyncActionToken.getUserContext();
		Platform.runLater(new MqttEventHandler(new MqttConnectionAttemptFailureEvent(connection, exception)));
		logger.warn("Disconnecting from " + connection.getProperties().getName() + " failed");
	}
}