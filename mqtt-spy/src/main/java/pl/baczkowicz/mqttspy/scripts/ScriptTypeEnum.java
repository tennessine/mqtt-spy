package pl.baczkowicz.mqttspy.scripts;

public enum ScriptTypeEnum
{
	PUBLICATION("Script folder"), SUBSCRIPTION("Subscription"), BACKGROUND("Predefined");
	
	private final String name;

	private ScriptTypeEnum(String s)
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
