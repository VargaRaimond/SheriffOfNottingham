package com.tema1.strategies;

import java.util.ArrayList;
import java.util.List;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsType;
import  com.tema1.main.Rounds;

public final class GreedyStrategy extends BaseStrategy {

    public GreedyStrategy(final int id) {
        super(id);
    }

    public List<Integer> startControl(final List<Player> players, final List<Integer> assets) {
        List<Integer> confiscatedGoods = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if (i != this.playerID) {
                players.get(i).drawCards(assets);
                List<Integer> itemsToCheck = players.get(i).chooseCards();
                // control players only if they don't have bribe money
                if (players.get(i).bribeMoney == 0) {
                    if (this.getMoney() >= Constants.MIN_AMOUNT_SHERIFF) {
                        confiscatedGoods.addAll(this.sherrifControl(players.get(i), itemsToCheck));
                    }
                } else {
                    this.addMoney(players.get(i).bribeMoney);
                    players.get(i).decMoney(players.get(i).bribeMoney);
                    players.get(i).bribeMoney = 0;
                    players.get(i).addAllToStall(itemsToCheck);
                }
            }
        }
        return confiscatedGoods;
    }

    public List<Integer> chooseCards() {
        List<Integer> result = super.chooseCards();
        // add an illegal item if it's an even round
        if (Rounds.getRoundNr() % 2 == 0) {
            int maxIllegal = bag.getItem(0);
            int maxProfit = allGoods.getGoodsById(bag.getItem(0)).getProfit();
            for (int i = 1; i < bag.getAssets().size(); i++) {
                if (allGoods.getGoodsById(bag.getItem(i)).getType() == GoodsType.Illegal) {
                    if (allGoods.getGoodsById(bag.getItem(i)).getProfit() > maxProfit) {
                        maxIllegal = bag.getItem(i);
                        maxProfit = allGoods.getGoodsById(bag.getItem(i)).getProfit();
                    }
                }
            }
            if (this.getMoney() >= Constants.ILLEGAL_PENALTY) {
                if (allGoods.getGoodsById(maxIllegal).getType() == GoodsType.Illegal) {
                    result.add(maxIllegal);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return this.playerID + " GREEDY " + this.getMoney();
    }
}
