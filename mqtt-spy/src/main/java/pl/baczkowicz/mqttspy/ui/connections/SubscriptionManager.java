package pl.baczkowicz.mqttspy.ui.connections;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import pl.baczkowicz.mqttspy.configuration.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.ConnectionController;
import pl.baczkowicz.mqttspy.ui.SubscriptionController;
import pl.baczkowicz.mqttspy.ui.utils.ContextMenuUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class SubscriptionManager
{
	private final static Logger logger = LoggerFactory.getLogger(SubscriptionManager.class);
	
	public static String ALL_SUBSCRIPTIONS_TAB_TITLE = "All";
		
	private final EventManager eventManager;
	
	private final StatisticsManager statisticsManager;
	
	private final Map<String, SubscriptionController> subscriptionControllers = new HashMap<>();
	
	private final Queue<MqttSpyUIEvent> uiEventQueue;
	
	public SubscriptionManager(final EventManager eventManager, final StatisticsManager statisticsManager, final Queue<MqttSpyUIEvent> uiEventQueue)
	{
		this.eventManager = eventManager;
		this.statisticsManager = statisticsManager;
		this.uiEventQueue = uiEventQueue;
	}
	
	public void createSubscription(final Color color, final boolean subscribe, final SubscriptionDetails subscriptionDetails, 
			final MqttConnection connection, final ConnectionController connectionController, Object parent)
	{
		logger.info("Creating subscription for " + subscriptionDetails.getTopic());
		final MqttSubscription subscription = new MqttSubscription(subscriptionDetails.getTopic(),
				subscriptionDetails.getQos(), color, connection.getMaxMessageStoreSize(), uiEventQueue);
		subscription.setConnection(connection);
		
		// Add a new tab
		final SubscriptionController subscriptionController = createSubscriptionTab(false, parent, subscription, connection,
				subscription, connectionController);
		subscriptionController.getTab().setContextMenu(ContextMenuUtils.createSubscriptionTabContextMenu(subscriptionController.getTab(), connection, subscription, eventManager));
		subscription.setSubscriptionController(subscriptionController);
		
		final TabPane subscriptionTabs = connectionController.getSubscriptionTabs();
		
		subscriptionTabs.getTabs().add(subscriptionController.getTab());
		
		if (subscribe)
		{
			connection.subscribe(subscription);
		}
		else
		{
			connection.addSubscription(subscription);
			subscription.setActive(false);
		}
	}
	
	public Map<String, SubscriptionController> getSubscriptionControllers()
	{
		return subscriptionControllers;
	}

	public SubscriptionController createSubscriptionTab(final boolean allTab, final Object parent,
			final ObservableMessageStoreWithFiltering observableMessageStore, final MqttConnection connection,
			final MqttSubscription subscription, final ConnectionController connectionController)
	{
		// Load a new tab and connection pane
		final FXMLLoader loader = Utils.createFXMLLoader(parent, Utils.FXML_LOCATION + "SubscriptionPane.fxml");

		final AnchorPane subscriptionPane = Utils.loadAnchorPane(loader);
		final SubscriptionController subscriptionController = ((SubscriptionController) loader.getController());
		
		final Tab tab = new Tab();
		observableMessageStore.addObserver(subscriptionController);
		subscriptionController.setStore(observableMessageStore);
		subscriptionController.setEventManager(eventManager);
		subscriptionController.setTab(tab);
		subscriptionController.setStatisticsManager(statisticsManager);
		subscriptionController.setConnectionProperties(connection.getProperties());
				
		tab.setClosable(false);
		tab.setContent(subscriptionPane);

		if (subscription != null)
		{
			tab.setStyle(Utils.createBaseRGBString(subscription.getColor()));
		}

		if (allTab)
		{
			subscriptionControllers.put(ALL_SUBSCRIPTIONS_TAB_TITLE, subscriptionController);						
			tab.setGraphic(new Label(ALL_SUBSCRIPTIONS_TAB_TITLE));
			tab.getGraphic().getStyleClass().add("subscribed");
		}
		else
		{
			subscriptionControllers.put(subscription.getTopic(), subscriptionController);						
			tab.setGraphic(new Label(subscription.getTopic()));
			tab.getGraphic().getStyleClass().add("unsubscribed");
			tab.setTooltip(new Tooltip("Status: " + "unsubscribed"));
		}		

		return subscriptionController;
	}
	
	public static void updateSubscriptionTabContextMenu(final Tab tab, final MqttSubscription subscription)
	{
		// Update title style
		tab.getGraphic().getStyleClass().remove(tab.getGraphic().getStyleClass().size() - 1);
		if (subscription.isActive())
		{
			tab.getGraphic().getStyleClass().add("subscribed");
			tab.getTooltip().setText("Status: " + "subscribed");
			StatisticsManager.newSubscription();
		}
		else
		{
			tab.getGraphic().getStyleClass().add("unsubscribed");
			tab.getTooltip().setText("Status: " + "unsubscribed");
		}

		// Set menu items
		if (subscription.getConnection().getConnectionStatus().equals(MqttConnectionStatus.CONNECTED))
		{									
			if (subscription.isActive())
			{
				tab.getContextMenu().getItems().get(0).setDisable(false);
				tab.getContextMenu().getItems().get(1).setDisable(true);
			}
			else
			{
				tab.getContextMenu().getItems().get(0).setDisable(true);
				tab.getContextMenu().getItems().get(1).setDisable(false);
			}
			
			tab.getContextMenu().getItems().get(2).setDisable(false);
		}
		else
		{
			tab.getContextMenu().getItems().get(0).setDisable(true);
			tab.getContextMenu().getItems().get(1).setDisable(true);
			tab.getContextMenu().getItems().get(2).setDisable(true);			
		}			
	}	
}
