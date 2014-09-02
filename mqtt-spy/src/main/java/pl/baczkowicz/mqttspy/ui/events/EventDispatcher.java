package pl.baczkowicz.mqttspy.ui.events;

import java.util.Observable;

public class EventDispatcher extends Observable
{
	public void dispatchEvent(final UIEvent event)
	{
		// Notifies the observers
		this.setChanged();
		this.notifyObservers(event);
	}
}
