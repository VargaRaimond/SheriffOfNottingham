package com.tema1.strategies;

import com.tema1.common.Constants;
import com.tema1.common.AssetsProfitIDComparator;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseStrategy extends Player {


    public BaseStrategy(final int id) {
        super(id);
    }

    private int highestFrequency(final List<Goods> highFreqItems) {
        int cFreq = 1;
        int maxFreq = 1;
        bag.sortItems();
        // get the legal items with the highest frequency
        for (int i = 0; i < bag.getSize() - 1; i++) {
            if (allGoods.getGoodsById(bag.getItem(i)).getType() == GoodsType.Legal) {
                if (bag.getItem(i).equals(bag.getItem(i + 1))) {
                    cFreq++;
                } else {
                    if (cFreq > maxFreq) {
                        maxFreq = cFreq;
                        highFreqItems.clear();
                        highFreqItems.add(allGoods.getGoodsById(bag.getItem(i)));
                    } else {
                        if (cFreq == maxFreq) {
                            highFreqItems.add(allGoods.getGoodsById(bag.getItem(i)));
                        }
                    }
                    cFreq = 1;
                }
            }
        }
        if (allGoods.getGoodsById(bag.getItem(bag.getSize() - 1)).getType() == GoodsType.Legal) {
            if (cFreq > maxFreq) {
                maxFreq = cFreq;
                highFreqItems.clear();
                highFreqItems.add(allGoods.getGoodsById(bag.getItem(bag.getSize() - 1)));
            } else {
                if (cFreq == maxFreq) {
                    highFreqItems.add(allGoods.getGoodsById(bag.getItem(bag.getSize() - 1)));
                }
            }
        }
        return maxFreq;
    }

    protected final List<Integer> chooseLegal() {
        List<Goods> sameFreq = new ArrayList<>();
        int maxFreq = highestFrequency(sameFreq);
        AssetsProfitIDComparator assetsProfitIDComparator = new AssetsProfitIDComparator();
        Collections.sort(sameFreq, assetsProfitIDComparator);
        List<Integer> result = new ArrayList<>();
        // choose the most profitable item from the highest frequency list
        if (maxFreq > Constants.MAX_ITEMS) {
            maxFreq = Constants.MAX_ITEMS;
        }
        for (int i = 0; i < maxFreq; i++) {
            result.add(sameFreq.get(0).getId());
        }
        declaredID = sameFreq.get(0).getId();
        return result;
    }

    /**
     * Choose the items that are to be presented to the sheriff while respecting a strategy.
     * @return a list with max 8 items that a player will present to the sheriff
     */
    public List<Integer> chooseCards() {
        if (!allIllegal()) {
            return chooseLegal();
        } else {
            // put illegal item only if he can pay the penaly
            if (this.getMoney() >= Constants.ILLEGAL_PENALTY) {
                int maxIllegal = bag.getItem(0);
                int maxProfit = allGoods.getGoodsById(bag.getItem(0)).getProfit();
                for (int i = 1; i < bag.getAssets().size(); i++) {
                    if (allGoods.getGoodsById(bag.getItem(i)).getProfit() > maxProfit) {
                        maxIllegal = bag.getItem(i);
                        maxProfit = allGoods.getGoodsById(bag.getItem(i)).getProfit();
                    }
                }
                List<Integer> result = new ArrayList<>();
                result.add(maxIllegal);
                // remove the item so i won't pick it again
                bag.removeItem((Integer) (maxIllegal));
                declaredID = 0;
                return result;
            } else {
                return new ArrayList<>();
            }
        }
    }

    private Boolean allIllegal() {
        for (int i = 0; i < bag.getSize(); i++) {
            if (allGoods.getGoodsById(bag.getItem(i)).getType() == GoodsType.Legal) {
                return false;
            }
        }
        return true;
    }

    /**
     * Decide who to control by respecting a certain strategy.
     * @param players list with all the players
     * @param assets a list with all items from which every player will take 10 every subround
     * @return a list with the assets confiscated after the sheriff control
     */
    public List<Integer> startControl(final List<Player> players, final List<Integer> assets) {
        List<Integer> confiscatedGoods = new ArrayList<>();
        // check all the players if i have enough money
        for (int i = 0; i < players.size(); i++) {
            if (i != this.playerID) {
                players.get(i).drawCards(assets);

                List<Integer> itToCheck = players.get(i).chooseCards();
                if (this.getMoney() >= Constants.MIN_AMOUNT_SHERIFF) {
                    confiscatedGoods.addAll(sherrifControl(players.get(i), itToCheck));
                } else {
                    for (Integer stallGoods : itToCheck) {
                        Goods goods = allGoods.getGoodsById(stallGoods);
                        players.get(i).stall.put(goods,
                                players.get(i).stall.getOrDefault(goods, 0) + 1);
                    }
                }
            }
        }
        return confiscatedGoods;
    }

    public final List<Integer> sherrifControl(final Player player, final List<Integer> itToCheck) {
        List<Integer> confiscatedGoods = new ArrayList<>();
        Goods goods;
        boolean playerLied = false;
                // check if player lied
                for (Integer item : itToCheck) {
                    if (!item.equals(player.declaredID)) {
                        playerLied = true;
                        break;
                    }
                }
                for (Integer item : itToCheck) {
                    goods = allGoods.getGoodsById(item);
                    if (goods.getId() != player.declaredID) {
                        confiscatedGoods.add(item);
                        this.addMoney(goods.getPenalty());
                        player.decMoney(goods.getPenalty());
                    } else {
                        if (!playerLied) {
                            this.decMoney(goods.getPenalty());
                            player.addMoney(goods.getPenalty());
                        }
                        player.stall.put(goods, player.stall.getOrDefault(goods, 0) + 1);
                    }
                }
            return confiscatedGoods;
        }

    /**
     *
     * @return a string with the format ID "type" Money
     */
    @Override
    public String toString() {
        return this.playerID + " BASIC " + this.getMoney();
    }

    }

