package me.pro777.market.market;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber
public class MarketEvents {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {

        ItemStack stack = event.getItemStack();

        if (
                stack.hasTag()
                        && stack.getTag().getBoolean(
                        "technical"
                )
        ) {
            return;
        }

        for (SellEntry entry : MarketManager.SELL_ITEMS) {

            if (stack.getItem() == entry.getItem()) {

                event.getToolTip().add(
                        Component.translatable(
                                "market.sell_price",
                                String.valueOf(
                                        (int) Math.round(
                                                entry.getPrice()
                                                        + (
                                                        entry.getPrice()
                                                                * (
                                                                ClientMarketState.SELL_DEMAND
                                                                        / 100.0
                                                        )
                                                )
                                        )
                                )
                        ).withStyle(
                                ChatFormatting.GOLD,
                                ChatFormatting.ITALIC
                        )
                );
                event.getToolTip().add(
                        Component.translatable(
                                "market.base_sell_price",
                                String.valueOf(entry.getPrice())
                        ).withStyle(ChatFormatting.GRAY)
                );

                break;
            }
        }

        for (SellEntry buyEntry : MarketManager.BUY_ITEMS) {

            if (buyEntry.getItem() == stack.getItem()) {

                event.getToolTip().add(
                        Component.translatable(
                                "market.buy_price",
                                String.valueOf(
                                        (int) Math.round(
                                                buyEntry.getPrice()
                                                        + (
                                                        buyEntry.getPrice()
                                                                * (
                                                                ClientMarketState.BUY_DEMAND
                                                                        / 100.0
                                                        )
                                                )
                                        )
                                )
                        ).withStyle(
                                ChatFormatting.GOLD,
                                ChatFormatting.ITALIC
                        )
                );
                event.getToolTip().add(
                        Component.translatable(
                                "market.base_buy_price",
                                String.valueOf(buyEntry.getPrice())
                        ).withStyle(ChatFormatting.GRAY)
                );

                break;
            }
        }
    }

    @SubscribeEvent
    public void onConfigReload(ModConfigEvent.Reloading event) {
        MarketManager.load();
    }
}