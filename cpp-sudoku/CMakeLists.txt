cmake_minimum_required(VERSION 3.20)
project(sudoku-cpp)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories
include_directories(src)

# Source files
set(SOURCES
    src/util/SudokuUtils.cpp
    src/util/FileUtils.cpp
    src/solver/Solver.cpp
    src/generator/Reducer.cpp
    src/generator/Generator.cpp
    src/main/DataDirs.cpp
)

# Create library
add_library(sudoku-lib STATIC ${SOURCES})

# Test files
set(SOLVER_TEST_SOURCES
    test/solver/SolverTest.cpp
)

set(REDUCER_TEST_SOURCES
    test/generator/ReducerTest.cpp
)

set(GENERATOR_TEST_SOURCES
    test/generator/GeneratorTest.cpp
)

# Create test executables
add_executable(solver-test ${SOLVER_TEST_SOURCES})
target_link_libraries(solver-test sudoku-lib)

add_executable(reducer-test ${REDUCER_TEST_SOURCES})
target_link_libraries(reducer-test sudoku-lib)

add_executable(generator-test ${GENERATOR_TEST_SOURCES})
target_link_libraries(generator-test sudoku-lib)

# Create main executables
add_executable(run-generator src/main/RunGenerator.cpp)
target_link_libraries(run-generator sudoku-lib)

add_executable(run-solver src/main/RunSolver.cpp)
target_link_libraries(run-solver sudoku-lib)

# Enable testing
enable_testing()
add_test(NAME solver-test COMMAND solver-test)
add_test(NAME reducer-test COMMAND reducer-test)
add_test(NAME generator-test COMMAND generator-test)
