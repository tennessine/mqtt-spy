package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.configuration.generated.ConnectionDetails;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.ui.controlpanel.ItemStatus;
import pl.baczkowicz.mqttspy.versions.VersionManager;
import pl.baczkowicz.mqttspy.versions.generated.MqttSpyVersions;

public class ControlPanelController extends AnchorPane implements Initializable
{
	private final static Logger logger = LoggerFactory.getLogger(ControlPanelController.class);

	private static final double MAX_CONNECTIONS_HEIGHT = 250;

	private static final double MAX_CONFIGURATION_FILE_HEIGHT = 100;

	/**
	 * The name of this field needs to be set to the name of the pane +
	 * Controller (i.e. <fx:id>Controller).
	 */
	@FXML
	private ControlPanelItemController controlPanelItem1Controller;
	
	@FXML
	private ControlPanelItemController controlPanelItem2Controller;
	
	@FXML
	private ControlPanelItemController controlPanelItem3Controller;
	
	@FXML
	private Button button1;
	
	@FXML
	private Button button2;
	
	@FXML
	private Button button3;

	private VersionManager versionManager;

	private Application application;

	private ConfigurationManager configurationManager;

	private MainController mainController;
	
	// ===============================
	// === Initialisation ============
	// ===============================
	
	public ControlPanelController()
	{
		try
		{
			this.versionManager = new VersionManager();
		}
		catch (XMLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initialize(URL location, ResourceBundle resources)
	{
		
	}		
		
	public void init()
	{			
		// Item 1
		button1.setContentDisplay(ContentDisplay.TOP);
		showConfigurationFileStatus(controlPanelItem1Controller, button1);		
		
		// Item 2
		button2.setContentDisplay(ContentDisplay.TOP);		
		showConnections(controlPanelItem2Controller, button2);					
		
		// Item 3
		// button3.setContentDisplay(ContentDisplay.TOP);
		// button3.setAlignment(Pos.TOP_CENTER);
		// button3.setGraphicTextGap(0);
		((AnchorPane) button3.getGraphic()).minHeightProperty().bind(button3.heightProperty().multiply(0.8));			
		// TODO: run this in a separate thread so the app is not blocked?
		checkForUpdates(controlPanelItem3Controller, button3);	
	}
	

	// ===============================
	// === FXML ======================
	// ===============================

	// ===============================
	// === Logic =====================
	// ===============================
	

	public void refreshConnectionsStatus()
	{
		showConnections(controlPanelItem2Controller, button2);				
	}

	public void refreshConfigurationFileStatus()
	{
		showConfigurationFileStatus(controlPanelItem1Controller, button1);		
	}

	private void showConfigurationFileStatus(
			final ControlPanelItemController controller, final Button button)
	{
		button.setMaxHeight(MAX_CONFIGURATION_FILE_HEIGHT);
		if (configurationManager.getLoadedConfigurationFile() == null)
		{
			controller.setTitle("No default configuration file found.");
			controller.setDetails("Click here to create and load a default configuration file from " + 
					ConfigurationManager.DEFAULT_HOME_DIRECTORY + " called " + ConfigurationManager.DEFAULT_FILE_NAME + ".");
			controller.setStatus(ItemStatus.WARN);
			
			button.setOnAction(new EventHandler<ActionEvent>()
			{			
				@Override
				public void handle(ActionEvent event)
				{
					configurationManager.createDefaultConfigurationFile();			
					mainController.loadConfigurationFileAndShowErrorWhenApplicable(ConfigurationManager.getDefaultConfigurationFile());
				}
			});
		}
		else
		{
			// TODO: if custom detected, offer creating a default one and importing all connections
			if (configurationManager.isConfigurationReadOnly())
			{
				controller.setTitle("Configuration file loaded, but it's read-only.");
				controller.setDetails("The configuration that has been loaded from " + configurationManager.getLoadedConfigurationFile().getAbsolutePath() + " is read-only.");
				controller.setStatus(ItemStatus.WARN);
			}
			else
			{
				controller.setTitle("Configuration file loaded successfully.");
				controller.setDetails("The configuration has been loaded from " + configurationManager.getLoadedConfigurationFile().getAbsolutePath() + ".");				
				controller.setStatus(ItemStatus.OK);
			}
		}
		
		controller.refresh();		
	}
//	
//	private ItemStatus convertVersionStatus(final ReleasedVersion release)
//	{
//		switch (release.getVersionStatus())
//		{
//			case CRITICAL:
//				return ItemStatus.ERROR;
//			case NEW_AVAILABLE:
//				return ItemStatus.INFO;
//			case ON_LATEST:
//				return ItemStatus.OK;
//			case UPDATE_RECOMMENDED:
//				return ItemStatus.WARN;
//			default:
//				return ItemStatus.ERROR;		
//		}
//	}
//	
//	private String convertVersionStatusToTitle(final ReleasedVersion release)
//	{
//		switch (release.getVersionStatus())
//		{
//			case ON_LATEST:
//				return "Your mqtt-spy is up to date!";
//			case NEW_AVAILABLE:
//				return "Your mqtt-spy is a bit old now - new version is available!";
//			case UPDATE_RECOMMENDED:
//				return "Your mqtt-spy is now pretty dated - consider an update!";
//			case CRITICAL:
//				return "A critical issue has been identified in your version - update now!";
//			default:
//				return "";		
//		}
//	}
//	
//	private String convertVersionStatusToDetails(final ReleaseStatus release, final List<LatestRelease> latestVersions)
//	{
//		final String current = "Your version is " + configurationManager.getProperty(ConfigurationManager.VERSION_PROPERTY)
//				+ " (released on " + release.getDate() + ")";
//		
//		final StringBuffer sb = new StringBuffer();
//		sb.append(System.lineSeparator() + "Latest versions:" + System.lineSeparator() + System.lineSeparator());
//		
//		for (LatestRelease latest : latestVersions)
//		{
//			sb.append("   - " + latest.getName() + " (released on " + latest.getDate() + "), " + latest.getKeyFeatures() + System.lineSeparator());
//		}
//		
//		switch (release.getUpdateStatus())
//		{
//			case ON_LATEST:
//				return current + ".";
//			case NEW_AVAILABLE:
//				return current + " - " + release.getUpdateNote() + "." + System.lineSeparator() + sb.toString();
//			case UPDATE_RECOMMENDED:
//				return current + " - " + release.getUpdateNote() + "." + System.lineSeparator() + sb.toString();
//			case CRITICAL:
//				return current + " - " + release.getUpdateNote() + "." + System.lineSeparator() + sb.toString();
//			default:
//				return "";		
//		}
//	}
	
	public void showConnections(final ControlPanelItemController controller, final Button button)
	{
		button.setMaxHeight(MAX_CONNECTIONS_HEIGHT);
		final int connections = configurationManager.getConnections().size();
		if (connections > 0)
		{
			controller.setTitle("You have " + connections + " connections configured.");
			controller.setDetails("Click here to edit your connections or on the relevant button to open, connect or reconnect or disconnect.");
			controller.setStatus(ItemStatus.OK);
			
			HBox buttons = new HBox();
			buttons.setSpacing(5);
			
			for (final ConnectionDetails connection : configurationManager.getConnections())
			{
				final Button connectionButton = new Button(connection.getName());
				connectionButton.setFocusTraversable(false);
				
				buttons.getChildren().add(connectionButton);
			}
			// TODO: this needs to refresh the connections, not recreate them
			controller.getItems().getChildren().add(buttons);
			
			button.setOnAction(new EventHandler<ActionEvent>()
			{			
				@Override
				public void handle(ActionEvent event)
				{
					mainController.editConnections();			
				}
			});
		}
		else
		{
			controller.setTitle("You haven't got any connections configured.");
			controller.setDetails("Click here to create a new connection...");
			controller.setStatus(ItemStatus.INFO);
			
			button.setOnAction(new EventHandler<ActionEvent>()
			{			
				@Override
				public void handle(ActionEvent event)
				{
					mainController.createNewConnection();			
				}
			});
		}
		controller.refresh();
	}
	
	public void checkForUpdates(final ControlPanelItemController controller, final Button button)
	{
		button.setOnAction(new EventHandler<ActionEvent>()
		{			
			@Override
			public void handle(ActionEvent event)
			{
				application.getHostServices().showDocument("https://drive.google.com/folderview?id=0B3K44MOFJ2-PTktfSGFPRVhzcEk&usp=sharing");			
			}
		});
		
		// Set the default state
		controller.setStatus(ItemStatus.WARN);
		controller.setTitle("Cannot check for updates - is your internet connection up?");
		controller.setDetails("... or maybe the mqtt-spy update server is down?");
		
		MqttSpyVersions versions = null; 
	
		try
		{
			versions = versionManager.loadVersions();
			logger.info(versions.toString());
		}
		catch (XMLException e)
		{
			// TODO
		}
			
		if (versions != null)
		{
//			for (final ReleasedVersion release : versions.getReleasedVersions().getReleasedVersion())
//			{
//				if (configurationManager.getProperty(ConfigurationManager.VERSION_PROPERTY) == release.getNumber())
//				{
//					controller.setStatus(convertVersionStatus(release));
//					controller.setTitle(convertVersionStatusToTitle(release));
//					controller.setDetails(convertVersionStatusToDetails(release, versions.getLatestVersions().getLatestVersion()));
//				}
//			}
		}	
		
		controller.refresh();
	}
	
	// ===============================
	// === Setters and getters =======
	// ===============================
	
	public void setApplication(final Application application)
	{
		this.application = application;
	}
	
	public void setConfigurationMananger(final ConfigurationManager configurationManager)
	{
		this.configurationManager = configurationManager;
	}
	
	public void setMainController(final MainController mainController)
	{
		this.mainController = mainController;
	}
}
