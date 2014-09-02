package org.controlsfx.dialog;

import static impl.org.controlsfx.i18n.Localization.asKey;
import static impl.org.controlsfx.i18n.Localization.localize;
import static org.controlsfx.dialog.Dialog.Actions.CANCEL;
import static org.controlsfx.dialog.Dialog.Actions.NO;
import static org.controlsfx.dialog.Dialog.Actions.OK;
import static org.controlsfx.dialog.Dialog.Actions.YES;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.dialog.Dialog.ActionTrait;
import org.controlsfx.dialog.Dialogs.UserInfo;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

public class CustomLoginDialog
{
	/**
	 * USE_DEFAULT can be passed in to {@link #title(String)} and
	 * {@link #masthead(String)} methods to specify that the default text for
	 * the dialog should be used, where the default text is specific to the type
	 * of dialog being shown.
	 */
	public static final String USE_DEFAULT = "$$$";

	private Object owner;
	private String title = USE_DEFAULT;
	private String masthead;
	private boolean lightweight;
	private DialogStyle style;
	private Effect backgroundEffect;

	/**
	 * Assigns the owner of the dialog. If an owner is specified, the dialog
	 * will block input to the owner and all parent owners. If no owner is
	 * specified, or if owner is null, the dialog will block input to the entire
	 * application.
	 * 
	 * @param owner
	 *            The dialog owner.
	 * @return dialog instance.
	 */
	public CustomLoginDialog owner(final Object owner)
	{
		this.owner = owner;
		return this;
	}

	/**
	 * Assigns dialog's title
	 * 
	 * @param title
	 *            dialog title
	 * @return dialog instance.
	 */
	public CustomLoginDialog title(final String title)
	{
		this.title = title;
		return this;
	}

	/**
	 * Assigns dialog's masthead
	 * 
	 * @param masthead
	 *            dialog masthead
	 * @return dialog instance.
	 */
	public CustomLoginDialog masthead(final String masthead)
	{
		this.masthead = masthead;
		return this;
	}

	/** This a replacement for Dialogs.showLogin. */
	public Optional<UserInfo> showLogin(final UserInfo initialUserInfo,
			final Callback<UserInfo, Void> authenticator)
	{
		final CustomTextField txUserName = (CustomTextField) TextFields.createClearableTextField();
		txUserName.setLeft(new ImageView(DialogResources.getImage("login.user.icon")));

		final CustomPasswordField txPassword = (CustomPasswordField) TextFields
				.createClearablePasswordField();
		txPassword.setLeft(new ImageView(DialogResources.getImage("login.password.icon")));

		final Label lbMessage = new Label("");
		lbMessage.getStyleClass().addAll("message-banner");
		lbMessage.setVisible(false);
		lbMessage.setManaged(false);

		// // layout a custom GridPane containing the input fields and labels
		final GridPane content = new GridPane();
		content.setHgap(10);
		content.setVgap(10);

		content.add(new Label("User name"), 0, 0);
		content.add(txUserName, 1, 0);
		GridPane.setHgrow(txUserName, Priority.ALWAYS);
		content.add(new Label("Password"), 0, 1);
		content.add(txPassword, 1, 1);
		GridPane.setHgrow(txPassword, Priority.ALWAYS);

		final Action actionLogin = new DefaultDialogAction("Connect", ActionTrait.DEFAULT)
		{
			{
				ButtonBar.setType(this, ButtonType.OK_DONE);
			}

			@Override
			public void handle(ActionEvent ae)
			{
				Dialog dlg = (Dialog) ae.getSource();
				try
				{
					if (authenticator != null)
					{
						authenticator
								.call(new UserInfo(txUserName.getText(), txPassword.getText()));
					}
					lbMessage.setVisible(false);
					lbMessage.setManaged(false);
					dlg.hide();
					dlg.setResult(this);
				}
				catch (final Throwable ex)
				{
					lbMessage.setVisible(true);
					lbMessage.setManaged(true);
					lbMessage.setText(ex.getMessage());
					dlg.sizeToScene();
					dlg.shake();
					ex.printStackTrace();
				}
			}

			public String toString()
			{
				return "LOGIN";
			};
		};

		final Dialog dlg = buildDialog(Type.LOGIN);
		dlg.setContent(content);

		dlg.setResizable(false);
		dlg.setIconifiable(false);
		if (dlg.getGraphic() == null)
		{
			dlg.setGraphic(new ImageView(DialogResources.getImage("login.icon")));
		}
		dlg.getActions().setAll(actionLogin, Dialog.Actions.CANCEL);
		final String userNameCation = "non-empty user name";
		String passwordCaption = "your secret password";
		txUserName.setPromptText(userNameCation);
		txUserName.setText(initialUserInfo.getUserName());
		txPassword.setPromptText(passwordCaption);
		txPassword.setText(new String(initialUserInfo.getPassword()));

		final ValidationSupport validationSupport = new ValidationSupport();
		Platform.runLater(new Runnable()
		{
			public void run()
			{
				String requiredFormat = "'%s' is required";
				validationSupport.registerValidator(txUserName, Validator
						.createEmptyValidator(String.format(requiredFormat, userNameCation)));
				actionLogin.disabledProperty().bind(validationSupport.invalidProperty());
				txUserName.requestFocus();
			}
		});

		return Optional.ofNullable(dlg.show() == actionLogin ? new UserInfo(txUserName.getText(),
				txPassword.getText()) : null);
	}

