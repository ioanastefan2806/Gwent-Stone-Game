package org.poo.main.GameAction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.ActionsInput;

import org.poo.main.CardHandler.Deck;
import org.poo.main.CardHandler.Environment;
import org.poo.main.CardHandler.Hero;
import org.poo.main.CardHandler.Minion;

import java.util.ArrayList;
import java.util.LinkedList;


public class GameInfo {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Private constructor to prevent instantiation of utility class
    private GameInfo() {
    }

    /**
     * Retrieves the cards in hand for the specified player and adds the result to the output.
     *
     * @param output            The output ArrayNode to add the player's cards to.
     * @param command           The action input containing player index information.
     * @param playerOneDeckInHand The deck in hand for player one.
     * @param playerTwoDeckInHand The deck in hand for player two.
     */
    public static void getCardsInHand(final ArrayNode output,
                                      final ActionsInput command,
                                      final LinkedList<Deck> playerOneDeckInHand,
                                      final LinkedList<Deck> playerTwoDeckInHand) {
        // Get the player's deck in hand based on the player index
        LinkedList<Deck> playerDeckInHand = getPlayerDeckInHand(command.getPlayerIdx(),
                playerOneDeckInHand, playerTwoDeckInHand);
        // Create a command node for "getCardsInHand" and add the player's cards
        ObjectNode outputNode = createCommandNode("getCardsInHand", command.getPlayerIdx());

        // Add a deep copy of the cards in hand to the output node
        outputNode.putPOJO("output", deepCopyCards(playerDeckInHand));
        // Add the output node to the output array
        output.addPOJO(outputNode);
    }

    /**
     * Retrieves the deck in hand for the specified player index.
     *
     * @param playerIdx         The index of the player (1 or 2).
     * @param pOneDeckInHand The deck in hand for player one.
     * @param pTwoDeckInHand The deck in hand for player two.
     * @return The deck in hand for the specified player.
     */
    private static LinkedList<Deck> getPlayerDeckInHand(final int playerIdx,
                                                        final LinkedList<Deck> pOneDeckInHand,
                                                        final LinkedList<Deck> pTwoDeckInHand) {
        // Return the deck in hand for the player based on their index
        return playerIdx == 1 ? pOneDeckInHand : pTwoDeckInHand;
    }

    /**
     * Retrieves the deck of a specified player and adds it to the output.
     *
     * @param output       The JSON array node to store the command output.
     * @param command      The command containing the player index.
     * @param playerOneDeck The deck of player one.
     * @param playerTwoDeck The deck of player two.
     */
    public static void getPlayerDeck(final ArrayNode output,
                                     final ActionsInput command,
                                     final LinkedList<Deck> playerOneDeck,
                                     final LinkedList<Deck> playerTwoDeck) {
        int playerIdx = command.getPlayerIdx();
        // Create a command node for "getPlayerDeck" and add the player's deck
        ObjectNode outputNode = createCommandNode("getPlayerDeck", playerIdx);

        // Create a deep copy of the player's deck
        LinkedList<Deck> playerDeckDeepCopy =
                deepCopyCards(playerIdx == 1 ? playerOneDeck : playerTwoDeck);

        // Add the deep copy of the player's deck to the output node
        outputNode.putPOJO("output", playerDeckDeepCopy);
        // Add the output node to the output array
        output.addPOJO(outputNode);
    }

    /**
     * Retrieves the cards on the table and adds them to the output JSON.
     *
     * @param output       The ArrayNode to which the output is appended.
     * @param playingTable The list of rows representing the current state of the table,
     *                     where each row is a list of minions.
     */
    public static void getCardsOnTable(final ArrayNode output,
                                       final ArrayList<LinkedList<Minion>> playingTable) {
        // Create a command node for "getCardsOnTable"
        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();
        outputNode.put("command", "getCardsOnTable");

        // Create a deep copy of the playing table
        LinkedList<LinkedList<Minion>> tableDeepCopy = new LinkedList<>();
        for (LinkedList<Minion> row : playingTable) {
            tableDeepCopy.add(deepCopyMinions(row));
        }

        // Add the deep copy of the playing table to the output node
        outputNode.putPOJO("output", tableDeepCopy);
        // Add the output node to the output array
        output.addPOJO(outputNode);
    }

