package com.buuz135.industrial.recipe;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class LaserDrillFluidRecipe extends SerializableRecipe {

    public static GenericSerializer<LaserDrillFluidRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "laser_drill_fluid"), LaserDrillFluidRecipe.class);
    public static List<LaserDrillFluidRecipe> RECIPES = new ArrayList<>();

    public static ResourceLocation EMPTY = new ResourceLocation("minecraft", "empty");

    public static void init() {
        new LaserDrillFluidRecipe(new FluidStack(Fluids.LAVA, 100),  1, EMPTY, new LaserDrillRarity(LaserDrillRarity.NETHER, new RegistryKey[0], 5, 20, 8));
        new LaserDrillFluidRecipe(new FluidStack(ModuleCore.ETHER.getSourceFluid(), 10),  10, new ResourceLocation("minecraft", "wither"), new LaserDrillRarity(new RegistryKey[0], new RegistryKey[0], 0, 256, 8));
        new LaserDrillFluidRecipe("oil", createNBT("pneumaticcraft:oil", 50),  15, EMPTY, new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.OIL, 20, 60, 8)).setModIdCondition("pneumaticcraft");
    }

    public CompoundNBT output;
    public LaserDrillRarity[] rarity;
    public int pointer = 0;
    public Ingredient catalyst;
    public ResourceLocation entity;
    private String modIdCondition;

    public LaserDrillFluidRecipe(String name, CompoundNBT output, Ingredient catalyst, ResourceLocation entity, LaserDrillRarity... rarity) {
        super(new ResourceLocation(Reference.MOD_ID, name));
        this.output = output;
        this.rarity = rarity;
        this.catalyst = catalyst;
        this.entity = entity;
        System.out.println(this.output);
        RECIPES.add(this);
    }

    public LaserDrillFluidRecipe(String name, CompoundNBT output, int color, ResourceLocation entity, LaserDrillRarity... rarity) {
        this(name, output, Ingredient.fromItems(ModuleCore.LASER_LENS[color]),entity,  rarity);
    }

    public LaserDrillFluidRecipe(FluidStack output, int color, ResourceLocation entity, LaserDrillRarity... rarity) {
        this(output.getFluid().getRegistryName().getPath(), output.writeToNBT(new CompoundNBT()), color,entity,  rarity);
    }

    public LaserDrillFluidRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public GenericSerializer<? extends SerializableRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return SERIALIZER.getRecipeType();
    }

    @Override
    public Pair<ICondition, IConditionSerializer> getOutputCondition() {
        if (modIdCondition != null){
            return Pair.of(new ModLoadedCondition(modIdCondition), ModLoadedCondition.Serializer.INSTANCE);
        }
        return null;
    }

    public void setModIdCondition(String modIdCondition) {
        this.modIdCondition = modIdCondition;
    }

    public static CompoundNBT createNBT(String name, int amount)
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("FluidName", name);
        nbt.putInt("Amount", amount);
        return nbt;
    }

}
