package me.pro777.market;

import me.pro777.market.command.MarketCommand;
import me.pro777.market.config.MarketConfig;

import me.pro777.market.market.MarketManager;
import me.pro777.market.market.MarketNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import me.pro777.market.market.PlayerBalanceManager;
import me.pro777.market.market.MarketStateManager;

@Mod(MarketMod.MODID)
public class MarketMod {

    public static final String MODID = "market";

    public MarketMod() {

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                MarketConfig.SPEC
        );

        MinecraftForge.EVENT_BUS.register(this);

        MarketNetwork.register();

        MarketManager.load();
        PlayerBalanceManager.load();
        MarketStateManager.load();
    }

    @SubscribeEvent
    public void onCommands(RegisterCommandsEvent event) {
        MarketCommand.register(event.getDispatcher());
    }
}