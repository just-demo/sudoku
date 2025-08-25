cmake_minimum_required(VERSION 3.16)
project(sudoku)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Main executable
add_executable(sudoku
    src/main.cpp
    src/clever_solver.cpp
    src/cell.cpp
    src/utils.cpp
    src/generator.cpp
    src/reducer.cpp
    src/data_dirs.cpp
    src/run_generator.cpp
    src/run_reducer.cpp
)

# Test executables
add_executable(test_clever_solver
    src/test_clever_solver.cpp
    src/clever_solver.cpp
    src/utils.cpp
)

add_executable(test_reducer
    src/test_reducer.cpp
    src/reducer.cpp
    src/clever_solver.cpp
    src/cell.cpp
    src/utils.cpp
)

# Set compiler flags
target_compile_options(sudoku PRIVATE -Wall -Wextra -O2)
target_compile_options(test_clever_solver PRIVATE -Wall -Wextra -O2)
target_compile_options(test_reducer PRIVATE -Wall -Wextra -O2)
