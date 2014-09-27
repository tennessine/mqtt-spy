package pl.baczkowicz.mqttspy.ui;

import java.io.File;
import java.net.URL;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.observers.ScriptStateChangeObserver;
import pl.baczkowicz.mqttspy.scripts.ScriptManager;
import pl.baczkowicz.mqttspy.scripts.ScriptRunningState;
import pl.baczkowicz.mqttspy.ui.properties.PublicationScriptProperties;

public class PublicationScriptsController implements Initializable, ScriptStateChangeObserver
{
	final static Logger logger = LoggerFactory.getLogger(PublicationScriptsController.class);
	
	@FXML
	private TableView<PublicationScriptProperties> scriptTable;
	
    @FXML
    private TableColumn<PublicationScriptProperties, String> nameColumn;
        
    @FXML
    private TableColumn<PublicationScriptProperties, ScriptRunningState> runningStatusColumn;
    
    @FXML
    private TableColumn<PublicationScriptProperties, String> lastStartedColumn;
    
    @FXML
    private TableColumn<PublicationScriptProperties, Integer> messageCountColumn;
		
	private MqttConnection connection;

	private ScriptManager scriptManager;

	private EventManager eventManager;

	public void initialize(URL location, ResourceBundle resources)
	{
		// Table
		nameColumn.setCellValueFactory(new PropertyValueFactory<PublicationScriptProperties, String>("name"));

		messageCountColumn.setCellValueFactory(new PropertyValueFactory<PublicationScriptProperties, Integer>("count"));
		messageCountColumn
				.setCellFactory(new Callback<TableColumn<PublicationScriptProperties, Integer>, TableCell<PublicationScriptProperties, Integer>>()
				{
					public TableCell<PublicationScriptProperties, Integer> call(
							TableColumn<PublicationScriptProperties, Integer> param)
					{
						final TableCell<PublicationScriptProperties, Integer> cell = new TableCell<PublicationScriptProperties, Integer>()
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
		
		runningStatusColumn.setCellValueFactory(new PropertyValueFactory<PublicationScriptProperties, ScriptRunningState>("status"));
		runningStatusColumn
			.setCellFactory(new Callback<TableColumn<PublicationScriptProperties, ScriptRunningState>, TableCell<PublicationScriptProperties, ScriptRunningState>>()
		{
			public TableCell<PublicationScriptProperties, ScriptRunningState> call(
					TableColumn<PublicationScriptProperties, ScriptRunningState> param)
			{
				final TableCell<PublicationScriptProperties, ScriptRunningState> cell = new TableCell<PublicationScriptProperties, ScriptRunningState>()				
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

		lastStartedColumn
				.setCellValueFactory(new PropertyValueFactory<PublicationScriptProperties, String>(
						"lastStarted"));

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
		scriptManager = new ScriptManager(eventManager, connection);
		eventManager.registerScriptStateChangeObserver(this, null);
		refreshList();
		scriptTable.setItems(scriptManager.getObservableScriptList());		
		scriptTable.setContextMenu(createScriptTableContextMenu());
	}
	
	private void refreshList()
	{
		scriptManager.populateScripts(connection.getProperties().getConfiguredProperties().getPublicationScripts());
	}

	public void setConnection(MqttConnection connection)
	{
		this.connection = connection;
	}
	
	public void startScript(final File file)
	{
		scriptManager.evaluateScriptFile(file);
	}
	
	public void stopScript(final File file)
	{
		scriptManager.stopScriptFile(file);
	}
	
	public void setEventManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
	
	public ContextMenu createScriptTableContextMenu()
	{
		final ContextMenu contextMenu = new ContextMenu();
		
		// Start script
		final MenuItem startScriptItem = new MenuItem("[Script] Start");
		startScriptItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final PublicationScriptProperties item = scriptTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					startScript(item.getFile());
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
				final PublicationScriptProperties item = scriptTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					stopScript(item.getFile());
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
