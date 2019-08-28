package com.teambrmodding.assistedprogression.api;

import com.teambrmodding.assistedprogression.api.grinder.JEIGrinderRecipeCategory;
import com.teambrmodding.assistedprogression.client.screen.GrinderScreen;
import com.teambrmodding.assistedprogression.lib.Reference;
import com.teambrmodding.assistedprogression.managers.BlockManager;
import com.teambrmodding.assistedprogression.managers.RecipeHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 8/27/2019
 */
@JeiPlugin
public class JEIAssistedProgression implements IModPlugin {

    // JEI Helper Reference
    public static IJeiHelpers jeiHelpers;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Reference.MOD_ID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories(new JEIGrinderRecipeCategory());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration
                .addRecipes(RecipeHelper.grinderRecipes, new ResourceLocation(Reference.MOD_ID, "grinder"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockManager.grinder),
                new ResourceLocation(Reference.MOD_ID, "grinder"));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(GrinderScreen.class, 47, 36, 27, 20,
                new ResourceLocation(Reference.MOD_ID, "grinder"));
    }
}
