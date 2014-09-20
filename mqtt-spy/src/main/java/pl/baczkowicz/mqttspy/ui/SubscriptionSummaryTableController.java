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
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableSubscriptionTopicSummaryProperties;
import pl.baczkowicz.mqttspy.ui.events.EventDispatcher;
import pl.baczkowicz.mqttspy.ui.events.ShowFirstEvent;
import pl.baczkowicz.mqttspy.ui.utils.ContextMenuUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class SubscriptionSummaryTableController implements Initializable
{
	final static Logger logger = LoggerFactory.getLogger(SubscriptionSummaryTableController.class);

	private ObservableMessageStoreWithFiltering store; 
	
	private EventDispatcher navigationEventDispatcher;

	@FXML
	private TableView<ObservableSubscriptionTopicSummaryProperties> filterTable;

	@FXML
	private TableColumn<ObservableSubscriptionTopicSummaryProperties, Boolean> showColumn;

	@FXML
	private TableColumn<ObservableSubscriptionTopicSummaryProperties, String> topicColumn;
	
	@FXML
	private TableColumn<ObservableSubscriptionTopicSummaryProperties, String> contentColumn;

	@FXML
	private TableColumn<ObservableSubscriptionTopicSummaryProperties, Integer> messageCountColumn;

	@FXML
	private TableColumn<ObservableSubscriptionTopicSummaryProperties, String> lastReceivedColumn;

	public void initialize(URL location, ResourceBundle resources)
	{				
		// Table
		showColumn.setCellValueFactory(new PropertyValueFactory<ObservableSubscriptionTopicSummaryProperties, Boolean>(
				"show"));
		showColumn
				.setCellFactory(new Callback<TableColumn<ObservableSubscriptionTopicSummaryProperties, Boolean>, TableCell<ObservableSubscriptionTopicSummaryProperties, Boolean>>()
				{

					public TableCell<ObservableSubscriptionTopicSummaryProperties, Boolean> call(
							TableColumn<ObservableSubscriptionTopicSummaryProperties, Boolean> param)
					{
						final CheckBoxTableCell<ObservableSubscriptionTopicSummaryProperties, Boolean> cell = new CheckBoxTableCell<ObservableSubscriptionTopicSummaryProperties, Boolean>()
						{
							@Override
							public void updateItem(final Boolean checked, boolean empty)
							{
								super.updateItem(checked, empty);
								if (!isEmpty() && checked != null && this.getTableRow() != null)
								{
									final ObservableSubscriptionTopicSummaryProperties item = (ObservableSubscriptionTopicSummaryProperties) this.getTableRow().getItem();
									
									logger.info("[{}] Show property changed; topic = {}, show value = {}", store.getName(), item.topicProperty().getValue(), checked);
									boolean filteringChanged = false;
									
									if (checked)
									{
										store.applyFilter(item.topicProperty().getValue());
									}
									else
									{
										store.removeFilter(item.topicProperty().getValue());
									}
									
									if (filteringChanged)
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

		topicColumn.setCellValueFactory(new PropertyValueFactory<ObservableSubscriptionTopicSummaryProperties, String>(
				"topic"));

		contentColumn
				.setCellValueFactory(new PropertyValueFactory<ObservableSubscriptionTopicSummaryProperties, String>(
						"lastReceivedPayload"));

		messageCountColumn
				.setCellValueFactory(new PropertyValueFactory<ObservableSubscriptionTopicSummaryProperties, Integer>(
						"count"));
		messageCountColumn
				.setCellFactory(new Callback<TableColumn<ObservableSubscriptionTopicSummaryProperties, Integer>, TableCell<ObservableSubscriptionTopicSummaryProperties, Integer>>()
				{
					public TableCell<ObservableSubscriptionTopicSummaryProperties, Integer> call(
							TableColumn<ObservableSubscriptionTopicSummaryProperties, Integer> param)
					{
						final TableCell<ObservableSubscriptionTopicSummaryProperties, Integer> cell = new TableCell<ObservableSubscriptionTopicSummaryProperties, Integer>()
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
				.setCellValueFactory(new PropertyValueFactory<ObservableSubscriptionTopicSummaryProperties, String>(
						"lastReceivedTimestamp"));

		filterTable
				.setRowFactory(new Callback<TableView<ObservableSubscriptionTopicSummaryProperties>, TableRow<ObservableSubscriptionTopicSummaryProperties>>()
				{
					public TableRow<ObservableSubscriptionTopicSummaryProperties> call(
							TableView<ObservableSubscriptionTopicSummaryProperties> tableView)
					{
						final TableRow<ObservableSubscriptionTopicSummaryProperties> row = new TableRow<ObservableSubscriptionTopicSummaryProperties>()
						{
							@Override
							protected void updateItem(ObservableSubscriptionTopicSummaryProperties item, boolean empty)
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
		filterTable.setContextMenu(ContextMenuUtils.createTopicTableContextMenu(filterTable, store, navigationEventDispatcher));
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
}
