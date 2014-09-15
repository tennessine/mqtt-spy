package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStore;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMqttContent;
import pl.baczkowicz.mqttspy.ui.events.EventDispatcher;
import pl.baczkowicz.mqttspy.ui.events.MessageFormatChangeEvent;
import pl.baczkowicz.mqttspy.ui.events.NewMessageEvent;
import pl.baczkowicz.mqttspy.ui.events.ShowFirstEvent;
import pl.baczkowicz.mqttspy.ui.properties.SearchOptions;

public class SearchPaneController implements Initializable, Observer
{
	final static Logger logger = LoggerFactory.getLogger(SearchPaneController.class);
	
	@FXML
	private TextField searchField;
	
	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private MessageController messagePaneController;
	
	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private MessageListTableController messageListTablePaneController;
	
	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private MessageNavigationController messageNavigationPaneController;
	
	@FXML
	private CheckBox autoRefreshCheckBox;
	
	@FXML
	private CheckBox caseSensitiveCheckBox;
	
	private ObservableMessageStoreWithFiltering store; 
	
	private ObservableMessageStore foundMessageStore = new ObservableMessageStore(Integer.MAX_VALUE);

	private Tab tab;

	// private SearchWindowController searchWindowController;
	
	private final ObservableList<ObservableMqttContent> foundMessages = FXCollections.observableArrayList();

	private EventDispatcher searchPaneEventDispatcher;
	
	public void initialize(URL location, ResourceBundle resources)
	{
		searchField.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() 
		{
	        @Override
	        public void handle(KeyEvent keyEvent) 
	        {
	        	switch (keyEvent.getCode())
	        	{
		        	case ENTER:
		        	{
		        		search();
		        		break;
		        	}		        	
		        	default:
		        		break;
	        	}
	        }
	    });
	}
	
	@FXML
	private void toggleAutoRefresh()
	{
		updateTabTitle();		
	}
	
	private boolean matches(final String value, final String substring)
	{
		if (caseSensitiveCheckBox.isSelected())
		{
			return value.contains(substring);
		}
		
		return value.toLowerCase().contains(substring.toLowerCase());
	}
	
	private void processMessage(final MqttContent message)
	{
		if (matches(message.getFormattedPayload(store.getFormatter()), searchField.getText()))
		{
			foundMessage(message);
		}
	}
	
	private void foundMessage(final MqttContent message)
	{
		foundMessages.add(0, new ObservableMqttContent(message, store.getFormatter()));
		foundMessageStore.storeMessage(message);		
	}
	
	private void clearMessages()
	{
		foundMessages.clear();
		foundMessageStore.clear();
	}
	
	@FXML
	private void search()
	{
		clearMessages();		
		
		for (final MqttContent message : store.getMessages())
		{
			processMessage(message);
		}
		
		updateTabTitle();	
		messagePaneController.setSearchOptions(new SearchOptions(searchField.getText(), caseSensitiveCheckBox.isSelected()));
		
		searchPaneEventDispatcher.dispatchEvent(new ShowFirstEvent());
	}
	
	private void updateTabTitle()
	{
		final HBox title = new HBox();
		title.setAlignment(Pos.CENTER);
				
		if (isAutoRefresh())
		{
			final ProgressIndicator progressIndicator = new ProgressIndicator();
			progressIndicator.setMaxSize(15, 15);
			title.getChildren().add(progressIndicator);
			title.getChildren().add(new Label(" "));
		}
		
		title.getChildren().add(new Label("Search for: \"" + searchField.getText() + "\""
				+ " [" + foundMessages.size() + " found / " + store.getMessages().size() + " searched]"));
		
		tab.setText(null);
		tab.setGraphic(title);		
	}

	public void setStore(ObservableMessageStoreWithFiltering store)
	{
		this.store = store;
		store.addObserver(this);
	}

	public void update(Observable observable, Object update)
	{
		if (update instanceof MqttContent)
		{
			if (autoRefreshCheckBox.isSelected() && (store.getFilters().contains(((MqttContent) update).getTopic())))
			{
				processMessage((MqttContent) update);
				updateTabTitle();
				
				if (messageNavigationPaneController.showLatest())
				{
					searchPaneEventDispatcher.dispatchEvent(new ShowFirstEvent());
				}
				else
				{
					searchPaneEventDispatcher.dispatchEvent(new NewMessageEvent());
				}
			}
			else
			{
				// Ignore
			}
		}
		else if (update instanceof MessageFormatChangeEvent && !observable.equals(searchPaneEventDispatcher))
		{
			foundMessageStore.setFormatter(store.getFormatter());
			searchPaneEventDispatcher.dispatchEvent(new MessageFormatChangeEvent());
		}
	}

	public void init()
	{
		foundMessageStore.setFormatter(store.getFormatter());
		
		searchPaneEventDispatcher = new EventDispatcher();
		searchPaneEventDispatcher.addObserver(this);
		
		messageListTablePaneController.setItems(foundMessages);
		messageListTablePaneController.setStore(foundMessageStore);
		messageListTablePaneController.setNavigationEventDispatcher(searchPaneEventDispatcher);
		messageListTablePaneController.init();
		
		messagePaneController.setStore(foundMessageStore);
		messagePaneController.setEventDispatcher(searchPaneEventDispatcher);
		messagePaneController.init();
		
		messageNavigationPaneController.setStore(foundMessageStore);
		messageNavigationPaneController.setNavigationEventDispatcher(searchPaneEventDispatcher);
		messageNavigationPaneController.init();
	}

	public void cleanup()
	{
		disableAutoSearch();
		searchPaneEventDispatcher.deleteObserver(this);
	}
		
	public void setTab(Tab tab)
	{
		this.tab = tab;
	}

	public boolean isAutoRefresh()
	{
		return autoRefreshCheckBox.isSelected();
	}

	public void disableAutoSearch()
	{
		autoRefreshCheckBox.setSelected(false);			
		updateTabTitle();
	}
}
