#include "reducer.hpp"
#include "utils.hpp"
#include <iostream>
#include <cassert>

void testReduceAlreadyMinimal() {
    std::string inputData = 
        ". . . 8 7 . . . .\n"
        ". . . . . . . 5 9\n"
        "3 . . 1 . . . . .\n"
        ". . . . 4 . 2 1 .\n"
        "5 8 . 7 . . . . .\n"
        "6 . . . . . . . 4\n"
        ". 2 . . 5 . 8 . .\n"
        ". . . . 3 4 . 9 .\n"
        ". . . . . . . 3 .";
    
    auto input = sudoku::Utils::fromString2D(inputData);
    sudoku::Reducer reducer;
    auto output = reducer.reduce(input);
    
    std::string inputStr = sudoku::Utils::toString2D(input);
    std::string outputStr = sudoku::Utils::toString2D(output);
    
    if (inputStr == outputStr) {
        std::cout << "testReduceAlreadyMinimal: PASSED" << std::endl;
    } else {
        std::cout << "testReduceAlreadyMinimal: FAILED" << std::endl;
        std::cout << "Input:" << std::endl << inputStr << std::endl;
        std::cout << "Output:" << std::endl << outputStr << std::endl;
    }
}

void testReduceMinusOne() {
    std::string inputData = 
        ". 5 4 . 1 3 . . .\n"
        "6 . . . . . . . 2\n"
        ". . . . . 5 . 7 .\n"
        ". 8 . 2 . . 7 . .\n"
        "4 . . . . . . . .\n"
        ". . 6 . . . 9 . 1\n"
        ". . 2 . . . 6 . .\n"
        ". . 1 . 4 . . 8 .\n"
        ". . . . . 8 5 . .";
    
    auto input = sudoku::Utils::fromString2D(inputData);
    sudoku::Reducer reducer;
    auto output = reducer.reduce(input);
    
    long inputCount = sudoku::Utils::countOpen(input);
    long outputCount = sudoku::Utils::countOpen(output);
    
    if (outputCount == inputCount - 1) {
        std::cout << "testReduceMinusOne: PASSED" << std::endl;
    } else {
        std::cout << "testReduceMinusOne: FAILED" << std::endl;
        std::cout << "Input count: " << inputCount << std::endl;
        std::cout << "Output count: " << outputCount << std::endl;
        std::cout << "Expected: " << inputCount - 1 << std::endl;
    }
}

void testReduceMinusTwo() {
    std::string inputData = 
        "2 . 9 . . . 3 . .\n"
        ". . . . 2 4 6 . .\n"
        "1 . . . 7 . . 5 .\n"
        ". 1 . . . . . . .\n"
        "3 4 . . . . . 7 .\n"
        ". 5 . . . . 9 4 8\n"
        "4 . . . . 8 . . .\n"
        ". . . 6 . . . . .\n"
        ". 2 . 3 5 . . 6 .";
    
    auto input = sudoku::Utils::fromString2D(inputData);
    sudoku::Reducer reducer;
    auto output = reducer.reduce(input);
    
    long inputCount = sudoku::Utils::countOpen(input);
    long outputCount = sudoku::Utils::countOpen(output);
    
    if (outputCount == inputCount - 2) {
        std::cout << "testReduceMinusTwo: PASSED" << std::endl;
    } else {
        std::cout << "testReduceMinusTwo: FAILED" << std::endl;
        std::cout << "Input count: " << inputCount << std::endl;
        std::cout << "Output count: " << outputCount << std::endl;
        std::cout << "Expected: " << inputCount - 2 << std::endl;
    }
}

int main() {
    std::cout << "Running Reducer tests..." << std::endl;
    
    testReduceAlreadyMinimal();
    testReduceMinusOne();
    testReduceMinusTwo();
    
    std::cout << "All tests completed!" << std::endl;
    return 0;
}
