package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.ConversionMethod;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.observers.ClearTabObserver;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.connections.SubscriptionManager;
import pl.baczkowicz.mqttspy.ui.events.EventDispatcher;
import pl.baczkowicz.mqttspy.ui.events.MessageFormatChangeEvent;
import pl.baczkowicz.mqttspy.ui.events.NewMessageEvent;
import pl.baczkowicz.mqttspy.ui.events.ShowFirstEvent;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;
import pl.baczkowicz.mqttspy.ui.utils.DialogUtils;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class SubscriptionController implements Observer, Initializable, ClearTabObserver
{
	public MqttSubscription getSubscription()
	{
		return subscription;
	}

	final static Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

	private static final int MIN_EXPANDED_SUMMARY_PANE_HEIGHT = 130;

	private static final int MIN_COLLAPSED_SUMMARY_PANE_HEIGHT = 31;

	/** Received messages summary (10 topics; average message rates per second [5s,30s,5m]: 0.1/0.5/5.0). */
	private static final String SUMMARY_PANE_TITLE_FORMAT = "Received messages summary (%s, " + DialogUtils.STATS_FORMAT + ")";

	@FXML
	private SplitPane splitPane;

	@FXML
	private AnchorPane messagePane;

	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private MessageNavigationController messageNavigationPaneController;
	
	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private MessageController messagePaneController;
	
	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private SubscriptionSummaryTableController summaryTablePaneController;

	@FXML
	private TitledPane summaryTitledPane;

	@FXML
	private ToggleGroup wholeMessageFormat;

	@FXML
	private MenuButton formattingMenuButton;

	@FXML
	private Menu formatterMenu;
	
	@FXML
	private Menu customFormatterMenu;

	@FXML
	private ToggleGroup selectionFormat;
	
	@FXML
	private ToggleButton searchButton;

	private ObservableMessageStoreWithFiltering store; 

	private Tab tab;

	private RuntimeConnectionProperties connectionProperties;

	private MqttSubscription subscription;

	private Stage searchStage;

	private SearchWindowController searchWindowController;

	private final EventDispatcher eventDispatcher;

	private EventManager eventManager;

	private StatisticsManager statisticsManager;

	private Label summaryTitledPaneTitleLabel;

	private ConnectionController connectionController;

	public SubscriptionController()
	{
		eventDispatcher = new EventDispatcher();
		eventDispatcher.addObserver(this);
	}
	
	@Override
	public void onClearTab(final ObservableMessageStoreWithFiltering subscription)
	{	
		messagePaneController.clear();
		messageNavigationPaneController.clear();
		store.setAllShowValues(false);		
	}
	
	public void update(Observable observable, Object update)
	{
		// Expecting to receive only notifications from subscriptions and the
		// connection (for all subscriptions)
		// if (update == null)
		// {
		// messagePaneController.clear();
		// messageNavigationPaneController.clear();
		// store.setAllShowValues(false);
		// }
		// else 
		if (update instanceof MqttContent)
		{
			if (store.getFilters().contains(((MqttContent) update).getTopic()))
			{
				if (messageNavigationPaneController.showLatest())
				{
					eventDispatcher.dispatchEvent(new ShowFirstEvent());
				}
				else
				{
					eventDispatcher.dispatchEvent(new NewMessageEvent());
				}
			}
			// else
			// {
			// logger.info("No match for " + ((MqttContent) update).getTopic());
			// }
		}
		else if (update instanceof MqttSubscription)
		{
			subscription = (MqttSubscription) update;
			updateContextMenu();
		}
	}
	
	@FXML
	public void formatWholeMessage()
	{
		store.setFormatter((FormatterDetails) wholeMessageFormat.getSelectedToggle().getUserData());
	
		formattingMenuButton.setText(store.getFormatter().getName());
		eventDispatcher.dispatchEvent(new MessageFormatChangeEvent());
	}
	
	@FXML
	public void formatSelection()
	{
		final FormatterDetails messageFormat = (FormatterDetails) selectionFormat.getSelectedToggle().getUserData();
		
		if (messageFormat != null)
		{
			formattingMenuButton.setText("[Selection] " + messageFormat.getName());
		}
		else
		{
			formattingMenuButton.setText(((FormatterDetails) wholeMessageFormat.getSelectedToggle().getUserData()).getName());
		}
		messagePaneController.formatSelection(messageFormat);
	}

	public void updateMinHeights()
	{
		if (summaryTitledPane.isExpanded())
		{
			summaryTitledPane.setMinHeight(MIN_EXPANDED_SUMMARY_PANE_HEIGHT);
		}
		else
		{

			summaryTitledPane.setMinHeight(MIN_COLLAPSED_SUMMARY_PANE_HEIGHT);
			splitPane.setDividerPosition(0, 0.95);
		}
	}

	public void initialize(URL location, ResourceBundle resources)
	{		
		summaryTitledPaneTitleLabel = new Label();
		
		wholeMessageFormat.getToggles().get(0).setUserData(FormattingUtils.createBasicFormatter("default", 				"Plain", ConversionMethod.PLAIN));
		wholeMessageFormat.getToggles().get(1).setUserData(FormattingUtils.createBasicFormatter("default-hexDecoder", 	"HEX decoder", ConversionMethod.HEX_DECODE));
		wholeMessageFormat.getToggles().get(2).setUserData(FormattingUtils.createBasicFormatter("default-hexEncoder", 	"HEX encoder", ConversionMethod.HEX_ENCODE));
		wholeMessageFormat.getToggles().get(3).setUserData(FormattingUtils.createBasicFormatter("default-base64Decoder","Base64 decoder", ConversionMethod.BASE_64_DECODE));
		wholeMessageFormat.getToggles().get(4).setUserData(FormattingUtils.createBasicFormatter("default-base64Encoder","Base64 encoder", ConversionMethod.BASE_64_ENCODE));		
		
		wholeMessageFormat.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
		{
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue,
					Toggle newValue)
			{
				if (wholeMessageFormat.getSelectedToggle() != null)
				{
					formatWholeMessage();
				}
			}
		});
		
		selectionFormat.getToggles().get(0).setUserData(null);
		selectionFormat.getToggles().get(1).setUserData(FormattingUtils.createBasicFormatter("default", 				"Plain", ConversionMethod.PLAIN));
		selectionFormat.getToggles().get(2).setUserData(FormattingUtils.createBasicFormatter("default-hexDecoder", 		"HEX decoder", ConversionMethod.HEX_DECODE));
		selectionFormat.getToggles().get(3).setUserData(FormattingUtils.createBasicFormatter("default-hexEncoder", 		"HEX encoder", ConversionMethod.HEX_ENCODE));
		selectionFormat.getToggles().get(4).setUserData(FormattingUtils.createBasicFormatter("default-base64Decoder",	"Base64 decoder", ConversionMethod.BASE_64_DECODE));
		selectionFormat.getToggles().get(5).setUserData(FormattingUtils.createBasicFormatter("default-base64Encoder",	"Base64 encoder", ConversionMethod.BASE_64_ENCODE));				
		
		selectionFormat.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
		{
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue,
					Toggle newValue)
			{
				if (selectionFormat.getSelectedToggle() != null)
				{
					formatSelection();
				}
			}
		});

		summaryTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
			{
				updateMinHeights();

			}
		});

		updateMinHeights();
	}
	
	private Window getParentWindow()
	{
		return tab.getTabPane().getScene().getWindow();
	}
	
	public void showSearchWindow()
	{
		if (searchStage == null)
		{
			// Create the search window controller
			final FXMLLoader searchLoader = Utils.createFXMLLoader(this, Utils.FXML_LOCATION + "SearchWindow.fxml");
			AnchorPane searchWindow = Utils.loadAnchorPane(searchLoader);
			searchWindowController = (SearchWindowController) searchLoader.getController();
			searchWindowController.setStore(store);
			searchWindowController.setSubscription(subscription);
			searchWindowController.setSubscriptionName(subscription != null ? subscription.getTopic() : SubscriptionManager.ALL_SUBSCRIPTIONS_TAB_TITLE);
			searchWindowController.setEventDispatcher(eventDispatcher);
			eventDispatcher.addObserver(searchWindowController);
		
			// Set scene width, height and style
			final Scene scene = new Scene(searchWindow, SearchWindowController.WIDTH, SearchWindowController.HEIGHT);
			scene.getStylesheets().addAll(tab.getTabPane().getScene().getStylesheets());
			
			searchStage = new Stage();
			// searchStage.setTitle(subscription != null ? subscription.getTopic() : tab.getText());
			searchStage.initModality(Modality.NONE);
			searchStage.initOwner(getParentWindow());
			searchStage.setScene(scene);
			
			searchWindowController.init();
		}

		if (!searchStage.isShowing())
		{
			searchStage.show();
			searchStage.setOnCloseRequest(new EventHandler<WindowEvent>(){

				@Override
				public void handle(WindowEvent event)
				{
					searchButton.setSelected(false);
					searchWindowController.handleClose();
				}				
			});
		}		
		else
		{
			searchStage.close();
		}			
	}

	public void init()
	{
		final Tooltip summaryTitledPaneTooltip = new Tooltip(
				"Load, the average number of messages per second, is calculated over the following intervals: " +  DialogUtils.getPeriodList() + ".");
		
		eventManager.registerClearTabObserver(this, store);
		
		summaryTitledPaneTitleLabel.setTooltip(summaryTitledPaneTooltip);
		summaryTitledPane.setText(null);
		summaryTitledPane.setGraphic(summaryTitledPaneTitleLabel);
		
		summaryTablePaneController.setStore(store);
		summaryTablePaneController.setNavigationEventDispatcher(eventDispatcher);
		summaryTablePaneController.setConnectionController(connectionController);
		summaryTablePaneController.init();
		
		messagePaneController.setStore(store);
		messagePaneController.setEventDispatcher(eventDispatcher);
		messagePaneController.init();
		
		messageNavigationPaneController.setStore(store);
		messageNavigationPaneController.setNavigationEventDispatcher(eventDispatcher);
		messageNavigationPaneController.init();
		
		if (connectionProperties != null && connectionProperties.getFormatter() != null)
		{
			customFormatterMenu.setDisable(false);
			final RadioMenuItem customFormatterMenuItem = new RadioMenuItem(connectionProperties.getFormatter().getName());
			customFormatterMenuItem.setToggleGroup(wholeMessageFormat);			
			// TODO: check if this is really a custom one
			customFormatterMenuItem.setUserData(connectionProperties.getFormatter());
			customFormatterMenuItem.setSelected(true);
			customFormatterMenu.getItems().add(customFormatterMenuItem);
		}
		
		store.setFormatter((FormatterDetails) wholeMessageFormat.getSelectedToggle().getUserData());
		updateSubscriptionStats();
		
		// logger.info("init(); finished on SubscriptionController");
	}

	public void setStore(final ObservableMessageStoreWithFiltering store)
	{
		this.store = store;
	}
	
	public void setEventManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;
	}

	/**
	 * 
	 * Sets the subscription tab for which this controller is.
	 * 
	 * @param tab The tab for which this controller is
	 */
	public void setTab(final Tab tab)
	{
		this.tab = tab;
	}

	public void setConnectionProperties(RuntimeConnectionProperties connectionProperties)
	{
		this.connectionProperties = connectionProperties;		
	}

	public Tab getTab()
	{
		return tab;
	}

	public void setStatisticsManager(StatisticsManager statisticsManager)
	{
		this.statisticsManager = statisticsManager;
	}
	
	public void setConnectionController(final ConnectionController connectionController)
	{
		this.connectionController = connectionController;
	}
	
	public void updateContextMenu()
	{
		SubscriptionManager.updateSubscriptionTabContextMenu(tab, subscription);
	}

	public void updateSubscriptionStats()
	{
		final int topicCount = store.getObservableMessagesPerTopic().size();
		
		if (subscription == null)
		{
			summaryTitledPaneTitleLabel.setText(String.format(SUMMARY_PANE_TITLE_FORMAT, 
				topicCount == 1 ? "1 topic" : topicCount + " topics",
				statisticsManager.getMessagesReceived(connectionProperties.getId(), 5).overallCount,
				statisticsManager.getMessagesReceived(connectionProperties.getId(), 30).overallCount,
				statisticsManager.getMessagesReceived(connectionProperties.getId(), 300).overallCount));
		}
		else
		{
			final Double avg5sec = statisticsManager.getMessagesReceived(connectionProperties.getId(), 5).messageCount.get(subscription.getTopic());
			final Double avg30sec = statisticsManager.getMessagesReceived(connectionProperties.getId(), 30).messageCount.get(subscription.getTopic());
			final Double avg300sec = statisticsManager.getMessagesReceived(connectionProperties.getId(), 300).messageCount.get(subscription.getTopic());
			
			summaryTitledPaneTitleLabel.setText(String.format(SUMMARY_PANE_TITLE_FORMAT, 
					topicCount == 1 ? "1 topic" : topicCount + " topics",
					avg5sec == null ? 0 : avg5sec, 
					avg30sec == null ? 0 : avg30sec, 
					avg300sec == null ? 0 : avg300sec));
		}
	}
}
