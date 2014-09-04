package pl.baczkowicz.mqttspy;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.ui.MainController;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

/**
 * 
 * The main.
 * 
 * @author Kamil Baczkowicz
 */
public class Main extends Application
{
	/** Initial and minimal scene/stage width. */	
	public final static int WIDTH = 800;
	
	/** Initial and minimal scene/stage height. */
	public final static int HEIGHT = 600;
	
	private final static String CONFIGURATION_PARAMETER_NAME = "configuration";
	
	private final static String NO_CONFIGURATION_PARAMETER_NAME = "no-configuration";
	
	// public final static int VERSION_ID = 800;
	
	// public final static String VERSION_NAME = "0.0.8-SNAPSHOT";

	@Override
	public void start(final Stage primaryStage)
	{
		try
		{
			// Load the main window
			final URL resource = getClass().getResource(Utils.FXML_PACKAGE + Utils.FXML_LOCATION + "MainWindow.fxml");
			final FXMLLoader loader = new FXMLLoader(resource);

			// Get the associated pane
			AnchorPane pane = (AnchorPane) loader.load();
			
			// Set scene width, height and style
			final Scene scene = new Scene(pane, WIDTH, HEIGHT);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			// Get the associated controller
			final MainController mainController = (MainController) loader.getController();

			// Set the stage's properties
			primaryStage.setScene(scene);			
			primaryStage.setMinWidth(WIDTH);
			primaryStage.setMinHeight(HEIGHT);

			// Initialise resources in the main controller			
			mainController.setApplication(this);
			mainController.setStage(primaryStage);
			mainController.init();
			
			// Show the main window
			primaryStage.show();
			
			// Load the config file if specified
			final String configurationFileLocation = this.getParameters().getNamed().get(CONFIGURATION_PARAMETER_NAME);
			if (this.getParameters().getNamed().get(NO_CONFIGURATION_PARAMETER_NAME) != null)
			{
				// Do nothing - no config wanted
			}
			else if (configurationFileLocation != null)
			{
				mainController.loadConfigurationFileAndShowErrorWhenApplicable(new File(configurationFileLocation));				
			}
			else
			{
				// If no configuration parameter is specified, use the user's home directory and the default configuration file name
				mainController.loadDefaultConfigurationFile();						
			}
		}
		catch (Exception e)
		{
			LoggerFactory.getLogger(Main.class).error("Error while loading the main window", e);
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