    /**
     * Creates a deep copy of a list of minions.
     *
     * @param original The original list of minions to be deep-copied.
     * @return A deep copy of the list of minions.
     */
    private static LinkedList<Minion> deepCopyMinions(final LinkedList<Minion> original) {
        // Create a deep copy of the list of minions
        LinkedList<Minion> copy = new LinkedList<>();
        for (Minion minion : original) {
            copy.add(new Minion(minion));
        }
        return copy;
    }

    /**
     * Adds the current player's turn information to the output JSON array.
     *
     * @param output The JSON array to which the player's turn information will be added.
     * @param turn   The current player's turn.
     */
    public static void getPlayerTurn(final ArrayNode output,
                                     final int turn) {
        // Create a command node for "getPlayerTurn" and add the player's turn
        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();
        outputNode.put("command", "getPlayerTurn");
        outputNode.put("output", turn);
        // Add the output node to the output array
        output.addPOJO(outputNode);
    }

    /**
     * Retrieves the hero of the specified player and adds it to the output.
     *
     * @param output       The ArrayNode to store the output of the command.
     * @param command      The input command containing the player's index.
     * @param playerOneHero The hero belonging to player one.
     * @param playerTwoHero The hero belonging to player two.
     */
    public static void getPlayerHero(final ArrayNode output,
                                     final ActionsInput command,
                                     final Hero playerOneHero,
                                     final Hero playerTwoHero) {
        int playerIdx = command.getPlayerIdx();
        // Create a command node for "getPlayerHero" and add the player's hero
        ObjectNode outputNode = createCommandNode("getPlayerHero", playerIdx);
        outputNode.putPOJO("output", new Hero(playerIdx == 1 ? playerOneHero : playerTwoHero));
        // Add the output node to the output array
        output.addPOJO(outputNode);
    }

    /**
     * Retrieves the card at a specific position on the table.
     *
     * @param output       The output ArrayNode to add the card information to.
     * @param command      The action input containing position information.
     * @param playingTable The list of rows representing the current state of the table.
     */
    public static void getCardsAtPosition(final ArrayNode output,
                                          final ActionsInput command,
                                          final ArrayList<LinkedList<Minion>> playingTable) {
        int x = command.getX();
        int y = command.getY();
        // Create a command node for "getCardAtPosition" and add the position details
        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();
        outputNode.put("command", "getCardAtPosition");
        outputNode.put("x", x);
        outputNode.put("y", y);

        // Get the card at the specified position and add it to the output node
        Object result = getCardAtTablePosition(playingTable, x, y);
        outputNode.putPOJO("output", result);

        // Add the output node to the output array
        output.addPOJO(outputNode);
    }

    /**
     * Retrieves the card information at the specified position.
     *
     * @param playingTable The list of rows representing the current state of the table.
     * @param x            The row index.
     * @param y            The column index.
     * @return The card at the specified position or a message if no card is available.
     */
    private static Object getCardAtTablePosition(final ArrayList<LinkedList<Minion>> playingTable,
                                                 final int x, final int y) {
        // Check if the specified position is valid
        if (x >= 0 && x < playingTable.size() && y >= 0 && y < playingTable.get(x).size()) {
            return new Minion(playingTable.get(x).get(y));
        } else {
            // Return a message if no card is available at the position
            return "No card available at that position.";
        }
    }

    /**
     * Retrieves the player's mana and adds it to the output.
     *
     * @param output       The output ArrayNode to add the mana information to.
     * @param command      The action input containing player index information.
     * @param playerOneMana The mana of player one.
     * @param playerTwoMana The mana of player two.
     */
    public static void getPlayerMana(final ArrayNode output,
                                     final ActionsInput command,
                                     final int playerOneMana,
                                     final int playerTwoMana) {
        int playerIdx = command.getPlayerIdx();
        // Create a command node for "getPlayerMana" and add the player's mana
        ObjectNode outputNode = createCommandNode("getPlayerMana", playerIdx);
        outputNode.put("output", playerIdx == 1 ? playerOneMana : playerTwoMana);
        // Add the output node to the output array
        output.addPOJO(outputNode);
    }

