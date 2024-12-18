package org.poo.main.GameAction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.GameHandler.ErrorHandler;
import org.poo.main.Utils;
import org.poo.fileio.ActionsInput;
import org.poo.main.CardHandler.Hero;
import org.poo.main.CardHandler.Minion;

import java.util.ArrayList;
import java.util.LinkedList;

public final class GameCardPlay {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static final int MAX_SIZE = 5;
    public static final int MAGIC = 3;

    private GameCardPlay() {
    }

    /**
     * Executes a card attack action based on the provided command and game state.
     *
     * @param output       The output ArrayNode to store the result of the action.
     * @param command      The action input containing the details of the command.
     * @param playingTable The current state of the playing table.
     * @param turn         The current player's turn (1 or 2).
     */
    public static void cardUsesAttack(final ArrayNode output,
                                      final ActionsInput command,
                                      final ArrayList<LinkedList<Minion>> playingTable,
                                      final int turn) {
        // Get coordinates
        int attackerRow = command.getCardAttacker().getX();
        int attackerColumn = command.getCardAttacker().getY();
        int attackedRow = command.getCardAttacked().getX();
        int attackedColumn = command.getCardAttacked().getY();

        if (playingTable.get(attackerRow).size() <= attackerColumn
                || playingTable.get(attackedRow).size() <= attackedColumn) {
            return;
        }

        Minion cardAttacker = playingTable.get(attackerRow).get(attackerColumn);
        Minion cardAttacked = playingTable.get(attackedRow).get(attackedColumn);

        if (!validateAttackConditions(output, command, playingTable,
                turn, cardAttacker, cardAttacked, attackedRow)) {
            return;
        }

        executeAttack(playingTable, cardAttacker, cardAttacked, attackedRow, attackedColumn);
    }

    /**
     * Validates all the conditions required for a valid attack action.
     *
     * @param output       The output ArrayNode to store the result of the action.
     * @param command      The action input containing the details of the command.
     * @param playingTable The current state of the playing table.
     * @param turn         The current player's turn (1 or 2).
     * @param cardAttacker The attacking card.
     * @param cardAttacked The attacked card.
     * @param attackedRow The row of the attacked card.
     * @return true if the attack conditions are met, false otherwise.
     */
    private static boolean validateAttackConditions(final ArrayNode output,
                                                    final ActionsInput command,
                                                    final ArrayList<LinkedList<Minion>> playingTable,
                                                    final int turn,
                                                    final Minion cardAttacker,
                                                    final Minion cardAttacked,
                                                    final int attackedRow) {
        // Check for invalid cases
        switch (turn) {
            case 1:
                if (attackedRow == 2 || attackedRow == MAGIC) {
                    ErrorHandler.useAttackNotEnemyCard(output, command);
                    return false;
                }
                break;
            case 2:
                if (attackedRow == 0 || attackedRow == 1) {
                    ErrorHandler.useAttackNotEnemyCard(output, command);
                    return false;
                }
                break;
            default:
                break;
        }

        if (cardAttacker.getAttackUsed() == 1) {
            ErrorHandler.useAttackAlreadyAttacked(output, command);
            return false;
        }

        if (cardAttacker.getIsFrozen() == 1) {
            ErrorHandler.useAttackIsFrozen(output, command);
            return false;
        }

        int isTank = Utils.isTank(playingTable, turn);
        if (isTank == 1 && !cardAttacked.getName().equals("Goliath")
                && !cardAttacked.getName().equals("Warden")) {
            ErrorHandler.useAttackTank(output, command);
            return false;
        }

        return true;
    }

    /**
     * Executes the attack action between the attacker and the attacked card.
     *
     * @param playingTable The current state of the playing table.
     * @param cardAttacker The attacking card.
     * @param cardAttacked The attacked card.
     * @param attackedRow The row of the attacked card.
     * @param attackedColumn The column of the attacked card.
     */
    private static void executeAttack(final ArrayList<LinkedList<Minion>> playingTable,
                                      final Minion cardAttacker,
                                      final Minion cardAttacked,
                                      final int attackedRow,
                                      final int attackedColumn) {
        cardAttacker.setAttackUsed(1);
        if (cardAttacked.getHealth() <= cardAttacker.getAttackDamage()) {
            // Card dies
            playingTable.get(attackedRow).remove(attackedColumn);
        } else {
            cardAttacked.setHealth(cardAttacked.getHealth() - cardAttacker.getAttackDamage());
        }
    }

