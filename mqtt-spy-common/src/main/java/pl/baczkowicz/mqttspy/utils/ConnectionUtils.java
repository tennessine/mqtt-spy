package pl.baczkowicz.mqttspy.utils;

import java.util.List;

public class ConnectionUtils
{
	public static final String SERVER_DELIMITER = ";";

	public static String composeConnectionName(final String cliendId,
			final List<String> serverURIs)
	{
		return cliendId + "@" + serverURIsToString(serverURIs);
	}

	public static String composeConnectionName(final String cliendId,
			final String serverURIs)
	{
		return cliendId + "@" + serverURIs;
	}

	public static String serverURIsToString(final List<String> serverURIs)
	{
		StringBuffer serverURIsAsString = new StringBuffer();
		boolean first = true;
		for (final String serverURI : serverURIs)
		{
			if (first)
			{
				serverURIsAsString.append(serverURI);
			}
			else
			{
				serverURIsAsString.append(ConnectionUtils.SERVER_DELIMITER
						+ " " + serverURI);
			}

			first = false;
		}

		return serverURIsAsString.toString();
	}
}
