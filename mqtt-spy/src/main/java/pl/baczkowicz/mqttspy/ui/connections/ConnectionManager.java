package pl.baczkowicz.mqttspy.ui.connections;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.events.ui.UIEventHandler;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.ConnectionController;
import pl.baczkowicz.mqttspy.ui.MainController;
import pl.baczkowicz.mqttspy.ui.SubscriptionController;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;
import pl.baczkowicz.mqttspy.ui.utils.ContextMenuUtils;
import pl.baczkowicz.mqttspy.ui.utils.TabUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class ConnectionManager
{
	// private final static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	
	private final MqttManager mqttManager;
	
	private final EventManager eventManager;
	
	private final StatisticsManager statisticsManager;
	
	private final Map<Integer, ConnectionController> connectionControllers = new HashMap<>();
	private final Map<Integer, Tab> connectionTabs = new HashMap<>();
	private final Map<Integer, SubscriptionManager> subscriptionManagers = new HashMap<>();
	private final Queue<MqttSpyUIEvent> uiEventQueue;

	public ConnectionManager(final MqttManager mqttManager, final EventManager eventManager, final StatisticsManager statisticsManager)
	{
		this.uiEventQueue = new LinkedBlockingQueue<>();
		
		this.mqttManager = mqttManager;
		this.eventManager = eventManager;
		this.statisticsManager = statisticsManager;
		
		new Thread(new UIEventHandler(uiEventQueue, eventManager)).start();
	}
	
	public void loadConnectionTab(final MainController mainController,
			final Object parent, final RuntimeConnectionProperties connectionProperties)
	{		
		// Create connection
		final MqttConnection connection = mqttManager.createConnection(connectionProperties, uiEventQueue);
		connection.setOpening(true);

		// Load a new tab and connection pane
		final FXMLLoader loader = Utils.createFXMLLoader(parent, Utils.FXML_LOCATION + "ConnectionTab.fxml");
		AnchorPane connectionPane = Utils.loadAnchorPane(loader);
		
		final ConnectionController connectionController = (ConnectionController) loader.getController();
		connectionController.setConnection(connection);
		connectionController.setConnectionManager(this);
		connectionController.setEventManager(eventManager);
		connectionController.setStatisticsManager(statisticsManager);
		
		final Tab connectionTab = createConnectionTab(connection, connectionPane, connectionController);
		final SubscriptionManager subscriptionManager = new SubscriptionManager(eventManager, uiEventQueue);			
		
		final SubscriptionController subscriptionController = subscriptionManager.createSubscriptionTab(
				true, parent, connection, connection, null, connectionController);
		subscriptionController.setConnectionController(connectionController);
		
		final ConnectionManager connectionManager = this;
		
		Platform.runLater(new Runnable()
		{			
			@Override
			public void run()
			{					
				connectionController.init();
				subscriptionController.init();				
								
				mainController.addConnectionTab(connectionTab);
				connectionTab.setContextMenu(ContextMenuUtils.createConnectionMenu(mqttManager, connection, connectionController, connectionManager));
				subscriptionController.getTab().setContextMenu(ContextMenuUtils.createAllSubscriptionsTabContextMenu(subscriptionController.getTab(), connection, eventManager));
				
				eventManager.registerConnectionStatusObserver(connectionController, connection);
				// connection.addObserver(connectionController);											
				connection.setOpening(false);
				connection.setOpened(true);
				
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
				connectionController.getSubscriptionTabs().getTabs().add(subscriptionController.getTab());
				
				connectionControllers.put(connection.getId(), connectionController);
				connectionTabs.put(connection.getId(), connectionTab);
				subscriptionManagers.put(connection.getId(), subscriptionManager);
								
				// Populate panes
				mainController.populateConnectionPanes(connectionProperties.getConfiguredProperties(), connectionController);	
				
				// Apply perspective
				mainController.showPerspective(connectionController);
			}
		});		
	}
	
	public void disconnectAndCloseTab(final int connectionId)
	{		
		disconnect(mqttManager, connectionId);
		mqttManager.close(connectionId);	
		
		TabUtils.requestClose(connectionControllers.get(connectionId).getTab());
		connectionControllers.remove(connectionId);
		connectionTabs.remove(connectionId);
		subscriptionManagers.remove(connectionId);
	}
	
	public static void disconnect(final MqttManager mqttManager, final int connectionId)
	{
		mqttManager.disconnectFromBroker(connectionId);
	}

	private Tab createConnectionTab(final MqttConnection connection, final Node content,
			final ConnectionController connectionController)
	{
		final Tab tab = new Tab();
		connectionController.setTab(tab);
		tab.setText(connection.getProperties().getName());
		tab.setContent(content);		

		return tab;
	}
	
	public Map<Integer, ConnectionController> getConnectionControllers()
	{
		return connectionControllers;
	}

	private Set<Integer> getConnectionIds()
	{
		return connectionControllers.keySet();
	}
	
	public void disconnectAndCloseAll()
	{
		for (final Integer connectionId : getConnectionIds())
		{
			disconnectAndCloseTab(connectionId);
		}
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
