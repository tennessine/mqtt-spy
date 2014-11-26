/**
 * 
 */
package pl.baczkowicz.mqttspy.logger;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import pl.baczkowicz.mqttspy.common.generated.MessageLog;
import pl.baczkowicz.mqttspy.common.generated.MessageLogEnum;
import pl.baczkowicz.mqttspy.connectivity.BaseMqttConnection;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessageWithSubscriptions;

/**
 * @author kamil
 *
 */
public class LogParserUtilsTest
{
	private static final String SAMPLE_PAYLOAD = "payload";
	
	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	
	/**
	 * Test method for {@link pl.baczkowicz.mqttspy.logger.LogParserUtils#createReceivedMessageLog(pl.baczkowicz.mqttspy.messages.ReceivedMqttMessageWithSubscriptions, pl.baczkowicz.mqttspy.common.generated.MessageLog)}.
	 */
	@Test
	public void testCreateReceivedMessageLogWithPlainPayload()
	{
		final MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setPayload(SAMPLE_PAYLOAD.getBytes());
		
		final BaseMqttConnection connection = context.mock(BaseMqttConnection.class); 
		final ReceivedMqttMessageWithSubscriptions message = new ReceivedMqttMessageWithSubscriptions(1, "topic", mqttMessage, connection);
		
		final MessageLog messageLog = new MessageLog(MessageLogEnum.XML_WITH_PLAIN_PAYLOAD, false, false, false, false);
		final long timestamp = message.getDate().getTime();
		
		final String loggedMessage = "<MqttMessage id=\"1\" timestamp=\"" + timestamp + "\" topic=\"topic\">" + SAMPLE_PAYLOAD + "</MqttMessage>";
		assertEquals(loggedMessage, LogParserUtils.createReceivedMessageLog(message, messageLog));
	}

	/**
	 * Test method for {@link pl.baczkowicz.mqttspy.logger.LogParserUtils#createReceivedMessageLog(pl.baczkowicz.mqttspy.messages.ReceivedMqttMessageWithSubscriptions, pl.baczkowicz.mqttspy.common.generated.MessageLog)}.
	 * 
	 * This is to cover Issue 18.
	 */
	@Test
	public void testCreateReceivedMessageLogWithEncodedPayload()
	{
		final MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setPayload("payload".getBytes());
		
		final BaseMqttConnection connection = context.mock(BaseMqttConnection.class); 
		final ReceivedMqttMessageWithSubscriptions message = new ReceivedMqttMessageWithSubscriptions(1, "topic", mqttMessage, connection);
		
		final MessageLog messageLog = new MessageLog(MessageLogEnum.XML_WITH_ENCODED_PAYLOAD, false, false, false, false);
		final long timestamp = message.getDate().getTime();
		
		final String loggedMessage = "<MqttMessage id=\"1\" timestamp=\"" + timestamp + "\" topic=\"topic\" encoded=\"true\">" + 
				Base64.encodeBase64String(SAMPLE_PAYLOAD.getBytes()) + "</MqttMessage>";
		
		assertEquals(loggedMessage, LogParserUtils.createReceivedMessageLog(message, messageLog));
	}
}
