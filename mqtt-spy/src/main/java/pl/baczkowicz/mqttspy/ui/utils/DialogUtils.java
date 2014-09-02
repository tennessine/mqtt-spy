package pl.baczkowicz.mqttspy.ui.utils;

import java.util.Optional;

import org.controlsfx.dialog.CustomLoginDialog;
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

	public static boolean showApplyChangesQuestion(final String message)
	{
		// TODO Auto-generated method stub
		return false;
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
}