    /**
     * Handles the usage of a card's ability in the game.
     * This method processes an action where
     * a card uses its special ability to affect another card on the playing table.
     * The ability may vary depending on the type of the card, and this method ensures that
     * the action is valid before applying the effects.
     *
     * @param output       the output to store any error messages or action logs
     * @param command      the action command containing the
     *                     attacker and target card details
     * @param playingTable the current state of the playing table
     * @param currentTurn  the current player's turn (1 or 2)
     */
    public static void cardUsesAbility(final ArrayNode output,
                                       final ActionsInput command,
                                       final ArrayList<LinkedList<Minion>> playingTable,
                                       final int currentTurn) {
        // Get attacker and attacked card coordinates
        int attackerX = command.getCardAttacker().getX();
        int attackerY = command.getCardAttacker().getY();
        int targetX = command.getCardAttacked().getX();
        int targetY = command.getCardAttacked().getY();

        // Retrieve attacker and attacked cards
        Minion attackerCard = getCardFromTable(playingTable, attackerX, attackerY);
        Minion targetCard = getCardFromTable(playingTable, targetX, targetY);

        // If either card is invalid, stop execution
        if (attackerCard == null || targetCard == null) {
            return;
        }

        // Handle invalid cases like frozen or already used ability
        if (isCardFrozen(output, command, attackerCard)
                || hasCardAlreadyAttacked(output, command, attackerCard)) {
            return;
        }

        // Determine if the attack is allowed based on card position and turn
        boolean isFriendlyCard = isFriendlyTarget(currentTurn, targetX);

        // Handle specific card abilities based on attacker card name
        switch (attackerCard.getName()) {
            case "Disciple":
                handleDiscipleAbility(output, command, attackerCard,
                        targetCard, isFriendlyCard);
                break;
            case "The Ripper":
            case "Miraj":
            case "The Cursed One":
                handleEnemyCardAbilities(output, command, attackerCard, targetCard,
                        isFriendlyCard, playingTable, targetX, targetY, currentTurn);
                break;
            default:
                break;
        }
    }

    /**
     * Retrieves a card from the playing table based on given coordinates.
     *
     * @param playingTable the current state of the playing table
     * @param x            the row index of the card
     * @param y            the column index of the card
     * @return the Minion card at the given coordinates, or null if not found
     */
    private static Minion getCardFromTable(
            final ArrayList<LinkedList<Minion>> playingTable,
                                           final int x,
                                           final int y) {
        if (x < playingTable.size() && y < playingTable.get(x).size()) {
            return playingTable.get(x).get(y);
        }
        return null;
    }

    /**
     * Checks if the card is frozen and logs an error if true.
     *
     * @param output       the output to store any error messages
     * @param command      the action command containing the attacker details
     * @param attackerCard the card that is attempting to use an ability
     * @return true if the card is frozen, false otherwise
     */
    private static boolean isCardFrozen(final ArrayNode output,
                                        final ActionsInput command,
                                        final Minion attackerCard) {
        if (attackerCard.getIsFrozen() == 1) {
            ErrorHandler.useAbilityIsFrozen(output, command);
            return true;
        }
        return false;
    }

    /**
     * Checks if the card has already attacked this
     * turn and logs an error if true.
     *
     * @param output       the output to store any error messages
     * @param command      the action command containing the attacker details
     * @param attackerCard the card that is attempting to use an ability
     * @return true if the card has already attacked, false otherwise
     */
    private static boolean hasCardAlreadyAttacked(final ArrayNode output,
                                                  final ActionsInput command,
                                                  final Minion attackerCard) {
        if (attackerCard.getAttackUsed() == 1) {
            ErrorHandler.useAbilityAlreadyAttacked(output, command);
            return true;
        }
        return false;
    }

