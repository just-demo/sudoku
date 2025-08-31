#include "Generator.hpp"
#include "../util/SudokuUtils.hpp"
#include <algorithm>
#include <random>
#include <cmath>

namespace just::demo::generator {

Generator::Generator(int size, int complexityLowerLimit) 
    : size_(size), complexityLowerLimit_(complexityLowerLimit), blockSize_(static_cast<int>(std::sqrt(size))) {
    for (int i = 1; i <= size; ++i) {
        values_.insert(i);
    }
}

std::vector<std::vector<int>> Generator::generate() {
    std::vector<std::vector<int>> initialValues(size_, std::vector<int>(size_, 0));
    return generate(initialValues);
}

std::vector<std::vector<int>> Generator::generate(const std::vector<std::vector<int>>& initialValues) {
    if (just::demo::util::SudokuUtils::countOpen(initialValues) > complexityLowerLimit_) {
        throw just::demo::exception::ComplexityLimitException();
    }

    try {
        just::demo::solver::Solver(initialValues).solve();
        return initialValues;
    } catch (const just::demo::exception::MultipleSolutionsException&) {
        std::vector<Cell> open;
        std::vector<Cell> pending;
        
        for (int row = 0; row < size_; ++row) {
            for (int col = 0; col < size_; ++col) {
                int value = initialValues[row][col];
                int block = blockSize_ * (row / blockSize_) + col / blockSize_;
                Cell cell(row, col, block, value);
                if (value != 0) {
                    open.push_back(cell);
                } else {
                    pending.push_back(cell);
                }
            }
        }

        // Shuffle pending cells
        std::random_device rd;
        std::mt19937 g(rd());
        std::shuffle(pending.begin(), pending.end(), g);
        
        for (const auto& cell : pending) {
            auto nextGuess = just::demo::util::SudokuUtils::copy(initialValues);
            std::vector<int> availableValues(values_.begin(), values_.end());
            
            // Remove values from related cells
            for (const auto& openCell : open) {
                if (cell.isRelated(openCell)) {
                    availableValues.erase(
                        std::remove(availableValues.begin(), availableValues.end(), openCell.value),
                        availableValues.end()
                    );
                }
            }
            
            // Shuffle available values
            std::shuffle(availableValues.begin(), availableValues.end(), g);
            
            for (int value : availableValues) {
                nextGuess[cell.row][cell.col] = value;
                try {
                    return generate(nextGuess);
                } catch (const just::demo::exception::NoSolutionException&) {
                    // Our guess did not work, let's try another one
                }
            }
        }
        throw just::demo::exception::NoSolutionException();
    }
}

} // namespace just::demo::generator
