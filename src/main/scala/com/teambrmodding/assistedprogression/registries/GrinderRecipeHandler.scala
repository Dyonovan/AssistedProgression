package com.teambrmodding.assistedprogression.registries

import java.util

import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import com.teambrmodding.assistedprogression.AssistedProgression
import com.teambrmodding.assistedprogression.managers.RecipeManager
import net.minecraft.command.{CommandBase, ICommandSender, WrongUsageException}
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary

/**
  * This file was created for Bookshelf API
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/21/2016
  */
class GrinderRecipeHandler extends AbstractRecipeHandler[GrinderRecipes, ItemStack, (ItemStack, ItemStack, Int)] {
    /**
      * Used to get the base name of the files
      *
      * @return
      */
    override def getBaseName: String = "grinder"

    /**
      * Used to get the default folder location
      *
      * @return
      */
    override def getBaseFolderLocation: String = AssistedProgression.configFolderLocation

    /**
      * This is the current version of the registry, if you update this it will cause the registry to be redone
      *
      * @return
      */
    override def getVersion: Int = 5

    /**
      * Used to get what type token to read from file (Generics don't handle well)
      *
      * @return
      */
    override def getTypeToken: TypeToken[util.ArrayList[GrinderRecipes]] = new TypeToken[util.ArrayList[GrinderRecipes]]() {}

    /**
      * Called when the file is not found, add all default recipes here
      */
    override def generateDefaultRecipes(): Unit = {
        LogHelper.info("Json not found. Creating Dynamic Grinder Recipe List...")

        addGrinderRecipe("oreRedstone",
            getItemStackString(new ItemStack(Items.REDSTONE)), 12, getItemStackString(new ItemStack(Items.REDSTONE)), 20)
        addGrinderRecipe("oreLapis",
            getItemStackString(new ItemStack(Items.DYE, 1, 4)), 8, getItemStackString(new ItemStack(Items.DYE, 1, 4)), 20)
        addGrinderRecipe(getOreDict(new ItemStack(Items.BLAZE_ROD)),
            getItemStackString(new ItemStack(Items.BLAZE_POWDER)), 4, getItemStackString(new ItemStack(Items.BLAZE_POWDER)), 15)
        addGrinderRecipe("cobblestone",
            getItemStackString(new ItemStack(Blocks.SAND)), 1, getItemStackString(new ItemStack(Blocks.GRAVEL)), 10)
        addGrinderRecipe(getOreDict(new ItemStack(Items.BONE)),
            getItemStackString(new ItemStack(Items.DYE, 1, 15)), 8, getItemStackString(new ItemStack(Items.DYE, 1, 15)), 10)
        addGrinderRecipe("oreQuartz",
            getItemStackString(new ItemStack(Items.QUARTZ)), 3, getItemStackString(new ItemStack(Items.QUARTZ)), 50)
        addGrinderRecipe(getOreDict(new ItemStack(Blocks.CLAY)),
            getItemStackString(new ItemStack(Items.CLAY_BALL)), 4, "", 0)
        addGrinderRecipe("oreDiamond",
            getItemStackString(new ItemStack(Items.DIAMOND)), 2, getItemStackString(new ItemStack(Items.DIAMOND)), 5)
        addGrinderRecipe("oreEmerald",
            getItemStackString(new ItemStack(Items.EMERALD)), 2, getItemStackString(new ItemStack(Items.EMERALD)), 5)
        addGrinderRecipe("glowstone",
            getItemStackString(new ItemStack(Items.GLOWSTONE_DUST)), 4, getItemStackString(new ItemStack(Items.GLOWSTONE_DUST)), 5)
        addGrinderRecipe("oreCoal",
            getItemStackString(new ItemStack(Items.COAL, 1, 0)), 3, getItemStackString(new ItemStack(Items.DIAMOND, 1, 0)), 1)
        addGrinderRecipe("minecraft:wool:" + OreDictionary.WILDCARD_VALUE ,
            getItemStackString(new ItemStack(Items.STRING)), 4, "", 0)
        addGrinderRecipe("blockGlass",
            getItemStackString(new ItemStack(Blocks.SAND)), 1, "", 0)
        addGrinderRecipe(getOreDict(new ItemStack(Blocks.GRAVEL)),
            getItemStackString(new ItemStack(Items.FLINT)), 2, getItemStackString(new ItemStack(Items.FLINT)), 10)

        val oreDict = OreDictionary.getOreNames

        for (i <- oreDict) {
            if (i.startsWith("dust")) {
                val oreList = OreDictionary.getOres(i.replaceFirst("dust", "ore"))
                if (!oreList.isEmpty) {
                    val itemList = OreDictionary.getOres(i)
                    if (itemList.size() > 0 && !doesExist(i.replaceFirst("dust", "ore")))
                        addGrinderRecipe(i.replaceFirst("dust", "ore"),
                            getItemStackString(new ItemStack(itemList.get(0).getItem, 1,
                                itemList.get(0).getItemDamage)), 2, "", 0)
                }
            } else if (i.startsWith("ingot")) {
                val oreList = OreDictionary.getOres(i.replaceFirst("ingot", "dust"))
                if (!oreList.isEmpty && !doesExist(i.replaceFirst("ingot", "dust"))) {
                    val itemList = OreDictionary.getOres(i.replaceFirst("ingot", "dust"))
                    if (itemList.size() > 0) {
                        addGrinderRecipe(i, getItemStackString(
                            new ItemStack(itemList.get(0).getItem, 1, itemList.get(0).getItemDamage)), 1, "", 0)
                    }
                }
            }
        }

        // Add Dyes
        addGrinderRecipe(getItemStackString(new ItemStack(Blocks.YELLOW_FLOWER, 1)), getItemStackString(new ItemStack(Items.DYE, 1, 11)), 3, "", 0)

        saveToFile()
        LogHelper.info("Finished adding " + recipes.size() + " Crusher Recipes")
    }

