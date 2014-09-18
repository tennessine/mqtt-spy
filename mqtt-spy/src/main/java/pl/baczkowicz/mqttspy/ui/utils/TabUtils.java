package pl.baczkowicz.mqttspy.ui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.ConnectionController;
import pl.baczkowicz.mqttspy.ui.MainController;
import pl.baczkowicz.mqttspy.ui.SubscriptionController;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;

public class TabUtils
{
	public static String ALL_TAB = "All";
	
	public static ConnectionController loadConnectionTab(final MainController mainController,
			final Object parent, final MqttManager mqttManager, 
			final RuntimeConnectionProperties connectionProperties, final EventManager eventManager, final StatisticsManager statisticsManager)
	{		
		// Create connection
		final MqttConnection connection = mqttManager.createConnection(connectionProperties);

		// Load a new tab and connection pane
		final FXMLLoader loader = Utils.createFXMLLoader(parent, Utils.FXML_LOCATION + "ConnectionTab.fxml");
		AnchorPane connectionPane = Utils.loadAnchorPane(loader);
		final ConnectionController connectionController = (ConnectionController) loader.getController();
		final Tab connectionTab = TabUtils.createConnectionTab(mqttManager, connectionProperties,
				connectionPane, connectionController); 
		mainController.addConnectionTab(connectionTab);

		connection.addObserver(connectionController);
		connection.setOpened(true);
		connection.setTab(connectionTab);

		connectionController.setConnection(connection);
		connectionController.setConnectionProperties(connectionProperties);
		connectionController.setEventManager(eventManager);
		connectionController.setStatisticsManager(statisticsManager);
		connectionController.init();
		
		// Connect
		if (connectionProperties.isAutoConnect())
		{
			mqttManager.connectToBroker(connection);
		}
		else
		{
			connection.setConnectionStatus(MqttConnectionStatus.NOT_CONNECTED);
		}	
		
		// Add "All" subscription tab
		connectionController.getSubscriptionTabs().getTabs().clear();
		final SubscriptionController subscriptionController = TabUtils.createSubscriptionTab(true, parent, connection, connection, null, connectionProperties, connectionController, eventManager);
		connectionController.getSubscriptionTabs().getTabs().add(subscriptionController.getTab());
		
		return connectionController;
	}

	public static Tab createConnectionTab(final MqttManager mqttManager,
			final RuntimeConnectionProperties connectionProperties, final Node content,
			final ConnectionController connectionController)
	{
		final Tab tab = new Tab();
		connectionController.setTab(tab);
		tab.setText(connectionProperties.getName());
		tab.setContent(content);
		tab.setContextMenu(ContextMenuUtils.createConnectionMenu(mqttManager, connectionProperties.getId(), tab));

		return tab;
	}

	public static SubscriptionController createSubscriptionTab(final boolean allTab, final Object parent,
			final ObservableMessageStoreWithFiltering observableMessageStore, final MqttConnection connection,
			final MqttSubscription subscription, final RuntimeConnectionProperties connectionProperties, final ConnectionController connectionController,
			final EventManager eventManager)
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
		subscriptionController.setConnectionProperties(connectionProperties);
		subscriptionController.init();

		tab.setClosable(false);
		tab.setContent(subscriptionPane);

		if (subscription != null)
		{
			tab.setStyle(Utils.createBaseRGBString(subscription.getColor()));
		}

		if (allTab)
		{
			tab.setContextMenu(ContextMenuUtils.createAllSubscriptionsTabContextMenu(tab, connection, eventManager));
			tab.setGraphic(new Label(ALL_TAB));
			tab.getGraphic().getStyleClass().add("subscribed");
		}
		else
		{
			tab.setContextMenu(ContextMenuUtils.createSubscriptionTabContextMenu(tab, connection, subscription, eventManager));
			tab.setGraphic(new Label(subscription.getTopic()));
			tab.getGraphic().getStyleClass().add("unsubscribed");
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
			StatisticsManager.newSubscription();
		}
		else
		{
			tab.getGraphic().getStyleClass().add("unsubscribed");
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

	public static void requestClose(final Tab tab)
	{
		TabPaneBehavior behavior = getBehavior(tab);
		if (behavior.canCloseTab(tab))
		{
			behavior.closeTab(tab);
		}
	}

	private static TabPaneBehavior getBehavior(final Tab tab)
	{
		return ((TabPaneSkin) tab.getTabPane().getSkin()).getBehavior();
	}
}