    /**
     * Retrieves the environment cards in the player's hand and adds them to the output.
     *
     * @param output            The output ArrayNode to add the environment cards to.
     * @param command           The action input containing player index information.
     * @param playerOneDeckInHand The deck in hand for player one.
     * @param playerTwoDeckInHand The deck in hand for player two.
     */
    public static void getEnvironmentCardsInHand(final ArrayNode output,
                                                 final ActionsInput command,
                                                 final LinkedList<Deck> playerOneDeckInHand,
                                                 final LinkedList<Deck> playerTwoDeckInHand) {
        int playerIdx = command.getPlayerIdx();
        // Create a command node for "getEnvironmentCardsInHand"
        ObjectNode outputNode = createCommandNode("getEnvironmentCardsInHand", playerIdx);
        // Get the player's deck in hand based on the player index
        LinkedList<Deck> playerDeckInHand =
                playerIdx == 1 ? playerOneDeckInHand : playerTwoDeckInHand;
        ArrayList<Deck> environmentCards = new ArrayList<>();

        // Iterate over the player's deck and add environment cards to the list
        for (Deck card : playerDeckInHand) {
            if (isEnvironment(card.getName())) {
                environmentCards.add(new Environment((Environment) card));
            }
        }
        // Add the list of environment cards to the output node
        outputNode.putPOJO("output", environmentCards);
        // Add the output node to the output array
        output.addPOJO(outputNode);
    }

    /**
     * Retrieves the frozen cards on the table and adds them to the output.
     *
     * @param output       The output ArrayNode to add the frozen cards to.
     * @param playingTable The list of rows representing the current state of the table.
     */
    public static void getFrozenCardsOnTable(final ArrayNode output,
                                             final ArrayList<LinkedList<Minion>> playingTable) {
        // Get the list of frozen cards on the table
        LinkedList<Minion> frozenCardsOnTable = getFrozenCards(playingTable);
        // Create a command node for "getFrozenCardsOnTable" and add the frozen cards
        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();
        outputNode.put("command", "getFrozenCardsOnTable");
        outputNode.putPOJO("output", frozenCardsOnTable);
        // Add the output node to the output array
        output.addPOJO(outputNode);
    }

    /**
     * Retrieves the list of frozen cards on the table.
     *
     * @param playingTable The list of rows representing the current state of the table.
     * @return A list of frozen cards.
     */
    private static LinkedList<Minion> getFrozenCards(
            final ArrayList<LinkedList<Minion>> playingTable) {
        // Create a list to store the frozen cards on the table
        LinkedList<Minion> frozenCardsOnTable = new LinkedList<>();
        // Iterate over the table and add frozen minions to the list
        for (LinkedList<Minion> minions : playingTable) {
            for (Minion minion : minions) {
                if (minion.getIsFrozen() == 1) {
                    frozenCardsOnTable.add(new Minion(minion));
                }
            }
        }
        return frozenCardsOnTable;
    }

    /**
     * Helper method to create a command node.
     *
     * @param command   The command name.
     * @param playerIdx The player index.
     * @return An ObjectNode representing the command.
     */
    private static ObjectNode createCommandNode(final String command,
                                                final int playerIdx) {
        // Create a new JSON object node for the command
        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();
        outputNode.put("command", command);
        outputNode.put("playerIdx", playerIdx);
        return outputNode;
    }

    /**
     * Helper method to create a deep copy of a deck of cards.
     *
     * @param originalDeck The original deck to be deep-copied.
     * @return A deep copy of the deck of cards.
     */
    private static LinkedList<Deck> deepCopyCards(final LinkedList<Deck> originalDeck) {
        // Create a deep copy of the deck of cards
        LinkedList<Deck> deepCopy = new LinkedList<>();
        for (Deck card : originalDeck) {
            if (isEnvironment(card.getName())) {
                deepCopy.add(new Environment((Environment) card));
            } else {
                deepCopy.add(new Minion((Minion) card));
            }
        }
        return deepCopy;
    }

    /**
     * Helper method to check if a card is an environment card.
     *
     * @param name The name of the card.
     * @return True if the card is an environment card, false otherwise.
     */
    private static boolean isEnvironment(final String name) {
        // Check if the card name matches one of the environment cards
        return name.equals("Firestorm")
                || name.equals("Winterfell")
                || name.equals("Heart Hound");
    }
}
