package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.ui.controlpanel.ItemStatus;

public class ControlPanelItemController extends AnchorPane implements Initializable
{
	final static Logger logger = LoggerFactory.getLogger(ControlPanelItemController.class);

	@FXML
	private ImageView statusIcon;

	@FXML
	private VBox itemsBox;
	
	@FXML
	private Label titleText;
	
	@FXML
	private Label detailsText;

	private ItemStatus status = ItemStatus.ERROR;
		
	// ===============================
	// === Initialisation ============
	// ===============================


	public void initialize(URL location, ResourceBundle resources)
	{		
		//
	}	
	
	public void init()
	{
		//
	}
	
	public void refresh()
	{
		String imageLocation = "";
		switch (status)
		{
			case OK:
				imageLocation = "/images/dialog-ok-apply.png";
				break;
			case INFO:
				imageLocation = "/images/dialog-information.png";
				break;
			case WARN:
				imageLocation = "/images/dialog-warning.png";
				break;
			case ERROR:
				imageLocation = "/images/dialog-error.png";
				break;
			default:
				imageLocation = "/images/dialog-error.png";
				break;
		}
		statusIcon.setImage(new Image(ControlPanelItemController.class.getResource(imageLocation).toString()));
		if (status == ItemStatus.OK)
		{
			statusIcon.setFitHeight(80);
			statusIcon.setFitWidth(80);
		}
		else
		{
			statusIcon.setFitHeight(64);
			statusIcon.setFitWidth(64);
		}
	}

	// ===============================
	// === FXML ======================
	// ===============================

	// ===============================
	// === Logic =====================
	// ===============================

	// ===============================
	// === Setters and getters =======
	// ===============================
	
	public void setValues(final ItemStatus status, final String title, final String details)
	{
		this.status = status;		
		this.titleText.setText(title);
		this.detailsText.setText(details);
	}
	
	public void setStatus(final ItemStatus status)
	{
		this.status = status;
	}
	
	public void setTitle(final String title)
	{
		this.titleText.setText(title);
	}
	
	public void setDetails(final String details)
	{
		this.detailsText.setText(details);
	}
	
	public VBox getItems()
	{
		return this.itemsBox;
	}
}
