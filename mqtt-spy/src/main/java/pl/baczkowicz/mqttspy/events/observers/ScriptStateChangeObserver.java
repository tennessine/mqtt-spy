package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.scripts.ScriptRunningState;

public interface ScriptStateChangeObserver
{
	void onScriptStateChange(final String scriptName, final ScriptRunningState state);
}
