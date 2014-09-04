package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import org.controlsfx.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.configuration.ConfiguredConnectionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.ConnectionDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.connectivity.MqttUtils;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.observers.ConnectionStatusChangeObserver;
import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;
import pl.baczkowicz.mqttspy.ui.utils.DialogUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EditConnectionsController extends AnchorPane implements Initializable, ConnectionStatusChangeObserver
{
	private final static String NEW_ITEM = "* ";
	
	private final static Logger logger = LoggerFactory.getLogger(EditConnectionsController.class);

	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private EditConnectionController editConnectionPaneController;
	
	@FXML
	private ListView<String> connectionList;
	
	@FXML
	private Button newConnectionButton;
	
	@FXML
	private Button duplicateConnectionButton;
	
	@FXML
	private Button deleteConnectionButton;
	
	@FXML
	private Button importConnectionsButton;
	
	@FXML
	private Button applyAllButton;
	
	@FXML
	private Button undoAllButton;
	
	private MqttManager mqttManager;

	private MainController mainController;

	private ConfigurationManager configurationManager;

	private List<ConfiguredConnectionDetails> connections = new ArrayList<ConfiguredConnectionDetails>();

	private EventManager eventManager;

	// ===============================
	// === Initialisation ============
	// ===============================
	
	private void showSelected()
	{
		synchronized (connections)
		{
			if (connectionList.getItems().size() > 0 && getSelectedIndex() == -1)
			{
				selectFirst();
				return;
			}

			duplicateConnectionButton.setDisable(true);
			deleteConnectionButton.setDisable(true);

			if (connectionList.getItems().size() > 0)
			{
				deleteConnectionButton.setDisable(false);
				duplicateConnectionButton.setDisable(false);
				
				if (!connections.get(getSelectedIndex()).isBeingCreated())
				{
					// logger.info("Editing connection {}", connections.get(getSelectedIndex()).getName());
					
					editConnectionPaneController.setRecordModifications(false);
					editConnectionPaneController.editConnection(connections.get(getSelectedIndex()));
					editConnectionPaneController.setRecordModifications(true);							
				}
			}
		}
	}
	
	public void initialize(URL location, ResourceBundle resources)
	{
		duplicateConnectionButton.setDisable(true);
		deleteConnectionButton.setDisable(true);
		
		connectionList.getStyleClass().add("connectionList");
		connectionList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				showSelected();
			}
		});
		connectionList.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent mouseEvent)
			{
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
				{
					if (mouseEvent.getClickCount() == 2)
					{
						try
						{
							editConnectionPaneController.createConnection();
						}
						catch (ConfigurationException e)
						{
							// TODO
							e.printStackTrace();
						}
					}
				}
			}
		});
	}	
		
	public void init()
	{		
		connections = configurationManager.getConnections();		
		eventManager.registerConnectionStatusObserver(this, null);
		
		editConnectionPaneController.setConfigurationManager(configurationManager);
		editConnectionPaneController.setManager(mqttManager);
		editConnectionPaneController.setMainController(mainController);
		editConnectionPaneController.setEditConnectionsController(this);
		editConnectionPaneController.init();
		
		editConnectionPaneController.getConnectionName().textProperty().addListener(new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				connectionNameChanged();
			}
		
		});
		
		editConnectionPaneController.setRecordModifications(false);
		listConnections();
		editConnectionPaneController.setRecordModifications(true);
	}
	
	// ===============================
	// === FXML ======================
	// ===============================

	@FXML
	public void newConnection()
	{
		final ConnectionDetails baseConnection = new ConnectionDetails();		
		baseConnection.setName("");
		baseConnection.setServerURI("127.0.0.1");
		baseConnection.setClientID(MqttUtils.generateClientIdWithTimestamp(System.getProperty("user.name")));
		baseConnection.setAutoConnect(true);
		
		final ConfiguredConnectionDetails connection = new ConfiguredConnectionDetails(configurationManager.getNextAvailableId(), true, true, true, baseConnection);
		
		connections.add(connection);
		newConnectionMode(connection);
	}
	
	@FXML
	private void duplicateConnection()
	{
		final ConfiguredConnectionDetails connection = new ConfiguredConnectionDetails(configurationManager.getNextAvailableId(), true, true, true, connections.get(getSelectedIndex()));		
		connections.add(connection);
		newConnectionMode(connection);
	}
	
	@FXML
	private void deleteConnection()
	{
		connections.get(getSelectedIndex()).setDeleted(true);
		
		if (DialogUtils.showDeleteQuestion(connections.get(getSelectedIndex()).getName()) == Dialog.Actions.YES)
		{
			connections.remove(getSelectedIndex());
			listConnections();
			selectFirst();
		
			saveAll();
		}
	}
	
	private void saveAll()
	{
		logger.debug("Saving all connections");
		configurationManager.saveConfiguration();
	}
	
	@FXML
	private void undoAll()
	{
		for (final ConfiguredConnectionDetails connection : connections)
		{
			connection.undo();
		}
		
		listConnections();
	}
	
	@FXML
	private void applyAll()
	{
		for (final ConfiguredConnectionDetails connection : connections)
		{
			connection.apply();
		}
		
		listConnections();
		
		saveAll();
	}
	
	@FXML
	private void importConnections()
	{
		// TODO: import
	}
	
	// ===============================
	// === Logic =====================
	// ===============================

	private void selectFirst()
	{
		// Select the first item if any connections present
		if (connectionList.getItems().size() > 0)
		{
			connectionList.getSelectionModel().select(0);
		}
	}
	
	private void selectLast()
	{
		connectionList.getSelectionModel().select(connectionList.getItems().size() - 1);
	}
	
	private int getSelectedIndex()
	{
		return connectionList.getSelectionModel().getSelectedIndex();
	}
	
	public void listConnections()
	{
		// logger.info("Listing connections: {}", connections.size());
		
		final int selected = getSelectedIndex();

		applyAllButton.setDisable(true);
		undoAllButton.setDisable(true);
		connectionList.getItems().clear();
		
		for (final ConfiguredConnectionDetails connection : connections)
		{
			if (connection.isModified())
			{
				connectionList.getItems().add(NEW_ITEM + connection.getName());
				applyAllButton.setDisable(false);
				undoAllButton.setDisable(false);
			}
			else
			{
				connectionList.getItems().add(connection.getName());
			}
		}
		
		// Reselect
		if (selected >= 0)
		{
			connectionList.getSelectionModel().select(selected);
		}
		else
		{
			selectFirst();
		}
		
		if (connectionList.getItems().size() > 0)
		{			
			deleteConnectionButton.setDisable(false);
		}
		else
		{
			deleteConnectionButton.setDisable(true);
			duplicateConnectionButton.setDisable(true);
		}
	}	
	
	protected void connectionNameChanged()
	{
		if (getSelectedIndex() >= 0)
		{
			final String newName = editConnectionPaneController.getConnectionName().getText();
			connections.get(getSelectedIndex()).setName(newName);
			listConnections();
		}
	}
	
	private void newConnectionMode(final ConfiguredConnectionDetails createdConnection)
	{			
		listConnections();
		selectLast();
		
		// Put an entry for the new connection and select it
		// editConnectionPaneController.setNewConnectionMode(true);						
		editConnectionPaneController.setRecordModifications(false);	
		
		if (createdConnection.getName() != null)
		{
			editConnectionPaneController.editConnection(createdConnection);
		}
		else
		{
			editConnectionPaneController.editConnection(createdConnection);			
		}
		editConnectionPaneController.setRecordModifications(true);
	}
	
	// ===============================
	// === Setters and getters =======
	// ===============================

	public void setMainController(MainController mainController)
	{
		this.mainController = mainController;
	}
	
	public void setManager(MqttManager manager)
	{
		this.mqttManager = manager;
	}
	
	public void setConfigurationManager(final ConfigurationManager configurationManager)
	{
		this.configurationManager = configurationManager;
	}	

	public MqttManager getManager()
	{
		return mqttManager;
	}

	public void setEventManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;		
	}

	@Override
	public void onConnectionStatusChanged(final MqttConnection changedConnection)
	{
		showSelected();		
	}
}
