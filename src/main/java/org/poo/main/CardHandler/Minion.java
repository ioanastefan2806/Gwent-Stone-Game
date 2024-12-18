package org.poo.main.CardHandler;

import org.poo.fileio.CardInput;

public class Minion extends Deck {
    /**
     * Constructor for creating a Minion object from a CardInput.
     *
     * @param cardInput the CardInput object containing minion details
     */
    public Minion(final CardInput cardInput) {
        super(cardInput.getMana(), cardInput.getDescription(), cardInput.getColors(),
                cardInput.getName());
        this.health = cardInput.getHealth();
        this.attackDamage = cardInput.getAttackDamage();
    }

    /**
     * Copy constructor for creating a new Minion object from an existing one.
     *
     * @param minion the existing Minion object to copy
     */
    public Minion(final Minion minion) {
        super(minion.getMana(), minion.getDescription(), minion.getColors(), minion.getName());
        this.health = minion.getHealth();
        this.attackDamage = minion.getAttackDamage();
    }


    /**
     * Gets the health of the minion.
     *
     * @return the health of the minion
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the health of the minion.
     *
     * @param health the new health value to set
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     * Gets the attack damage of the minion.
     *
     * @return the attack damage of the minion
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     * Sets the attack damage of the minion.
     *
     * @param attackDamage the new attack damage value to set
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }



    private int health;
    private int attackDamage;
}
