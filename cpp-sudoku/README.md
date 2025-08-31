# C++20 Sudoku Solver and Generator

This is a C++20 migration of the Java Sudoku solver and generator project. The project includes a complete Sudoku solver, puzzle generator, and puzzle reducer.

## Features

- **Solver**: Solves Sudoku puzzles using constraint propagation and backtracking
- **Generator**: Generates valid Sudoku puzzles
- **Reducer**: Reduces the number of clues in a Sudoku puzzle while maintaining uniqueness
- **Tests**: Comprehensive test suite for solver and reducer functionality

## Building

### Prerequisites

- CMake 3.20 or higher
- C++20 compatible compiler (GCC 10+, Clang 10+, MSVC 2019+)

### Build Instructions

```bash
mkdir build
cd build
cmake ..
make
```

## Running Tests

```bash
# Run solver tests
./solver-test

# Run reducer tests
./reducer-test
```

## Running Applications

```bash
# Run the solver on a complex puzzle
./run-solver

# Run the generator (generates and reduces puzzles)
./run-generator
```

## Project Structure

```
cpp-sudoku/
├── src/
│   ├── exception/          # Exception classes
│   ├── generator/          # Puzzle generator and reducer
│   ├── main/              # Main application entry points
│   ├── solver/            # Sudoku solver implementation
│   ├── util/              # Utility functions
│   └── test/              # Simple test framework
├── test/
│   ├── solver/            # Solver tests
│   └── generator/         # Reducer tests
├── CMakeLists.txt         # Build configuration
└── README.md             # This file
```

## Key Components

### Solver
The solver uses a constraint propagation algorithm with backtracking:
- **Cell**: Represents a Sudoku cell with row, column, and block information
- **Value**: Represents a Sudoku value (1-9) with candidate cells
- **Constraint Propagation**: Eliminates impossible candidates based on Sudoku rules
- **Backtracking**: Uses guessing when constraint propagation is insufficient

### Generator
The generator creates valid Sudoku puzzles:
- Starts with an empty grid
- Uses backtracking to fill cells while maintaining Sudoku rules
- Respects complexity limits to avoid overly complex puzzles

### Reducer
The reducer minimizes clues while maintaining puzzle uniqueness:
- Removes clues one by one
- Verifies that the puzzle still has a unique solution
- Uses the solver to validate solutions

## Migration Notes

This C++20 implementation maintains the same algorithmic approach as the original Java code while leveraging modern C++ features:

- **Smart Pointers**: Uses `std::shared_ptr` for memory management
- **STL Containers**: Uses `std::vector`, `std::list`, `std::unordered_map`, etc.
- **Modern C++**: Uses structured bindings, range-based for loops, and other C++20 features
- **Exception Handling**: Maintains the same exception hierarchy as the Java version
- **Namespaces**: Uses `just::demo` namespace structure similar to Java packages

## Performance

The C++ implementation should provide better performance than the Java version due to:
- Reduced memory allocation overhead
- More efficient data structures
- Compiler optimizations
- Lower runtime overhead

## License

This project is a migration of the original Java Sudoku project to C++20.
