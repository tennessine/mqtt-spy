package pl.baczkowicz.mqttspy.scripts;

public interface ScriptEventManagerInterface
{
	void notifyScriptStateChange(final String scriptName, final ScriptRunningState state);
}
