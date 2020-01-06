package com.tema1.common;

import com.tema1.goods.Goods;

import java.util.Comparator;

public final class AssetsProfitIDComparator implements Comparator<Goods> {
    @Override
    public int compare(final Goods g1, final Goods g2) {
        if (g2.getProfit() != g1.getProfit()) {
            return g2.getProfit() - g1.getProfit();
        } else {
            return g2.getId() - g1.getId();
        }
    }
}
