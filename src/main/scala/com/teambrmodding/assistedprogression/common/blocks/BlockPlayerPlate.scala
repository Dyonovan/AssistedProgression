package com.teambrmodding.assistedprogression.common.blocks

import com.teambrmodding.assistedprogression.AssistedProgression
import com.teambrmodding.assistedprogression.lib.Reference
import net.minecraft.block.material.Material
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.block.{BlockBasePressurePlate, BlockPressurePlate}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.World

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/16/2016
  */
class BlockPlayerPlate extends BlockBasePressurePlate(Material.IRON) {

    this.setDefaultState(this.blockState.getBaseState.withProperty(BlockPressurePlate.POWERED, false.asInstanceOf[java.lang.Boolean]))
    setUnlocalizedName(Reference.MOD_ID + ":" + "blockPlayerPlate")
    setCreativeTab(AssistedProgression.tabAssistedProgression)
    setHardness(2.0F)

    override def setRedstoneStrength(state: IBlockState, strength: Int): IBlockState =
        state.withProperty(BlockPressurePlate.POWERED, (strength > 0).asInstanceOf[java.lang.Boolean])

    override def getRedstoneStrength(state: IBlockState): Int =
        if(state.getValue(BlockPressurePlate.POWERED).asInstanceOf[Boolean]) 15 else 0

    override def computeRedstoneStrength(worldIn: World, pos: BlockPos): Int = {
        val f = 0.125F
        val list = worldIn.getEntitiesWithinAABB(classOf[EntityPlayer], new AxisAlignedBB((pos.getX.toFloat + f).toDouble, pos.getY.toDouble, (pos.getZ.toFloat + f).toDouble, ((pos.getX + 1).toFloat - f).toDouble, pos.getY.toDouble + 0.25D, ((pos.getZ + 1).toFloat - f).toDouble))
        if(!list.isEmpty)
            15
        else
            0
    }

    override def getStateFromMeta (meta: Int) : IBlockState = {
        this.getDefaultState.withProperty(BlockPressurePlate.POWERED, (meta == 1).asInstanceOf[java.lang.Boolean])
    }
    /**
      * Convert the BlockState into the correct metadata value
      */
    override def getMetaFromState(state : IBlockState) : Int = {
         if(state.getValue(BlockPressurePlate.POWERED).asInstanceOf[Boolean]) 1 else 0
    }

    override def createBlockState() : BlockStateContainer = {
         new BlockStateContainer(this, BlockPressurePlate.POWERED)
    }

    override def playClickOnSound(worldIn: World, color: BlockPos): Unit =
        worldIn.playSound(null.asInstanceOf[EntityPlayer], color, SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_ON,
            SoundCategory.BLOCKS, 0.3F, 0.6F)

    override def playClickOffSound(worldIn: World, pos: BlockPos): Unit =
        worldIn.playSound(null.asInstanceOf[EntityPlayer], pos, SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_OFF,
            SoundCategory.BLOCKS, 0.3F, 0.5F)
}
