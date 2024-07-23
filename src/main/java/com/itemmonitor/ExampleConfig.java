package com.itemmonitor;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface ExampleConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}

	@ConfigItem(keyName = "itemIds",
			name = "Item IDs",
			description = "Enter the IDs to keep track of. Use commas to separate multiple items.")
	default String itemIds()
	{
		return "563";
	}
}
