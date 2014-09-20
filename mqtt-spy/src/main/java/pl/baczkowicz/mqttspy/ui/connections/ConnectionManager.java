package pl.baczkowicz.mqttspy.ui.connections;

import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.ConnectionController;
import pl.baczkowicz.mqttspy.ui.MainController;
import pl.baczkowicz.mqttspy.ui.SubscriptionController;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;
import pl.baczkowicz.mqttspy.ui.utils.ContextMenuUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class ConnectionManager
{
	private final MqttManager mqttManager;
	
	private final EventManager eventManager;
	
	private final StatisticsManager statisticsManager;
	
	private final Map<Integer, ConnectionController> connectionControllers = new HashMap<>();
	private final Map<Integer, Tab> connectionTabs = new HashMap<>();
	private final Map<Integer, SubscriptionManager> subscriptionManagers = new HashMap<>();

	public ConnectionManager(final MqttManager mqttManager, final EventManager eventManager, final StatisticsManager statisticsManager)
	{
		this.mqttManager = mqttManager;
		this.eventManager = eventManager;
		this.statisticsManager = statisticsManager;
	}
	
	public ConnectionController loadConnectionTab(final MainController mainController,
			final Object parent, final RuntimeConnectionProperties connectionProperties)
	{		
		// Create connection
		final MqttConnection connection = mqttManager.createConnection(connectionProperties);

		// Load a new tab and connection pane
		final FXMLLoader loader = Utils.createFXMLLoader(parent, Utils.FXML_LOCATION + "ConnectionTab.fxml");
		AnchorPane connectionPane = Utils.loadAnchorPane(loader);
		final ConnectionController connectionController = (ConnectionController) loader.getController();
				
		final Tab connectionTab = createConnectionTab(connection, connectionPane, connectionController, parent);
		final SubscriptionManager subscriptionManager = new SubscriptionManager(eventManager, statisticsManager);
		
		connectionControllers.put(connection.getId(), connectionController);
		connectionTabs.put(connection.getId(), connectionTab);
		subscriptionManagers.put(connection.getId(), subscriptionManager);
		
		mainController.addConnectionTab(connectionTab);

		connection.addObserver(connectionController);
		connection.setOpened(true);
		// connection.setTab(connectionTab);

		connectionController.setConnection(connection);
		connectionController.setConnectionManager(this);
		connectionController.setStatisticsManager(statisticsManager);
		connectionController.init();
		
		// Connect
		if (connection.getProperties().isAutoConnect())
		{
			mqttManager.connectToBroker(connection);
		}
		else
		{
			connection.setConnectionStatus(MqttConnectionStatus.NOT_CONNECTED);
		}	
		
		// Add "All" subscription tab
		connectionController.getSubscriptionTabs().getTabs().clear();
		final SubscriptionController subscriptionController = subscriptionManager.createSubscriptionTab(
				true, parent, connection, connection, null, connectionController);
		connectionController.getSubscriptionTabs().getTabs().add(subscriptionController.getTab());
		
		return connectionController;
	}
	
	// TODO: this needs to be called
	public void closeConnectionTab(final int connectionId)
	{
		connectionControllers.remove(connectionId);
		connectionTabs.remove(connectionId);
		subscriptionManagers.remove(connectionId);
	}

	private Tab createConnectionTab(
			final MqttConnection connection, final Node content,
			final ConnectionController connectionController, Object parent)
	{
		final Tab tab = new Tab();
		connectionController.setTab(tab);
		tab.setText(connection.getProperties().getName());
		tab.setContent(content);
		tab.setContextMenu(ContextMenuUtils.createConnectionMenu(mqttManager, this, connection, connectionController, tab, parent));

		return tab;
	}
	
	public Map<Integer, ConnectionController> getConnectionControllers()
	{
		return connectionControllers;
	}

	public Map<Integer, Tab> getConnectionTabs()
	{
		return connectionTabs;
	}
	
	public SubscriptionManager getSubscriptionManager(final int connectionId)
	{
		return subscriptionManagers.get(connectionId);
	}
}
