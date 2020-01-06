**Copyright 2019 Varga Raimond**
# Sheriff of Nottingham

**Name:** Varga Raimond
**Group:** 325CA

## Introduction
I implemented a simple version of Sheriff of Nothingham game, with 3 strategies:
-Base Strategy - the honest and fair player;
-Greedy Strategy - the player who can be easily bribed;
-Bribe Strategy - the player that applies a high risk, high reward method.


## Game engine and strategies

I made a abstract class **Player** where I declared all the general aspects and implemented only the functionalities that are the same for all strategies. Even though every strategy is built on the base player, I chose to implement this class for a better encapsulation and a cleaner code.
Every player has two main methods that define their strategies : **startControl()** - a method that decides whose items will the player check when he is sheriff - and **chooseCards()** - a method that decides what items a player will try to get to his stall. 
All the other methods are implemented only in the player class - **addProfit()** , **addAllToStall()** - or the base strategy class - **sheriffControl()** - because they are the same for all strategies. this works because Greedy and Bribe extend the Base Strategy, which extends the Player class.
The game engine is implemented in the Round class, where I simulate every round and subround, add bonuses and build the final leaderboard.


##  Difficulties encountered

- There were a lot of special cases which I didn't observe at first - eg: minimum amount of money a Briber needs to apply his strategy;
- It was my first OOP project so I didn't really know how to start my thought process.


