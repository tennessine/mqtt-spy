package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;

public interface SubscriptionStatusChangeObserver
{
	void onSubscriptionStatusChanged(final MqttSubscription changedSubscription);
}
