package me.pro777.market.market;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MarketStateManager {

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static final File FILE =
            new File("config/market-state.json");

    public static int SELL_DEMAND = 0;

    public static int BUY_DEMAND = 0;

    public static void load() {

        try {

            if (!FILE.exists()) {

                save();

                return;
            }

            FileReader reader =
                    new FileReader(FILE);

            MarketState state =
                    GSON.fromJson(
                            reader,
                            MarketState.class
                    );

            reader.close();

            if (state != null) {

                SELL_DEMAND = state.sellDemand;

                BUY_DEMAND = state.buyDemand;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {

        try {

            FileWriter writer =
                    new FileWriter(FILE);

            GSON.toJson(

                    new MarketState(
                            SELL_DEMAND,
                            BUY_DEMAND
                    ),

                    writer
            );

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MarketState {

        int sellDemand;

        int buyDemand;

        public MarketState(
                int sellDemand,
                int buyDemand
        ) {

            this.sellDemand = sellDemand;

            this.buyDemand = buyDemand;
        }
    }
}