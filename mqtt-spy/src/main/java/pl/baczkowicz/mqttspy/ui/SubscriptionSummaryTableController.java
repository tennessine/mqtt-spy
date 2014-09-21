package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.ui.events.EventDispatcher;
import pl.baczkowicz.mqttspy.ui.events.ShowFirstEvent;
import pl.baczkowicz.mqttspy.ui.properties.SubscriptionTopicSummaryProperties;
import pl.baczkowicz.mqttspy.ui.utils.ContextMenuUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class SubscriptionSummaryTableController implements Initializable
{
	final static Logger logger = LoggerFactory.getLogger(SubscriptionSummaryTableController.class);

	private ObservableMessageStoreWithFiltering store; 
	
	private EventDispatcher navigationEventDispatcher;

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

	private ConnectionController connectionController;

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
																
									if (store.updateFilter(item.topicProperty().getValue(), checked))
									{
										// Wouldn't get updated properly if this is in the same thread 
										Platform.runLater(new Runnable()
										{
											@Override
											public void run()
											{
												navigationEventDispatcher.dispatchEvent(new ShowFirstEvent());	
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

		messageCountColumn
				.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummaryProperties, Integer>(
						"count"));
		messageCountColumn
				.setCellFactory(new Callback<TableColumn<SubscriptionTopicSummaryProperties, Integer>, TableCell<SubscriptionTopicSummaryProperties, Integer>>()
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

		lastReceivedColumn
				.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummaryProperties, String>(
						"lastReceivedTimestamp"));

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
	
	public void init()
	{
		filterTable.setContextMenu(ContextMenuUtils.createTopicTableContextMenu(filterTable, store, navigationEventDispatcher, connectionController));
		filterTable.setItems(store.getObservableMessagesPerTopic());	
	}

	public void setStore(final ObservableMessageStoreWithFiltering store)
	{
		this.store = store;
	}
	
	public void setNavigationEventDispatcher(final EventDispatcher navigationEventDispatcher)
	{
		this.navigationEventDispatcher = navigationEventDispatcher;
	}
	
	public void setConnectionController(final ConnectionController connectionController)
	{
		this.connectionController = connectionController;
	}
}
