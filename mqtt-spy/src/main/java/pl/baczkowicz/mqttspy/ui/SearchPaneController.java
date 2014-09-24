package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
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

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStore;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.observers.MessageFormatChangeObserver;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.ui.properties.MqttContentProperties;
import pl.baczkowicz.mqttspy.ui.properties.SearchOptions;

public class SearchPaneController implements Initializable, Observer, MessageFormatChangeObserver
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
	
	private EventManager eventManager;
	
	private ObservableMessageStoreWithFiltering store; 
	
	private ObservableMessageStore foundMessageStore;

	private Tab tab;

	private final ObservableList<MqttContentProperties> foundMessages = FXCollections.observableArrayList();

	// private EventDispatcher searchPaneEventDispatcher;

	private Queue<MqttSpyUIEvent> uiEventQueue;
	
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
	
	private boolean processMessage(final MqttContent message)
	{
		if (matches(message.getFormattedPayload(store.getFormatter()), searchField.getText()))
		{
			foundMessage(message);
			return true;
		}
		
		return false;
	}
	
	private void foundMessage(final MqttContent message)
	{
		foundMessages.add(0, new MqttContentProperties(message, store.getFormatter()));
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
		
		eventManager.changeMessageIndexToFirst(foundMessageStore);
		// searchPaneEventDispatcher.dispatchEvent(new ShowFirstMessageEvent());
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
	
	public void setUIQueue(final Queue<MqttSpyUIEvent> uiEventQueue)
	{
		this.uiEventQueue = uiEventQueue;
	}

	@Override
	public void onFormatChange()
	{
		//else if (update instanceof MessageFormatChangeEvent && !observable.equals(searchPaneEventDispatcher))
		//{
		foundMessageStore.setFormatter(store.getFormatter());
		eventManager.notifyFormatChanged(foundMessageStore);
		//searchPaneEventDispatcher.dispatchEvent(new MessageFormatChangeEvent());
		//}		
	}

	public void update(Observable observable, Object update)
	{
		if (update instanceof MqttContent)
		{
			if (autoRefreshCheckBox.isSelected() && (store.getFilters().contains(((MqttContent) update).getTopic())))
			{
				final boolean matchingSearch = processMessage((MqttContent) update); 
				if (matchingSearch)														
				{
					if (messageNavigationPaneController.showLatest())
					{
						eventManager.changeMessageIndexToFirst(foundMessageStore);
					}
					else
					{
						eventManager.incrementMessageIndex(foundMessageStore);
					}
				}
				
				updateTabTitle();
			}
			else
			{
				// Ignore
			}
		}
		
	}

	public void init()
	{
		eventManager.registerFormatChangeObserver(this, store);
		
		foundMessageStore = new ObservableMessageStore("search-" + store.getName(), Integer.MAX_VALUE, uiEventQueue);
		foundMessageStore.setFormatter(store.getFormatter());
		
		// searchPaneEventDispatcher = new EventDispatcher();
		// searchPaneEventDispatcher.addObserver(this);
		
		messageListTablePaneController.setItems(foundMessages);
		messageListTablePaneController.setStore(foundMessageStore);
		messageListTablePaneController.setEventManager(eventManager);
		messageListTablePaneController.init();
		eventManager.registerChangeMessageIndexObserver(messageListTablePaneController, foundMessageStore);
		
		messagePaneController.setStore(foundMessageStore);
		messagePaneController.init();		
		// The search pane's message browser wants to know about changing indices and format
		eventManager.registerChangeMessageIndexObserver(messagePaneController, foundMessageStore);
		eventManager.registerFormatChangeObserver(messagePaneController, foundMessageStore);
		
		messageNavigationPaneController.setStore(foundMessageStore);		
		messageNavigationPaneController.setEventManager(eventManager);
		messageNavigationPaneController.init();		
		// The search pane's message browser wants to know about show first, index change and update index events 
		eventManager.registerChangeMessageIndexObserver(messageNavigationPaneController, foundMessageStore);
		eventManager.registerChangeMessageIndexFirstObserver(messageNavigationPaneController, foundMessageStore);
		eventManager.registerIncrementMessageIndexObserver(messageNavigationPaneController, foundMessageStore);
	}

	public void setEventManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
	
	public void cleanup()
	{
		disableAutoSearch();
		
		// TODO:
		eventManager.deregisterFormatChangeObserver(this);
		// searchPaneEventDispatcher.deleteObserver(this);
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
