
## 🚗 Rush Hour Puzzle Solver

A Java implementation of the classic **Rush Hour sliding block puzzle**:
- Reads initial puzzle configuration from a text file
- Simulates cars and trucks with collision/bounds checking
- Finds the **shortest solution path** using BFS
- Animates moves visually with `StdDraw`
- Displays total moves required to solve

> Created by Lucas C.

---

## ✨ Features

- **Solver (BFS)**
  - Explores all valid board states
  - Guarantees shortest path solution
  - Tracks parents to reconstruct moves

- **Vehicles**
  - Each car/truck has size, orientation, and allowed moves
  - Red car is always the goal piece
  - Valid movement only within board bounds

- **Board & Input**
  - Customizable board size (default 6x6)
  - Text-file input format for puzzles
  - First vehicle listed is always the red car

- **Animation**
  - Renders grid and vehicles in unique colors
  - Animates each move step-by-step
  - Ends with “Solved in X move(s)” display

---

## 🧱 Tech Stack

- Java 8+  
- Custom `StdDraw.java` graphics library  
- No external dependencies

---

## 📁 Project Structure



TrafficJam-master/
├─ config                # Puzzle configuration file (in project root)
├─ src/
│  ├─ RushHour.java      # Entrypoint, loads puzzle, initializes board, starts solver
│  ├─ Solver.java        # BFS search, goal detection, backtracking, animation
│  ├─ Vehicle.java       # Vehicle representation (position, size, moves, color)
│  ├─ StdDraw.java       # Graphics helper for rendering
│  ├─ RushHour.class     # Compiled class files
│  ├─ Solver.class
│  ├─ Vehicle.class
│  ├─ StdDraw.class
│  ├─ sample_input       # Example puzzle file inside src
│  ├─ stdlib.jar         # External JAR dependency
│  └─ trafficjam.gif     # GIF asset for animation/demo
└─ README.md


## ▶️ Running the Program



1. **Compile the source code** (from inside the `src/` folder):

   ```bash
   javac *.java

2. **Run the solver with a puzzle file (from inside the src/ folder):**

# Run using the config file in the project root
java RushHour ../config

# Run using a puzzle file inside a puzzles/ folder
java RushHour ../puzzles/config.txt

# Run using the provided sample_input file inside src/
java RushHour sample_input

3. **What happens when you run it?**:
- The program loads the puzzle from the given file.
- It solves the puzzle using Breadth-First Search (BFS).
- It animates the solution sequence step by step in a window.

