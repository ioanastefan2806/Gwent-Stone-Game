package org.poo.main.CardHandler;


import org.poo.fileio.CardInput;

public class Hero extends Deck {

    private int health;

    /**
     * Creates a new Hero object by copying an existing Hero instance.
     *
     * @param hero the Hero instance to be copied
     */
    public Hero(final Hero hero) {
        super(hero.getMana(), hero.getDescription(), hero.getColors(), hero.getName());
        this.setHealth(hero.health);
    }

    /**
     * Creates a new Hero object using input data from CardInput.
     *
     * @param cardInput the CardInput object containing data to initialize the Hero
     */
    public Hero(final CardInput cardInput) {
        super(cardInput.getMana(), cardInput.getDescription(), cardInput.getColors(),
                cardInput.getName());
        // Set initial health to 30
        this.health = INITIAL_HEALTH;
    }

    /**
     * Sets the health points of the Hero.
     *
     * @param health the new health value
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     * Gets the health points of the Hero.
     *
     * @return the current health value
     */
    public int getHealth() {
        return health;
    }


    /**
     * The initial health value for all Hero cards.
     */
    private static final int INITIAL_HEALTH = 30;
}
