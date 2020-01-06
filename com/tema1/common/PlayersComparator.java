package com.tema1.common;

import com.tema1.strategies.Player;

import java.util.Comparator;

public final class PlayersComparator implements Comparator<Player> {
    @Override
    public int compare(final Player p1, final Player p2) {
        return p2.getMoney() - p1.getMoney();
    }
}
