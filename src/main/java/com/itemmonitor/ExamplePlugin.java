package com.itemmonitor;

import java.util.Set;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.game.ItemManager;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Item Monitor"
)
public class ExamplePlugin extends Plugin
{
	private static final Set<Integer> DIZANAS_QUIVER_IDS = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DIZANAS_QUIVER)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BLESSED_DIZANAS_QUIVER)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DIZANAS_MAX_CAPE)))
			.build();

	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	private ItemCounter lawRuneBox;

	private boolean isWearingQuiver;

	private ItemContainer container;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");

		this.container = client.getItemContainer(InventoryID.INVENTORY);
		log.info("" + this.container.count(563));

		if (container != null)
		{
			checkInventory(container);
		}

	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() != client.getItemContainer(InventoryID.EQUIPMENT))
		{
			return;
		}

		checkInventory(event.getItemContainer());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{

	}

	private void updateInfobox(final Item item, final ItemComposition comp)
	{
		if (lawRuneBox != null && lawRuneBox.getItemId() == item.getId())
		{
			lawRuneBox.setCount(item.getQuantity());
			return;
		}

//		removeInfobox();
		final BufferedImage image = itemManager.getImage(item.getId(), 5, false);
		lawRuneBox = new ItemCounter(this, item.getId(), item.getQuantity(), comp.getName(), image);
		infoBoxManager.addInfoBox(lawRuneBox);
	}

//	private void removeInfobox()
//	{
//		infoBoxManager.removeInfoBox(lawRuneBox);
//		lawRuneBox = null;
//	}

	public void checkInventory(ItemContainer equipment){
		final Item cape = equipment.getItem(EquipmentInventorySlot.CAPE.getSlotIdx());
		this.isWearingQuiver = cape != null && DIZANAS_QUIVER_IDS.contains(cape.getId());
		checkQuiver();

		// Check for weapon slot items. This overrides the ammo slot,
		// as the player will use the thrown weapon (eg. chinchompas, knives, darts)
		int weaponCount;
		int ammoCount;
		int inventoryCount;

		final Item weapon = equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weapon != null)
		{
			final ItemComposition weaponComp = itemManager.getItemComposition(weapon.getId());
			if (weaponComp.isStackable())
			{
				updateInfobox(weapon, weaponComp);
				return;
			}
		}

		final Item ammo = equipment.getItem(EquipmentInventorySlot.AMMO.getSlotIdx());
		final Item item = new Item(563, this.container.count(563));
		if (ammo == null)
		{
//			removeInfobox();
			return;
		}

//		final ItemComposition comp = itemManager.getItemComposition(ammo.getId());
		final ItemComposition comp = itemManager.getItemComposition(563);
		if (!comp.isStackable())
		{
//			removeInfobox();
			return;
		}

		updateInfobox(item, comp);
	}

	private void checkQuiver()
	{
		if (!isWearingQuiver)
		{
			//removeQuiverInfobox();
			return;
		}

		final int quiverAmmoId = client.getVarpValue(VarPlayer.DIZANAS_QUIVER_ITEM_ID);
		final int quiverAmmoCount = client.getVarpValue(VarPlayer.DIZANAS_QUIVER_ITEM_COUNT);
		if (quiverAmmoId == -1 || quiverAmmoCount == 0)
		{
			//removeQuiverInfobox();
			return;
		}

		//updateQuiverInfobox(quiverAmmoId, quiverAmmoCount);
	}

//	private void removeQuiverInfobox()
//	{
//		infoBoxManager.removeInfoBox(lawRuneBox);
//		lawRuneBox = null;
//	}

//	private void updateQuiverInfobox(final int itemId, final int count)
//	{
//		if (quiverBox != null && quiverBox.getItemID() == itemId)
//		{
//			quiverBox.setCount(count);
//			return;
//		}
//
//		final ItemComposition comp = itemManager.getItemComposition(itemId);
//
//		removeQuiverInfobox();
//		final BufferedImage image = itemManager.getImage(itemId, 5, false);
//		quiverBox = new AmmoCounter(this, itemId, count, comp.getName(), image);
//		infoBoxManager.addInfoBox(quiverBox);
//	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
