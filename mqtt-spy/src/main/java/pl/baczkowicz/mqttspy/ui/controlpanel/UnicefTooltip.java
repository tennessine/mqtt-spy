package pl.baczkowicz.mqttspy.ui.controlpanel;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.ui.ControlPanelController;

public class UnicefTooltip extends Tooltip
{		
	private final static Logger logger = LoggerFactory.getLogger(UnicefTooltip.class);
	
	private boolean hideRequest;
	
	private MouseEvent mousePositionOnShown;
	
	private MouseEvent currentMousePosition;
	
	public UnicefTooltip()
	{
		final HBox tooltipContent = new HBox();		
		final ImageView logo = new ImageView(new Image(ControlPanelController.class.getResource("/images/unicef.png").toString()));
		final Label text = new Label(
				"Please support UNICEF by donating " + System.lineSeparator()
				+ "at the mqtt-spy fundraising page -" + System.lineSeparator()
				+ "http://fundraise.unicef.org.uk/MyPage/mqtt-spy. " + System.lineSeparator()
				+ "Any donations are very much appreciated. " + System.lineSeparator()
				+ "If you can do it regularly " + System.lineSeparator()
				+ "(e.g. every couple of weeks or months) " + System.lineSeparator()
				+ "that would be awesome!");		
		text.setFont(new Font("System", 11));
		tooltipContent.getChildren().addAll(logo, text);
		tooltipContent.setSpacing(20);
		tooltipContent.setPadding(new Insets(0, 10, 0, 0));
		
		setGraphic(tooltipContent);
		setAutoHide(false);
		setHideOnEscape(true);
		setOpacity(0.8);
	}
	
	@Override
	protected void show()
	{			
		this.getScene().setOnKeyPressed(new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode().equals(KeyCode.ESCAPE))
				{
					logger.trace("Escape - closing tooltip");
					hideTooltip();
				}				
			}
		});
		
		super.show();
		mousePositionOnShown = currentMousePosition;
	}
	
	public MouseEvent getCurrentMousePosition()
	{
		return currentMousePosition;
	}

	public void setCurrentMousePosition(MouseEvent currentMousePosition)
	{
		this.currentMousePosition = currentMousePosition;
	}

	@Override
	public void hide()
	{
		logger.trace("Hiding tooltip request...");
		hideRequest = true;
		
		checkAndHide();			
	}
	
	public void hideTooltip()
	{
		logger.trace("Hiding tooltip...");
		hideRequest = false;
		super.hide();
	}
	
	public boolean hideRequested()
	{
		return hideRequest;
	}

	public MouseEvent getMousePositionOnShown()
	{
		return mousePositionOnShown;
	}
	
	private boolean mouseFurtherThan(final double delta)
	{
		if (getMousePositionOnShown() == null)
		{
			return false;
		}
		
		return (Math.abs(getMousePositionOnShown().getSceneX() - getCurrentMousePosition().getSceneX()) > delta 
				|| Math.abs(getMousePositionOnShown().getSceneY() - getCurrentMousePosition().getSceneY()) > delta);
	}
	
	public void checkAndHide()
	{
		if ((hideRequested() && mouseFurtherThan(5)) || (mouseFurtherThan(15)))
		{
			hideTooltip();
		}
	}
}
