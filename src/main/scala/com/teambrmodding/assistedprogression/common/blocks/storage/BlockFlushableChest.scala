package com.teambrmodding.assistedprogression.common.blocks.storage

import com.teambr.bookshelf.common.blocks.properties.Properties
import com.teambr.bookshelf.common.blocks.traits.DropsItems
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import com.teambr.bookshelf.util.WorldUtils
import com.teambrmodding.assistedprogression.client.gui.storage.GuiFlushableChest
import com.teambrmodding.assistedprogression.common.blocks.BaseBlock
import com.teambrmodding.assistedprogression.common.container.storage.ContainerFlushableChest
import com.teambrmodding.assistedprogression.common.tiles.storage.TileFlushableChest
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Container, IInventory}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.{BlockPos, MathHelper}
import net.minecraft.util.{EnumBlockRenderType, EnumFacing}
import net.minecraft.world.World
import net.minecraftforge.common.property.{ExtendedBlockState, IUnlistedProperty}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for AssistedProgression
  *
  * AssistedProgression is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 1/16/2017
  */
class BlockFlushableChest extends BaseBlock(Material.IRON, "blockFlushableChest", classOf[TileFlushableChest]) with DropsItems with OpensGui {

    this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F)

    /**
      * Called when neighbor changes so we can check for Redstone Signal
      */
    override def neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block): Unit = {
        if (!world.isRemote) {
            if (world.isBlockPowered(pos)) {
                world.getTileEntity(pos).asInstanceOf[TileFlushableChest].clear()
                world.setBlockState(pos, state, 6)
            }
        }
    }

    /**
      * Called when the block is placed, we check which way the player is facing and put our value as the opposite of that
      */
    override def onBlockPlaced(world : World, blockPos : BlockPos, facing : EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        val playerFacingDirection = if (placer == null) 0 else MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3
        val enumFacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite
        this.getDefaultState.withProperty(Properties.FOUR_WAY, enumFacing)
    }

    /**
      * Allows rotation of the block with a wrench
      */
    override def rotateBlock(world : World, pos : BlockPos, side : EnumFacing) : Boolean = {
        val tag = new NBTTagCompound
        world.getTileEntity(pos).writeToNBT(tag)
        if(side != EnumFacing.UP && side != EnumFacing.DOWN)
            world.setBlockState(pos, world.getBlockState(pos).withProperty(Properties.FOUR_WAY, side))
        else
            world.setBlockState(pos, world.getBlockState(pos).withProperty(Properties.FOUR_WAY, WorldUtils.rotateRight(world.getBlockState(pos).getValue(Properties.FOUR_WAY))))
        if(tag != null) {
            world.getTileEntity(pos).readFromNBT(tag)
        }
        true
    }

    /*
     * Block Display Properties
     */

    override def isOpaqueCube(state: IBlockState): Boolean = false

    override def isFullCube(state: IBlockState): Boolean = false

    override def getRenderType(state: IBlockState): EnumBlockRenderType = EnumBlockRenderType.ENTITYBLOCK_ANIMATED

    /*
     * BlockState Crap
     */

    /**
      * Used to say what our block state is
      */
    override def createBlockState() : BlockStateContainer = {
        val listed = new Array[IProperty[_]](1)
        listed(0) = Properties.FOUR_WAY
        val unlisted = new Array[IUnlistedProperty[_]](0)
        new ExtendedBlockState(this, listed, unlisted)
    }

    /**
      * Used to convert the meta to state
      *
      * @param meta The meta
      * @return
      */
    override def getStateFromMeta(meta : Int) : IBlockState = getDefaultState.withProperty(Properties.FOUR_WAY, EnumFacing.getFront(meta))

    /**
      * Called to convert state from meta
      *
      * @param state The state
      * @return
      */
    override def getMetaFromState(state : IBlockState): Int = state.getValue(Properties.FOUR_WAY).getIndex

    /*
     * Redstone Code
     */

    override def hasComparatorInputOverride(state : IBlockState) = true

    override def getComparatorInputOverride(state : IBlockState, world : World, pos : BlockPos) : Int = {
        val tile = world.getTileEntity(pos)
        tile match {
            case inventory: IInventory => Container.calcRedstoneFromInventory(inventory)
            case _ => 0
        }
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        // TODO Wrench for rotation???
        //if(player.inventory.getCurrentItem == null || (player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem != ItemManager.wrench))
            new ContainerFlushableChest(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFlushableChest])
        //else null
    }

    @SideOnly(Side.CLIENT)
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        //if(player.inventory.getCurrentItem == null || (player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem != ItemManager.wrench))
            new GuiFlushableChest(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFlushableChest])
        //else null
    }
}
