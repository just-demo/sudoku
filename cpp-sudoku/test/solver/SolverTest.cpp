#include "../../src/test/TestFramework.hpp"
#include "../../src/solver/Solver.hpp"
#include "../../src/util/SudokuUtils.hpp"

using namespace just::demo::solver;
using namespace just::demo::util;
using namespace test;

void testSolveSimple() {
    std::string inputData = R"(
5 3 . . 7 . . . .
6 . . 1 9 5 . . .
. 9 8 . . . . 6 .
8 . . . 6 . . . 3
4 . . 8 . 3 . . 1
7 . . . 2 . . . 6
. 6 . . . . 2 8 .
. . . 4 1 9 . . 5
. . . . 8 . . 7 9
)";

    std::string outputData = R"(
5 3 4 6 7 8 9 1 2
6 7 2 1 9 5 3 4 8
1 9 8 3 4 2 5 6 7
8 5 9 7 6 1 4 2 3
4 2 6 8 5 3 7 9 1
7 1 3 9 2 4 8 5 6
9 6 1 5 3 7 2 8 4
2 8 7 4 1 9 6 3 5
3 4 5 2 8 6 1 7 9
)";

    auto input = SudokuUtils::fromString2D(inputData);
    auto output = SudokuUtils::fromString2D(outputData);
    Solver resolver(input);
    auto result = resolver.solve();
    
    TestFramework::assertEqual(SudokuUtils::toString2D(output), SudokuUtils::toString2D(result), "testSolveSimple");
}

void testSolveComplex() {
    std::string inputData = R"(
5 7 . . . . . . .
8 . . . . . 6 . 2
. . 1 . . 3 . 9 .
. . . 1 . 6 7 . .
. . . . . . 5 1 8
. 3 . . . . . . .
6 2 . . . 7 . . .
. . . . . 9 . . .
. . 5 . 2 . . 3 9
)";

    std::string outputData = R"(
5 7 6 2 9 8 3 4 1
8 9 3 4 7 1 6 5 2
2 4 1 5 6 3 8 9 7
9 5 8 1 4 6 7 2 3
4 6 7 9 3 2 5 1 8
1 3 2 7 8 5 9 6 4
6 2 9 3 1 7 4 8 5
3 1 4 8 5 9 2 7 6
7 8 5 6 2 4 1 3 9
)";

    auto input = SudokuUtils::fromString2D(inputData);
    auto output = SudokuUtils::fromString2D(outputData);
    Solver resolver(input);
    auto result = resolver.solve();
    
    TestFramework::assertEqual(SudokuUtils::toString2D(output), SudokuUtils::toString2D(result), "testSolveComplex");
}

int main() {
    std::cout << "Running Solver Tests..." << std::endl;
    testSolveSimple();
    testSolveComplex();
    std::cout << "Solver Tests completed." << std::endl;
    return 0;
}
