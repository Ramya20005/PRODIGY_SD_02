# GuessingGame

`GuessingGame` is a Java Swing number guessing game where the player tries to find a hidden number between 1 and 100 using range hints and feedback messages.

## Features

- Random number generated between 1 and 100
- Live range updates after each valid guess
- Attempt counter
- Feedback for low, high, correct, and invalid guesses
- Progress bar showing how much the search range has narrowed
- New Game button to restart the game

## Technologies Used

- Java
- Java Swing
- AWT Graphics for custom UI design

## Project File

- `GuessingGame.java` - main source code file

## How to Run

1. Make sure Java is installed on your system.
2. Open a terminal in the project folder.
3. Compile the program:

```bash
javac GuessingGame.java
```

4. Run the program:

```bash
java GuessingGame
```

## How to Play

1. Start the application.
2. Enter a number within the current live range.
3. Click `Submit Guess` or press `Enter`.
4. Read the feedback:
   - `Too low` means choose a higher number.
   - `Too high` means choose a lower number.
   - `Correct` means you found the hidden number.
5. Click `New Game` to start another round.

## Game Logic

- The program chooses a random secret number from `1` to `100`.
- Each valid guess increases the attempt count.
- The visible range updates based on whether the guess is too low or too high.
- The game ends when the correct number is guessed.

## Example

If the secret number is `42`:

- Guess `20` -> Too low
- Guess `70` -> Too high
- Guess `42` -> Correct

## Learning Outcome

This project helps practice:

- Java Swing GUI development
- Event handling in Java
- Random number generation
- Conditional logic and input validation
- Custom UI styling

## Author

Created as a Java mini project for practice and learning.
