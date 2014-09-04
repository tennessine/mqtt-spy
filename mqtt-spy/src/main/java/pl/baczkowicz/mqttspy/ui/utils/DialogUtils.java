package pl.baczkowicz.mqttspy.ui.utils;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.CustomLoginDialog;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.controlsfx.dialog.Dialogs.UserInfo;

import pl.baczkowicz.mqttspy.configuration.generated.UserAuthentication;
import pl.baczkowicz.mqttspy.connectivity.MqttUtils;

public class DialogUtils
{
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
		.actions(Dialog.Actions.YES, Dialog.Actions.CANCEL)
		.message("Are you sure you want to delete connection '" + message + "'? This cannot be undone.").showConfirm();		
	}

	public static boolean showUsernameAndPasswordDialog(final Object owner,
			String connectionName, final UserAuthentication userCredentials)
	{
		final UserInfo userInfo = new UserInfo(userCredentials.getUsername(),
				MqttUtils.decodePassword(userCredentials.getPassword()));
		
		final CustomLoginDialog dialog = new CustomLoginDialog();
		dialog.owner(owner);
		dialog.masthead("Enter MQTT user name and password:");
		dialog.title("User credentials for connection " + connectionName);
		Optional<UserInfo> response = dialog.showLogin(userInfo, null);
		
		if (response.isPresent())
		{
			userCredentials.setUsername(response.get().getUserName());
			userCredentials.setPassword(response.get().getPassword());
			return true;
		}
		
		return false;
	}

	public static void showInvalidConfigurationFileDialog(final String message)
	{
		Dialogs.create().owner(null).title("Invalid configuration file").masthead(null)
				.message(message).showError();
		;
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
}
