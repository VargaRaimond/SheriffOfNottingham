package com.tema1.goods;

import com.tema1.common.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Bag {

    private ArrayList<Integer> assets;

    public ArrayList<Integer> getAssets() {
        return assets;
    }

    public Bag() {
        assets = new ArrayList<>();
    }

    public void drawCards(final List<Integer> cards) {
        if (!assets.isEmpty()) {
            assets.clear();
        }
        for (int i = 0; i < Constants.BAG_SIZE; i++) {
            assets.add(cards.get(i));
        }
        // reset hand for next subround
        for (int i = 0; i < Constants.BAG_SIZE; i++) {
            cards.remove(0);
        }
    }

    public Integer getItem(final Integer index) {
        return assets.get(index);
    }

    public void removeItem(final Integer index) {
        assets.remove(index);
    }

    public void sortItems() {
        Collections.sort(assets);
    }

    public int getSize() {
        return assets.size();
    }
}
