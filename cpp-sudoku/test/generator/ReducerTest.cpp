#include "../../src/test/TestFramework.hpp"
#include "../../src/generator/Reducer.hpp"
#include "../../src/util/SudokuUtils.hpp"

using namespace just::demo::generator;
using namespace just::demo::util;
using namespace test;

void testReduceAlreadyMinimal() {
    std::string inputData = R"(
. . . 8 7 . . . .
. . . . . . . 5 9
3 . . 1 . . . . .
. . . . 4 . 2 1 .
5 8 . 7 . . . . .
6 . . . . . . . 4
. 2 . . 5 . 8 . .
. . . . 3 4 . 9 .
. . . . . . . 3 .
)";

    auto input = SudokuUtils::fromString2D(inputData);
    Reducer reducer;
    auto output = reducer.reduce(input);
    
    TestFramework::assertEqual(SudokuUtils::toString2D(input), SudokuUtils::toString2D(output), "testReduceAlreadyMinimal");
}

void testReduceMinusOne() {
    std::string inputData = R"(
. 5 4 . 1 3 . . .
6 . . . . . . . 2
. . . . . 5 . 7 .
. 8 . 2 . . 7 . .
4 . . . . . . . .
. . 6 . . . 9 . 1
. . 2 . . . 6 . .
. . 1 . 4 . . 8 .
. . . . . 8 5 . .
)";

    auto input = SudokuUtils::fromString2D(inputData);
    Reducer reducer;
    auto output = reducer.reduce(input);
    
    TestFramework::assertEqual(SudokuUtils::countOpen(input) - 1, SudokuUtils::countOpen(output), "testReduceMinusOne");
}

void testReduceMinusTwo() {
    std::string inputData = R"(
2 . 9 . . . 3 . .
. . . . 2 4 6 . .
1 . . . 7 . . 5 .
. 1 . . . . . . .
3 4 . . . . . 7 .
. 5 . . . . 9 4 8
4 . . . . 8 . . .
. . . 6 . . . . .
. 2 . 3 5 . . 6 .
)";

    auto input = SudokuUtils::fromString2D(inputData);
    Reducer reducer;
    auto output = reducer.reduce(input);
    
    TestFramework::assertEqual(SudokuUtils::countOpen(input) - 2, SudokuUtils::countOpen(output), "testReduceMinusTwo");
}

int main() {
    std::cout << "Running Reducer Tests..." << std::endl;
    testReduceAlreadyMinimal();
    testReduceMinusOne();
    testReduceMinusTwo();
    std::cout << "Reducer Tests completed." << std::endl;
    return 0;
}
