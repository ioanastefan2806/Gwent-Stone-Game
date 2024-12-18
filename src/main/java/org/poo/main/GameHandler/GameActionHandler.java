package org.poo.main.GameHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.StartGameInput;
import org.poo.main.CardHandler.Deck;
import org.poo.main.CardHandler.Hero;
import org.poo.main.CardHandler.Minion;
import org.poo.main.Utils;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class responsible for handling various actions during
 * the game, such as placing cards on the table
 * and ending a player's turn.
 */
public final class GameActionHandler {
    // Maximum number of cards allowed on a row of the playing table
    public static final int MAX_SIZE = 5;
    // ObjectMapper instance for JSON processing
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Places a card on the playing table based on the provided command and game state.
     *
     * @param output              The output ArrayNode to store the result of the action.
     * @param command             The action input containing the details of the command.
     * @param turn                The current player's turn (1 or 2).
     * @param playingTable        The current state of the playing table.
     * @param playerOneDeckInHand The deck of cards in hand for player one.
     * @param playerTwoDeckInHand The deck of cards in hand for player two.
     * @param utils               Utility class containing game-related data (e.g., mana).
     */
    public static void placeCard(final ArrayNode output,
                                 final ActionsInput command,
                                 final int turn,
                                 final ArrayList<LinkedList<Minion>> playingTable,
                                 final LinkedList<Deck> playerOneDeckInHand,
                                 final LinkedList<Deck> playerTwoDeckInHand,
                                 final Utils utils) {
        // Extract the index of the card to be placed from the player's hand
        int handIdx = command.getHandIdx();
        LinkedList<Deck> currentDeckInHand;
        Deck cardToPlace;
        int currentMana;
        int targetRow;

        // Set the current player's deck, mana, and target row based on the turn
        switch (turn) {
            case 1:
                cardToPlace = playerOneDeckInHand.get(handIdx);
                currentDeckInHand = playerOneDeckInHand;
                currentMana = utils.getPlayerOneMana();
                targetRow = (cardToPlace.getName().equals("The Ripper")
                        || cardToPlace.getName().equals("Miraj")
                        || cardToPlace.getName().equals("Goliath")
                        || cardToPlace.getName().equals("Warden")) ? 2 : 3;
                break;
            case 2:
                cardToPlace = playerTwoDeckInHand.get(handIdx);
                currentDeckInHand = playerTwoDeckInHand;
                currentMana = utils.getPlayerTwoMana();
                targetRow = (cardToPlace.getName().equals("The Ripper")
                        || cardToPlace.getName().equals("Miraj")
                        || cardToPlace.getName().equals("Goliath")
                        || cardToPlace.getName().equals("Warden")) ? 1 : 0;
                break;
            default:
                return;
        }

        // Call helper method to place the card on the playing table
        placeCardOnTable(output, turn, handIdx, cardToPlace,
                currentDeckInHand, currentMana, targetRow, playingTable, utils);
    }

    /**
     * Places a card on the table, performing validation checks and updating game state accordingly.
     *
     * @param output              The output ArrayNode to store the result of the action.
     * @param turn                The current player's turn (1 or 2).
     * @param handIdx             The index of the card in the player's hand.
     * @param cardToPlace         The card to be placed on the table.
     * @param currentDeckInHand   The deck of cards in hand for the current player.
     * @param currentMana         The current mana of the player.
     * @param targetRow           The row on the playing table where the card should be placed.
     * @param playingTable        The current state of the playing table.
     * @param utils               Utility class containing game-related data (e.g., mana).
     */
    private static void placeCardOnTable(final ArrayNode output,
                                         final int turn,
                                         final int handIdx,
                                         final Deck cardToPlace,
                                         final LinkedList<Deck> currentDeckInHand,
                                         final int currentMana,
                                         final int targetRow,
                                         final ArrayList<LinkedList<Minion>> playingTable,
                                         final Utils utils) {
        String cardName = cardToPlace.getName();

        // Check for invalid cases
        switch (cardName) {
            case "Firestorm":
            case "Winterfell":
            case "Heart Hound":
                // Environment cards cannot be placed on the table
                ErrorHandler.placeCardEnvironmentCard(output, handIdx);
                return;
            default:
                if (cardToPlace.getMana() > currentMana) {
                    // Not enough mana to place the card
                    ErrorHandler.placeCardNotEnoughMana(output, handIdx);
                    return;
                }
                if (playingTable.get(targetRow).size() == EnvironmentCardHandler.MAX_SIZE) {
                    // No available space on the target row
                    ErrorHandler.placeCardNotEnoughSpace(output, handIdx);
                    return;
                }
                // Decrease mana and place the card on the table
                if (turn == 1) {
                    utils.setPlayerOneMana(currentMana - cardToPlace.getMana());
                } else {
                    utils.setPlayerTwoMana(currentMana - cardToPlace.getMana());
                }
                playingTable.get(targetRow).addLast((Minion) currentDeckInHand.remove(handIdx));
                break;
        }
    }