    /**
     * Determines if the target card is a friendly card
     * based on the current turn.
     *
     * @param currentTurn the current player's turn (1 or 2)
     * @param targetX     the row index of the target card
     * @return true if the target card is a friendly card, false otherwise
     */
    private static boolean isFriendlyTarget(final int currentTurn,
                                            final int targetX) {
        return (currentTurn == 1 && (targetX == 2 || targetX == 3))
                || (currentTurn == 2 && (targetX == 0
                || targetX == 1));
    }

    /**
     * Handles the ability for the "Disciple" card.
     *
     * @param output        the output to store any error messages or action logs
     * @param command       the action command containing
     *                      the attacker and target card details
     * @param attackerCard  the card using the ability
     * @param targetCard    the target card affected by the ability
     * @param isFriendlyCard whether the target card is friendly
     */
    private static void handleDiscipleAbility(final ArrayNode output,
                                              final ActionsInput command,
                                              final Minion attackerCard,
                                              final Minion targetCard,
                                              final boolean isFriendlyCard) {
        if (isFriendlyCard) {
            // Use ability to heal the target card
            attackerCard.setAttackUsed(1);
            targetCard.setHealth(targetCard.getHealth() + 2);
        } else {
            ErrorHandler.useAbilityNotMyCard(output, command);
        }
    }

