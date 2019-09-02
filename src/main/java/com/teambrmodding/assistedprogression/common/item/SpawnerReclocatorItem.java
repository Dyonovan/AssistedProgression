package com.teambrmodding.assistedprogression.common.item;

import com.teambrmodding.assistedprogression.managers.ItemManager;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since 9/2/2019
 */
public class SpawnerReclocatorItem extends Item {

    /**
     * @param name The item registry name
     */
    public SpawnerReclocatorItem(String name) {
        super(new Properties()
                .maxStackSize(1)
                .group(ItemManager.itemGroupAssistedProgression));
        setRegistryName(name);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 7200;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity livingEntity, int timeLeft) {

        if (timeLeft <= 7180 && livingEntity instanceof PlayerEntity) {
            BlockRayTraceResult movingObjectPosition = (BlockRayTraceResult) rayTrace(world, (PlayerEntity) livingEntity, RayTraceContext.FluidMode.NONE);

            if(movingObjectPosition.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos pos = new BlockPos(movingObjectPosition.getPos());
                if (!stack.hasTag()) {
                    TileEntity tile = world.getTileEntity(pos);

                    if (tile instanceof MobSpawnerTileEntity) {
                        CompoundNBT tag = new CompoundNBT();
                        ((MobSpawnerTileEntity) tile).getSpawnerBaseLogic().write(tag);
                        stack.setTag(tag);
                        world.removeBlock(pos, false);
                    }
                } else {
                    BlockPos newPosition = pos.offset(movingObjectPosition.getFace());
                    if(!world.isAirBlock(newPosition)) return;

                    CompoundNBT tag = stack.getTag();
                    tag.putInt("x", newPosition.getX());
                    tag.putInt("y", newPosition.getY());
                    tag.putInt("z", newPosition.getZ());
                    world.setBlockState(newPosition, Blocks.SPAWNER.getDefaultState());
                    MobSpawnerTileEntity spawnerTile = (MobSpawnerTileEntity) world.getTileEntity(newPosition);
                    spawnerTile.read(tag);
                    stack.setTag(null);
                }
            }
        }
    }
}
