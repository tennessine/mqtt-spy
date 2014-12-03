/***********************************************************************************
 * 
 * Copyright (c) 2014 Kamil Baczkowicz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 
 *    Kamil Baczkowicz - initial API and implementation and/or initial documentation
 *    
 */
package pl.baczkowicz.mqttspy.utils;

import java.util.List;

/**
 * Connection utils.
 */
public class ConnectionUtils
{
	/** Indicates connection was never started. */
	public static final long NEVER_STARTED = 0;

	/** This is the delimiter for separating server URIs in a single string. */
	public static final String SERVER_DELIMITER = ";";

	/**
	 * Composes a connection name based on the supplied client ID and server URIs.
	 * 
	 * @param clientId Client ID
	 * @param serverURIs Server URIs
	 * 
	 * @return Composed connection name
	 */
	public static String composeConnectionName(final String clientId, final List<String> serverURIs)
	{
		return composeConnectionName(clientId, serverURIsToString(serverURIs));
	}

	/**
	 * Composes a connection name based on the supplied client ID and server URIs.
	 * 
	 * @param clientId Client ID
	 * @param serverURIs Server URIs
	 * 
	 * @return Composed connection name
	 */
	public static String composeConnectionName(final String clientId, final String serverURIs)
	{
		return clientId + "@" + serverURIs;
	}

	/**
	 * Turns the given list of server URIs into a single string.
	 * 
	 * @param serverURIs List of server URIs
	 * 
	 * @return String representing all URIs
	 */
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
