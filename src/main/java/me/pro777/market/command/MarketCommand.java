package me.pro777.market.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.pro777.market.config.MarketConfig;
import me.pro777.market.market.MarketManager;
import me.pro777.market.market.MarketMenu;

import me.pro777.market.market.PlayerBalanceManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import me.pro777.market.market.MarketBuyMenu;

public class MarketCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("market")
                        .then(
                                Commands.literal("sell")
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                            MarketManager.load();

                                            player.openMenu(
                                                    new SimpleMenuProvider(
                                                            (id, inv, p) ->
                                                                    MarketMenu.create(id, inv, 0),
                                                            Component.translatable("market.sell_menu")
                                                    )
                                            );

                                            return 1;
                                        })
                        )
                        .then(
                                Commands.literal("buy")
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                            MarketManager.load();

                                            player.openMenu(
                                                    new SimpleMenuProvider(
                                                            (id, inv, p) ->
                                                                    MarketBuyMenu.create(id, inv, 0),
                                                            Component.translatable("market.buy_menu")
                                                    )
                                            );

                                            return 1;
                                        })
                        )
                        .then(
                                Commands.literal("money")
                                        .executes(ctx -> {

                                            ServerPlayer player =
                                                    ctx.getSource().getPlayerOrException();

                                            int money =
                                                    PlayerBalanceManager.getMoney(player);

                                            player.sendSystemMessage(

                                                    Component.translatable(
                                                            "market.balance",
                                                            Component.literal("$" + money)
                                                                    .withStyle(ChatFormatting.GREEN)

                                                    ).withStyle(ChatFormatting.YELLOW)
                                            );

                                            return 1;
                                        })
                        )
                        .then(
                                Commands.literal("pay")

                                        .then(
                                                Commands.argument(
                                                                "player",
                                                                EntityArgument.player()
                                                        )

                                                        .then(
                                                                Commands.argument(
                                                                                "money",
                                                                                IntegerArgumentType.integer(1)
                                                                        )

                                                                        .executes(ctx -> {

                                                                            ServerPlayer sender =
                                                                                    ctx.getSource()
                                                                                            .getPlayerOrException();

                                                                            ServerPlayer target =
                                                                                    EntityArgument.getPlayer(
                                                                                            ctx,
                                                                                            "player"
                                                                                    );

                                                                            int money =
                                                                                    IntegerArgumentType.getInteger(
                                                                                            ctx,
                                                                                            "money"
                                                                                    );

                                                                            int senderMoney =
                                                                                    PlayerBalanceManager.getMoney(
                                                                                            sender
                                                                                    );

                                                                            if (sender == target) {

                                                                                sender.sendSystemMessage(
                                                                                        Component.translatable(
                                                                                                "market.transfer_money_yourself"
                                                                                        ).withStyle(
                                                                                                ChatFormatting.RED
                                                                                        )
                                                                                );

                                                                                return 0;
                                                                            }

                                                                            if (senderMoney < money) {

                                                                                sender.sendSystemMessage(
                                                                                        Component.translatable(
                                                                                                "market.not_enough_money"
                                                                                        ).withStyle(
                                                                                                ChatFormatting.RED
                                                                                        )
                                                                                );

                                                                                return 0;
                                                                            }

                                                                            PlayerBalanceManager.removeMoney(
                                                                                    sender,
                                                                                    money
                                                                            );

                                                                            PlayerBalanceManager.addMoney(
                                                                                    target,
                                                                                    money
                                                                            );

                                                                            sender.server.getPlayerList().broadcastSystemMessage(
                                                                                    Component.translatable(
                                                                                            "market.transfer",
                                                                                            Component.literal(
                                                                                                    sender.getName().getString()
                                                                                            ).withStyle(ChatFormatting.GREEN),

                                                                                            Component.literal(
                                                                                                    "$" + money
                                                                                            ).withStyle(ChatFormatting.GREEN),

                                                                                            Component.literal(
                                                                                                    target.getName().getString()
                                                                                            ).withStyle(ChatFormatting.GREEN)
                                                                                    ),
                                                                                    false
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("reload")

                                        .requires(source ->
                                                source.hasPermission(2)
                                        )

                                        .executes(ctx -> {

                                            com.electronwill.nightconfig.core.file.CommentedFileConfig config =
                                                    com.electronwill.nightconfig.core.file.CommentedFileConfig
                                                            .builder(
                                                                    java.nio.file.Paths.get(
                                                                            "config/market-common.toml"
                                                                    )
                                                            )
                                                            .build();

                                            config.load();

                                            MarketConfig.SPEC.setConfig(config);

                                            MarketManager.load();

                                            ctx.getSource().sendSuccess(
                                                    () -> Component.translatable(
                                                            "market.reload_config"
                                                    ).withStyle(
                                                            ChatFormatting.GREEN
                                                    ),
                                                    false
                                            );

                                            return 1;
                                        })
                        )
        );
    }
}