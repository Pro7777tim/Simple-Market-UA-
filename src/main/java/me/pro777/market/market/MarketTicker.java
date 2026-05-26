package me.pro777.market.market;

import me.pro777.market.config.MarketConfig;
import me.pro777.market.network.SyncDemandPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

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

        if (ticks < /*1200*/ 120)
            return;

        ticks = 0;

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

        MarketManager.SOLD_THIS_MINUTE = 0;

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

        event.getServer().getPlayerList().getPlayers().forEach(player -> {

            if (
                    player.containerMenu instanceof net.minecraft.world.inventory.ChestMenu menu
            ) {

                MarketManager.updateMarketInfo(
                        menu,
                        player
                );
            }
        });

        MarketNetwork.CHANNEL.send(

                PacketDistributor.ALL.noArg(),

                new SyncDemandPacket(
                        sellDemand,
                        buyDemand
                )
        );

        MarketManager.BOUGHT_THIS_MINUTE = 0;
    }
}