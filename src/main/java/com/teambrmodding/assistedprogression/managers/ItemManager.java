package com.teambrmodding.assistedprogression.managers;

import com.teambrmodding.assistedprogression.common.items.*;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 11/13/17
 */
public class ItemManager {

    /*******************************************************************************************************************
     * Item Variables                                                                                                  *
     *******************************************************************************************************************/

    public static ItemMagnet itemCheapMagnet = new ItemMagnet(true, "itemCheapMagnet");
    public static ItemMagnet itemElectroMagnet = new ItemMagnet(false,"itemElectroMagnet");
    public static ItemTrashBag itemTrashBag = new ItemTrashBag("itemTrashBag", 1);
    public static ItemTrashBag itemHeftyBag = new ItemTrashBag("itemHeftyBag", 18);
    public static ItemSpawnerRelocator itemSpawnerRelocator = new ItemSpawnerRelocator();
    public static ItemExchanger itemExchanger = new ItemExchanger();
    public static ItemPipette itemPipette = new ItemPipette();

    /*******************************************************************************************************************
     * Registration                                                                                                    *
     *******************************************************************************************************************/

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(itemCheapMagnet, itemElectroMagnet,
                itemTrashBag, itemHeftyBag, itemSpawnerRelocator, itemExchanger, itemPipette);
    }
}
