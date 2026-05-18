package me.pro777.market.market;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MarketMenu {

    public static ChestMenu create(int id, Inventory inv, int page) {

        SimpleContainer container = MarketManager.createMenuContainer(page, (ServerPlayer) inv.player);

        final int[] currentPage = {page};

        return new ChestMenu(
                MenuType.GENERIC_9x6,
                id,
                inv,
                container,
                6
        ) {

            @Override
            public void clicked(int slotId, int dragType, ClickType clickType, net.minecraft.world.entity.player.Player player) {

                if (slotId >= 0 && slotId < 54) {

                    if (slotId == 45 && currentPage[0] > 0) {

                        currentPage[0]--;

                        SimpleContainer newContainer =
                                MarketManager.createMenuContainer(currentPage[0], (ServerPlayer) inv.player);

                        for (int i = 0; i < 54; i++) {

                            this.slots.get(i).setByPlayer(
                                    newContainer.getItem(i)
                            );
                        }

                        return;
                    }

                    if (slotId == 53) {

                        int maxPage = (MarketManager.SELL_ITEMS.size() - 1)
                                / MarketManager.ITEMS_PER_PAGE;

                        if (currentPage[0] < maxPage) {

                            currentPage[0]++;

                            SimpleContainer newContainer =
                                    MarketManager.createMenuContainer(
                                            currentPage[0],
                                            (ServerPlayer) inv.player
                                    );

                            for (int i = 0; i < 54; i++) {

                                this.slots.get(i).setByPlayer(
                                        newContainer.getItem(i)
                                );
                            }
                        }

                        return;
                    }

                    if (slotId < MarketManager.ITEMS_PER_PAGE) {

                        int realIndex =
                                currentPage[0] * MarketManager.ITEMS_PER_PAGE + slotId;
                        if (realIndex >= MarketManager.SELL_ITEMS.size())
                            return;

                        SellEntry entry =
                                MarketManager.SELL_ITEMS.get(realIndex);

                        if (player instanceof ServerPlayer serverPlayer) {

                            MarketManager.sell(serverPlayer, entry);

                            this.slots.get(slotId).setByPlayer(
                                    MarketManager.createDisplayStack(entry)
                            );

                            ItemStack moneyStack =
                                    new ItemStack(Items.GOLD_INGOT);

                            moneyStack.getOrCreateTag().putBoolean(
                                    "technical",
                                    true
                            );

                            moneyStack.setHoverName(
                                    Component.literal(
                                            "Ваший поточний баланс: $" + PlayerBalanceManager.getMoney(serverPlayer)
                                    ).withStyle(
                                            ChatFormatting.GOLD
                                    )
                            );

                            this.slots.get(49).setByPlayer(
                                    moneyStack
                            );
                        }
                    }

                    return;
                }

                this.setCarried(ItemStack.EMPTY);
            }

            @Override
            public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
                return ItemStack.EMPTY;
            }
        };
    }
}