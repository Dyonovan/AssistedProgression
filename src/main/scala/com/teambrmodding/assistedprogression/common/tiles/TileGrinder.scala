package com.teambrmodding.assistedprogression.common.tiles

import com.teambr.bookshelf.common.tiles.{InventoryHandler, Syncable}
import com.teambrmodding.assistedprogression.managers.RecipeManager
import com.teambrmodding.assistedprogression.registries.GrinderRecipeHandler
import net.minecraft.block.Block
import net.minecraft.init.{Blocks, SoundEvents}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumParticleTypes, SoundCategory}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/11/2016
  */
class TileGrinder extends Syncable with InventoryHandler {
    //override def initialSize: Int = 7

    var progress : Int = 0
    val MAX_PROGRESS : Int = 15

    def activateGrinder(progressValue : Int, multiplier : Double): Unit = {
        updateCurrentItem()
        getWorld.notifyBlockUpdate(pos, getWorld.getBlockState(pos), getWorld.getBlockState(pos), 6)
        if(getStackInSlot(3) != null && hasOutputAvailable) {
            var movement = progressValue
            sendValueToClient(0, 0)
            if(progressValue == 1)
                movement = 2
            progress += (movement * multiplier).toInt
            if(progress >= MAX_PROGRESS) {
                progress = progress - MAX_PROGRESS
                grindItem()
                //getWorld.playSound(null, pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, SoundEvents.BLOCK_GRAVEL_BREAK, SoundCategory.BLOCKS, 0.6F, 1.1F)
                getWorld.playSound(null, pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, SoundEvents.BLOCK_SAND_BREAK, SoundCategory.BLOCKS,  0.6F, 1.1F)
                getWorld.playSound(null, pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.1F, 0.4F)
                if(getStackInSlot(3) == null)
                    progress = 0
                getWorld.notifyBlockUpdate(pos, getWorld.getBlockState(pos), getWorld.getBlockState(pos), 6)
                if(progress >= MAX_PROGRESS)
                    activateGrinder(0, 0)
            }
        } else
            progress = 0
    }

    def updateCurrentItem(): Unit = {
        if(getStackInSlot(3) == null) {
            for(x <- 0 until 3) { //Loop inputs
                if(getStackInSlot(x) != null) { //If we have found something...
                    setInventorySlotContents(3, getStackInSlot(x).copy()) //Move stack into middle
                    setInventorySlotContents(x, null) //Remove stack
                    return //Leave
                }
            }
        } else if(getStackInSlot(3).stackSize < getStackInSlot(3).getMaxStackSize) { //If we can fill the grinding
            for(x <- 0 until 3) { //Loop Inputs
                if(getStackInSlot(x) != null && getStackInSlot(x).getItem == getStackInSlot(3).getItem
                        && getStackInSlot(x).getItemDamage == getStackInSlot(3).getItemDamage) { //Must be same item, I'm assuming its just item and meta, no NBT
                val space = getStackInSlot(3).getMaxStackSize - getStackInSlot(3).stackSize
                    if(getStackInSlot(x).stackSize <= space) { //We will move entire stack
                        getStackInSlot(3).stackSize += getStackInSlot(x).stackSize //Add smaller stack
                        setInventorySlotContents(x, null) //Remove used stack
                    } else { //Move what you can
                        getStackInSlot(x).stackSize -= space //Remove what we need
                        getStackInSlot(3).stackSize += space //Add what we need
                        return //Stack full, exit
                    }
                }
            }
        }
    }

    def hasOutputAvailable : Boolean = {
        val output = RecipeManager.getHandler[GrinderRecipeHandler](RecipeManager.Grinder).getOutput(getStackInSlot(3)).get._1
        for(x <- 4 until 7) {
            if(getStackInSlot(x) != null && getStackInSlot(x).stackSize <= getStackInSlot(x).getMaxStackSize &&
                    getStackInSlot(x).getItem == output.getItem &&
                    getStackInSlot(x).getItemDamage == output.getItemDamage &&
                    getStackInSlot(x).stackSize + output.stackSize <= getStackInSlot(x).getMaxStackSize) {
                return true
            }
            RecipeManager.getHandler[GrinderRecipeHandler](RecipeManager.Grinder)
        }
        for(x <- 4 until 7) { //We only want to do this is we have to, merge first if you can
            if (getStackInSlot(x) == null)
                return true
        }
        false
    }

    def grindItem() : Unit = {
        val output = RecipeManager.getHandler[GrinderRecipeHandler](RecipeManager.Grinder).getOutput(getStackInSlot(3)).get._1
        for(x <- 4 until 7) {
            if(getStackInSlot(x) != null && getStackInSlot(x).stackSize <= getStackInSlot(x).getMaxStackSize &&
                    getStackInSlot(x).getItem == output.getItem &&
                    getStackInSlot(x).getItemDamage == output.getItemDamage &&
                    getStackInSlot(x).stackSize + output.stackSize <= getStackInSlot(x).getMaxStackSize) {
                getStackInSlot(3).stackSize -= 1
                getStackInSlot(x).stackSize += output.stackSize
                if(getStackInSlot(3).stackSize <= 0)
                    setInventorySlotContents(3, null)
                return
            }
        }
        for(x <- 4 until 7) { //We only want to do this is we have to, merge first if you can
            if (getStackInSlot(x) == null) {
                getStackInSlot(3).stackSize -= 1
                setInventorySlotContents(x, output.copy())
                if (getStackInSlot(3).stackSize <= 0)
                    setInventorySlotContents(3, null)
                return
            }
        }
    }

    override def isItemValidForSlot(index: Int, stack: ItemStack) : Boolean = {
        if(index < 3)
            RecipeManager.getHandler[GrinderRecipeHandler](RecipeManager.Grinder).isValidInput(stack)
        else
            false
    }

    override def writeToNBT(tag : NBTTagCompound) : NBTTagCompound = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        tag.setInteger("progress", progress)
        tag
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        progress = tag.getInteger("progress")
    }

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
    }

    override def setVariable(id: Int, value: Double): Unit = {
        id match {
            case 0 =>
                for(x <- 0 until 4)
                getWorld.spawnParticle(EnumParticleTypes.FALLING_DUST, pos.getX + 0.5D, pos.getY + 0.3D, pos.getZ + 0.5, 0D, 0D, 0D,
                    Block.getStateId(Blocks.GRAVEL.getDefaultState))
            case _ =>
        }
    }

    override def getVariable(id: Int): Double = { 0.0 }
}
