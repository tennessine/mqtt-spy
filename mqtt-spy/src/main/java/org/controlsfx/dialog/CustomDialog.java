package org.controlsfx.dialog;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;

import org.controlsfx.tools.Utils;

@SuppressWarnings("deprecation")
public class CustomDialog extends Dialog
{
	public CustomDialog(Object owner, String title)
	{
		super(owner, title);
		Window window = Utils.getWindow(owner);
		this.getStylesheets().addAll(window.getScene().getStylesheets());
	}
	
	public final void setContentWithNoMaxWidth(String contentText)
	{
		if (contentText == null)
			return;

		Label label = new Label(contentText);
		label.setAlignment(Pos.TOP_LEFT);
		label.setTextAlignment(TextAlignment.LEFT);
		label.setMaxWidth(Double.MAX_VALUE);
		label.setMaxHeight(Double.MAX_VALUE);

		label.setWrapText(true);

		setContent(label);
	}
}