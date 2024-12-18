package org.poo.main.GameHandler;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.ActionsInput;
import org.poo.main.CardHandler.Deck;
import org.poo.main.CardHandler.Minion;
import org.poo.main.Utils;

import java.util.ArrayList;
import java.util.LinkedList;

public final class EnvironmentCardHandler {
    public static final int MAX_SIZE = 5;

    /**
     * Uses an environment card from the player's hand to perform an action on the playing table.
     * <p>
     * This method handles the logic for using an environment card, including checking if the card
     * can be played, validating affected rows, and executing the card's effect. The following types
     * of environment cards are supported: "Firestorm", "Winterfell", and "Heart Hound". Each card
     * has a distinct effect on the playing table.
     *
     * @param output              JSON output node to record any errors or game-ending messages.
     * @param command             The action command containing the card index and affected row.
     * @param playerOneDeckInHand The deck of cards in player one's hand.
     * @param playerTwoDeckInHand The deck of cards in player two's hand.
     * @param playingTable        The current state of the playing table, organized by rows.
     * @param utils               Utility object containing game-related methods and player states.
     * @param turn                Indicates the current player's turn
     *                            (1 for player one, 2 for player two).
     */
    public static void useEnvironmentCard(final ArrayNode output,
                                          final ActionsInput command,
                                          final LinkedList<Deck> playerOneDeckInHand,
                                          final LinkedList<Deck> playerTwoDeckInHand,
                                          final ArrayList<LinkedList<Minion>> playingTable,
                                          final Utils utils, final int turn) {
        // Extract card index and affected row from the command
        int cardIndex = command.getHandIdx();
        int targetRow = command.getAffectedRow();
        Deck environmentCard = getCardFromHand(turn, cardIndex,
                playerOneDeckInHand, playerTwoDeckInHand);

        // If the card does not exist, return immediately
        if (environmentCard == null) {
            return;
        }

        // Validate the card usage
        int validationCode = validateCardUsage(environmentCard, cardIndex, targetRow,
                turn, playerOneDeckInHand, playerTwoDeckInHand, playingTable, utils);
        if (validationCode != 0) {
            handleInvalidCases(validationCode, output, targetRow, cardIndex);
            return;
        }

        // Execute the action of the environment card
        executeCardAction(environmentCard.getName(),targetRow, cardIndex, turn,
                playerOneDeckInHand, playerTwoDeckInHand, playingTable, utils);
    }

    /**
     * Retrieves the environment card from the current player's
     * hand based on the turn and card index.
     *
     * @param turn                Indicates the current player's turn
     *                            (1 for player one, 2 for player two).
     * @param cardIndex           The index of the card in the player's hand.
     * @param playerOneDeckInHand The deck of cards in player one's hand.
     * @param playerTwoDeckInHand The deck of cards in player two's hand.
     * @return The environment card from the player's hand, or null if not found.
     */
    private static Deck getCardFromHand(final int turn,
                                        final int cardIndex,
                                        final LinkedList<Deck> playerOneDeckInHand,
                                        final LinkedList<Deck> playerTwoDeckInHand) {
        // Retrieve the card from the current player's hand
        if (turn == 1 && playerOneDeckInHand.size() > cardIndex) {
            return playerOneDeckInHand.get(cardIndex);
        } else if (turn == 2 && playerTwoDeckInHand.size() > cardIndex) {
            return playerTwoDeckInHand.get(cardIndex);
        }
        return null;
    }

    /**
     * Validates if the environment card can be used based on game rules.
     *
     * @param environmentCard     The environment card to be used.
     * @param cardIndex           The index of the card in the player's hand.
     * @param targetRow           The row affected by the card.
     * @param turn                Indicates the current player's turn
     *                            (1 for player one, 2 for player two).
     * @param playerOneDeckInHand The deck of cards in player one's hand.
     * @param playerTwoDeckInHand The deck of cards in player two's hand.
     * @param playingTable        The current state of the playing table.
     * @param utils               Utility object containing game-related methods and player states.
     * @return An integer representing the validation result (0 if valid, non-zero if invalid).
     */
    private static int validateCardUsage(final Deck environmentCard,
                                         final int cardIndex,
                                         final int targetRow,
                                         final int turn,
                                         final LinkedList<Deck> playerOneDeckInHand,
                                         final LinkedList<Deck> playerTwoDeckInHand,
                                         final ArrayList<LinkedList<Minion>> playingTable,
                                         final Utils utils) {
        String cardName = environmentCard.getName();

        // Check if the card is of type 'Environment'
        if (!isEnvironmentCard(cardName)) {
            return 1;
        }
        // Check if the player has enough mana to use the card
        if (!hasEnoughMana(turn, environmentCard.getMana(), utils)) {
            return 2;
        }
        // Check if the card is being used on an enemy row
        if (isOwnRowAffected(turn, targetRow)) {
            return 3;
        }
        // Check if there is space on the affected row for 'Heart Hound' card
        if (cardName.equals("Heart Hound")
                &&
                !hasSpaceOnAffectedRow(turn, targetRow, playingTable)) {
            return 4;
        }
        return 0;
    }

