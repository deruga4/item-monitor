package com.itemmonitor;

import lombok.Getter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.util.QuantityFormatter;

import java.awt.image.BufferedImage;

public class ItemCounter extends Counter {

    @Getter
    private final int itemId;

    private final String name;


    ItemCounter(Plugin plugin, int itemId, int count, String name, BufferedImage image){
        super(image, plugin, count);
        this.itemId = itemId;
        this.name = name;
    }

    @Override
    public String getText()
    {
        return QuantityFormatter.quantityToRSDecimalStack(getCount());
    }

    @Override
    public String getTooltip()
    {
        return name;
    }
}