    /**
     * Handles abilities for "The Ripper", "Miraj", and "The Cursed One" cards.
     *
     * @param output        the output to store any error messages or action logs
     * @param command       the action command containing the attacker and target card details
     * @param attackerCard  the card using the ability
     * @param targetCard    the target card affected by the ability
     * @param isFriendlyCard whether the target card is friendly
     * @param playingTable  the current state of the playing table
     * @param targetX       the row index of the target card
     * @param targetY       the column index of the target card
     * @param currentTurn   the current player's turn (1 or 2)
     */
    private static void handleEnemyCardAbilities(final ArrayNode output,
                                                 final ActionsInput command,
                                                 final Minion attackerCard,
                                                 final Minion targetCard,
                                                 final boolean isFriendlyCard,
                                                 final ArrayList<LinkedList<Minion>> playingTable,
                                                 final int targetX,
                                                 final int targetY,
                                                 final int currentTurn) {
        if (isFriendlyCard) {
            ErrorHandler.useAbilityNotEnemyCard(output, command);
            return;
        }

        // Check if there are any "Tank" cards on the enemy side
        int isTankPresent = Utils.isTank(playingTable, currentTurn);
        if (isTankPresent == 1 && !targetCard.getName().equals("Goliath")
                &&
                !targetCard.getName().equals("Warden")) {
            ErrorHandler.useAbilityTank(output, command);
            return;
        }

        // Use ability and mark the attacker as having used its attack
        attackerCard.setAttackUsed(1);
        switch (attackerCard.getName()) {
            case "The Ripper":
                targetCard.setAttackDamage(Math.max(0, targetCard.getAttackDamage() - 2));
                break;
            case "Miraj":
                int healthSwap = targetCard.getHealth();
                targetCard.setHealth(attackerCard.getHealth());
                attackerCard.setHealth(healthSwap);
                break;
            case "The Cursed One":
                int tempHealth = targetCard.getHealth();
                targetCard.setHealth(targetCard.getAttackDamage());
                targetCard.setAttackDamage(tempHealth);
                if (targetCard.getHealth() <= 0) {
                    playingTable.get(targetX).remove(targetY);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Handles the action of a card attacking the enemy hero. It checks various conditions such as
     * whether the card is frozen, has already attacked, or if a "Tank" card must be attacked first.
     * If the attack is valid, it proceeds with the attack on the enemy hero.
     *
     * @param output       the output to store action results
     * @param command      the attack command containing the attacker card's coordinates
     * @param playingTable the current state of the playing table
     * @param turn         the current player's turn (1 or 2)
     * @param utils        utility class for game state updates
     * @param playerOneHero the hero of player one
     * @param playerTwoHero the hero of player two
     */
    public static void cardAttackHero(final ArrayNode output,
                                      final ActionsInput command,
                                      final ArrayList<LinkedList<Minion>> playingTable,
                                      final int turn, final Utils utils,
                                      final Hero playerOneHero,
                                      final Hero playerTwoHero) {
        // Retrieve the attacking card based on the command
        Minion attackingCard = getCardAttacker(command, playingTable);

        // If the attacking card is not present on the table, exit
        if (attackingCard == null) {
            return;
        }

        // Check for invalid attack cases (e.g., card is frozen, has already attacked, etc.)
        int invalidAttackCase = getInvalidAttackCase(attackingCard, playingTable, turn);

        // Handle invalid attack cases
        if (invalidAttackCase != 0) {
            handleInvalidAttack(output, command, invalidAttackCase);
            return;
        }

        // Perform the attack on the enemy hero if all conditions are met
        performAttackOnHero(attackingCard, output, utils, turn, playerOneHero, playerTwoHero);
    }

    /**
     * Retrieves the card that is attempting to attack the enemy hero based on the command.
     *
     * @param command      the attack command containing the attacker card's coordinates
     * @param playingTable the current state of the playing table
     * @return the attacking card if present, otherwise null
     */
    private static Minion getCardAttacker(final ActionsInput command,
                                          final ArrayList<LinkedList<Minion>> playingTable) {
        // Get the coordinates of the attacking card
        int attackerRow = command.getCardAttacker().getX();
        int attackerColumn = command.getCardAttacker().getY();

        // Check if the attacking card is present on the table
        if (playingTable.get(attackerRow).size() > attackerColumn) {
            return playingTable.get(attackerRow).get(attackerColumn);
        } else {
            return null;
        }
    }


    /**
     * Determines if the attack is invalid.
     *
     * @param cardAttacker the attacking card
     * @param playingTable the current state of the playing table
     * @param turn         the current player's turn
     * @return an integer indicating the type of invalid case (0 if valid, 1 if frozen,
     * 2 if already attacked, MAGIC if a "Tank" must be attacked first)
     */
    private static int getInvalidAttackCase(final Minion cardAttacker,
                                            final ArrayList<LinkedList<Minion>> playingTable,
                                            final int turn) {
        if (cardAttacker.getIsFrozen() == 1) {
            return 1;
        } else if (cardAttacker.getAttackUsed() == 1) {
            return 2;
        } else if (Utils.isTank(playingTable, turn) == 1) {
            return MAGIC;
        }
        return 0;
    }

    /**
     * Handles invalid attack cases by generating appropriate error messages.
     *
     * @param output      the output to store action results
     * @param command     the attack command containing the attacker card's coordinates
     * @param invalidCase the type of invalid case
     */
    private static void handleInvalidAttack(final ArrayNode output,
                                            final ActionsInput command,
                                            final int invalidCase) {
        switch (invalidCase) {
            case 1:
                ErrorHandler.attackHeroIsFrozen(output, command);
                break;
            case 2:
                ErrorHandler.attackHeroAlreadyAttacked(output, command);
                break;
            case MAGIC:
                ErrorHandler.attackHeroTank(output, command);
                break;
            default:
                break;
        }
    }

    /**
     * Performs the attack on the enemy hero and updates the game state accordingly.
     *
     * @param cardAttacker  the attacking card
     * @param output        the output to store action results
     * @param utils         utility class for game state updates
     * @param turn          the current player's turn (1 or 2)
     * @param playerOneHero the hero of player one
     * @param playerTwoHero the hero of player two
     */
    private static void performAttackOnHero(final Minion cardAttacker,
                                            final ArrayNode output,
                                            final Utils utils,
                                            final int turn,
                                            final Hero playerOneHero,
                                            final Hero playerTwoHero) {
        cardAttacker.setAttackUsed(1);
        Hero targetHero = (turn == 1) ? playerTwoHero : playerOneHero;

        if (targetHero.getHealth() <= cardAttacker.getAttackDamage()) {
            // Hero is defeated, end game
            endGame(output, utils, turn);
        } else {
            // Reduce hero's health by attack damage
            targetHero.setHealth(targetHero.getHealth() - cardAttacker.getAttackDamage());
        }
    }

    /**
     * Ends the game by declaring the winner and adding the result to the output.
     *
     * @param output the output to store action results
     * @param utils  utility class for game state updates
     * @param turn   the current player's turn (1 or 2)
     */
    private static void endGame(final ArrayNode output,
                                final Utils utils,
                                final int turn) {
        if (turn == 1) {
            utils.setPlayerOneWins(utils.getPlayerOneWins() + 1);
            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("gameEnded", "Player one killed the enemy hero.");
            output.addPOJO(outputNode);
        } else {
            utils.setPlayerTwoWins(utils.getPlayerTwoWins() + 1);
            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("gameEnded", "Player two killed the enemy hero.");
            output.addPOJO(outputNode);
        }
    }

    /**
     * Uses the hero ability based on the provided command and game state.
     *
     * @param output        The output ArrayNode to store the result of the action.
     * @param command       The action input containing the details of the command.
     * @param playingTable  The current state of the playing table.
     * @param turn          The current player's turn (1 or 2).
     * @param utils         Utility class containing game-related data (e.g., mana).
     * @param playerOneHero The hero of player one.
     * @param playerTwoHero The hero of player two.
     */
    public static void useHeroAbility(final ArrayNode output,
                                      final ActionsInput command,
                                      final ArrayList<LinkedList<Minion>> playingTable,
                                      final int turn, final Utils utils,
                                      final Hero playerOneHero,
                                      final Hero playerTwoHero) {
        Hero currentHero = getCurrentHero(turn, playerOneHero, playerTwoHero);
        int currentMana = getCurrentMana(turn, utils);
        int affectedRow = command.getAffectedRow();

        if (!isValidHeroAbilityUsage(output, currentHero,
                currentMana, affectedRow, turn)) {
            return;
        }

        deductManaAndSetAttackUsed(turn, currentMana, currentHero, utils);
        executeHeroAbility(currentHero, playingTable, affectedRow);
    }

    /**
     * Retrieves the current hero based on the player's turn.
     *
     * @param turn          The current player's turn (1 or 2).
     * @param playerOneHero The hero of player one.
     * @param playerTwoHero The hero of player two.
     * @return The current hero.
     */
    private static Hero getCurrentHero(final int turn,
                                       final Hero playerOneHero,
                                       final Hero playerTwoHero) {
        return (turn == 1) ? playerOneHero : playerTwoHero;
    }

    /**
     * Retrieves the current mana of the player based on the turn.
     *
     * @param turn  The current player's turn (1 or 2).
     * @param utils Utility class containing game-related data.
     * @return The current mana of the player.
     */
    private static int getCurrentMana(final int turn,
                                      final Utils utils) {
        return (turn == 1) ? utils.getPlayerOneMana() : utils.getPlayerTwoMana();
    }

    /**
     * Validates whether the hero ability usage is allowed.
     *
     * @param output       The output ArrayNode to store the result of the action.
     * @param currentHero  The current hero.
     * @param currentMana  The current mana of the player.
     * @param affectedRow  The row affected by the hero's ability.
     * @param turn         The current player's turn (1 or 2).
     * @return True if the hero ability usage is valid, false otherwise.
     */
    private static boolean isValidHeroAbilityUsage(final ArrayNode output,
                                                   final Hero currentHero,
                                                   final int currentMana,
                                                   final int affectedRow,
                                                   final int turn) {
        if (currentMana < currentHero.getMana()) {
            ErrorHandler.heroAbilityNotEnoughMana(output, affectedRow);
            return false;
        }
        if (currentHero.getAttackUsed() == 1) {
            ErrorHandler.heroAbilityAlreadyAttacked(output, affectedRow);
            return false;
        }

        switch (currentHero.getName()) {
            case "Lord Royce":
            case "Empress Thorina":
                if (isInvalidEnemyRow(turn, affectedRow)) {
                    ErrorHandler.heroAbilityNotEnemyRow(output, affectedRow);
                    return false;
                }
                break;

            case "General Kocioraw":
            case "King Mudface":
                if (isInvalidMyRow(turn, affectedRow)) {
                    ErrorHandler.heroAbilityNotMyRow(output, affectedRow);
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Checks if the affected row is invalid for enemy targeting abilities.
     *
     * @param turn        The current player's turn (1 or 2).
     * @param affectedRow The row affected by the hero's ability.
     * @return True if the affected row is invalid for enemy targeting, false otherwise.
     */
    private static boolean isInvalidEnemyRow(final int turn,
                                             final int affectedRow) {
        return (turn == 1 && (affectedRow == 2 || affectedRow == MAGIC))
                || (turn == 2 && (affectedRow == 0 || affectedRow == 1));
    }

    /**
     * Checks if the affected row is invalid for self-targeting abilities.
     *
     * @param turn        The current player's turn (1 or 2).
     * @param affectedRow The row affected by the hero's ability.
     * @return True if the affected row is invalid for self-targeting, false otherwise.
     */
    private static boolean isInvalidMyRow(final int turn,
                                          final int affectedRow) {
        return (turn == 1 && (affectedRow == 0 || affectedRow == 1))
                || (turn == 2 && (affectedRow == 2 || affectedRow == MAGIC));
    }

    /**
     * Deducts mana from the player and marks the hero's attack as used.
     *
     * @param turn         The current player's turn (1 or 2).
     * @param currentMana  The current mana of the player.
     * @param currentHero  The current hero.
     * @param utils        Utility class containing game-related data.
     */
    private static void deductManaAndSetAttackUsed(final int turn,
                                                   final int currentMana,
                                                   final Hero currentHero,
                                                   final Utils utils) {
        if (turn == 1) {
            utils.setPlayerOneMana(currentMana - currentHero.getMana());
            currentHero.setAttackUsed(1);
        } else {
            utils.setPlayerTwoMana(currentMana - currentHero.getMana());
            currentHero.setAttackUsed(1);
        }
    }

    /**
     * Executes the hero ability on the affected row.
     *
     * @param currentHero  The current hero.
     * @param playingTable The current state of the playing table.
     * @param affectedRow  The row affected by the hero's ability.
     */
    private static void executeHeroAbility(final Hero currentHero,
                                           final ArrayList<LinkedList<Minion>> playingTable,
                                           final int affectedRow) {
        switch (currentHero.getName()) {
            case "Lord Royce":
                for (Minion minion : playingTable.get(affectedRow)) {
                    minion.setIsFrozen(1);
                }
                break;

            case "Empress Thorina":
                removeHighestHealthMinion(playingTable, affectedRow);
                break;

            case "General Kocioraw":
                for (Minion minion : playingTable.get(affectedRow)) {
                    minion.setAttackDamage(minion.getAttackDamage() + 1);
                }
                break;

            case "King Mudface":
                for (Minion minion : playingTable.get(affectedRow)) {
                    minion.setHealth(minion.getHealth() + 1);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Removes the minion with the highest health from the specified row.
     *
     * @param playingTable The current state of the playing table.
     * @param affectedRow  The row affected by the hero's ability.
     */
    private static void removeHighestHealthMinion(final ArrayList<LinkedList<Minion>> playingTable,
                                                  final int affectedRow) {
        int maxHealth = -1;
        int maxHealthIdx = -1;
        for (int k = 0; k < playingTable.get(affectedRow).size(); k++) {
            Minion minion = playingTable.get(affectedRow).get(k);
            if (minion.getHealth() >= maxHealth) {
                maxHealth = minion.getHealth();
                maxHealthIdx = k;
            }
        }
        if (maxHealthIdx != -1) {
            playingTable.get(affectedRow).remove(maxHealthIdx);
        }
    }
}
