package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;
import pl.baczkowicz.mqttspy.connectivity.messagestore.MessageStore;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMqttContentProperties;
import pl.baczkowicz.mqttspy.ui.events.EventDispatcher;
import pl.baczkowicz.mqttspy.ui.events.MessageIndexChangedEvent;
import pl.baczkowicz.mqttspy.ui.utils.ContextMenuUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

@SuppressWarnings({"rawtypes"})
public class MessageListTableController implements Initializable, Observer
{
	final static Logger logger = LoggerFactory.getLogger(MessageListTableController.class);
	
	private ObservableList<ObservableMqttContentProperties> items; 
	
	private EventDispatcher navigationEventDispatcher;

	@FXML
	private TableView<ObservableMqttContentProperties> messageTable;

	@FXML
	private TableColumn<ObservableMqttContentProperties, String> messageTopicColumn;
	
	@FXML
	private TableColumn<ObservableMqttContentProperties, String> messageContentColumn;

	@FXML
	private TableColumn<ObservableMqttContentProperties, String> messageReceivedAtColumn;

	private MessageStore store;

	public void initialize(URL location, ResourceBundle resources)
	{				
		// Table
		messageTopicColumn.setCellValueFactory(new PropertyValueFactory<ObservableMqttContentProperties, String>(
				"topic"));

		messageContentColumn
				.setCellValueFactory(new PropertyValueFactory<ObservableMqttContentProperties, String>(
						"lastReceivedPayload"));

		messageReceivedAtColumn
				.setCellValueFactory(new PropertyValueFactory<ObservableMqttContentProperties, String>(
						"lastReceivedTimestamp"));

		messageTable
				.setRowFactory(new Callback<TableView<ObservableMqttContentProperties>, TableRow<ObservableMqttContentProperties>>()
				{
					public TableRow<ObservableMqttContentProperties> call(
							TableView<ObservableMqttContentProperties> tableView)
					{
						final TableRow<ObservableMqttContentProperties> row = new TableRow<ObservableMqttContentProperties>()
						{
							@Override
							protected void updateItem(ObservableMqttContentProperties item, boolean empty)
							{
								super.updateItem(item, empty);
								if (!isEmpty() && item.getSubscription() != null)
								{
									
									this.setStyle(Utils.createBgRGBString(item.getSubscription()
											.getColor(), getIndex() % 2 == 0 ? 0.8 : 0.6)
											+ " -fx-background-radius: 6; ");
								}
								else
								{
									this.setStyle(null);
								}
							}
						};

						return row;
					}
				});
		messageTable.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue observable, Number oldValue, Number newValue)
			{
				final ObservableMqttContentProperties item = messageTable.getSelectionModel().getSelectedItem();
				if (item != null)
				{
					final Object[] array = store.getMessages().toArray();
					for (int i = 0; i < store.getMessages().size(); i++)
					{
						if (((MqttContent) array[i]).getId() == item.getId())
						{
							navigationEventDispatcher.dispatchEvent(new MessageIndexChangedEvent(array.length - i));
						}
					}

				}
			}
		});
	}
	
	public void update(Observable observable, Object update)
	{
		if (update instanceof MessageIndexChangedEvent)
		{
			if (store.getMessages().size() > 0)
			{
				final int messageIndex = ((MessageIndexChangedEvent) update).getIndex();
				final long id = ((MqttContent) store.getMessages().toArray()[store.getMessages().size()
						- messageIndex]).getId();
	
				for (final ObservableMqttContentProperties item : items)
				{
					if (item.getId() == id)
					{
						if (!item.equals(messageTable.getSelectionModel().getSelectedItem()))
						{
							messageTable.getSelectionModel().select(item);
							break;
						}
					}
				}
			}
		}
	}
	
	public void init()
	{
		navigationEventDispatcher.addObserver(this);
		
		messageTable.setContextMenu(ContextMenuUtils.createMessageListTableContextMenu(messageTable, navigationEventDispatcher));
		messageTable.setItems(items);	
	}

	public void setItems(final ObservableList<ObservableMqttContentProperties> items)
	{
		this.items = items;
	}
	
	public void setStore(final MessageStore store)
	{
		this.store = store;
	}
	
	public void setNavigationEventDispatcher(final EventDispatcher navigationEventDispatcher)
	{
		this.navigationEventDispatcher = navigationEventDispatcher;
	}
}
