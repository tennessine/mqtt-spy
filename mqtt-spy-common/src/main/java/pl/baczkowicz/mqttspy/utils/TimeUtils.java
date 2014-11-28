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

import java.text.SimpleDateFormat;

/** 
 * Time and date related utilities.
 */
public class TimeUtils
{	
	public final static String WITH_MILLISECONDS_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS";
	
	public final static String WITH_SECONDS_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	
	public final static String DATE_ONLY_FORMAT = "yyyy/MM/dd";

	public final static SimpleDateFormat DATE_WITH_MILLISECONDS_SDF = new SimpleDateFormat(WITH_MILLISECONDS_DATE_FORMAT);
	
	public final static SimpleDateFormat DATE_WITH_SECONDS_SDF = new SimpleDateFormat(WITH_SECONDS_DATE_FORMAT);
	
	public final static SimpleDateFormat DATE_SDF = new SimpleDateFormat(DATE_ONLY_FORMAT);
	
	public static long getMonotonicTimeInMilliseconds()
	{
		return System.nanoTime() / 1000000;
	}
}
