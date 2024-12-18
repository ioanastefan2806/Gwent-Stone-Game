package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.*;
import org.poo.main.CardHandler.Deck;
import org.poo.main.CardHandler.Environment;
import org.poo.main.CardHandler.Hero;
import org.poo.main.CardHandler.Minion;
import org.poo.main.GameAction.GameCardPlay;
import org.poo.main.GameAction.GameInfo;
import org.poo.main.GameHandler.EnvironmentCardHandler;
import org.poo.main.GameHandler.GameActionHandler;
import org.poo.main.GameHandler.Statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;


public class Command {
    private final Input inputData;
    private final LinkedList<LinkedList<Deck>> playerOne;
    private final LinkedList<LinkedList<Deck>> playerTwo;
    private final ArrayNode output;

    public static final int INITIAL_CAPACITY = 4;

    /**
     * Constructor to initialize the Command object with the necessary input data,
     * player decks, and output storage.
     *
     * @param inputData Input data containing game settings and commands.
     * @param playerOne Player one's deck list.
     * @param playerTwo Player two's deck list.
     * @param output    Array node to store output results.
     */
    public Command(final Input inputData,
                   final LinkedList<LinkedList<Deck>> playerOne,
                   final LinkedList<LinkedList<Deck>> playerTwo,
                   final ArrayNode output) {
        this.inputData = inputData;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.output = output;
    }

    /**
     * Executes the commands for each game session defined in the input data.
     * This method initializes the game state, manages player decks, and executes
     * various game commands such as placing cards, using abilities, and tracking
     * game statistics.
     */
    public void run() {
        Utils utils = new Utils();
        utils.setPlayerOneWins(0);
        utils.setPlayerTwoWins(0);

        for (int i = 0; i < inputData.getGames().size(); i++) {
            initializeGame(utils, i);
        }
    }

    /**
     * Initializes the game setup for the given game session.
     * This method includes setting initial mana, shuffling player decks, drawing the first card,
     * setting up the playing table, and determining the starting player.
     *
     * @param utils Utility object containing game state information.
     * @param gameIndex The index of the current game session.
     */
    private void initializeGame(final Utils utils,
                                final int gameIndex) {
        StartGameInput newGame = getCurrentGame(gameIndex);
        ArrayList<ActionsInput> commandList = getCommandList(gameIndex);
        LinkedList<Deck> playerOneDeck = deepCopyDeck(playerOne.get(newGame.getPlayerOneDeckIdx()));
        LinkedList<Deck> playerTwoDeck = deepCopyDeck(playerTwo.get(newGame.getPlayerTwoDeckIdx()));

        // Initialize players' mana, hands, and draw the first card
        setInitialMana(utils);
        LinkedList<Deck> playerOneDeckInHand = new LinkedList<>();
        LinkedList<Deck> playerTwoDeckInHand = new LinkedList<>();
        shuffleAndDrawFirstCard(playerOneDeck, playerOneDeckInHand, newGame);
        shuffleAndDrawFirstCard(playerTwoDeck, playerTwoDeckInHand, newGame);

        // Initialize the playing table and heroes
        ArrayList<LinkedList<Minion>> playingTable = initializePlayingTable();
        Hero playerOneHero = new Hero(newGame.getPlayerOneHero());
        Hero playerTwoHero = new Hero(newGame.getPlayerTwoHero());

        // Set the initial turn and number of rounds
        setInitialTurnAndRounds(utils, newGame);

        // Execute each command in the command list
        for (int j = 0; j < commandList.size(); j++) {
            ActionsInput command = commandList.get(j);
            switch (command.getCommand()) {
                case ("getCardsInHand") -> GameInfo.getCardsInHand(output, command,
                        playerOneDeckInHand, playerTwoDeckInHand);
                case ("getPlayerDeck") -> GameInfo.getPlayerDeck(output, command,
                        playerOneDeck, playerTwoDeck);
                case ("getCardsOnTable") -> GameInfo.getCardsOnTable(output, playingTable);
                case ("getPlayerTurn") -> GameInfo.getPlayerTurn(output, utils.getTurn());
                case ("getPlayerHero") -> GameInfo.getPlayerHero(output, command, playerOneHero,
                        playerTwoHero);
                case ("getCardAtPosition") -> GameInfo.getCardsAtPosition(output, command,
                        playingTable);
                case ("getPlayerMana") -> GameInfo.getPlayerMana(output, command,
                        utils.getPlayerOneMana(), utils.getPlayerTwoMana());
                case ("getEnvironmentCardsInHand") -> GameInfo.getEnvironmentCardsInHand(output,
                        command, playerOneDeckInHand, playerTwoDeckInHand);
                case ("getFrozenCardsOnTable") -> GameInfo.getFrozenCardsOnTable(output,
                        playingTable);
                case ("getTotalGamesPlayed") -> Statistics.getTotalGamesPlayed(output, gameIndex);
                case ("getPlayerOneWins") -> Statistics.getPlayerOneWins(output,
                        utils.getPlayerOneWins());
                case ("getPlayerTwoWins") -> Statistics.getPlayerTwoWins(output,
                        utils.getPlayerTwoWins());
                case ("endPlayerTurn") -> GameActionHandler.endPlayerTurn(playerOneDeck,
                        playerTwoDeck, playerOneDeckInHand, playerTwoDeckInHand,
                        playingTable, playerOneHero, playerTwoHero, newGame, utils);
                case ("placeCard") -> GameActionHandler.placeCard(output, command,
                        utils.getTurn(), playingTable, playerOneDeckInHand,
                        playerTwoDeckInHand, utils);
                case ("cardUsesAttack") -> GameCardPlay.cardUsesAttack(output, command,
                        playingTable, utils.getTurn());
                case ("cardUsesAbility") -> GameCardPlay.cardUsesAbility(output, command,
                        playingTable, utils.getTurn());
                case ("useAttackHero") -> GameCardPlay.cardAttackHero(output, command,
                        playingTable, utils.getTurn(), utils, playerOneHero, playerTwoHero);
                case ("useHeroAbility") -> GameCardPlay.useHeroAbility(output, command,
                        playingTable, utils.getTurn(),
                        utils, playerOneHero, playerTwoHero);
                case ("useEnvironmentCard") -> EnvironmentCardHandler.useEnvironmentCard(output,
                        command, playerOneDeckInHand, playerTwoDeckInHand, playingTable,
                        utils, utils.getTurn());
                default -> System.out.println("Invalid command received.");
            }
        }
    }

