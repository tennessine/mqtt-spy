package pl.baczkowicz.mqttspy.scripts;

public enum ScriptRunningState
{
	NOT_STARTED("Not started"), FAILED("Failed"), RUNNING("Running"), STOPPED("Stopped"), 
	FINISHED("Finished"), FROZEN("Not responding");

	private final String name;

	private ScriptRunningState(String s)
	{
		name = s;
	}

	public boolean equalsName(String otherName)
	{
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString()
	{
		return name;
	}
}
