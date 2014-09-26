package pl.baczkowicz.mqttspy.ui;

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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.scripts.ScriptManager;
import pl.baczkowicz.mqttspy.scripts.ScriptRunningState;
import pl.baczkowicz.mqttspy.ui.properties.PublicationScriptProperties;
import pl.baczkowicz.mqttspy.ui.properties.SubscriptionTopicSummaryProperties;

public class PublicationScriptsController implements Initializable
{
	final static Logger logger = LoggerFactory.getLogger(PublicationScriptsController.class);
	
	@FXML
	private TableView<PublicationScriptProperties> scriptTable;
	
    @FXML
    private TableColumn<PublicationScriptProperties, String> nameColumn;
    
	// @FXML
	// private TableColumn<PublicationScriptProperties, String> actionColumn;
    
    @FXML
    private TableColumn<PublicationScriptProperties, ScriptRunningState> runningStatusColumn;
    
    @FXML
    private TableColumn<PublicationScriptProperties, String> lastCompletedColumn;
    
    @FXML
    private TableColumn<PublicationScriptProperties, Integer> messageCountColumn;
		
	//private ObservableList<String> publicationTopics = FXCollections.observableArrayList();

	private MqttConnection connection;

	private ScriptManager scriptManager;

	public void initialize(URL location, ResourceBundle resources)
	{
		// Table
		nameColumn.setCellValueFactory(new PropertyValueFactory<PublicationScriptProperties, String>("name"));

		messageCountColumn
				.setCellValueFactory(new PropertyValueFactory<PublicationScriptProperties, Integer>("count"));
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

		lastCompletedColumn
				.setCellValueFactory(new PropertyValueFactory<PublicationScriptProperties, String>(
						"lastCompleted"));

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
		scriptManager = new ScriptManager(connection);
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
					scriptManager.evaluateScriptFile(item.getFile());
					// final ClipboardContent content = new ClipboardContent();
					// content.putString(item.topicProperty().getValue());
					// Clipboard.getSystemClipboard().setContent(content);
				}
			}
		});
		contextMenu.getItems().add(startScriptItem);
		
		// Pause script
		final MenuItem pauseScriptItem = new MenuItem("[Script] Pause");
		pauseScriptItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final PublicationScriptProperties item = scriptTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
//					final SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
//					subscriptionDetails.setTopic(item.topicProperty().getValue());
//					subscriptionDetails.setQos(0);
//					
//					connectionController.getNewSubscriptionPaneController().subscribe(subscriptionDetails, true);
				}
			}
		});
		contextMenu.getItems().add(pauseScriptItem);
		
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
//							final SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
//							subscriptionDetails.setTopic(item.topicProperty().getValue());
//							subscriptionDetails.setQos(0);
//							
//							connectionController.getNewSubscriptionPaneController().subscribe(subscriptionDetails, true);
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
}
