#pragma once

#include <vector>
#include <unordered_set>
#include <optional>
#include <random>
#include "exceptions.hpp"

namespace sudoku {

class CleverSolver;

class Generator {
private:
    int size;
    int complexityLowerLimit;
    int blockSize;
    std::unordered_set<int> values;
    std::mt19937 rng;

public:
    Generator(int size);
    Generator(int size, int complexityLowerLimit);
    
    std::vector<std::vector<std::optional<int>>> generate();

private:
    std::vector<std::vector<std::optional<int>>> generate(const std::vector<std::vector<std::optional<int>>>& initialValues);
};

} // namespace sudoku
