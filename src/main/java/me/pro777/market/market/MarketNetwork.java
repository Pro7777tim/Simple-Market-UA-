package me.pro777.market.market;

import me.pro777.market.MarketMod;
import me.pro777.market.network.SyncDemandPacket;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class MarketNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(

                    new ResourceLocation(
                            MarketMod.MODID,
                            "main"
                    ),

                    () -> PROTOCOL,

                    PROTOCOL::equals,

                    PROTOCOL::equals
            );

    public static void register() {

        int id = 0;

        CHANNEL.registerMessage(
                id++,
                SyncDemandPacket.class,
                SyncDemandPacket::encode,
                SyncDemandPacket::decode,
                SyncDemandPacket::handle
        );
    }
}