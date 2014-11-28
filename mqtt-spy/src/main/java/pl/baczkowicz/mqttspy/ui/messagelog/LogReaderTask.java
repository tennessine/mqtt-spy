package pl.baczkowicz.mqttspy.ui.messagelog;

import java.io.File;
import java.util.List;

import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.LoggedMqttMessage;
import pl.baczkowicz.mqttspy.logger.LogParserUtils;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;
import pl.baczkowicz.mqttspy.ui.MainController;
import pl.baczkowicz.mqttspy.ui.connections.ConnectionManager;
import pl.baczkowicz.mqttspy.utils.ThreadingUtils;

public class LogReaderTask extends TaskWithProgressUpdater<List<ReceivedMqttMessage>>
{
	private final static Logger logger = LoggerFactory.getLogger(LogReaderTask.class);
	
	private File selectedFile;
	protected ConnectionManager connectionManager;
	protected MainController controller;
	
	public LogReaderTask(final File selectedFile, final ConnectionManager connectionManager, final MainController mainController)
	{
		this.selectedFile = selectedFile;
		this.connectionManager = connectionManager;
		this.controller = mainController;
	}

	@Override
	protected List<ReceivedMqttMessage> call() throws Exception
	{
		try
		{
			updateMessage("Please wait - reading message log [1/4]");
			updateProgress(0, 4);
			final List<String> fileContent = LogParserUtils.readMessageLog(selectedFile);					
			final long totalItems = fileContent.size();
			updateProgress(totalItems, totalItems * 4);
			
			updateMessage("Please wait - parsing " + fileContent.size() + " messages [2/4]");					
			final List<LoggedMqttMessage> loggedMessages = LogParserUtils.parseMessageLog(fileContent, this, totalItems, totalItems * 4);
			updateProgress(totalItems * 2, totalItems * 4);
								
			updateMessage("Please wait - processing " + loggedMessages.size() + " messages [3/4]");					
			final List<ReceivedMqttMessage> processedMessages = LogParserUtils.processMessageLog(loggedMessages, this, totalItems * 2, totalItems * 4);
			updateProgress(totalItems * 3, totalItems * 4);
			
			updateMessage("Please wait - displaying " + loggedMessages.size() + " messages [4/4]");	
			Platform.runLater(new Runnable()
			{							
				@Override
				public void run()
				{
					connectionManager.loadReplayTab(controller, controller, selectedFile.getName(), processedMessages);								
				}
			});	
			updateMessage("Finished!");
			updateProgress(4, 4);
			ThreadingUtils.sleep(500);
			
			return processedMessages;
		}
		catch (Exception e)
		{
			logger.error("Cannot process the message log - {}", selectedFile.getName(), e);
		}
		
		return null;
	}
}
