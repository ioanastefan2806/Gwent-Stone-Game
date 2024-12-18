# 🌟 *GwentStone Lite* 🌟

### 👤 **Student**: *Ștefan Ioana*

### 📚 **Group**: *332CA*

---

## 🏛 *Project Overview*

*GwentStone Lite* is a simplified version of a popular strategy turn-based card game. The goal of the game is to defeat your opponent by bringing down their Hero's health points to zero. The game involves strategic use of various types of cards, which can either attack, defend, or provide special effects during the game. 🎴✨

---
## 🔸 *Card Types Explained*

### 1. *Minion Cards* ⛨️

*Minion cards* are used to **attack** and **defend** on the board. Each minion has the following attributes: *mana cost*, *health*, *attack damage*, and *name*.

*Minion Types:* 🛡️

- **Sentinel, Berserker**: Placed on the *back row*.
- **Goliath, Warden**: Placed on the *front row*.

*Conditions:* ⚔️
- Players need enough mana to place a minion.
- Minions cannot attack twice per turn.
- "Frozen" minions cannot attack until the end of the current turn.
- Minion attacks must target enemy cards or the enemy Hero.

**Tank Cards**: *Goliath* and *Warden* must be attacked first before other cards or Heroes.

*Special Minion Cards:* 🃏
- **The Ripper**: Reduces enemy minion attack by 2 points.
- **Miraj**: Swaps health with an enemy minion.
- **The Cursed One**: Swaps attack and health of an enemy minion.
- **Disciple**: Adds 2 health points to an allied minion.

### 2. *Hero Cards* 👑

*Heroes* represent the main characters of each player. Each hero has *30 health points* and a unique ability. 💥

*Heroes and Abilities:* ✨
- **Lord Royce**: "Sub-Zero" freezes all cards on a row. ❄️
- **Empress Thorina**: "Low Blow" destroys the card with the highest health on a row. 💣
- **King Mudface**: "Earth Born" adds 1 health point to all cards on a row. 🌱
- **General Kocioraw**: "Blood Thirst" adds 1 attack point to all cards on a row. 🩸

*Conditions for Heroes:* ⚠️
- **Lord Royce** and **Empress Thorina** abilities are used on enemy rows.
- **King Mudface** and **General Kocioraw** abilities are used on own rows.
- Players need enough mana to use the hero's ability.
- Heroes can attack only once per turn.

---

## 🎮 *Gameplay Mechanics*

- **Placing Cards**: Players can place minion cards on the board by spending the required amount of mana. 💰
- **Attacking**: Minions on the board can attack enemy minions or the enemy Hero. 🗡️
- **Using Abilities**: Special abilities of minions and Heroes can be used to influence the game, provided the conditions are met. 🔮
- **End Turn**: Players must explicitly end their turn. 🔄

*Game Board*: The board is a *4x5 matrix* where cards interact directly. Rows *0 and 1* belong to Player 2, while rows *2 and 3* belong to Player 1. Cards are placed in rows from *left to right*, filling empty spaces. If a card is eliminated, cards on its right shift left to maintain the order. Each row can hold a maximum of *5 cards*. 🗺️

*Victory Condition*: The game ends when one of the Heroes has their health reduced to *zero*. The player who deals the final blow is declared the winner. 🏆

---

## 🛠️ *Project Implementation*

### 📂 *Class and Packages Breakdown*

The project is organized into several packages, each containing classes that handle specific aspects of the game logic. Below is a breakdown of the key packages and their classes: 📦

### 📦 *CardHandler Package*

The *CardHandler* package handles the different types of cards used in *GwentStone Lite*, including *Deck*, *Minion*, *Environment*, and *Hero*.

- **`Deck Class`** 🃏: Represents a generic card deck with attributes like `mana`, `description`, `colors`, and `name`. Tracks `isFrozen` and `attackUsed` states, and provides methods to manage attributes and create categorized decks.
- **`Environment Class`** 🌿: Represents environment cards, derived from `Deck`. Initialized using `CardInput`.
- **`Hero Class`** 👑: Represents Hero cards, inheriting from `Deck`. Adds `health` (initially 30) and provides methods to get and set health.
- **`Minion Class`** 🛡️: Represents minion cards for attack and defense. Inherits from `Deck`, adding `health` and `attackDamage`. Provides methods to manage health and attack.

### 📦 *GameAction Package*

The *GameAction* package handles various game actions in *GwentStone Lite*, such as card attacks, abilities, and game state management.

- **`GameCardPlay Class`** 🗡️: Handles card-related actions like attacks and abilities.
    - **Key Methods**:
        - `cardUsesAttack()`: Executes a card attack based on game state.
        - `validateAttackConditions()`: Validates whether an attack can be performed.
        - `executeAttack()`: Performs the attack between two cards.
        - `cardUsesAbility()`: Processes actions where a card uses a special ability.
        - `cardAttackHero()`: Handles a card attacking the enemy hero.
        - `useHeroAbility()`: Executes a hero's special ability.

