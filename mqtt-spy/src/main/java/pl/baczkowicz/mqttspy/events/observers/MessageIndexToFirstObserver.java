package pl.baczkowicz.mqttspy.events.observers;

public interface MessageIndexToFirstObserver extends MessageIndexChangeObserver
{
	void onNavigateToFirst();
}
