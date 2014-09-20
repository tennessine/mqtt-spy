package pl.baczkowicz.mqttspy.ui.utils;

import javafx.scene.control.Tab;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;

public class TabUtils
{
	public static void requestClose(final Tab tab)
	{
		TabPaneBehavior behavior = getBehavior(tab);
		if (behavior.canCloseTab(tab))
		{
			behavior.closeTab(tab);
		}
	}

	private static TabPaneBehavior getBehavior(final Tab tab)
	{
		return ((TabPaneSkin) tab.getTabPane().getSkin()).getBehavior();
	}
}
