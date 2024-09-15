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

package com.buuz135.industrial.block;

import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;

import javax.annotation.Nullable;

public class MachineFrameBlock extends BasicBlock {

    private Rarity rarity;

    public MachineFrameBlock(Rarity rarity, TitaniumTab group) {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK));
        this.setItemGroup(group);
        this.rarity = rarity;
    }

    @Override
    public LootTable.Builder getLootTable(BasicBlockLootTables blockLootTables) {
        return blockLootTables.droppingSelf(this);
    }

    public static class MachineFrameItem extends BlockItem {

        private TitaniumTab group;

        public MachineFrameItem(Block blockIn, Rarity rarity, TitaniumTab group) {
            super(blockIn, new Item.Properties().rarity(rarity));
            this.group = group;
        }

        @Nullable
        @Override
        public String getCreatorModId(ItemStack itemStack) {
            return Component.translatable("itemGroup.industrialforegoing_" + this.group.getResourceLocation().getPath()).getString();
        }

    }
}
