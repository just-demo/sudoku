#include "../../src/test/TestFramework.hpp"
#include "../../src/generator/Generator.hpp"
#include "../../src/util/SudokuUtils.hpp"

using namespace just::demo::generator;
using namespace just::demo::util;
using namespace test;

void testGenerateSimple() {
    Generator generator(9, 31);
    auto result = generator.generate();
    
    // Verify it's a valid Sudoku
    long openCount = SudokuUtils::countOpen(result);
    TestFramework::assertEqual(81L, openCount, "testGenerateSimple - should have 81 filled cells");
    
    // Try to solve it to verify it's valid
    try {
        just::demo::solver::Solver(result).solve();
        std::cout << "✓ testGenerateSimple PASSED" << std::endl;
    } catch (const std::exception& e) {
        std::cout << "✗ testGenerateSimple FAILED - generated invalid Sudoku: " << e.what() << std::endl;
    }
}

int main() {
    std::cout << "Running Generator Tests..." << std::endl;
    testGenerateSimple();
    std::cout << "Generator Tests completed." << std::endl;
    return 0;
}
