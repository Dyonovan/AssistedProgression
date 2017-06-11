package com.teambrmodding.assistedprogression.common.blocks;

import com.teambrmodding.assistedprogression.AssistedProgression;
import com.teambrmodding.assistedprogression.lib.Reference;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since 6/2/2017
 */
public class BlockPlayerPlate extends BlockBasePressurePlate {

    protected BlockPlayerPlate() {
        super(Material.IRON);

        setUnlocalizedName(Reference.MOD_ID + ":" + "blockPlayerPlate");
        setCreativeTab(AssistedProgression.tabAssistedProgression);
        setHardness(2.0F);

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockPressurePlate.POWERED, false));
    }

    @Override
    public IBlockState setRedstoneStrength(IBlockState state, int strength) {
        return state.withProperty(BlockPressurePlate.POWERED, strength > 0);
    }

    @Override
    public int getRedstoneStrength(IBlockState state) {
        return state.getValue(BlockPressurePlate.POWERED) ? 15 : 0;
    }

    @Override
    public int computeRedstoneStrength(World world, BlockPos pos) {
        float f = 0.125F;
        List<EntityPlayer> list = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((pos.getX() + f), pos.getY(), (pos.getZ() + f), ((pos.getX() + 1) - f), pos.getY() + 0.25D, ((pos.getZ() + 1) - f)));
        return !list.isEmpty() ? 15 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BlockPressurePlate.POWERED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockPressurePlate.POWERED) ? 1 : 0;
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockPressurePlate.POWERED);
    }

    @Override
    public void playClickOnSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_ON,
                SoundCategory.BLOCKS, 0.3F, 0.6F);
    }

    @Override
    public void playClickOffSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_OFF,
                SoundCategory.BLOCKS, 0.3F, 0.5F);
    }
}
