package pl.baczkowicz.mqttspy.ui.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javafx.util.Pair;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.CustomDialogs;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogAction;
import org.controlsfx.dialog.Dialogs;

import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.configuration.ConfigurationUtils;
import pl.baczkowicz.mqttspy.configuration.generated.UserAuthentication;
import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;

@SuppressWarnings("deprecation")
public class DialogUtils
{
	public static final String STATS_FORMAT = "load: " + getPeriodValues();
	
	public static String getPeriodList()
	{
		final StringBuffer sb = new StringBuffer();
		
		final Iterator<Integer> iterator = StatisticsManager.periods.iterator();
		while (iterator.hasNext()) 
		{
			final int period = (int) iterator.next();
			if (period > 60)
			{
				sb.append((period / 60) + "m");
			}
			else
			{
				sb.append(period + "s");
			}
			
			if (iterator.hasNext())
			{
				sb.append(", ");
			}
		}
		
		return sb.toString();
	}
	
	public static String getPeriodValues()
	{
		final StringBuffer sb = new StringBuffer();
		
		final Iterator<Integer> iterator = StatisticsManager.periods.iterator();
		while (iterator.hasNext()) 
		{
			sb.append("%.1f");	
			iterator.next();
			
			if (iterator.hasNext())
			{
				sb.append("/");
			}
		}
		
		return sb.toString();
	}
	
	public static void showError(final String title, final String message)
	{
		Dialogs.create().owner(null).title(title).masthead(null).message(message).showError();
	}
	
	public static void showValidationWarning(final String message)
	{
		Dialogs.create().owner(null).title("Invalid value detected").masthead(null)
				.message(message + ".").showWarning();
	}

	public static Action showApplyChangesQuestion(final String message)
	{
		return Dialogs.create().owner(null).title("Unsaved changes detected").masthead(null)
		.message("You've got unsaved changes for " + message + ". Do you want to save/apply them now?").showConfirm();		
	}
	
	public static Action showDeleteQuestion(final String message)
	{
		return Dialogs.create().owner(null).title("Deleting connection").masthead(null)
		.actions(Dialog.ACTION_YES, Dialog.ACTION_CANCEL)
		.message("Are you sure you want to delete connection '" + message + "'? This cannot be undone.").showConfirm();		
	}

	public static boolean showUsernameAndPasswordDialog(final Object owner,
			String connectionName, final UserAuthentication userCredentials)
	{
		// final UserInfo userInfo = new UserInfo(userCredentials.getUsername(), MqttUtils.decodePassword(userCredentials.getPassword()));
		final Pair<String, String> userInfo = new Pair<String, String>(userCredentials.getUsername(), userCredentials.getPassword());
		
		final CustomDialogs dialog = new CustomDialogs();
		dialog.owner(owner);
		dialog.masthead("Enter MQTT user name and password:");
		dialog.title("User credentials for connection " + connectionName);
		Optional<Pair<String, String>> response = dialog.showLogin(userInfo, null);
		
		if (response.isPresent())
		{
			userCredentials.setUsername(response.get().getKey());
			userCredentials.setPassword(response.get().getValue());
			return true;
		}
		
		return false;
	}

	public static void showInvalidConfigurationFileDialog(final String message)
	{
		Dialogs.create().owner(null).title("Invalid configuration file").masthead(null)
				.message(message).showError();
	}

	public static void showReadOnlyWarning(final String absolutePath)
	{
		Dialogs.create()
				.owner(null)
				.title("Read-only configuration file")
				.masthead(null)
				.message(
						"The configuration file that has been loaded (" + absolutePath
								+ ") is read-only. Changes won't be saved. "
								+ "Please make the file writeable for any changes to be saved.")
				.showWarning();
	}
	
	public static void updateConnectionTooltip(final MqttAsyncConnection connection, final Tooltip tooltip, final StatisticsManager statisticsManager)
	{
		final StringBuffer sb = new StringBuffer();
		sb.append("Status: " + connection.getConnectionStatus().toString().toLowerCase());
		if (connection.getConnectionStatus().equals(MqttConnectionStatus.DISCONNECTED))
		{
			if (!connection.getDisconnectionReason().isEmpty())
			{
				sb.append(System.getProperty("line.separator") + "Reason: " + connection.getDisconnectionReason().toLowerCase());
			}
		}
		
		tooltip.setText(sb.toString());
	}
	
