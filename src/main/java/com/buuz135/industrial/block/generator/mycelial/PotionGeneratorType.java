/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.block.generator.mycelial;

import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PotionGeneratorType implements IMycelialGeneratorType {

    @Override
    public String getName() {
        return "potion";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList((stack, slot) -> stack.getItem() instanceof PotionItem);
    }

    @Override
    public List<Predicate<FluidStack>> getTankInputPredicates() {
        return new ArrayList<>();
    }

    @Override
    public boolean canStart(INBTSerializable<CompoundTag>[] inputs) {
        return inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0 && getSlotInputPredicates().get(0).test(((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0), 0);
    }

    @Override
    public Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundTag>[] inputs) {
        if (inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0) {
            ItemStack calculate = ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).copy();
            ItemStack stack = ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0);
            if (stack.getCount() == 1) {
                ((SidedInventoryComponent<?>) inputs[0]).setStackInSlot(0, new ItemStack(Items.GLASS_BOTTLE));
            } else {
                stack.shrink(1);
            }
            return calculate(calculate);
        }
        return Pair.of(0, 80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.PURPLE};
    }

    @Override
    public Item getDisplay() {
        return Items.POTION;
    }

    @Override
    public int getSlotSize() {
        return 1;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(Registries.POTION).stream().filter(potion -> potion != Potions.WATER).map(effect -> Arrays.asList(
                        PotionContents.createItemStack(Items.POTION, Holder.direct(effect)),
                        PotionContents.createItemStack(Items.SPLASH_POTION, Holder.direct(effect)),
                        PotionContents.createItemStack(Items.LINGERING_POTION, Holder.direct(effect))
                ))
                .flatMap(Collection::stream)
                .map(stack -> new MycelialGeneratorRecipe(Collections.singletonList(Collections.singletonList(Ingredient.of(stack))), new ArrayList<>(), calculate(stack).getLeft(), calculate(stack).getRight())).collect(Collectors.toList());
    }

    private Pair<Integer, Integer> calculate(ItemStack stack) {
        PotionContents potion = stack.get(DataComponents.POTION_CONTENTS);
        int duration = 80;
        int amplifier = 1;
        for (MobEffectInstance potionEffect : potion.getAllEffects()) {
            duration += potionEffect.getDuration();
            amplifier += potionEffect.getAmplifier();
        }
        double powValue = 2;
        if (stack.getItem() instanceof ThrowablePotionItem) powValue = 2.5;
        if (stack.getItem() instanceof LingeringPotionItem) powValue = 3;
        return Pair.of(duration, (int) (Math.pow(amplifier, powValue) * 10));
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.define('B', Items.NETHER_WART)
                .define('C', Blocks.BREWING_STAND)
                .define('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
        return recipeBuilder;
    }
}