    /**
     * Checks if the given card is of type 'Environment'.
     *
     * @param cardName The name of the card.
     * @return True if the card is an environment card, otherwise false.
     */
    private static boolean isEnvironmentCard(final String cardName) {
        // Check if the card is one of the environment card types
        return cardName.equals("Firestorm")
                || cardName.equals("Winterfell")
                || cardName.equals("Heart Hound");
    }

    /**
     * Checks if the player has enough mana to use the specified card.
     *
     * @param turn     Indicates the current player's turn (1 for player one, 2 for player two).
     * @param manaCost The mana cost of the card.
     * @param utils    Utility object containing game-related methods and player states.
     * @return True if the player has enough mana, otherwise false.
     */
    private static boolean hasEnoughMana(final int turn,
                                         final int manaCost,
                                         final Utils utils) {
        // Check if the player has enough mana to use the card
        return (turn == 1 && manaCost <= utils.getPlayerOneMana())
                || (turn == 2 && manaCost <= utils.getPlayerTwoMana());
    }

    /**
     * Checks if the affected row belongs to the current player.
     *
     * @param turn      Indicates the current player's turn (1 for player one, 2 for player two).
     * @param targetRow The row affected by the card.
     * @return True if the affected row is the player's own row, otherwise false.
     */
    private static boolean isOwnRowAffected(final int turn,
                                            final int targetRow) {
        // Check if the affected row is the player's own row
        return (turn == 1 && (targetRow == 2 || targetRow == 3))
                || (turn == 2 && (targetRow == 0 || targetRow == 1));
    }

    /**
     * Checks if there is space left on the affected row for the 'Heart Hound' card.
     *
     * @param turn         Indicates the current player's turn (1 for player one, 2 for player two).
     * @param targetRow    The row affected by the card.
     * @param playingTable The current state of the playing table.
     * @return True if there is space left on the row, otherwise false.
     */
    private static boolean hasSpaceOnAffectedRow(final int turn,
                                                 final int targetRow,
                                                 final ArrayList<LinkedList<Minion>> playingTable) {
        // Check if there is space left on the affected row for the 'Heart Hound' card
        if (turn == 1) {
            return (targetRow == 1 && playingTable.get(2).size() < MAX_SIZE)
                    || (targetRow == 0 && playingTable.get(3).size() < MAX_SIZE);
        } else {
            return (targetRow == 2 && playingTable.get(1).size() < MAX_SIZE)
                    || (targetRow == 3 && playingTable.get(0).size() < MAX_SIZE);
        }
    }

    /**
     * Handles invalid cases when using an environment card
     * and records the appropriate error message.
     *
     * @param validationCode The code representing the validation result.
     * @param output         JSON output node to record any errors or game-ending messages.
     * @param targetRow      The row affected by the card.
     * @param cardIndex      The index of the card in the player's hand.
     */
    private static void handleInvalidCases(final int validationCode,
                                           final ArrayNode output,
                                           final int targetRow,
                                           final int cardIndex) {
        // Handle different invalid cases and record the corresponding error message
        switch (validationCode) {
            case 1:
                ErrorHandler.notEnvironmentType(output, targetRow, cardIndex);
                break;
            case 2:
                ErrorHandler.environmentCardNotEnoughMana(output, targetRow, cardIndex);
                break;
            case 3:
                ErrorHandler.environmentCardNotEnemyRow(output, targetRow, cardIndex);
                break;
            case 4:
                ErrorHandler.environmentCardNotEnoughSpace(output, targetRow, cardIndex);
                break;
            default:
                break;
        }
    }

