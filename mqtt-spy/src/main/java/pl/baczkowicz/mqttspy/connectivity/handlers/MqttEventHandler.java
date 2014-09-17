package pl.baczkowicz.mqttspy.connectivity.handlers;

import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.connectivity.events.MqttConnectionAttemptSuccessEvent;
import pl.baczkowicz.mqttspy.connectivity.events.MqttConnectionFailureEvent;
import pl.baczkowicz.mqttspy.connectivity.events.MqttConnectionLostEvent;
import pl.baczkowicz.mqttspy.connectivity.events.MqttDisconnectionAttemptSuccessEvent;
import pl.baczkowicz.mqttspy.connectivity.events.MqttEvent;

public class MqttEventHandler implements Runnable
{
	private MqttEvent event;

	public MqttEventHandler(final MqttEvent event)
	{
		this.event = event;
	}

	public void run()
	{
		if (event instanceof MqttConnectionLostEvent)
		{
			((MqttConnectionLostEvent) event).getConnection().connectionLost(
					((MqttConnectionLostEvent) event).getCause());
		}

		// This also covers sub-types of the failure event
		if (event instanceof MqttConnectionFailureEvent)
		{
			final MqttConnectionFailureEvent mqttConnectionFailureEvent = (MqttConnectionFailureEvent) event;
			
			mqttConnectionFailureEvent.getConnection().setDisconnectionReason(mqttConnectionFailureEvent.getCause().getMessage());
			mqttConnectionFailureEvent.getConnection().setConnectionStatus(MqttConnectionStatus.DISCONNECTED);
		}
		
		if (event instanceof MqttConnectionAttemptSuccessEvent)
		{
			((MqttConnectionAttemptSuccessEvent) event).getConnection().setConnectionStatus(MqttConnectionStatus.CONNECTED);
		}
		
		if (event instanceof MqttDisconnectionAttemptSuccessEvent)
		{
			final MqttDisconnectionAttemptSuccessEvent mqttDisconnectionAttemptSuccessEvent = (MqttDisconnectionAttemptSuccessEvent) event;
			
			mqttDisconnectionAttemptSuccessEvent.getConnection().setDisconnectionReason("");
			mqttDisconnectionAttemptSuccessEvent.getConnection().setConnectionStatus(MqttConnectionStatus.DISCONNECTED);
		}
	}
}
