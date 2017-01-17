package com.teambrmodding.assistedprogression.common.tiles.storage

import com.teambr.bookshelf.common.tiles.traits.{Inventory, Syncable}
import com.teambrmodding.assistedprogression.common.container.storage.ContainerFlushableChest
import com.teambrmodding.assistedprogression.managers.BlockManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{EnumParticleTypes, SoundCategory}

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
class TileFlushableChest extends Syncable with Inventory {

    final val FLUSH_SOUND = 1
    final val AUTO_FLUSH = 2
    final val FLUSH_INTERVAL = 3

    override def initialSize: Int = 27

    var prevLidAngle : Float = 0
    var lidAngle : Float = 0
    var numUsingPlayers : Int = 0
    var ticksSinceSync : Int = -1
    private var flushSound : Boolean = true
    private var autoFlush : Boolean = false
    private var flushInterval : Int = 10
    private var flushTimer : Int = 0

    override def update(): Unit = {
        //AutoFlush
        if (getAutoFlush && !worldObj.isRemote) {
            if (flushTimer / 20 >= flushInterval) {
                this.clear()
                flushTimer = 0
            }
            else
                flushTimer += 1
        }

        if(worldObj != null && !worldObj.isRemote && numUsingPlayers != 0 && (ticksSinceSync + pos.getX + pos.getY + pos.getZ) % 200 == 0) {
            numUsingPlayers = 0
            val f = 5.0F
            val playerList : java.util.List[EntityPlayer] = worldObj.getEntitiesWithinAABB[EntityPlayer](classOf[EntityPlayer], new AxisAlignedBB(pos.getX - f, pos.getY - f, pos.getZ - f, pos.getX + 1 + f, pos.getY + 1 + f, pos.getZ + 1 + f))

            for(x <- 0 until playerList.size()) {
                if (playerList.get(x).openContainer.isInstanceOf[ContainerFlushableChest])
                    numUsingPlayers += 1
            }
        }

        if(worldObj != null && ticksSinceSync < 0 && !worldObj.isRemote)
            worldObj.addBlockEvent(pos, BlockManager.blockFlushableChest, 0, numUsingPlayers)

        ticksSinceSync += 1
        prevLidAngle = lidAngle
        val f = 0.1F
        if(numUsingPlayers > 0 && lidAngle == 0.0F)
            worldObj.playSound(null.asInstanceOf[EntityPlayer], pos, SoundEvents.BLOCK_CHEST_OPEN,
                SoundCategory.BLOCKS, 0.3F, 0.5F)
        if((numUsingPlayers == 0 && lidAngle > 0.0F) || (numUsingPlayers > 0 && lidAngle < 1.0F)) {
            val f1 = lidAngle
            if(numUsingPlayers > 0)
                lidAngle += f
            else
                lidAngle -= f

            if(lidAngle > 1.0F)
                lidAngle = 1.0F

            val f2 = 0.5F
            if(lidAngle < f2 && f1 > f2)
                worldObj.playSound(null.asInstanceOf[EntityPlayer], pos, SoundEvents.BLOCK_CHEST_CLOSE,
                    SoundCategory.BLOCKS, 0.3F, 0.5F)
            if(lidAngle < 0.0F)
                lidAngle = 0.0F
        }
    }

    override def receiveClientEvent(i : Int, j : Int) : Boolean = {
        numUsingPlayers = j
        true
    }

    override def openInventory(player : EntityPlayer) : Unit = {
        if(worldObj == null)
            return
        numUsingPlayers += 1
        worldObj.addBlockEvent(pos, BlockManager.blockFlushableChest, 1, numUsingPlayers)
    }

    override def closeInventory(player : EntityPlayer) : Unit = {
        if(worldObj == null)
            return
        numUsingPlayers -= 1
        worldObj.addBlockEvent(pos, BlockManager.blockFlushableChest, 1, numUsingPlayers)
    }

    override def onInventoryChanged(slot: Int): Unit = {
        super.onInventoryChanged(slot)
        flushTimer = 0
    }

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
    }

    override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        tag.setBoolean("FlushSound", getFlushSound)
        tag.setBoolean("AutoFlush", getAutoFlush)
        tag.setInteger("FlushInterval", getFlushInterval)
        tag
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        setFlushSound(tag.getBoolean("FlushSound"))
        setAutoFlush(tag.getBoolean("AutoFlush"))
        setFlushInterval(tag.getInteger("FlushInterval"))
    }

    override def clear(): Unit = {
        super.clear()
        if(worldObj != null && !worldObj.isRemote) {
            if (flushSound)
                worldObj.playSound(null.asInstanceOf[EntityPlayer], pos, SoundEvents.BLOCK_LAVA_EXTINGUISH,
                    SoundCategory.BLOCKS, 0.3F, 0.5F)
            sendValueToClient(0, 0)
        }
    }

    override def setVariable(id: Int, value: Double): Unit = {
        id match {
            case 0 =>
                if(worldObj != null && worldObj.isRemote) {
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.5, pos.getY + 1, pos.getZ + 0.5, 0, 0, 0)
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.3, pos.getY + 1, pos.getZ + 0.3, 0, 0, 0)
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.7, pos.getY + 1, pos.getZ + 0.7, 0, 0, 0)
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.3, pos.getY + 1, pos.getZ + 0.7, 0, 0, 0)
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.7, pos.getY + 1, pos.getZ + 0.3, 0, 0, 0)
                }
            case 1 => setFlushSound(if (value == -1) false else true)
            case 2 => setAutoFlush(if (value == -1) false else true)
            case 3 => setFlushInterval(value.toInt)
            case _ =>
        }
        markForUpdate()
    }

    override def getVariable(id: Int): Double = {0.0}

    def getFlushSound: Boolean = flushSound

    def setFlushSound(sound: Boolean): Unit = this.flushSound = sound

    def getAutoFlush: Boolean = autoFlush

    def setAutoFlush(flush: Boolean): Unit = this.autoFlush = flush

    def getFlushInterval: Int = flushInterval

    def setFlushInterval(timer: Int): Unit = this.flushInterval = timer
}
