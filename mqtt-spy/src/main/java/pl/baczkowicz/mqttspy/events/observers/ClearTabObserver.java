package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.storage.ManagedMessageStoreWithFiltering;

public interface ClearTabObserver
{
	void onClearTab(final ManagedMessageStoreWithFiltering subscription);
}
