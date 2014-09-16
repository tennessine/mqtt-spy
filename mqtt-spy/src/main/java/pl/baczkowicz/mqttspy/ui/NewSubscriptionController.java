package pl.baczkowicz.mqttspy.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;
import pl.baczkowicz.mqttspy.ui.utils.DialogUtils;
import pl.baczkowicz.mqttspy.ui.utils.TabUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class NewSubscriptionController implements Initializable
{
	final static Logger logger = LoggerFactory.getLogger(NewSubscriptionController.class);
	
	@FXML
	private Button subscribeButton;

	@FXML
	private ComboBox<String> subscriptionTopicText;
	@FXML
	
	private ChoiceBox<String> subscriptionQosChoice;

	@FXML
	private ColorPicker colorPicker;

	private ObservableList<String> subscriptionTopics = FXCollections.observableArrayList();

	private MqttConnection connection;

	private List<Color> colors = new ArrayList<Color>();

	private ConnectionController connectionController;

	private RuntimeConnectionProperties connectionProperties;

	private boolean active;

	private EventManager eventManager;

	public NewSubscriptionController()
	{
		// 8
		colors.add(Color.valueOf("f9d900"));
		colors.add(Color.valueOf("a9e200"));
		colors.add(Color.valueOf("22bad9"));
		colors.add(Color.valueOf("0181e2"));
		colors.add(Color.valueOf("2f357f"));
		colors.add(Color.valueOf("860061"));
		colors.add(Color.valueOf("c62b00"));
		colors.add(Color.valueOf("ff5700"));

		colors.add(Color.valueOf("f9d950"));
		colors.add(Color.valueOf("a9e250"));
		colors.add(Color.valueOf("22baa9"));
		colors.add(Color.valueOf("018122"));
		colors.add(Color.valueOf("2f351f"));
		colors.add(Color.valueOf("8600F1"));
		colors.add(Color.valueOf("c62b60"));
		colors.add(Color.valueOf("ff5760"));
	}

	public void initialize(URL location, ResourceBundle resources)
	{
		colorPicker.setValue(colors.get(0));
		subscriptionTopicText.setItems(subscriptionTopics);
		
		subscriptionTopicText.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() 
		{
	        @Override
	        public void handle(KeyEvent keyEvent) 
	        {
	        	switch (keyEvent.getCode())
	        	{
		        	case ENTER:
		        	{
		        		if (active)
		        		{
		        			subscribe();
		        		}
		        		break;
		        	}		        	
		        	default:
		        		break;
	        	}
	        }
	    });
	}
	
	public void setActive(final boolean active)
	{
		this.active = active;
		this.subscribeButton.setDisable(!active);
		this.subscriptionTopicText.setDisable(!active);
	}

	public boolean recordSubscriptionTopic(final String subscriptionTopic)
	{
		return Utils.recordTopic(subscriptionTopic, subscriptionTopics);
	}
	
	@FXML
	public void subscribe()
	{
		if (subscriptionTopicText.getValue() != null)
		{
			final String subscriptionTopic = subscriptionTopicText.getValue().toString();
			final SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
			subscriptionDetails.setTopic(subscriptionTopic);
			subscriptionDetails.setQos(subscriptionQosChoice.getSelectionModel().getSelectedIndex());
			
			if (recordSubscriptionTopic(subscriptionTopic))
			{
				subscribe(subscriptionDetails, true);
			}
			else
			{
				DialogUtils.showError("Duplicate topic", "You already have a subscription tab with " + subscriptionTopic + " topic.");
			}
		}
		else
		{
			DialogUtils.showError("Invalid topic", "Cannot subscribe to an empty topic.");
		}
	}
	

	public void subscribe(final SubscriptionDetails subscriptionDetails, final boolean subscribe)
	{
		final MqttSubscription subscription = new MqttSubscription(subscriptionDetails.getTopic(),
				subscriptionDetails.getQos(), colorPicker.getValue(), connection.getMaxMessageStoreSize());

		// Add a new tab
		final SubscriptionController subscriptionController = TabUtils.createSubscriptionTab(false, this, subscription, connection,
				subscription, connectionProperties, connectionController, eventManager);

		final TabPane subscriptionTabs = connectionController.getSubscriptionTabs();

		colorPicker.setValue(colors.get(subscriptionTabs.getTabs().size() % 16));
		subscriptionTabs.getTabs().add(subscriptionController.getTab());

		subscription.setSubscriptionController(subscriptionController);
		subscription.setConnection(connection);
		if (subscribe)
		{
			connection.subscribe(subscription);
		}
		else
		{
			connection.addSubscription(subscription);
			subscription.setActive(false);
		}
	}
	
	public void setEventManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
	
	public void setConnectionController(ConnectionController connectionController)
	{
		this.connectionController = connectionController;
	}

	public void setConnection(MqttConnection connection)
	{
		this.connection = connection;
	}
	
	public void setConnectionProperties(final RuntimeConnectionProperties connectionProperties)
	{
		this.connectionProperties = connectionProperties;		
	}
}
