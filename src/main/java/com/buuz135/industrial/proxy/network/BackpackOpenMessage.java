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

package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.item.infinity.item.ItemInfinityBackpack;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.instance.InventoryStackLocatorInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class BackpackOpenMessage extends Message {

    private boolean forceDisable;

    public BackpackOpenMessage(boolean forceDisable) {
        this.forceDisable = forceDisable;
    }

    public BackpackOpenMessage() {
    }

    @Override
    protected void handleMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player entity = context.player();
            ItemInfinityBackpack.findFirstBackpack(entity).ifPresent(target -> {
                ItemStack stack = target.getFinder().getStackGetter().apply(entity, target.getSlot());
                if (stack.getItem() instanceof ItemInfinityBackpack && entity instanceof ServerPlayer serverPlayer) {
                    if (!stack.has(IFAttachments.INFINITY_BACKPACK_ID)) {
                        UUID id = UUID.randomUUID();
                        BackpackDataManager.getData(entity.level()).createBackPack(id);
                        stack.set(IFAttachments.INFINITY_BACKPACK_ID, id.toString());
                    }
                    String id = stack.get(IFAttachments.INFINITY_BACKPACK_ID);
                    if (forceDisable) {
                        ItemInfinityBackpack.setPickUpMode(stack, 3);
                        entity.displayClientMessage(Component.translatable("tooltip.industrialforegoing.backpack.pickup_disabled").withStyle(ChatFormatting.RED), true);
                    } else if (entity.isShiftKeyDown()) {
                        int mode = (ItemInfinityBackpack.getPickUpMode(stack) + 1) % 4;
                        ItemInfinityBackpack.setPickUpMode(stack, mode);
                        switch (mode) {
                            case 0:
                                entity.displayClientMessage(Component.translatable("tooltip.industrialforegoing.backpack.pickup_all").withStyle(ChatFormatting.GREEN), true);
                                return;
                            case 1:
                                entity.displayClientMessage(Component.translatable("tooltip.industrialforegoing.backpack.item_pickup_enabled").withStyle(ChatFormatting.GREEN), true);
                                return;
                            case 2:
                                entity.displayClientMessage(Component.translatable("tooltip.industrialforegoing.backpack.xp_pickup_enabled").withStyle(ChatFormatting.GREEN), true);
                                return;
                            default:
                                entity.displayClientMessage(Component.translatable("tooltip.industrialforegoing.backpack.pickup_disabled").withStyle(ChatFormatting.RED), true);
                        }
                    } else {
                        ItemInfinityBackpack.sync(entity.level(), id, serverPlayer);
                        IndustrialForegoing.NETWORK.sendTo(new BackpackOpenedMessage(target.getSlot(), target.getName()), serverPlayer);
                        serverPlayer.openMenu(((ItemInfinityBackpack) ModuleTool.INFINITY_BACKPACK.get()), buffer ->
                                LocatorFactory.writePacketBuffer(buffer, new InventoryStackLocatorInstance(target.getName(), target.getSlot())));
                        return;
                    }
                }
            });
        });
    }
}
