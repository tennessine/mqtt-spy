package pl.baczkowicz.mqttspy.ui.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import pl.baczkowicz.mqttspy.configuration.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.ui.ConnectionController;
import pl.baczkowicz.mqttspy.ui.events.EventDispatcher;
import pl.baczkowicz.mqttspy.ui.events.ShowFirstEvent;
import pl.baczkowicz.mqttspy.ui.properties.MqttContentProperties;
import pl.baczkowicz.mqttspy.ui.properties.SubscriptionTopicSummaryProperties;

public class ContextMenuUtils
{

	public static ContextMenu createSubscriptionTabContextMenu(final Tab tab,
			final MqttConnection connection, final MqttSubscription subscription, final EventManager eventManager)
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
				connection.unsubscribeAndRemove(subscription);
				TabUtils.requestClose(tab);
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
				subscription.clear();
			}
		});
		contextMenu.getItems().add(clearItem);

		return contextMenu;
	}

	public static ContextMenu createAllSubscriptionsTabContextMenu(final Tab tab,
			final MqttConnection connection, final EventManager eventManager)
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
				connection.clear();
			}
		});
		contextMenu.getItems().add(clearItem);

		return contextMenu;
	}

	public static ContextMenu createTopicTableContextMenu(
			final TableView<SubscriptionTopicSummaryProperties> filterTable, final ObservableMessageStoreWithFiltering store, final EventDispatcher navigationEventDispatcher)
	{
		final ContextMenu contextMenu = new ContextMenu();
		
		// Copy topic
		final MenuItem copyTopicItem = new MenuItem("[Topic] Copy to clipboard");
		copyTopicItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					final ClipboardContent content = new ClipboardContent();
					content.putString(item.topicProperty().getValue());
					Clipboard.getSystemClipboard().setContent(content);
				}
			}
		});
		contextMenu.getItems().add(copyTopicItem);

		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		// Copy content
		final MenuItem copyContentItem = new MenuItem("[Content] Copy to clipboard");
		copyContentItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					final ClipboardContent content = new ClipboardContent();
					content.putString(item.lastReceivedPayloadProperty().getValue());
					Clipboard.getSystemClipboard().setContent(content);
				}
			}
		});
		contextMenu.getItems().add(copyContentItem);
		
		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		// Apply filters
		final MenuItem selectAllTopicsItem = new MenuItem("[Show] Select all topics");
		selectAllTopicsItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					store.setAllShowValues(true);
					navigationEventDispatcher.dispatchEvent(new ShowFirstEvent());
				}
			}
		});
		contextMenu.getItems().add(selectAllTopicsItem);

		// Toggle filters
		final MenuItem toggleAllTopicsItem = new MenuItem("[Show] Toggle all topics");
		toggleAllTopicsItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					store.toggleAllShowValues();
					navigationEventDispatcher.dispatchEvent(new ShowFirstEvent());	
				}
			}
		});
		contextMenu.getItems().add(toggleAllTopicsItem);
		
		// Only this topic
		final MenuItem selectOnlyThisItem = new MenuItem("[Show] Select only this");
		selectOnlyThisItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					store.setAllShowValues(false);
					store.setShowValue(item.topicProperty().getValue(), true);
					navigationEventDispatcher.dispatchEvent(new ShowFirstEvent());	
				}
			}
		});
		contextMenu.getItems().add(selectOnlyThisItem);
				
		// Remove filters
		final MenuItem removeAllTopicsItem = new MenuItem("[Show] Clear all selected topics");
		removeAllTopicsItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final SubscriptionTopicSummaryProperties item = filterTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					store.setAllShowValues(false);
					navigationEventDispatcher.dispatchEvent(new ShowFirstEvent());	
				}
			}
		});
		contextMenu.getItems().add(removeAllTopicsItem);

		return contextMenu;
	}
	
	public static ContextMenu createMessageListTableContextMenu(
			final TableView<MqttContentProperties> messageTable, final EventDispatcher navigationEventDispatcher)
	{
		final ContextMenu contextMenu = new ContextMenu();
		
		// Copy topic
		final MenuItem copyTopicItem = new MenuItem("[Topic] Copy to clipboard");
		copyTopicItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final MqttContentProperties item = messageTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					final ClipboardContent content = new ClipboardContent();
					content.putString(item.topicProperty().getValue());
					Clipboard.getSystemClipboard().setContent(content);
				}
			}
		});
		contextMenu.getItems().add(copyTopicItem);

		// Separator
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		// Copy content
		final MenuItem copyContentItem = new MenuItem("[Content] Copy to clipboard");
		copyContentItem.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				final MqttContentProperties item = messageTable.getSelectionModel()
						.getSelectedItem();
				if (item != null)
				{
					final ClipboardContent content = new ClipboardContent();
					content.putString(item.lastReceivedPayloadProperty().getValue());
					Clipboard.getSystemClipboard().setContent(content);
				}
			}
		});
		contextMenu.getItems().add(copyContentItem);
		
		// Separator
		// contextMenu.getItems().add(new SeparatorMenuItem());
		//
		// // Show message filters
		// final MenuItem showMessageItem = new MenuItem("Preview message");
		// showMessageItem.setOnAction(new EventHandler<ActionEvent>()
		// {
		// public void handle(ActionEvent e)
		// {
		// final ObservableMqttContent item = messageTable.getSelectionModel()
		// .getSelectedItem();
		// if (item != null)
		// {
		// navigationEventDispatcher.dispatchEvent(new MessageIndexChangedEvent(
		// messageTable.getSelectionModel().getSelectedIndex()));
		// }
		// }
		// });
		// contextMenu.getItems().add(showMessageItem);

		return contextMenu;
	}

	public static ContextMenu createConnectionMenu(final MqttManager mqttManager, final MqttConnection connection, 
			final ConnectionController connectionController, final Tab tab)
	{
		// Context menu
		ContextMenu contextMenu = new ContextMenu();

		MenuItem reconnectItem = new MenuItem("[Connection] Connect / reconnect");
		reconnectItem.setOnAction(ConnectionUtils.createConnectAction(mqttManager, connection.getProperties().getId()));
		
		MenuItem disconnectItem = new MenuItem("[Connection] Disconnect (and keep tab)");
		disconnectItem.setOnAction(ConnectionUtils.createDisconnectAction(mqttManager, connection.getProperties().getId()));

		MenuItem disconnectAndCloseItem = new MenuItem("[Connection] Disconnect (and close tab)");
		disconnectAndCloseItem.setOnAction(ConnectionUtils.createDisconnectAndCloseAction(mqttManager, connection.getProperties().getId(), tab));

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
				final SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
				subscriptionDetails.setTopic("$SYS/#");
				subscriptionDetails.setQos(0);
				
				connectionController.getNewSubscriptionPaneController().subscribe(subscriptionDetails, true);
			}
		});
		contextMenu.getItems().add(stats);

		return contextMenu;
	}
}
