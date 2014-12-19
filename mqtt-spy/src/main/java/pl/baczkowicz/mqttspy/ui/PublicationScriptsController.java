package pl.baczkowicz.mqttspy.ui;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.observers.ScriptStateChangeObserver;
import pl.baczkowicz.mqttspy.scripts.InteractiveScriptManager;
import pl.baczkowicz.mqttspy.scripts.ObservablePublicationScriptProperties;
import pl.baczkowicz.mqttspy.scripts.ScriptRunningState;
import pl.baczkowicz.mqttspy.scripts.ScriptTypeEnum;

public class PublicationScriptsController implements Initializable, ScriptStateChangeObserver
{
	final static Logger logger = LoggerFactory.getLogger(PublicationScriptsController.class);
	
	@FXML
	private TableView<ObservablePublicationScriptProperties> scriptTable;
	
    @FXML
    private TableColumn<ObservablePublicationScriptProperties, String> nameColumn;

    @FXML
    private TableColumn<ObservablePublicationScriptProperties, ScriptTypeEnum> typeColumn;
    
    @FXML
    private TableColumn<ObservablePublicationScriptProperties, Boolean> repeatColumn;
        
    @FXML
    private TableColumn<ObservablePublicationScriptProperties, ScriptRunningState> runningStatusColumn;
    
    @FXML
    private TableColumn<ObservablePublicationScriptProperties, String> lastPublishedColumn;
    
    @FXML
    private TableColumn<ObservablePublicationScriptProperties, Long> messageCountColumn;
		
	private MqttAsyncConnection connection;

	private InteractiveScriptManager scriptManager;

	private EventManager eventManager;

	private Map<ScriptTypeEnum, ContextMenu> contextMenus = new HashMap<>();

