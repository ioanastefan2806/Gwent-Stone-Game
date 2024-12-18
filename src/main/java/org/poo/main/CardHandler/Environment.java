package org.poo.main.CardHandler;
import org.poo.fileio.CardInput;

// extends for Environment cards
public class Environment extends Deck {

    /**
     * Constructor to create an Environment card from CardInput.
     *
     * @param cardInput the CardInput object containing the card details
     */
    public Environment(final CardInput cardInput) {
        super(cardInput.getMana(), cardInput.getDescription(), cardInput.getColors(),
                cardInput.getName());
    }

    public Environment(Environment card) {
        super();
    }
}
