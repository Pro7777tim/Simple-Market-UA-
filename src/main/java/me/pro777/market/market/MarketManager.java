package me.pro777.market.market;

import me.pro777.market.config.MarketConfig;
import me.pro777.market.network.SyncDemandPacket;
import net.minecraft.server.MinecraftServer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Items;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class MarketManager {

    public static final List<SellEntry> SELL_ITEMS = new ArrayList<>();

    public static final List<SellEntry> BUY_ITEMS = new ArrayList<>();

    public static MinecraftServer SERVER;

    public static final int ITEMS_PER_PAGE = 45;

    public static int getPriceWithDemand(int basePrice) {

        int demand = MarketStateManager.SELL_DEMAND;

        return (int) Math.round(
                basePrice + (basePrice * (demand / 100.0))
        );
    }

    public static int getBuyPriceWithDemand(int basePrice) {

        int demand = MarketStateManager.BUY_DEMAND;

        return (int) Math.round(
                basePrice + (basePrice * (demand / 100.0))
        );
    }

    public static void load() {

        SELL_ITEMS.clear();
        BUY_ITEMS.clear();

        for (String line : MarketConfig.SELL_ITEMS.get()) {

            try {

                String[] split = line.split("=");

                String itemId = split[0];

                int price = Integer.parseInt(split[1]);

                boolean sellByOne = false;

                if (split.length >= 3) {
                    sellByOne = Boolean.parseBoolean(split[2]);
                }

                Item item = ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(itemId)
                );

                if (item == null) {
                    System.out.println(
                            "[Market] Unknown object: " + itemId
                    );
                }

                if (item != null) {
                    SELL_ITEMS.add(
                            new SellEntry(item, price, sellByOne, null)
                    );
                }

            } catch (Exception ignored) {}
        }

        for (String line : MarketConfig.BUY_ITEMS.get()) {

            try {

                String[] split = line.split("=", 4);

                String itemId = split[0];

                int price = Integer.parseInt(split[1]);

                boolean sellByOne = false;
                String nbt = null;

                if (split.length >= 3) {
                    sellByOne = Boolean.parseBoolean(split[2]);
                }
                if (split.length >= 4) {
                    nbt = split[3];
                }

                Item item = ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(itemId)
                );

                if (item != null) {
                    BUY_ITEMS.add(
                            new SellEntry(item, price, sellByOne, nbt)
                    );
                }

                if (item == null) {
                    System.out.println(
                            "[Market] Unknown object: " + itemId
                    );
                }

            } catch (Exception ignored) {}
        }
    }

    public static void sell(ServerPlayer player, SellEntry entry) {

        int total = 0;

        for (ItemStack stack : player.getInventory().items) {

            if (stack.getItem() == entry.getItem()) {
                total += stack.getCount();
            }
        }

        int amount;

        if (entry.isSellByOne()) {
            amount = total;
        } else {
            amount = total / 64;
        }

        if (amount <= 0)
            return;

        int remove;

        if (entry.isSellByOne()) {
            remove = amount;
        } else {
            remove = amount * 64;
        }

        for (ItemStack stack : player.getInventory().items) {

            if (stack.getItem() == entry.getItem()) {

                int take = Math.min(stack.getCount(), remove);

                stack.shrink(take);

                remove -= take;

                if (remove <= 0)
                    break;
            }
        }

        int currentPrice = getPriceWithDemand(entry.getPrice());

        int money = amount * currentPrice;
        int finalMoney = money;

        PlayerBalanceManager.addMoney(
                player,
                money
        );

        MarketStateManager.SELL_DEMAND = Math.max(
                -25,
                MarketStateManager.SELL_DEMAND
                        - Math.max(1, money / 50)
        );

        MarketNetwork.CHANNEL.send(

                PacketDistributor.ALL.noArg(),

                new SyncDemandPacket(
                        MarketStateManager.SELL_DEMAND,
                        MarketStateManager.BUY_DEMAND
                )
        );

        if (
                player.containerMenu
                        instanceof net.minecraft.world.inventory.ChestMenu menu
        ) {

            updateMarketInfo(
                    menu,
                    player
            );
        }

        player.server.getPlayerList().broadcastSystemMessage(
                Component.translatable(
                        "market.player_sell",
                        Component.literal(
                                player.getName().getString()
                        ).withStyle(ChatFormatting.GREEN),
                        Component.literal(
                                entry.getItem()
                                        .getDescription()
                                        .getString()
                        ).withStyle(ChatFormatting.YELLOW),

                        Component.literal(
                                "$" + finalMoney
                        ).withStyle(ChatFormatting.GREEN)
                ),
                false
        );
    }

    public static void buy(ServerPlayer player, SellEntry entry) {
        int price = getBuyPriceWithDemand(entry.getPrice());

        int needMoney;

        ItemStack giveItem;

        if (entry.isSellByOne()) {

            needMoney = price;

            giveItem = new ItemStack(
                    entry.getItem(),
                    1
            );
        }
        else {

            needMoney = price;

            giveItem = new ItemStack(
                    entry.getItem(),
                    64
            );
        }

        if (entry.getNbt() != null) {
            try {

                giveItem.setTag(
                        net.minecraft.nbt.TagParser.parseTag(
                                entry.getNbt()
                        )
                );

            } catch (Exception e) {

                player.sendSystemMessage(
                        Component.literal(
                                "Subject NBT error"
                        ).withStyle(ChatFormatting.RED)
                );

                return;
            }
        }

        if (!PlayerBalanceManager.removeMoney(
                player,
                needMoney
        )) {
            return;
        }

        MarketStateManager.BUY_DEMAND = Math.min(
                25,
                MarketStateManager.BUY_DEMAND
                        + Math.max(1, needMoney / 50)
        );

        MarketNetwork.CHANNEL.send(

                PacketDistributor.ALL.noArg(),

                new SyncDemandPacket(
                        MarketStateManager.SELL_DEMAND,
                        MarketStateManager.BUY_DEMAND
                )
        );

        if (
                player.containerMenu
                        instanceof net.minecraft.world.inventory.ChestMenu menu
        ) {

            updateMarketInfo(
                    menu,
                    player
            );
        }

        boolean added = player.getInventory().add(giveItem);

        if (!added) {
            player.drop(
                    giveItem,
                    false
            );
        }

        player.server.getPlayerList().broadcastSystemMessage(
                Component.translatable(

                        "market.player_buy",

                        Component.literal(
                                player.getName().getString()
                        ).withStyle(ChatFormatting.GREEN),

                        Component.literal(
                                entry.getItem()
                                        .getDescription()
                                        .getString()
                        ).withStyle(ChatFormatting.YELLOW),

                        Component.literal(
                                "$" + needMoney
                        ).withStyle(ChatFormatting.GREEN)
                ),
                false
        );
    }

    public static ItemStack createDisplayStack(SellEntry entry) {

        ItemStack stack = new ItemStack(entry.getItem());

        if (entry.getNbt() != null) {

            try {

                stack.setTag(
                        net.minecraft.nbt.TagParser.parseTag(
                                entry.getNbt()
                        )
                );

            } catch (Exception ignored) {}
        }

        stack.setHoverName(
                entry.getItem()
                        .getDescription()
                        .copy()
                        .withStyle(style ->
                                style.withBold(true)
                                        .withItalic(false)
                        )
        );

        return stack;
    }

    public static SimpleContainer createMenuContainer(int page, ServerPlayer player) {

        SimpleContainer container = new SimpleContainer(54);

        int start = page * ITEMS_PER_PAGE;

        int end = Math.min(
                start + ITEMS_PER_PAGE,
                SELL_ITEMS.size()
        );

        int slot = 0;

        for (int i = start; i < end; i++) {

            SellEntry entry = SELL_ITEMS.get(i);

            container.setItem(
                    slot,
                    createDisplayStack(entry)
            );

            slot++;
        }

        if (page > 0) {
            ItemStack arrow =
                    new ItemStack(Items.BLAZE_ROD).setHoverName(
                            Component.translatable("market.previous_page")
                                    .withStyle(ChatFormatting.YELLOW)
                    );

            arrow.getOrCreateTag().putBoolean(
                    "technical",
                    true
            );

            container.setItem(45, arrow);
        }

        if (end < SELL_ITEMS.size()) {
            ItemStack arrow =
                    new ItemStack(Items.BLAZE_ROD).setHoverName(
                            Component.translatable("market.next_page")
                                    .withStyle(ChatFormatting.YELLOW)
                    );

            arrow.getOrCreateTag().putBoolean(
                    "technical",
                    true
            );

            container.setItem(53, arrow);
        }

        ItemStack moneyStack =
                new ItemStack(Items.GOLD_INGOT);

        moneyStack.getOrCreateTag().putBoolean(
                "technical",
                true
        );

        moneyStack.setHoverName(
                Component.translatable(
                        "market.balance",
                        "$" + PlayerBalanceManager.getMoney(player)
                ).withStyle(
                        ChatFormatting.GOLD
                )
        );

        container.setItem(
                50,
                moneyStack
        );

        ItemStack sellDemand =
                new ItemStack(Items.RED_SHULKER_BOX);

        sellDemand.getOrCreateTag().putBoolean(
                "technical",
                true
        );

        sellDemand.setHoverName(
                Component.translatable(
                        "market.sell_demand",
                        String.valueOf(
                                MarketStateManager.SELL_DEMAND + 100
                        )
                ).withStyle(ChatFormatting.GOLD)
        );

        container.setItem(48, sellDemand);

        ItemStack buyDemand =
                new ItemStack(Items.LIME_SHULKER_BOX);

        buyDemand.getOrCreateTag().putBoolean(
                "technical",
                true
        );

        buyDemand.setHoverName(
                Component.translatable(
                        "market.buy_demand",
                        String.valueOf(
                                MarketStateManager.BUY_DEMAND + 100
                        )
                ).withStyle(ChatFormatting.GOLD)
        );

        container.setItem(49, buyDemand);

        return container;
    }

    public static SimpleContainer createBuyMenuContainer(int page, ServerPlayer player) {

        SimpleContainer container = new SimpleContainer(54);

        int start = page * ITEMS_PER_PAGE;

        int end = Math.min(
                start + ITEMS_PER_PAGE,
                BUY_ITEMS.size()
        );

        int slot = 0;

        for (int i = start; i < end; i++) {

            SellEntry entry = BUY_ITEMS.get(i);

            container.setItem(
                    slot,
                    createDisplayStack(entry)
            );

            slot++;
        }

        if (page > 0) {
            ItemStack arrow =
                    new ItemStack(Items.BLAZE_ROD).setHoverName(
                            Component.translatable("market.previous_page")
                                    .withStyle(ChatFormatting.YELLOW)
                    );

            arrow.getOrCreateTag().putBoolean(
                    "technical",
                    true
            );

            container.setItem(45, arrow);
        }

        if (end < BUY_ITEMS.size()) {
            ItemStack arrow =
                    new ItemStack(Items.BLAZE_ROD).setHoverName(
                            Component.translatable("market.next_page")
                                    .withStyle(ChatFormatting.YELLOW)
                    );

            arrow.getOrCreateTag().putBoolean(
                    "technical",
                    true
            );

            container.setItem(53, arrow);
        }

        ItemStack moneyStack =
                new ItemStack(Items.GOLD_INGOT);

        moneyStack.getOrCreateTag().putBoolean(
                "technical",
                true
        );

        moneyStack.setHoverName(
                Component.translatable(
                        "market.balance",
                        "$" + PlayerBalanceManager.getMoney(player)
                ).withStyle(
                        ChatFormatting.GOLD
                )
        );

        container.setItem(
                50,
                moneyStack
        );

        ItemStack sellDemand =
                new ItemStack(Items.RED_SHULKER_BOX);

        sellDemand.getOrCreateTag().putBoolean(
                "technical",
                true
        );

        sellDemand.setHoverName(
                Component.translatable(
                        "market.sell_demand",
                        String.valueOf(
                                MarketStateManager.SELL_DEMAND + 100
                        )
                ).withStyle(ChatFormatting.GOLD)
        );

        container.setItem(48, sellDemand);

        ItemStack buyDemand =
                new ItemStack(Items.LIME_SHULKER_BOX);

        buyDemand.getOrCreateTag().putBoolean(
                "technical",
                true
        );

        buyDemand.setHoverName(
                Component.translatable(
                        "market.buy_demand",
                        String.valueOf(
                                MarketStateManager.BUY_DEMAND + 100
                        )
                ).withStyle(ChatFormatting.GOLD)
        );

        container.setItem(49, buyDemand);

        return container;
    }

    public static void updateMarketInfo(
            net.minecraft.world.inventory.ChestMenu menu,
            ServerPlayer player
    ) {

        ItemStack sellDemand =
                new ItemStack(Items.RED_SHULKER_BOX);

        sellDemand.getOrCreateTag().putBoolean(
                "technical",
                true
        );

        sellDemand.setHoverName(
                Component.translatable(
                        "market.sell_demand",
                        String.valueOf(
                                MarketStateManager.SELL_DEMAND + 100
                        )
                ).withStyle(ChatFormatting.GOLD)
        );

        menu.slots.get(48).setByPlayer(
                sellDemand
        );

        ItemStack buyDemand =
                new ItemStack(Items.LIME_SHULKER_BOX);

        buyDemand.getOrCreateTag().putBoolean(
                "technical",
                true
        );

        buyDemand.setHoverName(
                Component.translatable(
                        "market.buy_demand",
                        String.valueOf(
                                MarketStateManager.BUY_DEMAND + 100
                        )
                ).withStyle(ChatFormatting.GOLD)
        );

        menu.slots.get(49).setByPlayer(
                buyDemand
        );

        ItemStack moneyStack =
                new ItemStack(Items.GOLD_INGOT);

        moneyStack.getOrCreateTag().putBoolean(
                "technical",
                true
        );

        moneyStack.setHoverName(
                Component.translatable(
                        "market.balance",
                        "$" + PlayerBalanceManager.getMoney(player)
                ).withStyle(
                        ChatFormatting.GOLD
                )
        );

        menu.slots.get(50).setByPlayer(
                moneyStack
        );
    }
}