	public void initialize(URL location, ResourceBundle resources)
	{
		// Table
		nameColumn.setCellValueFactory(new PropertyValueFactory<ObservablePublicationScriptProperties, String>("name"));
		
		typeColumn.setCellValueFactory(new PropertyValueFactory<ObservablePublicationScriptProperties, ScriptTypeEnum>("type"));
		typeColumn
			.setCellFactory(new Callback<TableColumn<ObservablePublicationScriptProperties, ScriptTypeEnum>, TableCell<ObservablePublicationScriptProperties, ScriptTypeEnum>>()
		{
			public TableCell<ObservablePublicationScriptProperties, ScriptTypeEnum> call(
					TableColumn<ObservablePublicationScriptProperties, ScriptTypeEnum> param)
			{
				final TableCell<ObservablePublicationScriptProperties, ScriptTypeEnum> cell = new TableCell<ObservablePublicationScriptProperties, ScriptTypeEnum>()				
				{
					@Override
					public void updateItem(ScriptTypeEnum item, boolean empty)
					{
						super.updateItem(item, empty);
						
						if (!isEmpty())
						{
							setText(item.toString());
						}
						else
						{
							setText(null);
							setGraphic(null);
						}
					}
				};
				cell.setAlignment(Pos.TOP_CENTER);
				
				return cell;
			}
		});

		repeatColumn.setCellValueFactory(new PropertyValueFactory<ObservablePublicationScriptProperties, Boolean>("repeat"));
		repeatColumn
		.setCellFactory(new Callback<TableColumn<ObservablePublicationScriptProperties, Boolean>, TableCell<ObservablePublicationScriptProperties, Boolean>>()
		{
			public TableCell<ObservablePublicationScriptProperties, Boolean> call(
					TableColumn<ObservablePublicationScriptProperties, Boolean> param)
			{
				final CheckBoxTableCell<ObservablePublicationScriptProperties, Boolean> cell = new CheckBoxTableCell<ObservablePublicationScriptProperties, Boolean>()
				{
					@Override
					public void updateItem(final Boolean checked, boolean empty)
					{
						super.updateItem(checked, empty);
						if (!isEmpty() && checked != null && this.getTableRow() != null && this.getTableRow().getItem() != null)
						{
							final ObservablePublicationScriptProperties item = (ObservablePublicationScriptProperties) this.getTableRow().getItem();
							
							// Anything but subscription scripts can be repeated
							if (!ScriptTypeEnum.SUBSCRIPTION.equals(item.typeProperty().getValue()))
							{	
								this.setDisable(false);
								if (logger.isTraceEnabled())
								{
									logger.trace("Setting repeat for {} to {}", item.getName(), checked);
								}
								
								item.repeatProperty().setValue(checked);
							}
							else
							{
								this.setDisable(true);
							}
						}									
					}
				};
				cell.setAlignment(Pos.TOP_CENTER);
				
				return cell;
			}
		});
		
		messageCountColumn.setCellValueFactory(new PropertyValueFactory<ObservablePublicationScriptProperties, Long>("count"));
		messageCountColumn
		.setCellFactory(new Callback<TableColumn<ObservablePublicationScriptProperties, Long>, TableCell<ObservablePublicationScriptProperties, Long>>()
		{
			public TableCell<ObservablePublicationScriptProperties, Long> call(
					TableColumn<ObservablePublicationScriptProperties, Long> param)
			{
				final TableCell<ObservablePublicationScriptProperties, Long> cell = new TableCell<ObservablePublicationScriptProperties, Long>()
				{
					@Override
					public void updateItem(Long item, boolean empty)
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
		
		runningStatusColumn.setCellValueFactory(new PropertyValueFactory<ObservablePublicationScriptProperties, ScriptRunningState>("status"));
		runningStatusColumn
			.setCellFactory(new Callback<TableColumn<ObservablePublicationScriptProperties, ScriptRunningState>, TableCell<ObservablePublicationScriptProperties, ScriptRunningState>>()
		{
			public TableCell<ObservablePublicationScriptProperties, ScriptRunningState> call(
					TableColumn<ObservablePublicationScriptProperties, ScriptRunningState> param)
			{
				final TableCell<ObservablePublicationScriptProperties, ScriptRunningState> cell = new TableCell<ObservablePublicationScriptProperties, ScriptRunningState>()				
				{
					@Override
					public void updateItem(ScriptRunningState item, boolean empty)
					{
						super.updateItem(item, empty);
						
						if (!isEmpty())
						{
							setText(item.toString());
									// if
									// (item.equals(ScriptRunningState.RUNNING))
									// {
									// final ProgressIndicator progressIndicator
									// = new ProgressIndicator();
									// progressIndicator.setMaxSize(12, 12);
									//
									// setGraphic(progressIndicator);
									//
									// }
									// else
									// {
									// setGraphic(null);
									// }
						}
						else
						{
							setText(null);
							setGraphic(null);
						}
					}
				};
				// cell.setMaxHeight(cell.getHeight());
				// cell.setPadding(new Insets(0, 0, 0, 0));
				cell.setAlignment(Pos.TOP_CENTER);
				
				return cell;
			}
		});

		lastPublishedColumn.setCellValueFactory(new PropertyValueFactory<ObservablePublicationScriptProperties, String>("lastPublished"));
		lastPublishedColumn.setCellFactory(new Callback<TableColumn<ObservablePublicationScriptProperties, String>, TableCell<ObservablePublicationScriptProperties, String>>()
		{
			public TableCell<ObservablePublicationScriptProperties, String> call(
					TableColumn<ObservablePublicationScriptProperties, String> param)
			{
				final TableCell<ObservablePublicationScriptProperties, String> cell = new TableCell<ObservablePublicationScriptProperties, String>()
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
		
		scriptTable
			.setRowFactory(new Callback<TableView<ObservablePublicationScriptProperties>, TableRow<ObservablePublicationScriptProperties>>()
			{
				public TableRow<ObservablePublicationScriptProperties> call(
						TableView<ObservablePublicationScriptProperties> tableView)
				{
					final TableRow<ObservablePublicationScriptProperties> row = new TableRow<ObservablePublicationScriptProperties>()
					{
						@Override
						protected void updateItem(ObservablePublicationScriptProperties item, boolean empty)
						{
							super.updateItem(item, empty);
							if (!isEmpty() && item != null)
							{
								final ContextMenu rowMenu = contextMenus.get(item.typeProperty().getValue());

								this.setContextMenu(rowMenu);
							}
						}
					};
	
					return row;
				}
			});				

		// scriptTable
		// .setRowFactory(new Callback<TableView<PublicationScriptProperties>,
		// TableRow<PublicationScriptProperties>>()
		// {
		// public TableRow<PublicationScriptProperties> call(
		// TableView<PublicationScriptProperties> tableView)
		// {
		// final TableRow<PublicationScriptProperties> row = new
		// TableRow<PublicationScriptProperties>()
		// {
		// @Override
		// protected void updateItem(PublicationScriptProperties item, boolean
		// empty)
		// {
		// super.updateItem(item, empty);
		// if (!isEmpty() && item.getSubscription() != null)
		// {
		// this.setStyle(Utils.createBgRGBString(item.getSubscription()
		// .getColor(), getIndex() % 2 == 0 ? 0.8 : 0.6)
		// + " -fx-background-radius: 6; ");
		// }
		// else
		// {
		// this.setStyle(null);
		// }
		// }
		// };
		//
		// return row;
		// }
		// });		
		
		// publicationTopicText.setItems(publicationTopics);
		// formatGroup.getToggles().get(0).setUserData(ConversionMethod.PLAIN);
		// formatGroup.getToggles().get(1).setUserData(ConversionMethod.HEX_DECODE);
		// formatGroup.selectToggle(formatGroup.getToggles().get(0));
		//
		// formatGroup.selectedToggleProperty().addListener(new
		// ChangeListener<Toggle>()
		// {
		// @Override
		// public void changed(ObservableValue<? extends Toggle> observable,
		// Toggle oldValue, Toggle newValue)
		// {
		// // If plain has been selected
		// if (newValue != null)
		// {
		// if
		// (formatGroup.getSelectedToggle().getUserData().equals(ConversionMethod.PLAIN))
		// {
		// showAsPlain();
		// }
		// else
		// {
		// showAsHex();
		// }
		// }
		// }
		// });
	}
	
	public void init()
	{
		scriptManager = connection.getScriptManager();
		eventManager.registerScriptStateChangeObserver(this, null);
		refreshList();
		scriptTable.setItems(scriptManager.getObservableScriptList());
		
		// Note: subscription script don't have context menus because they can't be started/stopped manually - for future, consider enabled/disabled
		contextMenus.put(ScriptTypeEnum.PUBLICATION, createDirectoryTypeScriptTableContextMenu());		
	}
	
	private void refreshList()
	{
		scriptManager.addScripts(connection.getProperties().getConfiguredProperties().getPublicationScripts());
		scriptManager.addSubscriptionScripts(connection.getProperties().getConfiguredProperties().getSubscription());
		
		// TODO: move this to script manager?
		eventManager.notifyScriptListChange(connection);
	}

	public void setConnection(MqttAsyncConnection connection)
	{
		this.connection = connection;
	}
	
	public void startScript(final ObservablePublicationScriptProperties item)
	{
		scriptManager.runScript(item, true);
	}
	
	public void stopScript(final File file)
	{
		scriptManager.stopScriptFile(file);
	}
	
	public void setEventManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
	
	public ContextMenu createDirectoryTypeScriptTableContextMenu()
	{
		final ContextMenu contextMenu = new ContextMenu();
		
		// Start script
		final MenuItem startScriptItem = new MenuItem("[Script] Start");
		startScriptItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final ObservablePublicationScriptProperties item = scriptTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					startScript(item);
				}
			}
		});
		contextMenu.getItems().add(startScriptItem);
		
		// Stop script
		final MenuItem stopScriptItem = new MenuItem("[Script] Stop");
		stopScriptItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final ObservablePublicationScriptProperties item = scriptTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					stopScript(item.getScriptFile());
				}
			}
		});
		contextMenu.getItems().add(stopScriptItem);

		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		// Refresh list
		final MenuItem refreshListItem = new MenuItem("[Scripts] Refresh list");
		refreshListItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				refreshList();
			}
		});
		contextMenu.getItems().add(refreshListItem);

		return contextMenu;
	}

	@Override
	public void onScriptStateChange(String scriptName, ScriptRunningState state)
	{
		// TODO: update the context menu - but this requires context menu per row
	}
}
