package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

import org.controlsfx.dialog.Dialogs;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.ConfiguredMessage;
import pl.baczkowicz.mqttspy.configuration.generated.ConversionMethod;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.ui.format.ConversionException;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

@SuppressWarnings("deprecation")
public class NewPublicationController implements Initializable
{
	final static Logger logger = LoggerFactory.getLogger(NewPublicationController.class);

	@FXML
	private Button publishButton;

	@FXML
	private ComboBox<String> publicationTopicText;

	@FXML
	private ChoiceBox<String> publicationQosChoice;

	@FXML
	private StyleClassedTextArea publicationData;
		
	@FXML
	private ToggleGroup formatGroup;
	
	@FXML
	private CheckBox retainedBox;
	
	@FXML
	private Label retainedLabel;
	
	@FXML
	private Label publicationQosLabel;
	
	@FXML
	private MenuButton formatMenu;
		
	private ObservableList<String> publicationTopics = FXCollections.observableArrayList();

	private MqttConnection connection;

	private boolean plainSelected = true;

	private boolean previouslyPlainSelected = true;

	private boolean detailedView;

	public void initialize(URL location, ResourceBundle resources)
	{
		publicationTopicText.setItems(publicationTopics);
		formatGroup.getToggles().get(0).setUserData(ConversionMethod.PLAIN);
		formatGroup.getToggles().get(1).setUserData(ConversionMethod.HEX_DECODE);
		formatGroup.selectToggle(formatGroup.getToggles().get(0));
		
		formatGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
		{
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue)
			{
				// If plain has been selected
				if (newValue != null)
				{
					if (formatGroup.getSelectedToggle().getUserData().equals(ConversionMethod.PLAIN))
					{
						showAsPlain();
					}
					else
					{
						showAsHex();
					}
				}
			}
		});
	}

	public void recordPublicationTopic(final String publicationTopic)
	{
		Utils.recordTopic(publicationTopic, publicationTopics);
	}
	
	public void setConnected(final boolean connected)
	{
		this.publishButton.setDisable(!connected);
		this.publicationTopicText.setDisable(!connected);
	}
	
	@FXML
	public void showAsPlain()
	{
		plainSelected = true;
		if (previouslyPlainSelected != plainSelected)
		{
			try
			{
				final String convertedText = FormattingUtils.hexToString(publicationData.getText());
				logger.info("Converted {} to {}", publicationData.getText(), convertedText);
				
				publicationData.clear();				
				publicationData.appendText(convertedText);
				
				formatMenu.setText("Input format: Plain");
				previouslyPlainSelected = plainSelected;
			}
			catch (ConversionException e)
			{
				showAndLogHexError();
				
				formatGroup.selectToggle(formatGroup.getToggles().get(1));
				formatMenu.setText("Input format: Hex");
				plainSelected = false;
			}
		}
	}
	
	@FXML
	public void showAsHex()
	{
		plainSelected = false;
		if (previouslyPlainSelected != plainSelected)
		{
			final String convertedText = FormattingUtils.stringToHex(publicationData.getText());
			logger.info("Converted {} to {}", publicationData.getText(), convertedText);
			
			publicationData.clear();
			publicationData.appendText(convertedText);
			
			formatMenu.setText("Input format: Hex");
			previouslyPlainSelected = plainSelected;
		}
	}
	
	private void updateVisibility()
	{
		if (detailedView)
		{
			AnchorPane.setRightAnchor(publicationTopicText, 327.0);
			publicationQosChoice.setVisible(true);
			publicationQosLabel.setVisible(true);
			retainedBox.setVisible(true);
			retainedLabel.setVisible(true);
		}
		else
		{
			AnchorPane.setRightAnchor(publicationTopicText, 126.0);
			publicationQosChoice.setVisible(false);
			publicationQosLabel.setVisible(false);
			retainedBox.setVisible(false);
			retainedLabel.setVisible(false);
		}
	}
	
	public void setDetailedViewVisibility(final boolean visible)
	{
		detailedView = visible;
		updateVisibility();
	}
	
	public void toggleDetailedViewVisibility()
	{
		detailedView = !detailedView;
		updateVisibility();
	}
	
	public void displayMessage(final ConfiguredMessage message)
	{
		if (message == null)
		{
			publicationTopicText.setValue("");
			publicationTopicText.setPromptText("(cannot be empty)");
			publicationQosChoice.getSelectionModel().select(0);
			publicationData.clear();
			retainedBox.setSelected(false);
		}
		else
		{
			publicationTopicText.setValue(message.getTopic());
			publicationQosChoice.getSelectionModel().select(message.getQoS());
			publicationData.clear();
			publicationData.appendText(message.getPayload());
			retainedBox.setSelected(message.isRetained());
		}
	}
	
	public ConfiguredMessage readMessage(final boolean verify)
	{
		if (verify && (publicationTopicText.getValue() == null || publicationTopicText.getValue().isEmpty()))
		{
			logger.error("Cannot publish to an empty topic");
			
			Dialogs.create()
			      .owner(null)
			      .title("Invalid topic")
			      .masthead(null)
			      .message("Cannot publish to an empty topic.")
			      .showError();
			return null;
		}
		
		final ConfiguredMessage message = new ConfiguredMessage();
		try
		{
			String data = publicationData.getText();
		
			if (!previouslyPlainSelected)
			{
				data = FormattingUtils.hexToString(data);
			}
					
			message.setTopic(publicationTopicText.getValue());
			message.setQoS(publicationQosChoice.getSelectionModel().getSelectedIndex());
			message.setPayload(data);
			message.setRetained(retainedBox.isSelected());
			
			return message;
		}
		catch (ConversionException e)
		{
			showAndLogHexError();
			return null;
		}		
	}
	
	@FXML
	public void publish()
	{						
		final ConfiguredMessage message = readMessage(true);
				
		connection.publish(message.getTopic(), message.getPayload(), message.getQoS(), message.isRetained());
		
		recordPublicationTopic(message.getTopic());		
	}
	
	private void showAndLogHexError()
	{
		logger.error("Cannot convert " + publicationData.getText() + " to plain text");
		
		Dialogs.create()
			      .owner(null)
			      .title("Invalid hex format")
			      .masthead(null)
			      .message("Provided text is not a valid hex string.")
			      .showError();
	}

	public void setConnection(MqttConnection connection)
	{
		this.connection = connection;
	}

	public void clearTopics()
	{
		publicationTopics.clear();		
	}	

	public ComboBox<String> getPublicationTopicText()
	{
		return publicationTopicText;
	}

	public ChoiceBox<String> getPublicationQosChoice()
	{
		return publicationQosChoice;
	}

	public StyleClassedTextArea getPublicationData()
	{
		return publicationData;
	}

	public CheckBox getRetainedBox()
	{
		return retainedBox;
	}
}
