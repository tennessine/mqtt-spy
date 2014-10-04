package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.storage.ObservableMessageStoreWithFiltering;

public interface ClearTabObserver
{
	void onClearTab(final ObservableMessageStoreWithFiltering subscription);
}
