package org.poo.main.GameHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class Statistics {
    private static ObjectMapper objectMapper = new ObjectMapper();

    private Statistics() {
    }

    /**
     * Adds an entry to the output that displays the total number of games played.
     *
     * @param output the ArrayNode to which the result should be added
     * @param i the current total count of games played
     */
    public static void getTotalGamesPlayed(final ArrayNode output, final int i) {
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("command", "getTotalGamesPlayed");
        outputNode.put("output", i + 1);
        output.addPOJO(outputNode);
    }

    /**
     * Adds an entry to the output that displays the total number of games won by Player One.
     *
     * @param output the ArrayNode to which the result should be added
     * @param playerOneWins the total number of games won by Player One
     */
    public static void getPlayerOneWins(final ArrayNode output,
                                        final int playerOneWins) {
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("command", "getPlayerOneWins");
        outputNode.put("output", playerOneWins);
        output.addPOJO(outputNode);
    }

    /**
     * Adds an entry to the output that displays the total number of games won by Player Two.
     *
     * @param output the ArrayNode to which the result should be added
     * @param playerTwoWins the total number of games won by Player Two
     */
    public static void getPlayerTwoWins(final ArrayNode output,
                                        final int playerTwoWins) {
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("command", "getPlayerTwoWins");
        outputNode.put("output", playerTwoWins);
        output.addPOJO(outputNode);
    }
}
