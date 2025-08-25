#include <iostream>
#include <string>
#include "clever_solver.hpp"
#include "generator.hpp"
#include "reducer.hpp"
#include "run_generator.hpp"
#include "run_reducer.hpp"
#include "utils.hpp"

int main(int argc, char* argv[]) {
    if (argc < 2) {
        std::cout << "Usage: " << argv[0] << " <command> [options]" << std::endl;
        std::cout << "Commands:" << std::endl;
        std::cout << "  solve <puzzle>     - Solve a sudoku puzzle" << std::endl;
        std::cout << "  generate           - Generate sudoku puzzles" << std::endl;
        std::cout << "  reduce             - Reduce failed puzzles" << std::endl;
        std::cout << "  copy-reduced       - Copy reduced puzzles to ready" << std::endl;
        return 1;
    }

    std::string command = argv[1];

    try {
        if (command == "solve") {
            if (argc < 3) {
                std::cout << "Error: solve command requires a puzzle string" << std::endl;
                return 1;
            }
            
            std::string puzzle = argv[2];
            auto matrix = sudoku::Utils::fromString1D(puzzle);
            
            sudoku::CleverSolver solver(matrix);
            auto solution = solver.solve();
            
            std::cout << "Solution:" << std::endl;
            std::cout << sudoku::Utils::toString2D(solution) << std::endl;
            
        } else if (command == "generate") {
            sudoku::RunGenerator::generateMany();
            
        } else if (command == "reduce") {
            sudoku::RunReducer::reduceFailedByGeneratorTimeout();
            
        } else if (command == "copy-reduced") {
            sudoku::RunReducer::copyReducedToReady();
            
        } else {
            std::cout << "Unknown command: " << command << std::endl;
            return 1;
        }
        
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}
