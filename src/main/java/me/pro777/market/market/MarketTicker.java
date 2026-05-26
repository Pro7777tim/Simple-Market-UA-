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

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        ticks++;
        saveTicks++;

        if (saveTicks >= 600) {

            saveTicks = 0;

            MarketStateManager.save();
        }

        if (ticks < /*1200*/ 120)
            return;

        ticks = 0;

        int increase = RANDOM.nextInt(3) + 1;

        MarketStateManager.SELL_DEMAND = Math.min(
                25,
                MarketStateManager.SELL_DEMAND + increase
        );


        int buyIncrease = RANDOM.nextInt(3) + 1;

        MarketStateManager.BUY_DEMAND = Math.max(
                -25,
                MarketStateManager.BUY_DEMAND - buyIncrease
        );

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
                        MarketStateManager.SELL_DEMAND,
                        MarketStateManager.BUY_DEMAND
                )
        );
    }
}