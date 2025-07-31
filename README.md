# ♟️ Chess Duel - Save and Load

A **Java-based two-player chess game** with a **Swing GUI**, implementing standard chess rules such as:

- ✅ Move validation
- ✅ Check and Checkmate detection
- ✅ Castling, En Passant, and Pawn Promotion
- ✅ Timer Mode (optional)
- ✅ Game Save & Load functionality with MySQL

The game lets players choose to play with or without a timer, save games by name, and resume them later with full board and timer state restoration using **JDBC + MySQL (Aiven Cloud DB)**.

---

## 🎮 Features

- 🎯 Two-player chess with all legal rules
- 🧠 Object-Oriented Design using Java and Swing
- 🕒 Optional Timer with pause/resume and time tracking
- 💾 Save and Load games by name (with full game and timer state)
- 📦 Runnable as a standalone `.jar` file

---

## 📁 Project Structure

Chess_Duel/
├── src/ # Java source code (main logic)
├── res/ # Piece images and assets
├── Chess_Game.jar # Executable JAR file
├── README.md # Project documentation
└── .gitignore # Git ignored files

## 🚀 How to Run the Game

1. Download the jar;
2. 2. Navigate to the project folder.
3. Double-click `Chess_Game.jar` to start playing!
> 💡 Make sure **Java 17 or later** is installed on your system.


## 💾 Save & Load System

- At game start, players choose:
- **"With Timer"** or **"Without Timer"**
- Use the **Save** button to save a game by name.
- Use the **Load** button to restore a previously saved game.
- Timer mode stores each player's remaining time in the database.

   
