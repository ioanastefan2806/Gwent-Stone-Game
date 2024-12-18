package org.poo.main.GameHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.ActionsInput;

public final class ErrorHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ErrorHandler() {
        // Private constructor to prevent instantiation
    }

    private static void addErrorNode(final ArrayNode output,
                                     final String command,
                                     final String error,
                                     final int handIdx,
                                     final Integer affectedRow,
                                     final ActionsInput commandInput) {
        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();
        outputNode.put("command", command);
        outputNode.put("error", error);
        if (handIdx != -1) {
            outputNode.put("handIdx", handIdx);
        }
        if (affectedRow != null) {
            outputNode.put("affectedRow", affectedRow);
        }
        if (commandInput != null) {
            if (commandInput.getCardAttacker() != null) {
                outputNode.putPOJO("cardAttacker", commandInput.getCardAttacker());
            }
            if (commandInput.getCardAttacked() != null) {
                outputNode.putPOJO("cardAttacked", commandInput.getCardAttacked());
            }
        }
        output.addPOJO(outputNode);
    }

    /**
     * Handles the error when attempting to place an environment card on the table.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param handIdx The index of the card in hand.
     */
    public static void placeCardEnvironmentCard(final ArrayNode output,
                                                final int handIdx) {
        addErrorNode(output, "placeCard",
                "Cannot place environment card on table.",
                handIdx, null, null);
    }

    /**
     * Handles the error when there is not enough mana to place a card on the table.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param handIdx The index of the card in hand.
     */
    public static void placeCardNotEnoughMana(final ArrayNode output,
                                              final int handIdx) {
        addErrorNode(output, "placeCard",
                "Not enough mana to place card on table.",
                handIdx, null, null);
    }

    /**
     * Handles the error when there is no space available on the table to place a card.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param handIdx The index of the card in hand.
     */
    public static void placeCardNotEnoughSpace(final ArrayNode output,
                                               final int handIdx) {
        addErrorNode(output, "placeCard",
                "Cannot place card on table since row is full.",
                handIdx, null, null);
    }

    /**
     * Handles the error when an attack is attempted on a card that does not belong to the enemy.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker and attacked card details.
     */
    public static void useAttackNotEnemyCard(final ArrayNode output,
                                             final ActionsInput command) {
        addErrorNode(output, "cardUsesAttack",
                "Attacked card does not belong to the enemy.",
                -1, null, command);
    }

    /**
     * Handles the error when an attacker card has already attacked during the turn.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker and attacked card details.
     */
    public static void useAttackAlreadyAttacked(final ArrayNode output,
                                                final ActionsInput command) {
        addErrorNode(output, "cardUsesAttack",
                "Attacker card has already attacked this turn.",
                -1, null, command);
    }

    /**
     * Handles the error when an attacker card is frozen.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker and attacked card details.
     */
    public static void useAttackIsFrozen(final ArrayNode output,
                                         final ActionsInput command) {
        addErrorNode(output, "cardUsesAttack",
                "Attacker card is frozen.",
                -1, null, command);
    }

    /**
     * Handles the error when an attacked card is not of type 'Tank' while it is required.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker and attacked card details.
     */
    public static void useAttackTank(final ArrayNode output,
                                     final ActionsInput command) {
        addErrorNode(output, "cardUsesAttack",
                "Attacked card is not of type 'Tank'.",
                -1, null, command);
    }

    /**
     * Handles the error when an attacker card is frozen while attempting to use an ability.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker and attacked card details.
     */
    public static void useAbilityIsFrozen(final ArrayNode output,
                                          final ActionsInput command) {
        addErrorNode(output, "cardUsesAbility",
                "Attacker card is frozen.",
                -1, null, command);
    }

    /**
     * Handles the error when an attacker card has already attacked during the turn
     * while attempting to use an ability.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker and attacked card details.
     */
    public static void useAbilityAlreadyAttacked(final ArrayNode output,
                                                 final ActionsInput command) {
        addErrorNode(output, "cardUsesAbility",
                "Attacker card has already attacked this turn.",
                -1, null, command);
    }

    /**
     * Handles the error when an ability is used on a card that does not belong to the
     * current player.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker and attacked card details.
     */
    public static void useAbilityNotMyCard(final ArrayNode output,
                                           final ActionsInput command) {
        addErrorNode(output, "cardUsesAbility",
                "Attacked card does not belong to the current player.",
                -1, null, command);
    }

    /**
     * Handles the error when an ability is used on a card that does not belong to the enemy.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker and attacked card details.
     */
    public static void useAbilityNotEnemyCard(final ArrayNode output,
                                              final ActionsInput command) {
        addErrorNode(output, "cardUsesAbility",
                "Attacked card does not belong to the enemy.",
                -1, null, command);
    }

    /**
     * Handles the error when an ability targets a card that is not of type 'Tank'.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker and attacked card details.
     */
    public static void useAbilityTank(final ArrayNode output,
                                      final ActionsInput command) {
        addErrorNode(output, "cardUsesAbility",
                "Attacked card is not of type 'Tank'.",
                -1, null, command);
    }

    /**
     * Handles the error when an attacker card is frozen while attempting
     * to attack the hero.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker card details.
     */
    public static void attackHeroIsFrozen(final ArrayNode output,
                                          final ActionsInput command) {
        addErrorNode(output, "useAttackHero",
                "Attacker card is frozen.",
                -1, null, command);
    }

    /**
     * Handles the error when an attacker card has already attacked during
     * the turn while attempting to attack the hero.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker card details.
     */
    public static void attackHeroAlreadyAttacked(final ArrayNode output,
                                                 final ActionsInput command) {
        addErrorNode(output, "useAttackHero",
                "Attacker card has already attacked this turn.",
                -1, null, command);
    }

    /**
     * Handles the error when an attack targets a card that is not of type 'Tank'.
     *
     * @param output  The output ArrayNode to add the error message to.
     * @param command The action input containing attacker card details.
     */
    public static void attackHeroTank(final ArrayNode output,
                                      final ActionsInput command) {
        addErrorNode(output, "useAttackHero",
                "Attacked card is not of type 'Tank'.",
                -1, null, command);
    }

    /**
     * Handles the error when there is not enough mana to use a hero's ability.
     *
     * @param output      The output ArrayNode to add the error message to.
     * @param affectedRow The row affected by the hero's ability.
     */
    public static void heroAbilityNotEnoughMana(final ArrayNode output,
                                                final int affectedRow) {
        addErrorNode(output, "useHeroAbility",
                "Not enough mana to use hero's ability.",
                -1, affectedRow, null);
    }

    /**
     * Handles the error when a hero has already used an ability during the turn.
     *
     * @param output      The output ArrayNode to add the error message to.
     * @param affectedRow The row affected by the hero's ability.
     */
    public static void heroAbilityAlreadyAttacked(final ArrayNode output,
                                                  final int affectedRow) {
        addErrorNode(output, "useHeroAbility",
                "Hero has already attacked this turn.",
                -1, affectedRow, null);
    }

    /**
     * Handles the error when a hero's ability targets a row that
     * does not belong to the enemy.
     *
     * @param output      The output ArrayNode to add the error message to.
     * @param affectedRow The row affected by the hero's ability.
     */
    public static void heroAbilityNotEnemyRow(final ArrayNode output,
                                              final int affectedRow) {
        addErrorNode(output, "useHeroAbility",
                "Selected row does not belong to the enemy.",
                -1, affectedRow, null);
    }

    /**
     * Handles the error when a hero's ability targets a row that
     * does not belong to the current player.
     *
     * @param output      The output ArrayNode to add the error message to.
     * @param affectedRow The row affected by the hero's ability.
     */
    public static void heroAbilityNotMyRow(final ArrayNode output,
                                           final int affectedRow) {
        addErrorNode(output,
                "useHeroAbility",
                "Selected row does not belong to the current player.",
                -1, affectedRow, null);
    }

    /**
     * Handles the error when a card that is not of type environment is attempted to be used.
     *
     * @param output      The output ArrayNode to add the error message to.
     * @param affectedRow The row affected by the environment card.
     * @param handIdx     The index of the card in hand.
     */
    public static void notEnvironmentType(final ArrayNode output,
                                          final int affectedRow,
                                          final int handIdx) {
        addErrorNode(output, "useEnvironmentCard",
                "Chosen card is not of type environment.",
                handIdx, affectedRow, null);
    }

    /**
     * Handles the error when there is not enough mana to use an environment card.
     *
     * @param output      The output ArrayNode to add the error message to.
     * @param affectedRow The row affected by the environment card.
     * @param handIdx     The index of the card in hand.
     */
    public static void environmentCardNotEnoughMana(final ArrayNode output,
                                                    final int affectedRow,
                                                    final int handIdx) {
        addErrorNode(output,
                "useEnvironmentCard",
                "Not enough mana to use environment card.",
                handIdx, affectedRow, null);
    }

    /**
     * Handles the error when an environment card is used on a row that
     * does not belong to the enemy.
     *
     * @param output      The output ArrayNode to add the error message to.
     * @param affectedRow The row affected by the environment card.
     * @param handIdx     The index of the card in hand.
     */
    public static void environmentCardNotEnemyRow(final ArrayNode output,
                                                  final int affectedRow,
                                                  final int handIdx) {
        addErrorNode(output, "useEnvironmentCard",
                "Chosen row does not belong to the enemy.",
                handIdx, affectedRow, null);
    }

    /**
     * Handles the error when there is not enough space to steal an enemy card using
     * an environment card.
     *
     * @param output      The output ArrayNode to add the error message to.
     * @param affectedRow The row affected by the environment card.
     * @param handIdx     The index of the card in hand.
     */
    public static void environmentCardNotEnoughSpace(final ArrayNode output,
                                                     final int affectedRow,
                                                     final int handIdx) {
        addErrorNode(output, "useEnvironmentCard",
                "Cannot steal enemy card since the player's row is full.",
                handIdx, affectedRow, null);
    }
}
