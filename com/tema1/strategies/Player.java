package com.tema1.strategies;

import com.tema1.common.Constants;
import com.tema1.goods.Bag;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Player {
    private int money;
    protected Bag bag;
    protected GoodsFactory allGoods;
    protected Integer declaredID;
    protected Map<Goods, Integer> stall;
    protected int bribeMoney;
    protected final int playerID;

    public Player(final int id) {
        money = Constants.INITIAL_MONEY;
        allGoods = GoodsFactory.getInstance();
        bag = new Bag();
        stall = new HashMap<>();
        playerID = id;
        bribeMoney = 0;
    }

    public final int getMoney() {
        return money;
    }

    public final void addMoney(final int coins) {
        money += coins;
    }

    public final void decMoney(final int coins) {
        money -= coins;
    }

    public final void drawCards(final List<Integer> assets) {
        bag.drawCards(assets);
    }

    public abstract List<Integer> chooseCards();

    public abstract List<Integer> sherrifControl(Player player, List<Integer> itemsToCheck);

    public abstract List<Integer> startControl(List<Player> players, List<Integer> assets);

    public final void addProfit() {
        for (Map.Entry<Goods, Integer> it : stall.entrySet()) {
                this.money += it.getKey().getProfit() * it.getValue();
        }
    }

    public final Map<Goods, Integer> getStall() {
        return stall;
    }

    public final void addAllToStall(final List<Integer> itemsToAdd) {
        for (Integer stallGoods : itemsToAdd) {
            Goods goods = allGoods.getGoodsById(stallGoods);
            stall.put(goods, stall.getOrDefault(goods, 0) + 1);
        }
    }
}
