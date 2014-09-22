package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
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
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.connections.ConnectionManager;
import pl.baczkowicz.mqttspy.ui.utils.DialogUtils;
import pl.baczkowicz.mqttspy.ui.utils.StylingUtils;

public class ConnectionController implements Initializable, Observer
{
	private static final int MIN_EXPANDED_SUBSCRIPTION_PANE_HEIGHT = 71;

	private static final int MIN_COLLAPSED_SUBSCRIPTION_PANE_HEIGHT = 26;
	
	private static final int MIN_EXPANDED_PUBLICATION_PANE_HEIGHT = 110;
	
	private static final int MIN_COLLAPSED_PUBLICATION_PANE_HEIGHT = 26;

	final static Logger logger = LoggerFactory.getLogger(ConnectionController.class);

	@FXML
	AnchorPane connectionPane;
	
	@FXML
	AnchorPane newPublicationPane;
	
	@FXML
	AnchorPane newSubscriptionPane;
	
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
	NewSubscriptionController newSubscriptionPaneController;

	@FXML
	TitledPane publishMessageTitledPane;
	
	@FXML
	TitledPane newSubscriptionTitledPane;
	
	@FXML
	TitledPane subscriptionsTitledPane;
	
	@FXML
	TabPane subscriptionTabs;

	private MqttConnection connection;

	private Tab connectionTab;
	
	private Tooltip tooltip;

	private StatisticsManager statisticsManager;

	private ConnectionManager connectionManager;

	public void initialize(URL location, ResourceBundle resources)
	{		
		publishMessageTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
			{
				updateMinHeights();

			}
		});
		
		newSubscriptionTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
			{
				updateMinHeights();
			}
		});
		
		subscriptionsTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
			{
				updateMinHeights();
			}
		});
		
		updateMinHeights();		
	}
	
	public void init()
	{
		newPublicationPaneController.setConnection(connection);
		newSubscriptionPaneController.setConnection(connection);
		newSubscriptionPaneController.setConnectionController(this);
		newSubscriptionPaneController.setConnectionManager(connectionManager);
		connection.setStatisticsManager(statisticsManager);
		
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
		
		if (newSubscriptionTitledPane.isExpanded())
		{
			newSubscriptionTitledPane.setMinHeight(MIN_EXPANDED_SUBSCRIPTION_PANE_HEIGHT);
		}
		else
		{
			newSubscriptionTitledPane.setMinHeight(MIN_COLLAPSED_SUBSCRIPTION_PANE_HEIGHT);
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

	public void update(Observable observable, Object update)
	{
		if (observable instanceof MqttConnection)
		{
			if (update instanceof MqttConnectionStatus)
			{
				newSubscriptionPaneController.setConnected(false);
				newPublicationPaneController.setConnected(false);
				
				for (final MqttSubscription sub : connection.getSubscriptions().values())
				{
					sub.getSubscriptionController().updateContextMenu();
				}
				
				switch ((MqttConnectionStatus) update)
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

				// connectionTab.getStyleClass().clear();
				if (connectionTab.getStyleClass().size() > 1)
				{
					connectionTab.getStyleClass().remove(1);
				}
				connectionTab.getStyleClass().add(StylingUtils.getStyleForMqttConnectionStatus((MqttConnectionStatus) update));
				
				DialogUtils.updateConnectionTooltip(connection, tooltip, statisticsManager);
			}
		}
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
}
