package pl.baczkowicz.mqttspy.ui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.ui.ConnectionController;
import pl.baczkowicz.mqttspy.ui.MainController;
import pl.baczkowicz.mqttspy.ui.SubscriptionController;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;

public class TabUtils
{
	final static String CANCELLED_SUBSCRIPTION = " [cancelled]";
	
	public static ConnectionController loadConnectionTab(final MainController mainController,
			final Object parent, final MqttManager mqttManager, 
			final RuntimeConnectionProperties connectionProperties)
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

		// Connect
		if (connectionProperties.isAutoConnect())
		{
			mqttManager.connectToBroker(connection);
		}
		else
		{
			connection.setConnectionStatus(MqttConnectionStatus.NOT_CONNECTED);
		}	
		
		connectionController.setConnection(connection);
		connectionController.setConnectionProperties(connectionProperties);
		connectionController.init();

		// Add "All" subscription tab
		connectionController.getSubscriptionTabs().getTabs().clear();
		connectionController.getSubscriptionTabs().getTabs().add(TabUtils.createSubscriptionTab(true, parent, connection, connection, null, connectionProperties, connectionController));
		
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

	public static Tab createSubscriptionTab(final boolean allTab, final Object parent,
			final ObservableMessageStoreWithFiltering observableMessageStore, final MqttConnection connection,
			final MqttSubscription subscription, final RuntimeConnectionProperties connectionProperties, final ConnectionController connectionController)
	{
		// Load a new tab and connection pane
		final FXMLLoader loader = Utils.createFXMLLoader(parent, Utils.FXML_LOCATION + "SubscriptionPane.fxml");

		final AnchorPane subscriptionPane = Utils.loadAnchorPane(loader);
		final SubscriptionController subscriptionController = ((SubscriptionController) loader.getController());
		
		final Tab tab = new Tab();
		observableMessageStore.addObserver(subscriptionController);
		subscriptionController.setStore(observableMessageStore);
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
			tab.setContextMenu(ContextMenuUtils.createAllSubscriptionsTabContextMenu(tab,
					connection));
			tab.setText("All");
		}
		else
		{
			tab.setContextMenu(ContextMenuUtils.createSubscriptionTabContextMenu(tab, connection,
					subscription));
			tab.setText(subscription.getTopic());
		}

		return tab;
	}

	public static void updateSubscriptionTab(final Tab tab, final MqttSubscription subscription)
	{
		if (subscription.isActive())
		{
			tab.setText(tab.getText().replace(CANCELLED_SUBSCRIPTION, ""));
			tab.getContextMenu().getItems().get(0).setDisable(false);
			tab.getContextMenu().getItems().get(1).setDisable(true);
		}
		else
		{
			tab.setText(tab.getText() + CANCELLED_SUBSCRIPTION);
			tab.getContextMenu().getItems().get(0).setDisable(true);
			tab.getContextMenu().getItems().get(1).setDisable(false);
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
