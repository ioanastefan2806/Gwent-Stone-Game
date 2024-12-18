package org.poo.main;

import org.poo.main.CardHandler.Minion;

import java.util.ArrayList;
import java.util.LinkedList;

public final class    Utils {

    // Player stats
    private int playerOneMana = 1;
    private int playerTwoMana = 1;
    private int turn = 0;
    private int playerOneWins = 0;
    private int playerTwoWins = 0;
    private int numberOfRounds = 0;

    /**
     * Gets the current mana of player one.
     *
     * @return the mana of player one
     */
    public int getPlayerOneMana() {
        return playerOneMana;
    }

    /**
     * Sets the mana for player one.
     *
     * @param playerOneMana the mana to set for player one
     */
    public void setPlayerOneMana(final int playerOneMana) {
        this.playerOneMana = playerOneMana;
    }

    /**
     * Gets the current mana of player two.
     *
     * @return the mana of player two
     */
    public int getPlayerTwoMana() {
        return playerTwoMana;
    }

    /**
     * Sets the mana for player two.
     *
     * @param playerTwoMana the mana to set for player two
     */
    public void setPlayerTwoMana(final int playerTwoMana) {
        this.playerTwoMana = playerTwoMana;
    }

    /**
     * Gets the current turn number.
     *
     * @return the current turn number
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Sets the current turn number.
     *
     * @param turn the turn number to set
     */
    public void setTurn(final int turn) {
        this.turn = turn;
    }

    /**
     * Gets the win count of player one.
     *
     * @return the number of wins for player one
     */
    public int getPlayerOneWins() {
        return playerOneWins;
    }

    /**
     * Sets the win count for player one.
     *
     * @param playerOneWins the number of wins to set for player one
     */
    public void setPlayerOneWins(final int playerOneWins) {
        this.playerOneWins = playerOneWins;
    }

    /**
     * Gets the win count of player two.
     *
     * @return the number of wins for player two
     */
    public int getPlayerTwoWins() {
        return playerTwoWins;
    }

    /**
     * Sets the win count for player two.
     *
     * @param playerTwoWins the number of wins to set for player two
     */
    public void setPlayerTwoWins(final int playerTwoWins) {
        this.playerTwoWins = playerTwoWins;
    }

    /**
     * Gets the total number of rounds played.
     *
     * @return the number of rounds played
     */
    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    /**
     * Sets the total number of rounds played.
     *
     * @param numberOfRounds the number of rounds to set
     */
    public void setNumberOfRounds(final int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    /**
     * Checks if there are any 'Tanks' on the specified player's side of the table.
     * A 'Tank' is represented by a minion with the name 'Goliath' or 'Warden'.
     *
     * @param playingTable the playing table represented by an ArrayList of LinkedLists of minions
     * @param turn the player's side of the table to check (0 for player one, 1 for player two)
     * @return 1 if there is a 'Tank' on the specified side, 0 otherwise
     */
    public static int isTank(final ArrayList<LinkedList<Minion>> playingTable, final int turn) {
        for (Minion minion : playingTable.get(turn)) {
            if (minion.getName().equals("Goliath") || minion.getName().equals("Warden")) {
                return 1; // Tank found
            }
        }
        return 0; // No Tank found
    }
}