    def addGrinderRecipe(s1: String, s2: String, i1: Int, s3: String, i2: Int) : Unit = {
        addRecipe(new GrinderRecipes(s1, s2, i1, s3, i2))
    }

    /**
      * Get the oreDict tag for an item
      *
      * @param itemstack The stack to try
      * @return The string for this stack or OreDict name
      */
    private def getOreDict(itemstack: ItemStack): String = {
        val registered: Array[Int] = OreDictionary.getOreIDs(itemstack)
        if (registered.length > 0)
            OreDictionary.getOreName(registered(0))
        else {
            getItemStackString(itemstack)
        }
    }

    def doesExist(stack: String): Boolean = {
        for (i <- recipes.toArray()) {
            val recipe = i.asInstanceOf[GrinderRecipes]
            if (stack.equalsIgnoreCase(recipe.input)) return true
        }
        false
    }

    /**
      * Get the command to add values to the registry
      *
      * @return A new command
      */
    override def getCommand: CommandBase = {
        new CommandBase {
            override def getCommandName: String = "addGrinderRecipe"

            override def getRequiredPermissionLevel : Int = 3

            override def getCommandUsage(sender: ICommandSender): String = "commands.addGrinderRecipe.usage"

            override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit = {
                if(args.length < 3 || args.length > 5)
                    throw new WrongUsageException("commands.addGrinderRecipe.usage")
                else {
                    if (args.length == 3)
                        addGrinderRecipe(args(0), args(1), args(2).toInt, "", 0)
                    else if (args.length == 5)
                        addGrinderRecipe(args(0), args(1), args(2).toInt, args(3), args(4).toInt)
                    else throw new WrongUsageException("commands.addGrinderRecipe.usage")
                }
                saveToFile()
            }
        }
    }
}

class GrinderRecipes(val input: String, val output: String, val qty: Int, val outputSecondary: String, val percentChance: Int)
        extends AbstractRecipe[ItemStack, (ItemStack, ItemStack, Int)] {

    /**
      * Used to get the output of this recipe
      *
      * @param input The input object
      * @return The output object
      */
    override def getOutput(input: ItemStack): Option[(ItemStack, ItemStack, Int)] = {
        if (input != null && input.getItem != null) {
            val crusherRecipes = RecipeManager.getHandler[GrinderRecipeHandler](RecipeManager.Grinder).recipes.toArray()
            for (recipe <- crusherRecipes) {
                val i = recipe.asInstanceOf[GrinderRecipes]
                val name = i.input.split(":")
                val stackOut = getItemStackFromString(i.output)
                val stackExtra = getItemStackFromString(i.outputSecondary)
                name.length match {
                    case 3 =>
                        val stackIn = getItemStackFromString(i.input)
                        if (stackIn != null && input.isItemEqual(stackIn)) {
                            return Some((new ItemStack(stackOut.getItem, i.qty, stackOut.getItemDamage), stackExtra, i.percentChance))
                        } else if (stackIn != null && stackIn.getItemDamage == OreDictionary.WILDCARD_VALUE) {
                            if (input.getItem == stackIn.getItem)
                                return Some((new ItemStack(stackOut.getItem, i.qty, stackOut.getItemDamage), stackExtra, i.percentChance))
                        }
                    case 1 =>
                        if (checkOreDict(i.input, input))
                            return Some((new ItemStack(stackOut.getItem, i.qty, stackOut.getItemDamage), stackExtra, i.percentChance))
                }
            }
        }
        None
    }

    /**
      * Is the input valid for an output
      *
      * @param input The input object
      * @return True if there is an output
      */
    override def isValidInput(input: ItemStack): Boolean = getOutput(input).isDefined

    /**
      * Get the oreDict tag for an item
      *
      * @param itemStack The stack to find
      * @return The string for this stack or OreDict name
      */
    def checkOreDict(oreDict: String, itemStack: ItemStack): Boolean = {
        val oreList = OreDictionary.getOres(oreDict).toArray()
        for (j <- oreList) {
            val i = j.asInstanceOf[ItemStack]
            if (i.getItemDamage == OreDictionary.WILDCARD_VALUE) {
                if (i.getItem == itemStack.getItem)
                    return true
            } else if (i.isItemEqual(itemStack)) {
                return true
            }
        }
        false
    }

    override def getItemStackFromString(item: String): ItemStack = {
        if(item == null || item == "")
            return null
        val name: Array[String] = item.split(":")
        name.length match {
            case 3 =>
                new ItemStack(GameRegistry.findItem(name(0), name(1)), 1, Integer.valueOf(name(2)))
            case 1 =>
                val itemList = OreDictionary.getOres(name(0), false)
                if (!itemList.isEmpty)
                    itemList.get(0)
                else null
            case _ => null
        }
    }
}
