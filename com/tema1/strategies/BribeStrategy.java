package com.tema1.strategies;

import com.tema1.common.Constants;
import com.tema1.common.AssetsProfitIDComparator;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BribeStrategy extends BaseStrategy {

    public BribeStrategy(final int id) {
        super(id);
    }

    public List<Integer> startControl(final List<Player> players, final List<Integer> assets) {
        List<Integer> seizedGoods = new ArrayList<>();
        List<Integer> itToCheck;
        int left = -1;
        int right = -1;
        for (int i = 0; i < players.size(); i++) {
            if (i != this.playerID) {
                players.get(i).drawCards(assets);
                // control only left and right
                if (i + 1 == playerID || (playerID == 0 && i == players.size() - 1)) {
                    left = i;
                    itToCheck = players.get(i).chooseCards();
                    if (getMoney() >= Constants.MIN_AMOUNT_SHERIFF) {
                        seizedGoods.addAll(sherrifControl(players.get(i), itToCheck));
                    } else {
                        players.get(i).addAllToStall(itToCheck);
                    }
                } else {
                    if (i - 1 == playerID  || (playerID == players.size() - 1 && i == 0)) {
                        itToCheck = players.get(i).chooseCards();
                        right = i;
                        if (getMoney() >= Constants.MIN_AMOUNT_SHERIFF) {
                            seizedGoods.addAll(sherrifControl(players.get(i), itToCheck));
                        } else {
                            players.get(i).addAllToStall(itToCheck);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < players.size(); i++) {
            if (i != this.playerID && i != left && i != right) {
                // take the bribe from players I don't control
                itToCheck = players.get(i).chooseCards();
                this.addMoney(players.get(i).bribeMoney);
                players.get(i).decMoney(players.get(i).bribeMoney);
                players.get(i).bribeMoney = 0;
                players.get(i).addAllToStall(itToCheck);
            }
        }
        return seizedGoods;
    }

    public List<Integer> chooseCards() {
        // choose illegal by profit
        List<Goods>  sortedGoods = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        // reset his unused bribe money
        bribeMoney = 0;
        if (allLegal() || this.getMoney() <= Constants.MIN_BRIBE) {
            return super.chooseCards();
        } else {
            for (Integer aux : bag.getAssets()) {
                sortedGoods.add(allGoods.getGoodsById(aux));
            }
            AssetsProfitIDComparator assetsProfitIDComparator = new AssetsProfitIDComparator();
            Collections.sort(sortedGoods, assetsProfitIDComparator);
            int maxPenalty = 0;
            int illegalItems = 0;
            for (Goods temp : sortedGoods) {
                maxPenalty += temp.getPenalty();
                if (getMoney() - maxPenalty > 0 && result.size() < Constants.MAX_ITEMS) {
                    result.add(temp.getId());
                    if (temp.getType() == GoodsType.Illegal) {
                        illegalItems++;
                    }
                } else {
                    // if he can't add anymore illegal but can still add legal
                    maxPenalty -= temp.getPenalty();
                }
            }
            bribeMoney = 0;
            if (illegalItems <= 2) {
                this.bribeMoney = Constants.MIN_BRIBE;
            } else {
                if (illegalItems > 2) {
                    this.bribeMoney = 2 * Constants.MIN_BRIBE;
                }
            }
            declaredID = 0;
            return result;
        }
    }

    private boolean allLegal() {
        for (int i = 0; i < bag.getSize(); i++) {
            if (allGoods.getGoodsById(bag.getItem(i)).getType() == GoodsType.Illegal) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return this.playerID + " BRIBED " + this.getMoney();
    }
}
