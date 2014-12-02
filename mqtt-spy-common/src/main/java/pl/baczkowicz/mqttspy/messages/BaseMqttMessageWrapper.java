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
package pl.baczkowicz.mqttspy.messages;

import pl.baczkowicz.mqttspy.common.generated.BaseMqttMessage;

public class BaseMqttMessageWrapper implements IMqttMessage
{
	private final BaseMqttMessage message;

	public BaseMqttMessageWrapper(final BaseMqttMessage message)
	{
		this.message = message;
	}

	public String getTopic()
	{
		return message.getTopic();
	}

	public String getPayload()
	{
		return message.getValue();
	}

	public int getQoS()
	{
		return message.getQos();
	}

	public boolean isRetained()
	{
		return message.isRetained();
	}

	public void setPayload(String payload)
	{
		message.setValue(payload);		
	}
}