    /**
     * Ends the current player's turn and performs necessary game state updates.
     *
     * @param playerOneDeck       The deck of player one.
     * @param playerTwoDeck       The deck of player two.
     * @param playerOneDeckInHand The deck of cards in hand for player one.
     * @param playerTwoDeckInHand The deck of cards in hand for player two.
     * @param playingTable        The current state of the playing table.
     * @param playerOneHero       The hero of player one.
     * @param playerTwoHero       The hero of player two.
     * @param newGame             The new game input containing initial game settings.
     * @param utils               Utility class containing game-related data (e.g., mana, turn).
     */
    public static void endPlayerTurn(final LinkedList<Deck> playerOneDeck,
                                     final LinkedList<Deck> playerTwoDeck,
                                     final LinkedList<Deck> playerOneDeckInHand,
                                     final LinkedList<Deck> playerTwoDeckInHand,
                                     final ArrayList<LinkedList<Minion>> playingTable,
                                     final Hero playerOneHero,
                                     final Hero playerTwoHero,
                                     final StartGameInput newGame,
                                     final Utils utils) {
        // Check if the turn should end
        if (shouldEndTurn(utils, newGame)) {
            updatePlayerManaAndDrawCards(playerOneDeck, playerTwoDeck,
                    playerOneDeckInHand, playerTwoDeckInHand, utils);
        }

        // Update the playing table and reset heroes' attack status
        updatePlayingTable(playingTable, utils);
        resetHeroAttackStatus(playerOneHero, playerTwoHero);
        // Switch turns
        switchTurns(utils);
    }

    /**
     * Determines whether the player's turn should end based on the game state.
     *
     * @param utils   Utility class containing game-related data.
     * @param newGame The new game input containing initial game settings.
     * @return True if the turn should end, false otherwise.
     */
    private static boolean shouldEndTurn(final Utils utils,
                                         final StartGameInput newGame) {
        return utils.getTurn() != newGame.getStartingPlayer();
    }

    /**
     * Updates players' mana and draws new cards from their decks.
     *
     * @param playerOneDeck       The deck of player one.
     * @param playerTwoDeck       The deck of player two.
     * @param playerOneDeckInHand The deck of cards in hand for player one.
     * @param playerTwoDeckInHand The deck of cards in hand for player two.
     * @param utils               Utility class containing game-related data.
     */
    private static void updatePlayerManaAndDrawCards(final LinkedList<Deck> playerOneDeck,
                                                     final LinkedList<Deck> playerTwoDeck,
                                                     final LinkedList<Deck> playerOneDeckInHand,
                                                     final LinkedList<Deck> playerTwoDeckInHand,
                                                     final Utils utils) {
        // Increment the round number
        utils.setNumberOfRounds(utils.getNumberOfRounds() + 1);
        // Update players' mana based on the round number
        utils.setPlayerOneMana(utils.getPlayerOneMana() + utils.getNumberOfRounds());
        utils.setPlayerTwoMana(utils.getPlayerTwoMana() + utils.getNumberOfRounds());

        // Draw cards from each player's deck if available
        if (!playerOneDeck.isEmpty()) {
            playerOneDeckInHand.addLast(playerOneDeck.removeFirst());
        }
        if (!playerTwoDeck.isEmpty()) {
            playerTwoDeckInHand.addLast(playerTwoDeck.removeFirst());
        }
    }

    /**
     * Updates the playing table by removing any frozen
     * status and resetting attack usage for minions.
     *
     * @param playingTable The current state of the playing table.
     * @param utils        Utility class containing game-related data.
     */
    private static void updatePlayingTable(final ArrayList<LinkedList<Minion>> playingTable,
                                           final Utils utils) {
        // Iterate over each row of the playing table
        for (LinkedList<Minion> minions : playingTable) {
            for (Minion minion : minions) {
                // Unfreeze minions based on the current player's turn
                if (utils.getTurn() == 1 && (minions == playingTable.get(2)
                        || minions == playingTable.get(3))) {
                    if (minion.getIsFrozen() == 1) {
                        minion.setIsFrozen(0);
                    }
                } else if (utils.getTurn() == 2 && (minions == playingTable.get(0)
                        || minions == playingTable.get(1))) {
                    if (minion.getIsFrozen() == 1) {
                        minion.setIsFrozen(0);
                    }
                }
                // Reset minions' attack usage
                if (minion.getAttackUsed() == 1) {
                    minion.setAttackUsed(0);
                }
            }
        }
    }

    /**
     * Resets the attack status for both heroes at the end of a turn.
     *
     * @param playerOneHero The hero of player one.
     * @param playerTwoHero The hero of player two.
     */
    private static void resetHeroAttackStatus(final Hero playerOneHero,
                                              final Hero playerTwoHero) {
        playerOneHero.setAttackUsed(0);
        playerTwoHero.setAttackUsed(0);
    }

    /**
     * Switches turns between player one and player two.
     *
     * @param utils Utility class containing
     *              game-related data (e.g., current turn).
     */
    private static void switchTurns(final Utils utils) {
        utils.setTurn(utils.getTurn() == 1 ? 2 : 1);
    }
}