	private Dialog buildDialog(final Type dlgType)
	{
		String actualTitle = title == null ? null : USE_DEFAULT.equals(title) ? dlgType
				.getDefaultTitle() : title;
		String actualMasthead = masthead == null ? null : (USE_DEFAULT.equals(masthead) ? dlgType
				.getDefaultMasthead() : masthead);
		Dialog dlg = new Dialog(owner, actualTitle, lightweight, style);
		dlg.setResizable(false);
		dlg.setIconifiable(false);
		Image image = dlgType.getImage();
		if (image != null)
		{
			dlg.setGraphic(new ImageView(image));
		}
		dlg.setMasthead(actualMasthead);
		dlg.getActions().addAll(dlgType.getActions());
		dlg.setBackgroundEffect(backgroundEffect);
		return dlg;
	}

	private static enum Type
	{
		ERROR("error.image", asKey("error.dlg.title"), asKey("error.dlg.masthead"), OK), INFORMATION(
				"info.image", asKey("info.dlg.title"), asKey("info.dlg.masthead"), OK), WARNING(
				"warning.image", asKey("warning.dlg.title"), asKey("warning.dlg.masthead"), OK), CONFIRMATION(
				"confirm.image", asKey("confirm.dlg.title"), asKey("confirm.dlg.masthead"), YES,
				NO, CANCEL), INPUT("confirm.image", asKey("input.dlg.title"),
				asKey("input.dlg.masthead"), OK, CANCEL), FONT(null, asKey("font.dlg.title"),
				asKey("font.dlg.masthead"), OK, CANCEL), PROGRESS("info.image",
				asKey("progress.dlg.title"), asKey("progress.dlg.masthead")), LOGIN("login.image",
				asKey("login.dlg.title"), asKey("login.dlg.masthead"), OK, CANCEL);

		private final String defaultTitle;
		private final String defaultMasthead;
		private final Collection<Action> actions;
		private final String imageResource;
		private Image image;

		Type(String imageResource, String defaultTitle, String defaultMasthead, Action... actions)
		{
			this.actions = Arrays.asList(actions);
			this.imageResource = imageResource;
			this.defaultTitle = defaultTitle;
			this.defaultMasthead = defaultMasthead;
		}

		public Image getImage()
		{
			if (image == null && imageResource != null)
			{
				image = DialogResources.getImage(imageResource);
			}
			return image;
		}

		public String getDefaultMasthead()
		{
			return localize(defaultMasthead);
		}

		public String getDefaultTitle()
		{
			return localize(defaultTitle);
		}

		public Collection<Action> getActions()
		{
			return actions;
		}
	}
}
