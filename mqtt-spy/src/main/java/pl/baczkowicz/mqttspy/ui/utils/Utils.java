package pl.baczkowicz.mqttspy.ui.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import pl.baczkowicz.mqttspy.exceptions.CriticalException;

public class Utils
{
	public final static String FXML_PACKAGE = "ui/";

	public static final String FXML_LOCATION = "fxml/";

	public static String createBaseRGBString(Color c)
	{
		return "-fx-base: " + createRGBString(c);
	}

	public static String createBgRGBString(Color c, double opacity)
	{
		// -fx-control-inner-background
		// return "-fx-background-color: " + createRGBAString(c, opacity);
		return "-fx-control-inner-background: " + createRGBAString(c, opacity);
	}

	public static String createRGBString(Color c)
	{
		return "rgb(" + (c.getRed() * 255) + "," + (c.getGreen() * 255) + "," + (c.getBlue() * 255) + ");";
	}

	public static String createRGBAString(Color c, double opacity)
	{
		return "rgba(" + (c.getRed() * 255) + "," + (c.getGreen() * 255) + "," + (c.getBlue() * 255) + ", " + opacity
				+ ");";
	}

	public static FXMLLoader createFXMLLoader(final Object object, final String fxmlFile)
	{
		return new FXMLLoader(object.getClass().getResource(fxmlFile));
	}
	
	public static AnchorPane loadAnchorPane(final FXMLLoader loader)
	{
		try
		{
			return (AnchorPane) loader.load();
		}
		catch (IOException e)
		{
			// TODO: log
			throw new CriticalException("Cannot load FXML", e);
		}
	}
}
