package com.teambrmodding.assistedprogression.common.items;

import com.teambrmodding.assistedprogression.AssistedProgression;
import com.teambrmodding.assistedprogression.lib.Reference;
import com.teambrmodding.assistedprogression.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This file was created for Assisted Progression
 * <p>
 * Assisted Progression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan - Paul Davis
 * @since 10/8/2016 - 01/18/2017
 */
public class ItemExchanger extends Item {

    private static final String SIZE_NBT_TAG = "size";
    private static final String EXCHANGE_NBT_TAG = "exchanging";

    public ItemExchanger() {
        super();
        this.setUnlocalizedName(Reference.MOD_ID() + ":itemExchanger");
        this.setCreativeTab(AssistedProgression.tabAssistedProgression());
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                if (world.getTileEntity(pos) == null) {
                    setExchangeBlock(stack, new ItemStack(world.getBlockState(pos).getBlock(), 1, world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))));
                    String blockAdded = I18n.translateToLocal("assistedprogression.text.exchanger.blockSet") + " " + getExchangingStack(stack).getDisplayName();
                    player.addChatComponentMessage(new TextComponentString(blockAdded));
                }
            } else if (getExchangingStack(stack) != null) {
                List<BlockPos> posList = BlockUtils.getBlockList(getSize(stack), facing, pos, world);
                    ItemStack compareStack = new ItemStack(world.getBlockState(pos).getBlock(), 1, world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)));
                posList.stream().filter(blockPos -> world.getTileEntity(blockPos) == null).forEach(blockPos -> {
                    ItemStack changeStack = new ItemStack(world.getBlockState(blockPos).getBlock(), 1, world.getBlockState(blockPos).getBlock().getMetaFromState(world.getBlockState(blockPos)));
                    if (compareStack.isItemEqual(changeStack)) {
                        if (world.isAirBlock(blockPos.offset(facing)) || pos.equals(blockPos))
                            if (player.capabilities.isCreativeMode || (player.inventory.clearMatchingItems(getExchangingStack(stack).getItem(), getExchangingStack(stack).getItemDamage(), 1, null) == 1))
                                world.setBlockState(blockPos, Block.getBlockFromItem(getExchangingStack(stack).getItem()).getStateFromMeta(getExchangingStack(stack).getItemDamage()));
                    }
                });
            }
        }

        if (world.isRemote)
            world.playSound(player, pos, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 0.5F, 1.0F);

        return EnumActionResult.SUCCESS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool) {
        if (getExchangingStack(stack) != null)
            list.add("Set Block: " + getExchangingStack(stack).getDisplayName());
        else
            list.add("Set Block: No Block Set!");
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    private void validateNBT(ItemStack stack) {
        // If we don't have a tag
        if(!stack.hasTagCompound() || (!stack.getTagCompound().hasKey(SIZE_NBT_TAG) || !stack.getTagCompound().hasKey(EXCHANGE_NBT_TAG))) {
            NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();

            // Set initial Size
            tag.setInteger(SIZE_NBT_TAG, 1);

            // Set block to air
            tag.setTag(EXCHANGE_NBT_TAG, new ItemStack(Blocks.AIR).writeToNBT(new NBTTagCompound()));

            // Write to the stack
            stack.setTagCompound(tag);
        }
    }


    /*******************************************************************************************************************
     * Accessors and Mutators                                                                                          *
     *******************************************************************************************************************/

    /**
     * Gets the current size the Exchanger is set to
     *
     * @return  current set size
     */
    public int getSize(ItemStack stack) {
        validateNBT(stack);
        return stack.getTagCompound().getInteger(SIZE_NBT_TAG);
    }

    /***
     * Used to set the size of the exchange radius
     * @param stack Stack to modify
     * @param newSize New size to set
     */
    public void setSize(ItemStack stack, int newSize) {
        validateNBT(stack);
        stack.getTagCompound().setInteger(SIZE_NBT_TAG, newSize);
    }

    /**
     * Returns the stack that we are using to store the swapped block
     * @param stack The stack to read
     * @return The stack swapping
     */
    public ItemStack getExchangingStack(ItemStack stack) {
        validateNBT(stack);
        ItemStack returnStack = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag(EXCHANGE_NBT_TAG));
        return returnStack != null && returnStack.getItem() != null && Block.getBlockFromItem(returnStack.getItem()) != Blocks.AIR
                ? returnStack : null;
    }

    /**
     * Used to set the block we are exchanging
     * @param stack The in stack
     * @param toWrite The stack to store
     */
    public void setExchangeBlock(ItemStack stack, ItemStack toWrite) {
        validateNBT(stack);
        stack.getTagCompound().setTag(EXCHANGE_NBT_TAG, toWrite.writeToNBT(new NBTTagCompound()));
    }
}
