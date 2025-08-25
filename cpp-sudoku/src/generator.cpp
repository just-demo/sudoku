#include "generator.hpp"
#include "clever_solver.hpp"
#include "cell.hpp"
#include "utils.hpp"
#include <cmath>
#include <algorithm>
#include <random>

namespace sudoku {

Generator::Generator(int size) : Generator(size, size * size) {}

Generator::Generator(int size, int complexityLowerLimit) 
    : size(size), complexityLowerLimit(complexityLowerLimit), 
      blockSize(static_cast<int>(std::sqrt(size))), rng(std::random_device{}()) {
    
    for (int i = 1; i <= size; ++i) {
        values.insert(i);
    }
}

std::vector<std::vector<std::optional<int>>> Generator::generate() {
    std::vector<std::vector<std::optional<int>>> initialValues(size, std::vector<std::optional<int>>(size));
    return generate(initialValues);
}

std::vector<std::vector<std::optional<int>>> Generator::generate(const std::vector<std::vector<std::optional<int>>>& initialValues) {
    if (Utils::countOpen(initialValues) > complexityLowerLimit) {
        throw ComplexityLimitException();
    }

    try {
        CleverSolver solver(initialValues);
        solver.solve();
        // Return the original puzzle with clues, not the complete solution
        return initialValues;
    } catch (const MultipleSolutionsException& e) {
        std::vector<Cell> open;
        std::vector<Cell> pending;
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                std::optional<int> value = initialValues[row][col];
                int block = blockSize * (row / blockSize) + col / blockSize;
                if (value.has_value()) {
                    std::unordered_set<int> candidates{value.value()};
                    open.emplace_back(row, col, block, candidates);
                } else {
                    pending.emplace_back(row, col, block);
                }
            }
        }

        // Shuffle pending cells
        std::shuffle(pending.begin(), pending.end(), rng);
        
        for (const Cell& cell : pending) {
            std::vector<std::vector<std::optional<int>>> nextGuess = Utils::copy(initialValues);
            std::vector<int> availableValues(values.begin(), values.end());
            
            // Remove values that are already used in related cells
            for (const Cell& openCell : open) {
                if (cell.isRelated(openCell)) {
                    auto candidate = openCell.getCandidate();
                    if (candidate.has_value()) {
                        auto it = std::find(availableValues.begin(), availableValues.end(), candidate.value());
                        if (it != availableValues.end()) {
                            availableValues.erase(it);
                        }
                    }
                }
            }
            
            // Shuffle available values
            std::shuffle(availableValues.begin(), availableValues.end(), rng);
            
            for (int value : availableValues) {
                nextGuess[cell.getRow()][cell.getCol()] = value;
                try {
                    return generate(nextGuess);
                } catch (const NoSolutionException& e2) {
                    // Our guess did not work, let's try another one
                }
            }
        }
        throw NoSolutionException();
    }
}

} // namespace sudoku
