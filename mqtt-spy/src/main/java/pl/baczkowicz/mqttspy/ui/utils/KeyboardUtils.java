package pl.baczkowicz.mqttspy.ui.utils;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class KeyboardUtils
{
	public static final EventHandler<KeyEvent> nonNumericKeyConsumer = new EventHandler<KeyEvent>()
	{
		public void handle(KeyEvent t)
		{
			char ar[] = t.getCharacter().toCharArray();
			char ch = ar[t.getCharacter().toCharArray().length - 1];
			if (!(ch >= '0' && ch <= '9'))
			{
				t.consume();
			}
		}
	};
}
