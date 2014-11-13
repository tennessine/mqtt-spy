package pl.baczkowicz.mqttspy.ui.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import pl.baczkowicz.mqttspy.configuration.generated.TabbedSubscriptionDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.ConnectionController;
import pl.baczkowicz.mqttspy.ui.connections.ConnectionManager;
import pl.baczkowicz.mqttspy.ui.connections.SubscriptionManager;

public class ContextMenuUtils
{
	// private final static Logger logger = LoggerFactory.getLogger(ContextMenuUtils.class);
	
	public static ContextMenu createSubscriptionTabContextMenu(
			final MqttAsyncConnection connection, final MqttSubscription subscription, 
			final EventManager eventManager, final SubscriptionManager subscriptionManager)
	{
		final ContextMenu contextMenu = new ContextMenu();

		// Cancel
		MenuItem cancelItem = new MenuItem("[Subscription] Unsubscribe (and keep the tab)");
		cancelItem.setDisable(false);

		cancelItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				connection.unsubscribe(subscription);
			}
		});
		contextMenu.getItems().add(cancelItem);

		// Re-subscribe
		MenuItem resubscribeItem = new MenuItem("[Subscription] Re-subscribe");
		resubscribeItem.setDisable(true);

		resubscribeItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				connection.resubscribe(subscription);
			}
		});
		contextMenu.getItems().add(resubscribeItem);

		// Close
		MenuItem closeItem = new MenuItem("[Subscription] Unsubscribe (and close tab)");

		closeItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{				
				subscriptionManager.removeSubscription(subscription.getTopic());				
			}
		});
		contextMenu.getItems().add(closeItem);

		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());

		// Copy subscription topic string
		final MenuItem copyTopicItem = new MenuItem("[Subscription] Copy subscription topic to clipboard");
		copyTopicItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final ClipboardContent content = new ClipboardContent();
				content.putString(subscription.getTopic());
				Clipboard.getSystemClipboard().setContent(content);
			}
		});
		contextMenu.getItems().add(copyTopicItem);

		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		// Clear data
		MenuItem clearItem = new MenuItem("[History] Clear subscription history");

		clearItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{				
				eventManager.notifyClearHistory(subscription);
				StatisticsManager.resetMessagesReceived(connection.getId(), subscription.getTopic());
				subscription.clear();
			}
		});
		contextMenu.getItems().add(clearItem);

		return contextMenu;
	}

	public static ContextMenu createAllSubscriptionsTabContextMenu(final Tab tab,
			final MqttAsyncConnection connection, final EventManager eventManager)
	{
		final ContextMenu contextMenu = new ContextMenu();

		MenuItem cancelItem = new MenuItem("[Subscriptions] Unsubscribe from all active subscriptions (if any)");
		cancelItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				connection.unsubscribeAll();
			}
		});
		contextMenu.getItems().add(cancelItem);

		MenuItem resubscribeItem = new MenuItem(
				"[Subscriptions] Re-subscribe to all non-active subscriptions (if any)");

		resubscribeItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				connection.resubscribeAll();
			}
		});
		contextMenu.getItems().add(resubscribeItem);

		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());

		// Clear data
		MenuItem clearItem = new MenuItem("[History] Clear tab history");

		clearItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				eventManager.notifyClearHistory(connection);
				StatisticsManager.resetMessagesReceived(connection.getId());
				connection.clear();
			}
		});
		contextMenu.getItems().add(clearItem);

		return contextMenu;
	}
	
	

	public static ContextMenu createConnectionMenu(final MqttManager mqttManager, final MqttAsyncConnection connection, 
			final ConnectionController connectionController, final ConnectionManager connectionManager)
	{
		// Context menu
		ContextMenu contextMenu = new ContextMenu();

		MenuItem reconnectItem = new MenuItem("[Connection] Connect / reconnect");
		reconnectItem.setOnAction(ConnectionUtils.createConnectAction(mqttManager, connection.getProperties().getId()));
		
		MenuItem disconnectItem = new MenuItem("[Connection] Disconnect (and keep tab)");
		disconnectItem.setOnAction(ConnectionUtils.createDisconnectAction(mqttManager, connection.getProperties().getId()));

		MenuItem disconnectAndCloseItem = new MenuItem("[Connection] Disconnect (and close tab)");
		disconnectAndCloseItem.setOnAction(ConnectionUtils.createDisconnectAndCloseAction(connection.getProperties().getId(), connectionManager));

		contextMenu.getItems().add(reconnectItem);

		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());

		contextMenu.getItems().add(disconnectItem);		
		contextMenu.getItems().add(disconnectAndCloseItem);
		
		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());

		// Show statistics
		final MenuItem stats = new MenuItem("[Statistics] Show broker's statistics");
		stats.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final TabbedSubscriptionDetails subscriptionDetails = new TabbedSubscriptionDetails();
				subscriptionDetails.setTopic("$SYS/#");
				subscriptionDetails.setQos(0);
				
				connectionController.getNewSubscriptionPaneController().subscribe(subscriptionDetails, true);
			}
		});
		contextMenu.getItems().add(stats);
		
		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		// View
		final Menu view = new Menu("[View] Show/hide panes");
		final MenuItem manualPublications = new MenuItem("Toggle 'Publish message' pane");
		final MenuItem scriptedPublications = new MenuItem("Toggle 'Scripted publications' pane");
		final MenuItem newSubscription = new MenuItem("Toggle 'Define new subscription' pane");
		final MenuItem messageSummary = new MenuItem("Toggle 'Subscriptions and received messages' pane");
		final MenuItem detailedView = new MenuItem("Toggle between simplified and detailed views (QoS, Retained)");
		
		view.getItems().add(manualPublications);
		view.getItems().add(scriptedPublications);
		view.getItems().add(newSubscription);
		view.getItems().add(messageSummary);
		view.getItems().add(new SeparatorMenuItem());
		view.getItems().add(detailedView);
		
		manualPublications.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{				
				connectionController.togglePane(connectionController.getPublishMessageTitledPane());
			}
		});
		scriptedPublications.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{				
				connectionController.togglePane(connectionController.getScriptedPublicationsTitledPane());
			}
		});
		newSubscription.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{				
				connectionController.togglePane(connectionController.getNewSubscriptionTitledPane());
			}
		});
		messageSummary.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{				
				connectionController.togglePane(connectionController.getSubscriptionsTitledPane());
			}
		});
		detailedView.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{				
				connectionController.toggleDetailedViewVisibility();
			}
		});
		contextMenu.getItems().add(view);

		return contextMenu;
	}
	
	public static ContextMenu createReplayMenu(final Tab tab)
	{
		// Context menu
		ContextMenu contextMenu = new ContextMenu();

		MenuItem closedItem = new MenuItem("[Tab] Close");
		closedItem.setOnAction(new EventHandler<ActionEvent>()
		{			
			@Override
			public void handle(ActionEvent event)
			{
				TabUtils.requestClose(tab);				
			}
		});
		
		contextMenu.getItems().add(closedItem);

		return contextMenu;
	}
}
