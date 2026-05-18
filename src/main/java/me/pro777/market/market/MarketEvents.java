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
                        Component.literal(
                                "Ціна продажу: $" + MarketManager.getPriceWithDemand(entry.getPrice())
                        ).withStyle(
                                ChatFormatting.GOLD,
                                ChatFormatting.ITALIC
                        )
                );
                event.getToolTip().add(
                        Component.literal(
                                "Початкова ціна продажу: $" + entry.getPrice()
                        ).withStyle(ChatFormatting.GRAY)
                );

                break;
            }
        }

        for (SellEntry buyEntry : MarketManager.BUY_ITEMS) {

            if (buyEntry.getItem() == stack.getItem()) {

                event.getToolTip().add(
                        Component.literal(
                                "Ціна купівлі: $" + MarketManager.getBuyPriceWithDemand(buyEntry.getPrice())
                        ).withStyle(
                                ChatFormatting.GOLD,
                                ChatFormatting.ITALIC
                        )
                );
                event.getToolTip().add(
                        Component.literal(
                                "Початкова ціна купівлі: $" + buyEntry.getPrice()
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