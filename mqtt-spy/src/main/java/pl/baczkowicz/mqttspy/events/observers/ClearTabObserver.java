package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;

public interface ClearTabObserver
{
	void onClearTab(final ObservableMessageStoreWithFiltering subscription);
}
