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
import pl.baczkowicz.mqttspy.connectivity.messagestore.SubscriptionTopicSummary;
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
	private TableView<SubscriptionTopicSummary> filterTable;

	@FXML
	private TableColumn<SubscriptionTopicSummary, Boolean> showColumn;

	@FXML
	private TableColumn<SubscriptionTopicSummary, String> topicColumn;
	
	@FXML
	private TableColumn<SubscriptionTopicSummary, String> contentColumn;

	@FXML
	private TableColumn<SubscriptionTopicSummary, Integer> messageCountColumn;

	@FXML
	private TableColumn<SubscriptionTopicSummary, String> lastReceivedColumn;

	public void initialize(URL location, ResourceBundle resources)
	{				
		// Table
		showColumn.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummary, Boolean>(
				"show"));
		showColumn
				.setCellFactory(new Callback<TableColumn<SubscriptionTopicSummary, Boolean>, TableCell<SubscriptionTopicSummary, Boolean>>()
				{

					public TableCell<SubscriptionTopicSummary, Boolean> call(
							TableColumn<SubscriptionTopicSummary, Boolean> param)
					{
						final CheckBoxTableCell<SubscriptionTopicSummary, Boolean> cell = new CheckBoxTableCell<SubscriptionTopicSummary, Boolean>()
						{
							@Override
							public void updateItem(Boolean checked, boolean empty)
							{
								super.updateItem(checked, empty);
								if (!isEmpty() && checked != null && this.getTableRow() != null)
								{
									final SubscriptionTopicSummary item = (SubscriptionTopicSummary) this.getTableRow().getItem();
									
									// if (checked != item.showProperty().getValue())
									{																		
										logger.info("Item changed = " + item.topicProperty().getValue());
										if (checked)
										{
											store.applyFilter(item.topicProperty().getValue());
										}
										else
										{
											store.removeFilter(item.topicProperty().getValue());
										}
									
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

		topicColumn.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummary, String>(
				"topic"));

		contentColumn
				.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummary, String>(
						"lastReceivedPayload"));

		messageCountColumn
				.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummary, Integer>(
						"count"));
		messageCountColumn
				.setCellFactory(new Callback<TableColumn<SubscriptionTopicSummary, Integer>, TableCell<SubscriptionTopicSummary, Integer>>()
				{
					public TableCell<SubscriptionTopicSummary, Integer> call(
							TableColumn<SubscriptionTopicSummary, Integer> param)
					{
						final TableCell<SubscriptionTopicSummary, Integer> cell = new TableCell<SubscriptionTopicSummary, Integer>()
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
				.setCellValueFactory(new PropertyValueFactory<SubscriptionTopicSummary, String>(
						"lastReceivedTimestamp"));

		filterTable
				.setRowFactory(new Callback<TableView<SubscriptionTopicSummary>, TableRow<SubscriptionTopicSummary>>()
				{
					public TableRow<SubscriptionTopicSummary> call(
							TableView<SubscriptionTopicSummary> tableView)
					{
						final TableRow<SubscriptionTopicSummary> row = new TableRow<SubscriptionTopicSummary>()
						{
							@Override
							protected void updateItem(SubscriptionTopicSummary item, boolean empty)
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