	public static void showTooltip(final Button button, final String message)
	{
		final Tooltip tooltip = new Tooltip(message);
		button.setTooltip(tooltip);
		tooltip.setAutoHide(true);
		tooltip.setAutoFix(true);
		Point2D p = button.localToScene(0.0, 0.0);	    
		tooltip.show(button.getScene().getWindow(), 
				p.getX() + button.getScene().getX() + button.getScene().getWindow().getX() - 50, 
		        p.getY() + button.getScene().getY() + button.getScene().getWindow().getY() - 50);
		
		new Thread(new Runnable()
		{
			@Override
			public void run()			
			{
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						button.setTooltip(null);
						tooltip.hide();
					}				
				});
			}		
		}).start();
	}

	public static boolean showDefaultConfigurationFileMissingChoice(final String title, final Window window)
	{	
		final DialogAction createWithSample = new DialogAction("Create mqtt-spy configuration file with sample content");
		createWithSample.setLongText(System.getProperty("line.separator") + "This creates a configuration file " +  
                "in \"" + ConfigurationManager.DEFAULT_HOME_DIRECTORY + "\"" + 
                " called \"" + ConfigurationManager.DEFAULT_FILE_NAME + "\"" + 
                ", which will include sample connections to localhost and iot.eclipse.org.");
		
		 final DialogAction createEmpty = new DialogAction("Create empty mqtt-spy configuration file");
		 createEmpty.setLongText(
 				System.getProperty("line.separator") + "This creates a configuration file " +  
                 "in \"" + ConfigurationManager.DEFAULT_HOME_DIRECTORY + "\"" + 
                 " called \"" + ConfigurationManager.DEFAULT_FILE_NAME + "\" with no sample connections.");
		 
		 final DialogAction copyExisting = new DialogAction("Copy existing mqtt-spy configuration file");
		 copyExisting.setLongText(
				 System.getProperty("line.separator") + "This copies an existing configuration file (selected in the next step) " +  
                 "to \"" + ConfigurationManager.DEFAULT_HOME_DIRECTORY + "\"" + 
                 " and renames it to \"" + ConfigurationManager.DEFAULT_FILE_NAME + "\".");
		 
		 final DialogAction dontDoAnything = new DialogAction("Don't do anything");
		 dontDoAnything.setLongText(
				 System.getProperty("line.separator") + "You can still point mqtt-spy at your chosen configuration file " +  
                 "by using the \"--configuration=my_custom_path\"" + 
                 " command line parameter or open a configuration file from the main menu.");
		
		final List<DialogAction> links = Arrays.asList(createWithSample, createEmpty, copyExisting, dontDoAnything);
		
		final CustomDialogs dialog = new CustomDialogs();
		dialog
	      .owner(window)
	      .title(title)
	      .masthead(null)
	      .message("Please select one of the following options with regards to the mqtt-spy configuration file:");
		
		Action response = dialog.showCommandLinks(links.get(0), links, 650, 30, 110);
		boolean configurationFileCreated = false;
		
		if (response.textProperty().getValue().toLowerCase().contains("sample"))
		{
			configurationFileCreated = ConfigurationUtils.createDefaultConfigFromClassPath("sample");
		}
		else if (response.textProperty().getValue().toLowerCase().contains("empty"))
		{
			configurationFileCreated = ConfigurationUtils.createDefaultConfigFromClassPath("empty");
		}
		else if (response.textProperty().getValue().toLowerCase().contains("copy"))
		{
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select configuration file to copy");
			String extensions = "xml";
			fileChooser.setSelectedExtensionFilter(new ExtensionFilter("XML file", extensions));

			final File selectedFile = fileChooser.showOpenDialog(window);

			if (selectedFile != null)
			{
				configurationFileCreated = ConfigurationUtils.createDefaultConfigFromFile(selectedFile);
			}
		}
		else
		{
			// Do nothing
		}
		
		return configurationFileCreated;
	}
}
