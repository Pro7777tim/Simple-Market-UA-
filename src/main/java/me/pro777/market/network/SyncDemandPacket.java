package me.pro777.market.network;

import me.pro777.market.market.ClientMarketState;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDemandPacket {

    private final int sellDemand;

    private final int buyDemand;

    public SyncDemandPacket(
            int sellDemand,
            int buyDemand
    ) {

        this.sellDemand = sellDemand;
        this.buyDemand = buyDemand;
    }

    public static void encode(
            SyncDemandPacket msg,
            FriendlyByteBuf buf
    ) {

        buf.writeInt(msg.sellDemand);
        buf.writeInt(msg.buyDemand);
    }

    public static SyncDemandPacket decode(
            FriendlyByteBuf buf
    ) {

        return new SyncDemandPacket(
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(
            SyncDemandPacket msg,
            Supplier<NetworkEvent.Context> ctx
    ) {

        ctx.get().enqueueWork(() -> {

            ClientMarketState.SELL_DEMAND =
                    msg.sellDemand;

            ClientMarketState.BUY_DEMAND =
                    msg.buyDemand;
        });

        ctx.get().setPacketHandled(true);
    }
}