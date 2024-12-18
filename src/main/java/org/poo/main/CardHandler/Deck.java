package org.poo.main.CardHandler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.poo.fileio.CardInput;
import org.poo.fileio.DecksInput;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Deck {

    public static final int INITIAL_HEALTH = 30;
    private int mana;
    private String description;
    private ArrayList<String> colors;

    private String name;

    @JsonIgnore
    private int isFrozen;

    @JsonIgnore
    private int attackUsed;

    /**
     * Constructor to initialize a Deck object with specific attributes.
     *
     * @param mana        The mana cost of the deck.
     * @param description The description of the deck.
     * @param colors      A list of colors associated with the deck.
     * @param name        The name of the deck.
     */
    public Deck(final int mana,
                final String description,
                final ArrayList<String> colors,
                final String name) {
        this.mana = mana;
        this.description = description;
        this.colors = new ArrayList<>(colors);
        this.name = name;
        this.isFrozen = 0;
        this.attackUsed = 0;
    }

    public Deck() {

    }

    /**
     * Gets the number of times the deck has used an attack.
     *
     * @return The number of times an attack has been used.
     */
    public int getAttackUsed() {
        return attackUsed;
    }

    /**
     * Sets the number of times the deck has used an attack.
     *
     * @param attackUsed The number of times an attack has been used.
     */
    public void setAttackUsed(final int attackUsed) {
        this.attackUsed = attackUsed;
    }

    /**
     * Gets the mana cost of the deck.
     *
     * @return The mana cost.
     */
    public int getMana() {
        return mana;
    }

    /**
     * Sets the mana cost of the deck.
     *
     * @param mana The mana cost to set.
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * Gets the frozen state of the deck.
     *
     * @return The frozen state (0 for not frozen, 1 for frozen).
     */
    public int getIsFrozen() {
        return isFrozen;
    }

    /**
     * Sets the frozen state of the deck.
     *
     * @param isFrozen The frozen state to set (0 for not frozen, 1 for frozen).
     */
    public void setIsFrozen(final int isFrozen) {
        this.isFrozen = isFrozen;
    }

    /**
     * Gets the description of the deck.
     *
     * @return The description of the deck.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the deck.
     *
     * @param description The description to set.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the list of colors associated with the deck.
     *
     * @return The list of colors.
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     * Sets the list of colors associated with the deck.
     *
     * @param colors The list of colors to set.
     */
    public void setColors(final ArrayList<String> colors) {
        this.colors = new ArrayList<>(colors);
    }

    /**
     * Gets the name of the deck.
     *
     * @return The name of the deck.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the deck.
     *
     * @param name The name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Creates a list of decks from the input data, assigning appropriate card types.
     *
     * @param decksInput The input data containing deck information.
     * @return A linked list of decks with appropriate card types.
     */
    public static LinkedList<LinkedList<Deck>> setCardType(final DecksInput decksInput) {
        LinkedList<LinkedList<Deck>> deckOfDecks = new LinkedList<>();

        for (List<CardInput> cardInputsDeck : decksInput.getDecks()) {
            LinkedList<Deck> deck = new LinkedList<>();

            for (CardInput cardInput : cardInputsDeck) {
                String name = cardInput.getName();

                if (isMinion(name)) {
                    deck.addLast(new Minion(cardInput));
                } else if (isEnvironment(name)) {
                    deck.addLast(new Environment(cardInput));
                }
            }

            deckOfDecks.addLast(deck);
        }
        return deckOfDecks;
    }

    /**
     * Checks if a card is a minion based on its name.
     *
     * @param name The name of the card.
     * @return True if the card is a minion, false otherwise.
     */
    private static boolean isMinion(final String name) {
        return switch (name) {
            case "The Ripper",
                    "Miraj",
                    "The Cursed One",
                    "Disciple",
                    "Sentinel",
                    "Berserker",
                    "Goliath",
                    "Warden" -> true;
            default -> false;
        };
    }

    /**
     * Checks if a card is an environment card based on its name.
     *
     * @param name The name of the card.
     * @return True if the card is an environment card, false otherwise.
     */
    private static boolean isEnvironment(final String name) {
        return switch (name) {
            case "Firestorm", "Winterfell", "Heart Hound" -> true;
            default -> false;
        };
    }
}
