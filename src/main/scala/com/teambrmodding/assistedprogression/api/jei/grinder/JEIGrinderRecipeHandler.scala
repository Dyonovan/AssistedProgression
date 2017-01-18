package com.teambrmodding.assistedprogression.api.jei.grinder

import com.teambrmodding.assistedprogression.lib.Reference
import mezz.jei.api.recipe.{IRecipeHandler, IRecipeWrapper}

/**
  * Created by Dyonovan on 1/16/2016.
  */
class JEIGrinderRecipeHandler extends IRecipeHandler[JEIGrinderRecipe] {

    override def getRecipeCategoryUid: String = Reference.MOD_ID + ":grinder"

    override def getRecipeWrapper(recipe: JEIGrinderRecipe): IRecipeWrapper = recipe

    override def isRecipeValid(recipe: JEIGrinderRecipe): Boolean = true

    override def getRecipeClass: Class[JEIGrinderRecipe] = classOf[JEIGrinderRecipe]

    override def getRecipeCategoryUid(recipe: JEIGrinderRecipe): String = "assistedprogression.grinder"
}
