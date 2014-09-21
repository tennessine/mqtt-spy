package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.messagestore.MessageStore;
import pl.baczkowicz.mqttspy.ui.events.EventDispatcher;
import pl.baczkowicz.mqttspy.ui.events.MessageIndexChangedEvent;
import pl.baczkowicz.mqttspy.ui.events.NewMessageEvent;
import pl.baczkowicz.mqttspy.ui.events.ShowFirstEvent;
import pl.baczkowicz.mqttspy.ui.utils.TextUtils;

public class MessageNavigationController implements Observer, Initializable
{
	final static Logger logger = LoggerFactory.getLogger(MessageNavigationController.class);

	@FXML
	private Label messageLabel;

	@FXML
	private Label filterStatusLabel;
	
	@FXML
	private CheckBox showLatestBox;

	@FXML
	private ToggleGroup wholeMessageFormat;

	@FXML
	private MenuButton formattingMenuButton;

	@FXML
	private Menu formatterMenu;
	
	@FXML
	private Menu customFormatterMenu;

	@FXML
	private ToggleGroup selectionFormat;

	@FXML
	private Button moreRecentButton;

	@FXML
	private Button lessRecentButton;

	@FXML
	private Button showFirstButton;

	@FXML
	private Button showLastButton;

	@FXML
	private HBox messageIndexBox; 

	private int selectedMessage;

	private MessageStore store; 
	
	private TextField messageIndexValueField;
	
	private Label totalMessagesValueLabel;
	
	private EventDispatcher navigationEventDispatcher;

	// ===================
	// === FXML methods ==
	// ===================
	@FXML
	private void showFirst()
	{
		showFirstMessage();
	}

	@FXML
	private void showLast()
	{
		showLastMessage();
	}	

	@FXML
	private void showMoreRecent()
	{
		changeSelectedMessageIndex(-1);
	}	
	
	@FXML
	private void showLessRecent()
	{
		changeSelectedMessageIndex(1);
	}

	// ====================
	// === Other methods ==
	// ====================
		
	public void update(Observable observable, Object update)
	{
		if (update instanceof ShowFirstEvent)
		{
			showFirstMessage();			
		}
		else if (update instanceof NewMessageEvent)
		{
			selectedMessage++;
			updateIndex();			
		}
		else if (update instanceof MessageIndexChangedEvent)
		{
			final int newSelectedMessage = ((MessageIndexChangedEvent) update).getIndex(); 
			if (selectedMessage != newSelectedMessage)
			{
				selectedMessage = newSelectedMessage;
				updateIndex();
			}
		}
	}
	
	private void showFirstMessage()
	{
		if (store.getMessages().size() > 0)
		{
			selectedMessage = 1;
			updateIndex();
		}
		else
		{
			selectedMessage = 0;
			updateIndex();
		}
	}

	private void showLastMessage()
	{
		if (store.getMessages().size() > 0)
		{
			selectedMessage = store.getMessages().size();
			updateIndex();
		}
	}
	
	private void changeSelectedMessageIndex(final int count)
	{
		if (store.getMessages().size() > 0)
		{
			if (selectedMessage + count <= 1)
			{
				showFirstMessage();
			}
			else if (selectedMessage + count >= store.getMessages().size())
			{
				showLastMessage();
			}
			else
			{
				selectedMessage = selectedMessage + count;
				updateIndex();
			}
		}		
	}

	private void updateIndex()
	{
		final String selectedIndexValue = selectedMessage > 0 ? String.valueOf(selectedMessage) : "-";
		final String totalMessagesValue = "/ " + store.getMessages().size(); 		
		
		if (messageIndexBox.getChildren().size() == 1)
		{
			messageLabel.setText("Message ");	
			messageIndexBox.getChildren().add(messageIndexValueField);
			messageIndexBox.getChildren().add(totalMessagesValueLabel);		
			messageIndexBox.getChildren().add(filterStatusLabel);
		}
		
		messageIndexValueField.setText(selectedIndexValue);		
		totalMessagesValueLabel.setText(totalMessagesValue);

		if (!store.filtersEnabled())
		{			
			filterStatusLabel.setText("");
		}
		else
		{
			filterStatusLabel.setText("(filter is on)");		
		}
		
		navigationEventDispatcher.dispatchEvent(new MessageIndexChangedEvent(selectedMessage));
	}

