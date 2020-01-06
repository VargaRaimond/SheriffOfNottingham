package com.tema1.main;

import com.tema1.common.PlayersComparator;
import com.tema1.goods.Goods;
import com.tema1.goods.IllegalGoods;
import com.tema1.goods.LegalGoods;
import com.tema1.goods.GoodsType;
import com.tema1.goods.GoodsFactory;
import com.tema1.strategies.GreedyStrategy;
import com.tema1.strategies.BaseStrategy;
import com.tema1.strategies.BribeStrategy;
import com.tema1.strategies.Player;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public final class Rounds {
    private GameInput game;
    private List<Player> players = new ArrayList<>();
    private List<Integer> allAssets;
    private static int roundNr;

    Rounds(final GameInput game) {
        int id = 0;
        this.game = game;
        for (String aux : game.getPlayerNames()) {
            if (aux.equals("bribed")) {
                players.add(new BribeStrategy(id));
            } else {
                if (aux.equals("greedy")) {
                    players.add(new GreedyStrategy(id));
                } else {
                    players.add(new BaseStrategy(id));
            }
            }
            id++;
        }
        allAssets = new ArrayList<>(game.getAssetIds());
    }

    void computeRounds() {
        for (int i = 0; i < game.getRounds(); i++) {
            roundNr = i + 1;
            computeSubRounds();
        }
        illegalBonus();
        //add normal profit
        for (int i = 0; i < players.size(); i++) {
            players.get(i).addProfit();
        }
        kingAndQueenBonus();
        sortAndPrintPlayers();
    }

    private void computeSubRounds() {
        List<Integer> confiscatedGoods = new ArrayList<>();
        // every player gets to be sheriff every round
        for (int j = 0; j < game.getPlayerNames().size(); j++) {
            confiscatedGoods.addAll(players.get(j).startControl(players, allAssets));
        }
        allAssets.addAll(confiscatedGoods);
        confiscatedGoods.clear();
    }

    public static int getRoundNr() {
        return roundNr;
    }

    private void illegalBonus() {
        Map<Goods, Integer> copy = new HashMap<>();
        Map<Goods, Integer> helper;
        for (int i = 0; i < players.size(); i++) {
            copy.clear();
            copy.putAll(players.get(i).getStall());
            for (Map.Entry<Goods, Integer> it : copy.entrySet()) {
                if (it.getKey().getType() == GoodsType.Illegal) {
                    for (Map.Entry<Goods, Integer> bonus
                            : ((IllegalGoods) it.getKey()).getIllegalBonus().entrySet()) {
                        // add the bonus legal items to stall
                        for (int j = 0; j < it.getValue(); j++) {
                            helper = players.get(i).getStall();
                            helper.put(bonus.getKey(), helper.getOrDefault(bonus.getKey(), 0)
                                    + bonus.getValue());
                        }
                    }

                }
            }
        }
    }

    private void kingAndQueenBonus() {
        GoodsFactory instance = GoodsFactory.getInstance();
        Player king;
        Player queen;
        int maxQueen;
        int maxKing;
        // find king and queen for every item
        for (Map.Entry<Integer, Goods> it : instance.getAllGoods().entrySet()) {
            if (it.getValue().getType() == GoodsType.Legal) {
                maxKing = 0;
                maxQueen = 0;
                king = null;
                queen = null;
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getStall().get(it.getValue()) != null) {
                        if (players.get(i).getStall().get(it.getValue()) > maxKing) {
                            queen = king;
                            maxQueen = maxKing;
                            king = players.get(i);
                            maxKing = players.get(i).getStall().get(it.getValue());
                        } else {
                            if (players.get(i).getStall().get(it.getValue()) > maxQueen) {
                                queen = players.get(i);
                                maxQueen = players.get(i).getStall().get(it.getValue());
                            }
                        }
                    }
                }
                if (king != null) {
                    king.addMoney(((LegalGoods) it.getValue()).getKingBonus());
                }
                if (queen != null) {
                    queen.addMoney(((LegalGoods) it.getValue()).getQueenBonus());
                }
            }
        }
    }

    private void sortAndPrintPlayers() {
        PlayersComparator playersComparator = new PlayersComparator();
        Collections.sort(players, playersComparator);
        for (Player player : players) {
            System.out.println(player);
        }
    }
}
