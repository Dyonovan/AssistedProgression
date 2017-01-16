package com.teambrmodding.assistedprogression.managers

import com.teambrmodding.assistedprogression.common.blocks.{BlockCrafter, BlockPlayerPlate}
import com.teambrmodding.assistedprogression.common.tiles.TileCrafter
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary

/**
  * This file was created for Assisted-Progression
  *
  * Assisted-Progression is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis - pauljoda
  * @since 1/16/2017
  */
object BlockManager {

    // Crafter
    val blockCrafter = new BlockCrafter("blockCrafter", classOf[TileCrafter])

    // Player Plate
    val playerPlate = new BlockPlayerPlate

    def preInit(): Unit = {
        //Crafter
        registerBlock(blockCrafter, "blockCrafter", classOf[TileCrafter])

        // Player Plate
        registerBlock(playerPlate, "playerPlate", null)
    }


    /**
      * Helper method for registering block
      *
      * @param block      The block to register
      * @param name       The name to register the block to
      * @param tileEntity The tile entity, null if none
      * @param oreDict    The ore dict tag, should it be needed
      */
    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity], oreDict: String) : Block = {
        GameRegistry.registerBlock(block, name)
        if (tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name)
        if (oreDict != null)
            OreDictionary.registerOre(oreDict, block)
        block
    }

    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity], itemBlock: Class[_ <: ItemBlock]) : Block = {
        GameRegistry.registerBlock(block, itemBlock, name)
        if (tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name)
        block
    }

    /**
      * No ore dict helper method
      *
      * @param block      The block to add
      * @param name       The name
      * @param tileEntity The tile
      */
    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity]) : Block = {
        val oreDict: String = null
        registerBlock(block, name, tileEntity, oreDict)
    }
}
