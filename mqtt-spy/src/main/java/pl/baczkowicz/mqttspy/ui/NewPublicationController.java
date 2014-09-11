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
import javafx.scene.control.MenuButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import org.controlsfx.dialog.Dialogs;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.ConversionMethod;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.ui.format.ConversionException;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

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
	private MenuButton formatMenu;
		
	private ObservableList<String> publicationTopics = FXCollections.observableArrayList();

	private MqttConnection connection;

	private boolean plainSelected = true;

	private boolean previouslyPlainSelected = true;

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
	
	public void setActive(final boolean active)
	{
		this.publishButton.setDisable(!active);
		this.publicationTopicText.setDisable(!active);
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
	
	@FXML
	public void publish()
	{
		if (publicationTopicText.getValue() == null)
		{
			logger.error("Cannot publish to an empty topic");
			
			Dialogs.create()
			      .owner(null)
			      .title("Invalid topic")
			      .masthead(null)
			      .message("Cannot publish to an empty topic.")
			      .showError();
			return;
		}
		
		try
		{
			String data = publicationData.getText();
		
			if (!previouslyPlainSelected)
			{
				data = FormattingUtils.hexToString(data);
			}
					
			final String publicationTopic = publicationTopicText.getValue().toString(); 
			
			connection.publish(publicationTopic, data, publicationQosChoice.getSelectionModel().getSelectedIndex(), retainedBox.isSelected());
			
			recordPublicationTopic(publicationTopic);
		}
		catch (ConversionException e)
		{
			showAndLogHexError();
		}
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
}