    /**
     * Executes the action of the specified environment card.
     *
     * @param cardName           The name of the environment card.
     * @param targetRow          The row affected by the card.
     * @param cardIndex          The index of the card in the player's hand.
     * @param turn               Indicates the current player's turn
     *                           (1 for player one, 2 for player two).
     * @param playerOneDeckInHand The deck of cards in player one's hand.
     * @param playerTwoDeckInHand The deck of cards in player two's hand.
     * @param playingTable       The current state of the playing table.
     * @param utils              Utility object containing game-related methods and player states.
     */
    private static void executeCardAction(final String cardName,
                                          final int targetRow,
                                          final int cardIndex,
                                          final int turn,
                                          final LinkedList<Deck> playerOneDeckInHand,
                                          final LinkedList<Deck> playerTwoDeckInHand,
                                          final ArrayList<LinkedList<Minion>> playingTable,
                                          final Utils utils) {
        // Execute the action based on the environment card type
        switch (cardName) {
            case "Heart Hound":
                executeHeartHound(targetRow, turn, playingTable);
                break;
            case "Firestorm":
                executeFirestorm(targetRow, playingTable);
                break;
            case "Winterfell":
                executeWinterfell(targetRow, playingTable);
                break;
            default:
                break;
        }
        // Deduct mana cost and remove the used card from the player's hand
        deductManaAndRemoveCard(turn, cardIndex, playerOneDeckInHand, playerTwoDeckInHand, utils);
    }

    /**
     * Executes the action for the 'Heart Hound' card by moving the minion with the highest health.
     *
     * @param targetRow    The row affected by the card.
     * @param turn         Indicates the current player's turn (1 for player one, 2 for player two).
     * @param playingTable The current state of the playing table.
     */
    private static void executeHeartHound(final int targetRow,
                                          final int turn,
                                          final ArrayList<LinkedList<Minion>> playingTable) {
        // Execute the action for 'Heart Hound' card by moving the minion with the highest health
        int maxHealth = 0;
        int maxHealthIdx = 0;
        int sourceRow = turn == 1 ? targetRow : (targetRow == 2 ? 2 : 3);
        int destinationRow = turn == 1 ? (targetRow == 1 ? 2 : 3) : (targetRow == 2 ? 1 : 0);

        // Find the minion with the highest health in the source row
        for (int i = 0; i < playingTable.get(sourceRow).size(); i++) {
            int cardHealth = playingTable.get(sourceRow).get(i).getHealth();
            if (cardHealth > maxHealth) {
                maxHealth = cardHealth;
                maxHealthIdx = i;
            }
        }
        // Move the minion with the highest health to the destination row
        playingTable.get(destinationRow).add(playingTable.get(sourceRow).remove(maxHealthIdx));
    }

    /**
     * Executes the action for the 'Firestorm' card by reducing
     * the health of each minion in the target row.
     *
     * @param targetRow    The row affected by the card.
     * @param playingTable The current state of the playing table.
     */
    private static void executeFirestorm(final int targetRow,
                                         final ArrayList<LinkedList<Minion>> playingTable) {
        // Execute the action for 'Firestorm' card by reducing
        // the health of each minion in the target row
        for (int i = 0; i < playingTable.get(targetRow).size(); i++) {
            Minion card = playingTable.get(targetRow).get(i);
            card.setHealth(card.getHealth() - 1);
            // Remove the minion if its health drops to zero or below
            if (card.getHealth() <= 0) {
                playingTable.get(targetRow).remove(i);
                i--;
            }
        }
    }

    /**
     * Executes the action for the 'Winterfell' card by freezing each minion in the target row.
     *
     * @param targetRow    The row affected by the card.
     * @param playingTable The current state of the playing table.
     */
    private static void executeWinterfell(final int targetRow,
                                          final ArrayList<LinkedList<Minion>> playingTable) {
        // Execute the action for 'Winterfell' card by freezing each minion in the target row
        for (Minion card : playingTable.get(targetRow)) {
            card.setIsFrozen(1);
        }
    }

    /**
     * Deducts the mana cost from the current player and removes
     * the used card from the player's hand.
     *
     * @param turn                Indicates the current player's turn
     *                            (1 for player one, 2 for player two).
     * @param cardIndex           The index of the card in the player's hand.
     * @param playerOneDeckInHand The deck of cards in player one's hand.
     * @param playerTwoDeckInHand The deck of cards in player two's hand.
     * @param utils               Utility object containing game-related methods and player states.
     */
    private static void deductManaAndRemoveCard(final int turn,
                                                final int cardIndex,
                                                final LinkedList<Deck> playerOneDeckInHand,
                                                final LinkedList<Deck> playerTwoDeckInHand,
                                                final Utils utils) {
        // Deduct the mana cost and remove the used card from the player's hand
        if (turn == 1) {
            utils.setPlayerOneMana(utils.getPlayerOneMana()
                    -
                    playerOneDeckInHand.get(cardIndex).getMana());
            playerOneDeckInHand.remove(cardIndex);
        } else {
            utils.setPlayerTwoMana(utils.getPlayerTwoMana()
                    -
                    playerTwoDeckInHand.get(cardIndex).getMana());
            playerTwoDeckInHand.remove(cardIndex);
        }
    }
}
