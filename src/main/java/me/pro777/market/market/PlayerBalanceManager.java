package me.pro777.market.market;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerBalanceManager {

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static final File FILE =
            new File("config/market-balances.json");

    private static final Map<UUID, Integer> BALANCES =
            new HashMap<>();

    public static void load() {

        try {

            if (!FILE.exists()) {
                save();
                return;
            }

            FileReader reader = new FileReader(FILE);

            Type type = new TypeToken<HashMap<UUID, Integer>>(){}.getType();

            Map<UUID, Integer> loaded =
                    GSON.fromJson(reader, type);

            reader.close();

            BALANCES.clear();

            if (loaded != null) {
                BALANCES.putAll(loaded);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {

        try {

            FileWriter writer = new FileWriter(FILE);

            GSON.toJson(BALANCES, writer);

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getMoney(ServerPlayer player) {

        return BALANCES.getOrDefault(
                player.getUUID(),
                0
        );
    }

    public static void setMoney(ServerPlayer player, int money) {

        BALANCES.put(
                player.getUUID(),
                money
        );

        save();
    }

    public static void addMoney(ServerPlayer player, int money) {

        setMoney(
                player,
                getMoney(player) + money
        );
    }

    public static boolean removeMoney(ServerPlayer player, int money) {

        int current = getMoney(player);

        if (current < money)
            return false;

        setMoney(
                player,
                current - money
        );

        return true;
    }
}