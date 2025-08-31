#include "Reducer.hpp"
#include "../util/SudokuUtils.hpp"
#include <algorithm>
#include <random>

namespace just::demo::generator {

std::vector<std::vector<int>> Reducer::reduce(const std::vector<std::vector<int>>& initialValues) {
    int size = static_cast<int>(initialValues.size());
    std::vector<Cell> open;
    
    for (int row = 0; row < size; ++row) {
        for (int col = 0; col < size; ++col) {
            if (initialValues[row][col] != 0) {
                open.emplace_back(row, col);
            }
        }
    }
    
    return reduce(initialValues, open);
}

std::vector<std::vector<int>> Reducer::reduce(const std::vector<std::vector<int>>& initialValues, 
                                             const std::vector<Cell>& closeCandidates) {
    std::unordered_map<Cell, std::vector<std::vector<int>>> candidates;
    
    for (const auto& cell : closeCandidates) {
        auto nextGuess = just::demo::util::SudokuUtils::copy(initialValues);
        nextGuess[cell.row][cell.col] = 0;
        
        try {
            just::demo::solver::Solver(nextGuess).solve();
            candidates[cell] = nextGuess;
        } catch (const just::demo::exception::MultipleSolutionsException&) {
            // no-op
        }
    }
    
    std::vector<Cell> nextCloseCandidates;
    for (const auto& [cell, _] : candidates) {
        nextCloseCandidates.push_back(cell);
    }
    
    // Shuffle the candidates
    std::random_device rd;
    std::mt19937 g(rd());
    std::shuffle(nextCloseCandidates.begin(), nextCloseCandidates.end(), g);
    
    // Find the minimum result using the same logic as Java
    std::vector<std::vector<int>> bestResult = initialValues;
    long minOpen = just::demo::util::SudokuUtils::countOpen(initialValues);
    
    for (const auto& cell : nextCloseCandidates) {
        // Create a copy of nextCloseCandidates and remove the current cell
        auto remainingCandidates = nextCloseCandidates;
        remainingCandidates.erase(
            std::remove(remainingCandidates.begin(), remainingCandidates.end(), cell),
            remainingCandidates.end()
        );
        
        auto result = reduce(candidates[cell], remainingCandidates);
        long openCount = just::demo::util::SudokuUtils::countOpen(result);
        
        if (openCount < minOpen) {
            minOpen = openCount;
            bestResult = result;
        }
    }
    
    return bestResult;
}

} // namespace just::demo::generator
