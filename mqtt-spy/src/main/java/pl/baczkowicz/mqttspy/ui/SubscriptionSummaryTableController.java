package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.storage.ManagedMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.ui.properties.SubscriptionTopicSummaryProperties;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class SubscriptionSummaryTableController implements Initializable
{
	final static Logger logger = LoggerFactory.getLogger(SubscriptionSummaryTableController.class);

	private ManagedMessageStoreWithFiltering store; 
	
	@FXML
	private TableView<SubscriptionTopicSummaryProperties> filterTable;

	@FXML
	private TableColumn<SubscriptionTopicSummaryProperties, Boolean> showColumn;

	@FXML
	private TableColumn<SubscriptionTopicSummaryProperties, String> topicColumn;
	
	@FXML
	private TableColumn<SubscriptionTopicSummaryProperties, String> contentColumn;

	@FXML
	private TableColumn<SubscriptionTopicSummaryProperties, Integer> messageCountColumn;

	@FXML
	private TableColumn<SubscriptionTopicSummaryProperties, String> lastReceivedColumn;

	private FilteredList<SubscriptionTopicSummaryProperties> filteredData;
	
	private ConnectionController connectionController;
	private EventManager eventManager;

	// private ObservableList<SubscriptionTopicSummaryProperties> nonFilteredData;
	
	public void initialize(URL location, ResourceBundle resources)
	{				
		// Table
		showColumn.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummaryProperties, Boolean>(
				"show"));
		showColumn
				.setCellFactory(new Callback<TableColumn<SubscriptionTopicSummaryProperties, Boolean>, TableCell<SubscriptionTopicSummaryProperties, Boolean>>()
		{
			public TableCell<SubscriptionTopicSummaryProperties, Boolean> call(
					TableColumn<SubscriptionTopicSummaryProperties, Boolean> param)
			{
				final CheckBoxTableCell<SubscriptionTopicSummaryProperties, Boolean> cell = new CheckBoxTableCell<SubscriptionTopicSummaryProperties, Boolean>()
				{
					@Override
					public void updateItem(final Boolean checked, boolean empty)
					{
						super.updateItem(checked, empty);
						if (!isEmpty() && checked != null && this.getTableRow() != null)
						{
							final SubscriptionTopicSummaryProperties item = (SubscriptionTopicSummaryProperties) this.getTableRow().getItem();
							
							logger.trace("[{}] Show property changed; topic = {}, show value = {}", store.getName(), item.topicProperty().getValue(), checked);
														
							if (store.getFilteredMessageStore().updateFilter(item.topicProperty().getValue(), checked))
							{
								// Wouldn't get updated properly if this is in the same thread 
								Platform.runLater(new Runnable()
								{
									@Override
									public void run()
									{
										eventManager.navigateToFirst(store);	
									}											
								});
							}																			
						}									
					}
				};
				cell.setAlignment(Pos.TOP_CENTER);
				
				return cell;
			}
		});

		topicColumn.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummaryProperties, String>(
				"topic"));

		contentColumn
				.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummaryProperties, String>(
						"lastReceivedPayload"));

		messageCountColumn.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummaryProperties, Integer>("count"));
		messageCountColumn.setCellFactory(new Callback<TableColumn<SubscriptionTopicSummaryProperties, Integer>, TableCell<SubscriptionTopicSummaryProperties, Integer>>()
		{
			public TableCell<SubscriptionTopicSummaryProperties, Integer> call(
					TableColumn<SubscriptionTopicSummaryProperties, Integer> param)
			{
				final TableCell<SubscriptionTopicSummaryProperties, Integer> cell = new TableCell<SubscriptionTopicSummaryProperties, Integer>()
				{
					@Override
					public void updateItem(Integer item, boolean empty)
					{
						super.updateItem(item, empty);
						if (!isEmpty())
						{
							setText(item.toString());
						}
						else
						{
							setText(null);
						}
					}
				};
				cell.setAlignment(Pos.TOP_CENTER);
				
				return cell;
			}
		});

		lastReceivedColumn.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummaryProperties, String>("lastReceivedTimestamp"));
		lastReceivedColumn.setCellFactory(new Callback<TableColumn<SubscriptionTopicSummaryProperties, String>, TableCell<SubscriptionTopicSummaryProperties, String>>()
		{
			public TableCell<SubscriptionTopicSummaryProperties, String> call(
					TableColumn<SubscriptionTopicSummaryProperties, String> param)
			{
				final TableCell<SubscriptionTopicSummaryProperties, String> cell = new TableCell<SubscriptionTopicSummaryProperties, String>()
				{
					@Override
					public void updateItem(String item, boolean empty)
					{
						super.updateItem(item, empty);
						if (!isEmpty())
						{
							setText(item.toString());
						}
						else
						{
							setText(null);
						}
					}
				};
				cell.setAlignment(Pos.TOP_CENTER);
				
				return cell;
			}
		});

		filterTable
		.setRowFactory(new Callback<TableView<SubscriptionTopicSummaryProperties>, TableRow<SubscriptionTopicSummaryProperties>>()
		{
			public TableRow<SubscriptionTopicSummaryProperties> call(
					TableView<SubscriptionTopicSummaryProperties> tableView)
			{
				final TableRow<SubscriptionTopicSummaryProperties> row = new TableRow<SubscriptionTopicSummaryProperties>()
				{
					@Override
					protected void updateItem(SubscriptionTopicSummaryProperties item, boolean empty)
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
	}
	
	public void setEventManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
	
	public void init()
	{		
		filterTable.setContextMenu(createTopicTableContextMenu());		
		
		final ObservableList<SubscriptionTopicSummaryProperties> nonFilteredData = 
				store.getNonFilteredMessageList().getTopicSummary().getObservableMessagesPerTopic();
		
		filteredData = new FilteredList<>(nonFilteredData);
				
		filterTable.setItems(new SortedList<>(filteredData));
	}
	
	public int getFilteredDataSize()
	{
		return filteredData.size();
	}
	
	public void updateTopicFilter(final String topicFilter)
	{
		filteredData.setPredicate(new Predicate<SubscriptionTopicSummaryProperties>()
		{
			@Override
			public boolean test(final SubscriptionTopicSummaryProperties item)
			{
				// If filter text is empty, display all persons.
	            if (topicFilter == null || topicFilter.isEmpty()) 
	            {
	                return true;
	            }

	            if (item.topicProperty().getValue().toLowerCase().indexOf(topicFilter.toLowerCase()) != -1) 
	            {
	            	// Filter matches first name
	                return true; 
	            }
	            
	            // Does not match
	            return false; 
			}
		});
	}

	public void setStore(final ManagedMessageStoreWithFiltering store)
	{
		this.store = store;
	}
	
	// public void setNavigationEventDispatcher(final EventDispatcher
	// navigationEventDispatcher)
	// {
	// this.navigationEventDispatcher = navigationEventDispatcher;
	// }
	
	public void setConnectionController(final ConnectionController connectionController)
	{
		this.connectionController = connectionController;
	}
	
	public ContextMenu createTopicTableContextMenu()
	{
		final ContextMenu contextMenu = new ContextMenu();
		
		// Copy topic
		final MenuItem copyTopicItem = new MenuItem("[Topic] Copy to clipboard");
		copyTopicItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					final ClipboardContent content = new ClipboardContent();
					content.putString(item.topicProperty().getValue());
					Clipboard.getSystemClipboard().setContent(content);
				}
			}
		});
		contextMenu.getItems().add(copyTopicItem);
		
		// Subscribe to topic
		final MenuItem subscribeToTopicItem = new MenuItem("[Topic] Subscribe (and create tab)");
		subscribeToTopicItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					final SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
					subscriptionDetails.setTopic(item.topicProperty().getValue());
					subscriptionDetails.setQos(0);
					
					connectionController.getNewSubscriptionPaneController().subscribe(subscriptionDetails, true);
				}
			}
		});
		contextMenu.getItems().add(subscribeToTopicItem);

		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		// Copy content
		final MenuItem copyContentItem = new MenuItem("[Content] Copy to clipboard");
		copyContentItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					final ClipboardContent content = new ClipboardContent();
					content.putString(item.lastReceivedPayloadProperty().getValue());
					Clipboard.getSystemClipboard().setContent(content);
				}
			}
		});
		contextMenu.getItems().add(copyContentItem);
		
		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		// Apply filters
		final MenuItem selectAllTopicsItem = new MenuItem("[Browse] Select all topics");
		selectAllTopicsItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					store.setAllShowValues(true, filteredData);
					eventManager.navigateToFirst(store);
				}
			}
		});
		contextMenu.getItems().add(selectAllTopicsItem);

		// Toggle filters
		final MenuItem toggleAllTopicsItem = new MenuItem("[Browse] Toggle all topics");
		toggleAllTopicsItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					store.toggleAllShowValues(filteredData);
					eventManager.navigateToFirst(store);
				}
			}
		});
		contextMenu.getItems().add(toggleAllTopicsItem);
		
		// Remove filters
		final MenuItem removeAllTopicsItem = new MenuItem("[Browse] Clear all selected topics");
		removeAllTopicsItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					store.setAllShowValues(false, filteredData);
					eventManager.navigateToFirst(store);
				}
			}
		});
		contextMenu.getItems().add(removeAllTopicsItem);

		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());		
		
		// Only this topic
		final MenuItem selectOnlyThisItem = new MenuItem("[Browse] Select only this");
		selectOnlyThisItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					store.setAllShowValues(false, null);
					store.setShowValue(item.topicProperty().getValue(), true);
					eventManager.navigateToFirst(store);
				}
			}
		});
		contextMenu.getItems().add(selectOnlyThisItem);

		return contextMenu;
	}
}
