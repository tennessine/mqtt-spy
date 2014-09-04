package org.controlsfx.dialog;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class CustomDialog extends Dialog
{
	public CustomDialog(Object owner, String title)
	{
		super(owner, title);
	}
	
	public CustomDialog(Object owner, String title, boolean lightweight, DialogStyle style)
	{
		super(owner, title, lightweight, style);
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