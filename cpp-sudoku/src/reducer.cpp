#include "reducer.hpp"
#include "clever_solver.hpp"
#include "cell.hpp"
#include "utils.hpp"
#include "exceptions.hpp"
#include <algorithm>
#include <random>

namespace sudoku {

Reducer::Reducer() : rng(std::random_device{}()) {}

std::vector<std::vector<std::optional<int>>> Reducer::reduce(const std::vector<std::vector<std::optional<int>>>& initialValues) {
    int size = static_cast<int>(initialValues.size());
    std::vector<Cell> open;
    
    for (int row = 0; row < size; row++) {
        for (int col = 0; col < size; col++) {
            if (initialValues[row][col].has_value()) {
                open.emplace_back(row, col, 0);
            }
        }
    }

    return reduce(initialValues, open);
}

std::vector<std::vector<std::optional<int>>> Reducer::reduce(
    const std::vector<std::vector<std::optional<int>>>& initialValues, 
    const std::vector<Cell>& closeCandidates) {
    
    std::unordered_map<Cell, std::vector<std::vector<std::optional<int>>>> candidates;
    
    for (const Cell& cell : closeCandidates) {
        std::vector<std::vector<std::optional<int>>> nextGuess = Utils::copy(initialValues);
        nextGuess[cell.getRow()][cell.getCol()] = std::nullopt;
        
        try {
            CleverSolver solver(nextGuess);
            solver.solve();
            candidates[cell] = nextGuess;
        } catch (const MultipleSolutionsException& e) {
            // no-op
        }
    }

    if (candidates.empty()) {
        return initialValues;
    }

    std::vector<Cell> nextCloseCandidates;
    for (const auto& [cell, _] : candidates) {
        nextCloseCandidates.push_back(cell);
    }
    
    // Shuffle candidates
    std::shuffle(nextCloseCandidates.begin(), nextCloseCandidates.end(), rng);
    
    std::vector<std::vector<std::optional<int>>> bestResult = initialValues;
    long bestCount = Utils::countOpen(initialValues);
    
    for (const Cell& cell : nextCloseCandidates) {
        // Create a copy of the remaining candidates without the current cell
        std::vector<Cell> remainingCandidates;
        for (const Cell& remainingCell : nextCloseCandidates) {
            if (!(remainingCell == cell)) {
                remainingCandidates.push_back(remainingCell);
            }
        }
        
        std::vector<std::vector<std::optional<int>>> result = reduce(candidates[cell], remainingCandidates);
        long count = Utils::countOpen(result);
        
        if (count < bestCount) {
            bestCount = count;
            bestResult = result;
        }
    }
    
    return bestResult;
}

} // namespace sudoku
