package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.observers.ConnectionStatusChangeObserver;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.connections.ConnectionManager;
import pl.baczkowicz.mqttspy.ui.utils.DialogUtils;
import pl.baczkowicz.mqttspy.ui.utils.StylingUtils;

public class ConnectionController implements Initializable, ConnectionStatusChangeObserver
{
	private static final int MIN_COLLAPSED_PANE_HEIGHT = 26;
	
	private static final int MIN_EXPANDED_SUBSCRIPTION_PANE_HEIGHT = 71;

	private static final int MIN_COLLAPSED_SUBSCRIPTION_PANE_HEIGHT = MIN_COLLAPSED_PANE_HEIGHT;
	
	private static final int MIN_EXPANDED_PUBLICATION_PANE_HEIGHT = 110;	
	
	private static final int MIN_COLLAPSED_PUBLICATION_PANE_HEIGHT = MIN_COLLAPSED_PANE_HEIGHT;
	
	private static final int MIN_EXPANDED_SCRIPTED_PUBLICATION_PANE_HEIGHT = 145;	
	
	private static final int MIN_COLLAPSED_SCRIPTED_PUBLICATION_PANE_HEIGHT = MIN_COLLAPSED_PANE_HEIGHT;

	final static Logger logger = LoggerFactory.getLogger(ConnectionController.class);

	@FXML
	private AnchorPane connectionPane;
	
	@FXML
	private AnchorPane newPublicationPane;
	
	@FXML
	private AnchorPane newSubscriptionPane;
	
	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	NewPublicationController newPublicationPaneController;
	
	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private PublicationScriptsController publicationScriptsPaneController;
	
	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	NewSubscriptionController newSubscriptionPaneController;

	@FXML
	private TitledPane publishMessageTitledPane;
	
	@FXML
	private TitledPane newSubscriptionTitledPane;
	
	@FXML
	private TitledPane scriptedPublicationsTitledPane;
	
	@FXML
	TitledPane subscriptionsTitledPane;
	
	@FXML
	private TabPane subscriptionTabs;

	private MqttConnection connection;

	private Tab connectionTab;
	
	private Tooltip tooltip;

	private StatisticsManager statisticsManager;

	private ConnectionManager connectionManager;

	private EventManager eventManager;