	public void initialize(URL location, ResourceBundle resources)
	{				
		messageIndexValueField = new TextField();
		messageIndexValueField.setEditable(false);
		messageIndexValueField.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> ob, String o, String n)
			{
				// expand the textfield
				messageIndexValueField.setPrefWidth(TextUtils.computeTextWidth(
						messageIndexValueField.getFont(), messageIndexValueField.getText(), 0.0D) + 12);
			}
		});
		
		messageLabel.getStyleClass().add("messageIndex");
		messageLabel.setPadding(new Insets(2, 2, 2, 2));
		
		totalMessagesValueLabel = new Label();
		totalMessagesValueLabel.getStyleClass().add("messageIndex");
		totalMessagesValueLabel.setPadding(new Insets(2, 2, 2, 2));
				
		filterStatusLabel = new Label();
		filterStatusLabel.getStyleClass().add("filterOn");
		filterStatusLabel.setPadding(new Insets(2, 2, 2, 2));
		
		messageIndexValueField.setPadding(new Insets(2, 5, 2, 5));
		messageIndexValueField.getStyleClass().add("messageIndex");
		messageIndexValueField.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>()
		{
			@Override
			public void handle(ScrollEvent event)
			{
				
				switch(event.getTextDeltaYUnits()) 
				{
			        case LINES:
			        	updateMessageIndexFromScroll((int) event.getTextDeltaY());
			            break;
			        case PAGES:
			        	updateMessageIndexFromScroll((int) event.getTextDeltaY());
			            break;
			        case NONE:
			        	updateMessageIndexFromScroll((int) event.getDeltaY());			        	
			            break;
				}
			}
		});
		messageIndexValueField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() 
		{
	        @Override
	        public void handle(KeyEvent keyEvent) 
	        {
	        	switch (keyEvent.getCode())
	        	{
		        	case SPACE:
		        	{
		        		showLatestBox.setSelected(!showLatestBox.isSelected());
		        		break;
		        	}
		        	case HOME:
		        	{
		        		showFirst();
		        		break;
		        	}
		        	case END:
		        	{
		        		showLast();
		        		break;
		        	}
		        	case PAGE_UP:
		        	{
		        		changeSelectedMessageIndex(5);
		        		break;
		        	}
		        	case PAGE_DOWN:
		        	{
		        		changeSelectedMessageIndex(-5);
		        		break;
		        	}
		        	case UP:
		        	{
		        		changeSelectedMessageIndex(1);
		        		break;
		        	}
		        	case DOWN:
		        	{
		        		changeSelectedMessageIndex(-1);
		        		break;
		        	}
		        	default:
		        		break;
	        	}
	        }
	    });		
	}
	
	private void updateMessageIndexFromScroll(final int scroll)
	{
		if (scroll > 0)
    	{
    		changeSelectedMessageIndex(1);
    	}
    	else
    	{
    		changeSelectedMessageIndex(-1);
    	}
	}	

	public void init()
	{
		moreRecentButton.setTooltip(new Tooltip("Show more recent message"));
		lessRecentButton.setTooltip(new Tooltip("Show less recent message"));
		showFirstButton.setTooltip(new Tooltip("Show the latest message"));
		showLastButton.setTooltip(new Tooltip("Show the oldest message"));
		
		navigationEventDispatcher.addObserver(this);			
	}

	public void setStore(final MessageStore store)
	{
		this.store = store;
	}
	
	public void setNavigationEventDispatcher(final EventDispatcher navigationEventDispatcher)
	{
		this.navigationEventDispatcher = navigationEventDispatcher;
	}

	public void clear()
	{
		messageLabel.setText("No messages");
		messageIndexBox.getChildren().clear();
		messageIndexBox.getChildren().add(messageLabel);
	}
	
	public boolean showLatest()
	{
		return showLatestBox.isSelected();
	}
	
	public int getSelectedMessageIndex()
	{
		return selectedMessage;
	}
}
