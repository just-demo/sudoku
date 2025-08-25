# Sudoku Solver, Generator, and Reducer (C++20)

This is a C++20 migration of the Java Sudoku project, providing a complete Sudoku solving, generation, and reduction system.

## Features

- **CleverSolver**: Advanced Sudoku solving algorithm with constraint propagation
- **Generator**: Creates new Sudoku puzzles with specified complexity
- **Reducer**: Minimizes Sudoku puzzles by removing redundant clues
- **File I/O**: Handles puzzle storage and retrieval
- **Multi-threading**: Supports concurrent puzzle generation and reduction

## Building

### Using Make
```bash
make clean
make
```

### Using CMake
```bash
mkdir build && cd build
cmake ..
make
```

## Usage

### Main Application
```bash
# Solve a puzzle
./sudoku solve ".......9......8.2.7.3.54.....52.................6....88....3..7.9....6..6...8...4"

# Generate puzzles
./sudoku generate

# Reduce failed puzzles
./sudoku reduce

# Copy reduced puzzles to ready directory
./sudoku copy-reduced
```

### Running Tests
```bash
# Run all tests
make test

# Run specific tests
./test_clever_solver
./test_reducer
```

## Project Structure

### Core Classes
- `CleverSolver`: Main solving algorithm with constraint propagation
- `Generator`: Creates new Sudoku puzzles
- `Reducer`: Minimizes puzzles by removing redundant clues
- `Cell`: Represents a cell in the Sudoku grid
- `Utils`: Utility functions for I/O and matrix operations

### Main Applications
- `RunGenerator`: Orchestrates puzzle generation with timeouts
- `RunReducer`: Processes failed puzzles for reduction

### Data Management
- `DataDirs`: Manages directory structure for puzzle storage
- File utilities for reading/writing puzzles

## Algorithm Details

### CleverSolver
The solver uses constraint propagation and backtracking:
1. Initializes cells with all possible values
2. Propagates constraints when cells are filled
3. Uses guessing when no direct progress is possible
4. Handles multiple solutions and no-solution cases

### Generator
Creates puzzles by:
1. Starting with an empty grid
2. Using the solver to find a complete solution
3. When multiple solutions exist, strategically adding clues
4. Respecting complexity limits

### Reducer
Minimizes puzzles by:
1. Identifying cells that can be removed
2. Testing if removal creates multiple solutions
3. Recursively finding the minimal puzzle
4. Preserving unique solution property

## File Format

Puzzles are stored in two formats:
- **1D**: Single line string (e.g., ".......9......8.2.7.3.54.....52...")
- **2D**: Multi-line format with spaces (e.g., ". . . 8 7 . . . .")

## Directory Structure
```
data/
├── ready/           # Completed puzzles
├── reducer-failed/  # Puzzles that failed reduction
└── reducer-fixed/   # Successfully reduced puzzles
```

## Dependencies

- C++20 compatible compiler (GCC 10+, Clang 10+, MSVC 2019+)
- Standard library with filesystem support

## Performance

The C++ implementation provides significant performance improvements over the Java version:
- Faster constraint propagation
- More efficient memory usage
- Better cache locality
- Reduced garbage collection overhead

## Testing

The test suite verifies:
- Solver correctness on various difficulty levels
- Reducer functionality for puzzle minimization
- File I/O operations
- Error handling

All tests from the original Java implementation have been migrated and pass successfully.
