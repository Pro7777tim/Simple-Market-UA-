package me.pro777.market.market;

import me.pro777.market.config.MarketConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber
public class MarketTicker {

    private static int ticks = 0;

    private static int saveTicks = 0;

    private static boolean loaded = false;

    private static int sellDemand = 0;

    private static int buyDemand = 0;

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        if (!loaded) {

            sellDemand = MarketStateManager.SELL_DEMAND;
            buyDemand = MarketStateManager.BUY_DEMAND;

            loaded = true;
        }

        ticks++;
        saveTicks++;

        if (saveTicks >= 600) {

            saveTicks = 0;

            MarketStateManager.save();
        }

        if (ticks < 1200)
            return;

        ticks = 0;

        int oldSellDemand = sellDemand;

        if (MarketManager.SOLD_THIS_MINUTE <= 0) {

            int increase = RANDOM.nextInt(5) + 1;

            sellDemand = Math.min(
                    50,
                    sellDemand + increase
            );
        }

        else {

            int decrease = Math.max(
                    1,
                    Math.min(
                            500,
                            MarketManager.SOLD_THIS_MINUTE / 20
                    )
            );

            sellDemand = Math.max(
                    -50,
                    sellDemand - decrease
            );
        }

        MarketStateManager.SELL_DEMAND = sellDemand;

        if (oldSellDemand != sellDemand) {

            broadcast(
                    sellDemand > oldSellDemand,
                    oldSellDemand,
                    sellDemand,
                    event.getServer()
            );
        }

        MarketManager.SOLD_THIS_MINUTE = 0;

        int oldBuyDemand = buyDemand;

        if (MarketManager.BOUGHT_THIS_MINUTE <= 0) {

            int increase = RANDOM.nextInt(5) + 1;

            buyDemand = Math.max(
                    -50,
                    buyDemand - increase
            );
        }
        else {
            int increase = Math.max(
                    1,
                    Math.min(
                            500,
                            MarketManager.BOUGHT_THIS_MINUTE / 20
                    )
            );

            buyDemand = Math.min(
                    50,
                    buyDemand + increase
            );
        }

        MarketStateManager.BUY_DEMAND = buyDemand;

        if (oldBuyDemand != buyDemand) {

            buyBroadcast(
                    event.getServer(),
                    buyDemand > oldBuyDemand,
                    oldBuyDemand,
                    buyDemand
            );
        }

        MarketManager.BOUGHT_THIS_MINUTE = 0;
    }

    private static void broadcast(
            boolean up,
            int oldDemand,
            int newDemand,
            net.minecraft.server.MinecraftServer server
    ) {

        server.getPlayerList()
                .broadcastSystemMessage(

                        Component.literal("Продаж")
                                .withStyle(ChatFormatting.RED)

                                .append(
                                        Component.literal(" | Ціни на ринку ")
                                                .withStyle(ChatFormatting.WHITE)
                                )

                                .append(
                                        Component.literal(
                                                up ? "зросли" : "впали"
                                        ).withStyle(
                                                up
                                                        ? ChatFormatting.GREEN
                                                        : ChatFormatting.RED
                                        )
                                )

                                .append(
                                        Component.literal(" ")
                                                .withStyle(ChatFormatting.WHITE)
                                )

                                .append(
                                        Component.literal(
                                                (oldDemand + 100) + "% -> "
                                                        + (newDemand + 100) + "%"
                                        ).withStyle(ChatFormatting.YELLOW)
                                ),

                        false
                );
    }

    private static void buyBroadcast(
            net.minecraft.server.MinecraftServer server,
            boolean up,
            int oldDemand,
            int newDemand
    ) {

        server.getPlayerList()
                .broadcastSystemMessage(

                        Component.literal("Купівля")
                                .withStyle(ChatFormatting.GREEN)

                                .append(
                                        Component.literal(" | Ціни на ринку ")
                                                .withStyle(ChatFormatting.WHITE)
                                )

                                .append(
                                        Component.literal(
                                                up ? "зросли" : "впали"
                                        ).withStyle(
                                                up
                                                        ? ChatFormatting.RED
                                                        : ChatFormatting.GREEN
                                        )
                                )

                                .append(
                                        Component.literal(" ")
                                                .withStyle(ChatFormatting.WHITE)
                                )

                                .append(
                                        Component.literal(
                                                (oldDemand + 100) + "% -> "
                                                        + (newDemand + 100) + "%"
                                        ).withStyle(ChatFormatting.YELLOW)
                                ),

                        false
                );
    }
}