	private ChangeListener<Boolean> createChangeListener()
	{
		return new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
			{
				updateMinHeights();

			}
		};
	}
	
	public void initialize(URL location, ResourceBundle resources)
	{		
		publishMessageTitledPane.expandedProperty().addListener(createChangeListener());		
		scriptedPublicationsTitledPane.expandedProperty().addListener(createChangeListener());		
		newSubscriptionTitledPane.expandedProperty().addListener(createChangeListener());		
		subscriptionsTitledPane.expandedProperty().addListener(createChangeListener());
		
		scriptedPublicationsTitledPane.setExpanded(false);
		updateMinHeights();		
	}
	
	public void init()
	{
		newPublicationPaneController.setConnection(connection);
		newSubscriptionPaneController.setConnection(connection);
		newSubscriptionPaneController.setConnectionController(this);
		newSubscriptionPaneController.setConnectionManager(connectionManager);
		connection.setStatisticsManager(statisticsManager);
		
		publicationScriptsPaneController.setConnection(connection);
		publicationScriptsPaneController.setEventManager(eventManager);
		publicationScriptsPaneController.init();
		
		tooltip = new Tooltip();
		connectionTab.setTooltip(tooltip);
		
		// connectionPane.setMaxWidth(500);
		// subscriptionsTitledPane.setMaxWidth(500);
		// subscriptionTabs.setMaxWidth(500);
		// TODO: how not to resize the tab pane on too many tabs? All max sizes seems to be ignored
	}
	
	public void setConnectionManager(final ConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
	}
	
	public void updateMinHeights()
	{
		if (publishMessageTitledPane.isExpanded())
		{
			publishMessageTitledPane.setMinHeight(MIN_EXPANDED_PUBLICATION_PANE_HEIGHT);
		}
		else
		{
			publishMessageTitledPane.setMinHeight(MIN_COLLAPSED_PUBLICATION_PANE_HEIGHT);
		}
		
		if (scriptedPublicationsTitledPane.isExpanded())
		{
			scriptedPublicationsTitledPane.setMinHeight(MIN_EXPANDED_SCRIPTED_PUBLICATION_PANE_HEIGHT);
		}
		else
		{
			scriptedPublicationsTitledPane.setMinHeight(MIN_COLLAPSED_SCRIPTED_PUBLICATION_PANE_HEIGHT);
		}
		
		if (newSubscriptionTitledPane.isExpanded())
		{
			newSubscriptionTitledPane.setMinHeight(MIN_EXPANDED_SUBSCRIPTION_PANE_HEIGHT);
			newSubscriptionTitledPane.setMaxHeight(MIN_EXPANDED_SUBSCRIPTION_PANE_HEIGHT);
		}
		else
		{
			newSubscriptionTitledPane.setMinHeight(MIN_COLLAPSED_SUBSCRIPTION_PANE_HEIGHT);
			newSubscriptionTitledPane.setMaxHeight(MIN_COLLAPSED_SUBSCRIPTION_PANE_HEIGHT);
		}
	}

	public MqttConnection getConnection()
	{
		return connection;
	}

	public void setConnection(MqttConnection connection)
	{
		this.connection = connection;
	}

	public Tab getTab()
	{
		return connectionTab;
	}

	public void setTab(Tab tab)
	{
		this.connectionTab = tab;
	}

	public TabPane getSubscriptionTabs()
	{
		return subscriptionTabs;
	}
	
	public void showTabTile(final boolean pending)
	{
		if (pending)
		{
			final HBox title = new HBox();
			title.setAlignment(Pos.CENTER);
			final ProgressIndicator progressIndicator = new ProgressIndicator();
			progressIndicator.setMaxSize(15, 15);												
			title.getChildren().add(progressIndicator);
			title.getChildren().add(new Label(" " + connection.getName()));
			connectionTab.setGraphic(title);
			connectionTab.setText(null);
		}
		else
		{
			connectionTab.setGraphic(null);
			connectionTab.setText(connection.getName());
		}
	}
	
	public void onConnectionStatusChanged(final MqttConnection changedConnection)
	{
		final MqttConnectionStatus connectionStatus = changedConnection.getConnectionStatus();
		
		newSubscriptionPaneController.setConnected(false);
		newPublicationPaneController.setConnected(false);
		
		for (final MqttSubscription sub : connection.getSubscriptions().values())
		{
			sub.getSubscriptionController().updateContextMenu();
		}
		
		// If the context menu is available and has items in it
		if (connectionTab.getContextMenu() != null && connectionTab.getContextMenu().getItems().size() > 0)
		{
			switch (connectionStatus)
			{
				case NOT_CONNECTED:
					connectionTab.getContextMenu().getItems().get(0).setDisable(false);
					connectionTab.getContextMenu().getItems().get(2).setDisable(true);										
					connectionTab.getContextMenu().getItems().get(3).setDisable(false);
					connectionTab.getContextMenu().getItems().get(5).setDisable(true);
					showTabTile(false);
					break;
				case CONNECTED:					
					connectionTab.getContextMenu().getItems().get(0).setDisable(true);
					connectionTab.getContextMenu().getItems().get(2).setDisable(false);
					connectionTab.getContextMenu().getItems().get(3).setDisable(false);
					connectionTab.getContextMenu().getItems().get(5).setDisable(false);
					newSubscriptionPaneController.setConnected(true);
					newPublicationPaneController.setConnected(true);
					showTabTile(false);
					break;
				case CONNECTING:
					connectionTab.getContextMenu().getItems().get(2).setDisable(true);
					connectionTab.getContextMenu().getItems().get(0).setDisable(true);					
					connectionTab.getContextMenu().getItems().get(3).setDisable(true);
					connectionTab.getContextMenu().getItems().get(5).setDisable(true);
					showTabTile(true);						
					break;
				case DISCONNECTED:
					connectionTab.getContextMenu().getItems().get(0).setDisable(false);
					connectionTab.getContextMenu().getItems().get(2).setDisable(true);										
					connectionTab.getContextMenu().getItems().get(3).setDisable(false);
					connectionTab.getContextMenu().getItems().get(5).setDisable(true);
					showTabTile(false);
					break;
				case DISCONNECTING:					
					connectionTab.getContextMenu().getItems().get(0).setDisable(true);
					connectionTab.getContextMenu().getItems().get(2).setDisable(true);
					connectionTab.getContextMenu().getItems().get(3).setDisable(false);
					connectionTab.getContextMenu().getItems().get(5).setDisable(true);
					showTabTile(false);
					break;
				default:
					break;
			}
		}

		// connectionTab.getStyleClass().clear();
		if (connectionTab.getStyleClass().size() > 1)
		{
			connectionTab.getStyleClass().remove(1);
		}
		connectionTab.getStyleClass().add(StylingUtils.getStyleForMqttConnectionStatus(connectionStatus));
		
		DialogUtils.updateConnectionTooltip(connection, tooltip, statisticsManager);
	}
	
	public void updateConnectionStats()
	{
		for (final SubscriptionController subscriptionController : connectionManager.getSubscriptionManager(connection.getId()).getSubscriptionControllers())
		{
			subscriptionController.updateSubscriptionStats();
		}
	}

	public StatisticsManager getStatisticsManager()
	{
		return statisticsManager;
	}

	public void setStatisticsManager(StatisticsManager statisticsManager)
	{
		this.statisticsManager = statisticsManager;
	}
	
	public NewSubscriptionController getNewSubscriptionPaneController()
	{
		return newSubscriptionPaneController;
	}

	public void setEventManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
}