    /**
     * Sets the initial mana for both players at the beginning of the game.
     *
     * @param utils Utility object containing game state information.
     */
    private void setInitialMana(final Utils utils) {
        utils.setPlayerOneMana(1);
        utils.setPlayerTwoMana(1);
    }

    /**
     * Retrieves the current game data for the specified game index.
     *
     * @param gameIndex The index of the current game session.
     * @return The StartGameInput object containing game data for the specified game.
     */
    private StartGameInput getCurrentGame(final int gameIndex) {
        return inputData.getGames().get(gameIndex).getStartGame();
    }

    /**
     * Retrieves the list of commands to be executed for the specified game index.
     *
     * @param gameIndex The index of the current game session.
     * @return A list of ActionsInput commands for the specified game.
     */
    private ArrayList<ActionsInput> getCommandList(final int gameIndex) {
        return inputData.getGames().get(gameIndex).getActions();
    }

    /**
     * Shuffles the player's deck and draws the first card to add to the player's hand.
     * This ensures that each player starts with one card in their hand.
     *
     * @param playerDeck The player's deck to shuffle and draw from.
     * @param playerHand The player's hand to add the drawn card.
     * @param newGame The current game data containing the shuffle seed for randomness.
     */
    private void shuffleAndDrawFirstCard(final LinkedList<Deck> playerDeck,
                                         final LinkedList<Deck> playerHand,
                                         final StartGameInput newGame) {
        Collections.shuffle(playerDeck, new Random(newGame.getShuffleSeed()));
        playerHand.addLast(playerDeck.removeFirst());
    }

    /**
     * Sets the initial turn and number of rounds for the game session.
     * This establishes which player goes first and initializes the round count.
     *
     * @param utils Utility object containing game state information.
     * @param newGame The StartGameInput object containing game data, including the starting player.
     */
    private void setInitialTurnAndRounds(final Utils utils,
                                         final StartGameInput newGame) {
        utils.setTurn(newGame.getStartingPlayer());
        utils.setNumberOfRounds(1);
    }

    /**
     * Initializes the playing table with empty rows to represent each player's minion rows.
     * Each row can hold multiple minions during the game.
     *
     * @return An ArrayList containing 4 rows of LinkedList to store Minion objects.
     */
    private ArrayList<LinkedList<Minion>> initializePlayingTable() {
        ArrayList<LinkedList<Minion>> playingTable = new ArrayList<>(INITIAL_CAPACITY);

        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            playingTable.add(new LinkedList<>());
        }

        return playingTable;
    }

    /**
     * Creates a deep copy of the given deck to ensure that any modifications made during
     * one game session do not affect the deck in other sessions.
     *
     * @param originalDeck The deck to be deep copied.
     * @return A deep copy of the original deck containing new instances of each card.
     */
    private LinkedList<Deck> deepCopyDeck(final LinkedList<Deck> originalDeck) {
        LinkedList<Deck> copiedDeck = new LinkedList<>();
        for (Deck card : originalDeck) {
            if (card.getName().equals("Winterfell") || card.getName().equals("Firestorm")
                    || card.getName().equals("Heart Hound")) {
                // Environment card requires a deep copy of the environment object
                copiedDeck.add(new Environment((Environment) card));
            } else {
                // Minion card requires a deep copy of the minion object
                copiedDeck.add(new Minion((Minion) card));
            }
        }
        return copiedDeck;
    }
}
