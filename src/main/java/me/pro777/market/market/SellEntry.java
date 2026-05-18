package me.pro777.market.market;

import net.minecraft.world.item.Item;

public class SellEntry {

    private final Item item;
    private final int price;
    private final boolean sellByOne;
    private final String nbt;

    public SellEntry(Item item, int price, boolean sellByOne, String nbt) {
        this.item = item;
        this.price = price;
        this.sellByOne = sellByOne;
        this.nbt = nbt;
    }

    public Item getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public boolean isSellByOne() {
        return sellByOne;
    }

    public String getNbt() {
        return nbt;
    }
}