package pl.baczkowicz.mqttspy.ui.properties;

public class SearchOptions
{
	private String searchValue;
	
	private boolean matchCase;
	
	public SearchOptions(String searchValue, boolean matchCase)
	{
		this.searchValue = searchValue;
		this.matchCase = matchCase;
	}

	public String getSearchValue()
	{
		return matchCase ? searchValue : searchValue.toLowerCase();
	}

	public void setSearchValue(String searchValue)
	{
		this.searchValue = searchValue;
	}

	public boolean isMatchCase()
	{
		return matchCase;
	}

	public void setMatchCase(boolean matchCase)
	{
		this.matchCase = matchCase;
	}
}	
