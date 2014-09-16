package pl.baczkowicz.mqttspy.ui;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.configuration.ConfiguredConnectionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.PublicationDetails;
import pl.baczkowicz.mqttspy.configuration.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.UserAuthentication;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.connectivity.MqttUtils;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;
import pl.baczkowicz.mqttspy.ui.utils.DialogUtils;
import pl.baczkowicz.mqttspy.ui.utils.TabUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class MainController
{
	private final static Logger logger = LoggerFactory.getLogger(MainController.class);
	
	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private ControlPanelController controlPanelPaneController;
	
	@FXML
	private AnchorPane mainPane;

	@FXML
	private TabPane connectionTabs;

	@FXML
	private MenuItem openConfigFileMenu;

	private EditConnectionsController editConnectionsController;
	
	private Stage editConnectionsStage; 
	
	private MqttManager mqttManager;

	private Application application;

	private final ConfigurationManager configurationManager;
	
	private Stage stage;

	private EventManager eventManager;
	
	private StatisticsManager statisticsManager;
	
	public MainController() throws XMLException
	{
		this.eventManager = new EventManager();
		this.mqttManager = new MqttManager(eventManager);
		this.configurationManager = new ConfigurationManager(eventManager);
		this.statisticsManager = new StatisticsManager();
	}

	@FXML
	public void createNewConnection()
	{
		logger.trace("Creating new connection...");

		showEditConnectionsWindow(true);
	}

	@FXML
	public void editConnections()
	{
		showEditConnectionsWindow(false);
	}
	
	private void initialiseEditConnectionsWindow()
	{
		// This is a dirty hack to reload connection settings :) possibly could be removed if all connections are closed before loading a new config file
		if (editConnectionsController != null)
		{
			eventManager.deregisterConnectionStatusObserver(editConnectionsController);
		}
		
		final FXMLLoader loader = Utils.createFXMLLoader(this, Utils.FXML_LOCATION + "EditConnectionsWindow.fxml");
		final AnchorPane connectionWindow = Utils.loadAnchorPane(loader);
		editConnectionsController = ((EditConnectionsController) loader.getController());
		editConnectionsController.setManager(mqttManager); 		
		editConnectionsController.setMainController(this);
		editConnectionsController.setEventManager(eventManager);
		editConnectionsController.setConfigurationManager(configurationManager);
		editConnectionsController.init();
		
		Scene scene = new Scene(connectionWindow);
		scene.getStylesheets().addAll(mainPane.getScene().getStylesheets());		

		editConnectionsStage = new Stage();
		editConnectionsStage.setTitle("Connection list");		
		editConnectionsStage.initModality(Modality.WINDOW_MODAL);
		editConnectionsStage.initOwner(getParentWindow());
		editConnectionsStage.setScene(scene);
	}
	
	private void showEditConnectionsWindow(final boolean createNew)
	{
		if (editConnectionsController  == null)
		{
			initialiseEditConnectionsWindow();
		}
		
		if (createNew)
		{
			editConnectionsController.newConnection();
		}

		editConnectionsStage.showAndWait();		
		controlPanelPaneController.refreshConnectionsStatus();
	}

	@FXML
	public void exit()
	{
		mqttManager.disconnectAll();		
		statisticsManager.saveStats();
		System.exit(0);
	}

	public void init()
	{
		statisticsManager.loadStats();
		getParentWindow().setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			public void handle(WindowEvent t)
			{
				exit();
			}
		});

		// Clear any test tabs
		stage.setTitle("mqtt-spy v" + configurationManager.getProperty(ConfigurationManager.VERSION_PROPERTY));
		
		controlPanelPaneController.setMainController(this);
		controlPanelPaneController.setConfigurationMananger(configurationManager);
		controlPanelPaneController.setApplication(application);
		controlPanelPaneController.setEventManager(eventManager);
		controlPanelPaneController.setMqttManager(mqttManager);
		controlPanelPaneController.init();	
		
		final StatsChartWindow statsWindow = new StatsChartWindow();
		
		// Scene scene = new Scene(statsWindow);
		// scene.getStylesheets().addAll(mainPane.getScene().getStylesheets());		
		statsWindow.start(new Stage());
	}
	
	public TabPane getConnectionTabs()
	{
		return connectionTabs;
	}

	public void addConnectionTab(Tab tab)
	{
		connectionTabs.getTabs().add(tab);
	}

	private Window getParentWindow()
	{
		return mainPane.getScene().getWindow();
	}

	@FXML
	public void openConfigurationFile()
	{
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select configuration file to open");
		String extensions = "xml";
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("XML file", extensions));

		final File selectedFile = fileChooser.showOpenDialog(getParentWindow());

		if (selectedFile != null)
		{
			loadConfigurationFileAndShowErrorWhenApplicable(selectedFile);			
		}
	}
	
	public void loadConfigurationFileAndShowErrorWhenApplicable(final File selectedFile)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				loadConfigurationFile(selectedFile);				
			}
			
		});		
	}
	
	private void loadConfigurationFile(final File selectedFile)
	{
		logger.info("Loading configuration file from " + selectedFile.getAbsolutePath());

		configurationManager.clear();
		if (configurationManager.loadConfiguration(selectedFile))
		{
			// Only re-initialise if it has been initialised already
			if (editConnectionsController  != null)
			{
				initialiseEditConnectionsWindow();
			}			
			
			// Process the connection settings		
			for (final ConfiguredConnectionDetails connection : configurationManager.getConnections())
			{
				if (connection.isAutoOpen() != null && connection.isAutoOpen())
				{
					
					try
					{
						openConnection(connection, mqttManager);
					}
					catch (ConfigurationException e)
					{
						// TODO: show warning dialog for invalid
						logger.error("Cannot open conection {}", connection.getName(), e);
					}
				}
			}
			
			openConfigFileMenu.setDisable(true);
		}
		
		controlPanelPaneController.refreshConfigurationFileStatus();
		controlPanelPaneController.refreshConnectionsStatus();
	}	
	
	public void openConnection(final ConfiguredConnectionDetails configuredConnectionDetails, final MqttManager mqttManager) throws ConfigurationException
	{
		// Note: this is not a complete ConfiguredConnectionDetails copy but ConnectionDetails copy
		final ConfiguredConnectionDetails connectionDetails = new ConfiguredConnectionDetails();
		configuredConnectionDetails.copyTo(connectionDetails);
		connectionDetails.setId(configuredConnectionDetails.getId());
		
		// Populates the undefined properties with MQTT defaults
		// ConfigurationUtils.populateConnectionDefaults(connectionDetails);
		
		UserAuthentication userCredentials = null;
		
		boolean cancelled = false;
		if (connectionDetails.getUserAuthentication() != null)
		{
			// Copy so that we don't store it in the connection and don't save those values
			userCredentials = new UserAuthentication();
			connectionDetails.getUserAuthentication().copyTo(userCredentials);
			userCredentials.setPassword(MqttUtils.decodePassword(userCredentials.getPassword()));
			
			if (userCredentials.isAskForPassword() || userCredentials.isAskForUsername())
			{
				if (!DialogUtils.showUsernameAndPasswordDialog(stage, connectionDetails.getName(), userCredentials))
				{
					cancelled = true;
				}
				connectionDetails.setUserAuthentication(userCredentials);
			}
		}
		
		if (!cancelled)
		{
			final String validationResult = MqttUtils.validateConnectionDetails(connectionDetails, true);
			if (validationResult != null)
			{
				DialogUtils.showValidationWarning(validationResult);
			}
			else
			{
				final ConnectionController connectionController = TabUtils.loadConnectionTab(this,
						this, mqttManager, new RuntimeConnectionProperties(connectionDetails, userCredentials), eventManager);
				
				for (final PublicationDetails publicationDetails : connectionDetails.getPublication())
				{
					// Add it to the list of pre-defined topics
					connectionController.newPublicationPaneController.recordPublicationTopic(publicationDetails.getTopic());
				}
				
				for (final SubscriptionDetails subscriptionDetails : connectionDetails.getSubscription())
				{
					// Check if we should create a tab for the subscription
					if (subscriptionDetails.isCreateTab())
					{
						connectionController.newSubscriptionPaneController.subscribe(subscriptionDetails, false);
					}
					
					// Add it to the list of pre-defined topics
					connectionController.newSubscriptionPaneController.recordSubscriptionTopic(subscriptionDetails.getTopic());
				}
			}
		}
	}

	public void loadDefaultConfigurationFile()
	{		
		final File defaultConfigurationFile = ConfigurationManager.getDefaultConfigurationFile();
		
		logger.info("Default configuration file present (" + defaultConfigurationFile.getAbsolutePath() + ") = " + defaultConfigurationFile.exists());
		
		if (defaultConfigurationFile.exists())
		{
			loadConfigurationFileAndShowErrorWhenApplicable(defaultConfigurationFile);
		}
		// else
		// {
		// if
		// (DialogUtils.showDefaultConfigurationFileMissingChoice("Default configuration file not found",
		// mainPane.getScene().getWindow()))
		// {
		// loadConfigurationFileAndShowErrorWhenApplicable(ConfigurationManager.getDefaultConfigurationFile());
		// }
		// }
	}
	
	@FXML
	private void restoreConfiguration()
	{
		if (DialogUtils.showDefaultConfigurationFileMissingChoice("Restore defaults", mainPane.getScene().getWindow()))
		{
			loadConfigurationFileAndShowErrorWhenApplicable(ConfigurationManager.getDefaultConfigurationFile());			
		}
	}
	
	@FXML
	private void openProjectWebsite()
	{
		application.getHostServices().showDocument("https://code.google.com/p/mqtt-spy/");
	}
	
	@FXML
	private void overviewWiki()
	{
		application.getHostServices().showDocument("https://code.google.com/p/mqtt-spy/wiki/Overview");		
	}
	
	@FXML
	private void changelogWiki()
	{
		application.getHostServices().showDocument("https://code.google.com/p/mqtt-spy/wiki/Changelog");
	}
	
	@FXML
	private void configurationWiki()
	{
		application.getHostServices().showDocument("https://code.google.com/p/mqtt-spy/wiki/Configuration");
	}
	
	@FXML
	private void loggingWiki()
	{
		application.getHostServices().showDocument("https://code.google.com/p/mqtt-spy/wiki/Logging");
	}

	public void setApplication(Application application)
	{
		this.application = application;
	}

	public void setStage(Stage primaryStage)
	{
		this.stage = primaryStage;		
	}
}
