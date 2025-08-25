#include "clever_solver.hpp"
#include "utils.hpp"
#include <iostream>
#include <cassert>

void testSolveSimple() {
    std::string inputData = 
        "5 3 . . 7 . . . .\n"
        "6 . . 1 9 5 . . .\n"
        ". 9 8 . . . . 6 .\n"
        "8 . . . 6 . . . 3\n"
        "4 . . 8 . 3 . . 1\n"
        "7 . . . 2 . . . 6\n"
        ". 6 . . . . 2 8 .\n"
        ". . . 4 1 9 . . 5\n"
        ". . . . 8 . . 7 9";
    
    std::string outputData = 
        "5 3 4 6 7 8 9 1 2\n"
        "6 7 2 1 9 5 3 4 8\n"
        "1 9 8 3 4 2 5 6 7\n"
        "8 5 9 7 6 1 4 2 3\n"
        "4 2 6 8 5 3 7 9 1\n"
        "7 1 3 9 2 4 8 5 6\n"
        "9 6 1 5 3 7 2 8 4\n"
        "2 8 7 4 1 9 6 3 5\n"
        "3 4 5 2 8 6 1 7 9";
    
    auto input = sudoku::Utils::fromString2D(inputData);
    auto expected = sudoku::Utils::fromString2D(outputData);
    
    sudoku::CleverSolver solver(input);
    auto result = solver.solve();
    
    std::string resultStr = sudoku::Utils::toString2D(result);
    std::string expectedStr = sudoku::Utils::toString2D(expected);
    
    if (resultStr == expectedStr) {
        std::cout << "testSolveSimple: PASSED" << std::endl;
    } else {
        std::cout << "testSolveSimple: FAILED" << std::endl;
        std::cout << "Expected:" << std::endl << expectedStr << std::endl;
        std::cout << "Got:" << std::endl << resultStr << std::endl;
    }
}

void testSolveComplex() {
    std::string inputData = 
        "5 7 . . . . . . .\n"
        "8 . . . . . 6 . 2\n"
        ". . 1 . . 3 . 9 .\n"
        ". . . 1 . 6 7 . .\n"
        ". . . . . . 5 1 8\n"
        ". 3 . . . . . . .\n"
        "6 2 . . . 7 . . .\n"
        ". . . . . 9 . . .\n"
        ". . 5 . 2 . . 3 9";
    
    std::string outputData = 
        "5 7 6 2 9 8 3 4 1\n"
        "8 9 3 4 7 1 6 5 2\n"
        "2 4 1 5 6 3 8 9 7\n"
        "9 5 8 1 4 6 7 2 3\n"
        "4 6 7 9 3 2 5 1 8\n"
        "1 3 2 7 8 5 9 6 4\n"
        "6 2 9 3 1 7 4 8 5\n"
        "3 1 4 8 5 9 2 7 6\n"
        "7 8 5 6 2 4 1 3 9";
    
    auto input = sudoku::Utils::fromString2D(inputData);
    auto expected = sudoku::Utils::fromString2D(outputData);
    
    sudoku::CleverSolver solver(input);
    auto result = solver.solve();
    
    std::string resultStr = sudoku::Utils::toString2D(result);
    std::string expectedStr = sudoku::Utils::toString2D(expected);
    
    if (resultStr == expectedStr) {
        std::cout << "testSolveComplex: PASSED" << std::endl;
    } else {
        std::cout << "testSolveComplex: FAILED" << std::endl;
        std::cout << "Expected:" << std::endl << expectedStr << std::endl;
        std::cout << "Got:" << std::endl << resultStr << std::endl;
    }
}

void testSolveMostComplex() {
    std::string inputData = 
        "8 . . . . . . . .\n"
        ". . 3 6 . . . . .\n"
        ". 7 . . 9 . 2 . .\n"
        ". 5 . . . 7 . . .\n"
        ". . . . 4 5 7 . .\n"
        ". . . 1 . . . 3 .\n"
        ". . 1 . . . . 6 8\n"
        ". . 8 5 . . . 1 .\n"
        ". 9 . . . . 4 . .";
    
    std::string outputData = 
        "8 1 2 7 5 3 6 4 9\n"
        "9 4 3 6 8 2 1 7 5\n"
        "6 7 5 4 9 1 2 8 3\n"
        "1 5 4 2 3 7 8 9 6\n"
        "3 6 9 8 4 5 7 2 1\n"
        "2 8 7 1 6 9 5 3 4\n"
        "5 2 1 9 7 4 3 6 8\n"
        "4 3 8 5 2 6 9 1 7\n"
        "7 9 6 3 1 8 4 5 2";
    
    auto input = sudoku::Utils::fromString2D(inputData);
    auto expected = sudoku::Utils::fromString2D(outputData);
    
    sudoku::CleverSolver solver(input);
    auto result = solver.solve();
    
    std::string resultStr = sudoku::Utils::toString2D(result);
    std::string expectedStr = sudoku::Utils::toString2D(expected);
    
    if (resultStr == expectedStr) {
        std::cout << "testSolveMostComplex: PASSED" << std::endl;
    } else {
        std::cout << "testSolveMostComplex: FAILED" << std::endl;
        std::cout << "Expected:" << std::endl << expectedStr << std::endl;
        std::cout << "Got:" << std::endl << resultStr << std::endl;
    }
}

int main() {
    std::cout << "Running CleverSolver tests..." << std::endl;
    
    testSolveSimple();
    testSolveComplex();
    testSolveMostComplex();
    
    std::cout << "All tests completed!" << std::endl;
    return 0;
}