- **`GameInfo Class`** 📝: Provides information about the current game state.
    - **Key Methods**:
        - `getCardsInHand()`: Retrieves cards in a player's hand.
        - `getPlayerDeck()`: Retrieves the player's deck.
        - `getCardsOnTable()`: Retrieves cards currently on the playing table.
        - `getPlayerTurn()`: Adds the current player's turn information to the output.
        - `getPlayerHero()`: Retrieves the specified player's hero.
        - `getCardsAtPosition()`: Retrieves card information at a specific position.
        - `getPlayerMana()`: Retrieves the player's current mana.
        - `getEnvironmentCardsInHand()`: Retrieves environment cards in a player's hand.
        - `getFrozenCardsOnTable()`: Retrieves frozen cards on the table.

### 📦 *GameHandler Package*

The *GameHandler* package manages the core game mechanics, focusing on executing player actions, validating moves, and handling game flow.

- **`EnvironmentCardHandler Class`** 🌍: Handles the usage of environment cards, such as affecting rows or performing special abilities.
    - **Key Methods**:
        - `useEnvironmentCard()`: Uses an environment card to affect the playing table.
        - `validateCardUsage()`: Validates if an environment card can be used.
        - `executeCardAction()`: Executes the action based on the environment card type.

- **`ErrorHandler Class`** ❌: Handles errors during gameplay, ensuring appropriate messages are generated.
    - **Key Methods**:
        - `placeCardEnvironmentCard()`: Handles error when attempting to place an environment card on the table.
        - `useAttackNotEnemyCard()`, `useAttackAlreadyAttacked()`, etc.: Manage various error cases during card attacks or ability usage.

- **`GameActionHandler Class`** 🕹️: Handles main gameplay actions like placing cards, ending turns, and managing the overall flow.
    - **Key Methods**:
        - `placeCard()`: Places a card on the playing table after validating conditions.
        - `endPlayerTurn()`: Ends the current player's turn, updating game state and switching turns.
        - `updatePlayerManaAndDrawCards()`: Manages mana increment and card draw at the start of a turn.
        - `resetHeroAttackStatus()`: Resets attack status for heroes at the end of a turn.

- **`Statistics Class`** 📊: Manages game statistics, such as tracking the total number of games played and the number of games won by each player.
    - **Key Methods**:
        - `getTotalGamesPlayed(ArrayNode output, int i)`: Adds an entry to the output that displays the total number of games played.
        - `getPlayerOneWins(ArrayNode output, int playerOneWins)`: Adds an entry to the output that displays the total number of games won by Player One.
        - `getPlayerTwoWins(ArrayNode output, int playerTwoWins)`: Adds an entry to the output that displays the total number of games won by Player Two.

### 📦 *Command Class*

The *Command* class is responsible for managing the game flow in *GwentStone Lite* by executing commands for each game session. It initializes the game setup, manages players' actions, and handles the playing sequence. 🌀

- **Key Attributes**:
    - **`inputData`**: Contains game settings and commands.
    - **`playerOne`** & **`playerTwo`**: Represent the decks of Player One and Player Two.
    - **`output`**: Stores output results for each action executed.

- **Key Methods**:
    - **`Command(Input inputData, LinkedList<LinkedList<Deck>> playerOne, LinkedList<LinkedList<Deck>> playerTwo, ArrayNode output)`**: Constructor to initialize the *Command* object with game settings, player decks, and output storage.
    - **`run()`**: Executes commands for each game session, initializing the game state and handling gameplay commands like placing cards and using abilities.
    - **`initializeGame(Utils utils, int gameIndex)`**: Sets up the game session, including shuffling player decks, drawing cards, and initializing the playing table.
    - **`shuffleAndDrawFirstCard(LinkedList<Deck> playerDeck, LinkedList<Deck> playerHand, StartGameInput newGame)`**: Shuffles the player's deck and draws the first card to ensure each player starts with one card.
    - **`setInitialTurnAndRounds(Utils utils, StartGameInput newGame)`**: Sets the initial turn and round count for the game session.
    - **`deepCopyDeck(LinkedList<Deck> originalDeck)`**: Creates a deep copy of a player's deck to prevent modifications affecting future sessions.

This class plays a crucial role in orchestrating the entire game flow, ensuring proper execution of commands and maintaining game consistency. 🛡️

---
## 🧠 *Object-Oriented Design*

- **Encapsulation**: Each card type, action, and player component is encapsulated within its own class. This makes the code more modular and easier to understand, as each class handles its specific responsibilities.
- **Inheritance**: Card types inherit from a base class, reducing redundancy and making it easier to add new cards. For instance, `Minion`, `Hero`, and `Environment` classes inherit from the `Deck` class, sharing common attributes.
- **Reusability**: The use of utility classes like `Utils` ensures that common functions are reused throughout the code, minimizing duplication and making the game more maintainable.

---

## 🚀 *Possible Improvements*

- Improve **code structure** to enhance readability and maintainability. 🔧
- Create **separate classes** for every card subtype, such as individual classes for each type of minion. This would make the game more modular and easier to expand. 🛠️
- **Shorten methods** to adhere to best practices, improving code readability and reducing complexity. ✂️
- Use more **descriptive names** for utility instances to improve code clarity, moving away from generic names. 🔍
---

