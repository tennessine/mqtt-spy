package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStore;
import pl.baczkowicz.mqttspy.ui.events.EventDispatcher;
import pl.baczkowicz.mqttspy.ui.events.MessageFormatChangeEvent;
import pl.baczkowicz.mqttspy.ui.events.MessageIndexChangedEvent;
import pl.baczkowicz.mqttspy.ui.properties.SearchOptions;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class MessageController implements Initializable, Observer
{
	final static Logger logger = LoggerFactory.getLogger(MessageController.class);

	@FXML
	private StyleClassedTextArea dataField;

	@FXML
	private ToggleButton wrapToggle;
	
	@FXML
	private CheckBox retainedField;

	@FXML
	private TextField topicField;

	@FXML
	private TextField timeField;

	@FXML
	private TextField qosField;

	@FXML
	private Label lengthLabel;

	private EventDispatcher eventDispatcher;
	
	private ObservableMessageStore store;
	
	private MqttContent message;

	private FormatterDetails selectionFormat = null;

	private Tooltip tooltip;

	private SearchOptions searchOptions;

	public void populate(final MqttContent message)
	{
		this.message = message;

		final String payload = new String(message.getMessage().getPayload());
		logger.trace("Message payload = " + payload);

		topicField.setText(message.getTopic());
		qosField.setText(String.valueOf(message.getMessage().getQos()));
		timeField.setText(Utils.SDF.format(message.getDate()));
		lengthLabel.setText("(" + payload.length() + ")");
		retainedField.setSelected(message.getMessage().isRetained());

		showMessageData();
	}

	public void clear()
	{
		this.message = null;

		topicField.setText("");
		
		dataField.clear();		
		
		qosField.setText("");
		timeField.setText("");
		lengthLabel.setText("(0)");
		retainedField.setSelected(false);
	}
	
	public void formatSelection(final FormatterDetails messageFormat)
	{
		this.selectionFormat = messageFormat;
		
		if (selectionFormat != null)
		{
			updateTooltipText();
			dataField.setTooltip(tooltip);
		}
		else			
		{
			dataField.setTooltip(null);
		}
	}

	private void showMessageData()
	{
		if (message != null)
		{
			final String payload = new String(message.getMessage().getPayload());

			dataField.clear();
			dataField.appendText(FormattingUtils.convertText(store.getFormatter(), payload));

			dataField.setStyleClass(0, dataField.getText().length(), "messageText");
			
			if (searchOptions != null && searchOptions.getSearchValue().length() > 0)
			{
				final String textToSearch = searchOptions.isMatchCase() ? dataField.getText() : dataField.getText().toLowerCase();
				
				int pos = textToSearch.indexOf(searchOptions.getSearchValue());
				while (pos >= 0)
				{
					dataField.setStyleClass(pos, pos + searchOptions.getSearchValue().length(), "messageTextHighlighted");
					pos = textToSearch.indexOf(searchOptions.getSearchValue(), pos + 1);
				}
			}
			
			updateTooltipText();
		}
	}
	
	private void updateTooltipText()
	{
		if (selectionFormat != null)
		{
			final String tooltipText = FormattingUtils.convertText(selectionFormat, dataField.getSelectedText());
			
			if (tooltipText.length() > 0)
			{
				tooltip.setText(tooltipText);
			}
			else
			{
				tooltip.setText("[select text to convert]");
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		tooltip = new Tooltip("");
		tooltip.setWrapText(true);
		
		dataField.selectedTextProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue)
			{
				updateTooltipText();				
			}
		});		
	}

	@Override
	public void update(Observable observable, Object update)
	{
		if (update instanceof MessageIndexChangedEvent)
		{
			updateMessage(((MessageIndexChangedEvent) update).getIndex());			
		}
		else if (update instanceof MessageFormatChangeEvent)
		{
			showMessageData();
		}
	}
	
	private void updateMessage(final int messageIndex)
	{
		if (messageIndex > 0)
		{
			populate((MqttContent) store.getMessages().toArray()[store.getMessages().size() - messageIndex]);
		}
		else
		{
			clear();
		}
	}
	
	public void setSearchOptions(final SearchOptions searchOptions)
	{
		this.searchOptions = searchOptions;
	}
	
	public void init()
	{
		eventDispatcher.addObserver(this);
	}
	
	public void setStore(final ObservableMessageStore store)
	{
		this.store = store;
	}
	
	public void setEventDispatcher(final EventDispatcher eventDispatcher)
	{
		this.eventDispatcher = eventDispatcher;
	}
}
