#pragma once

#include <vector>
#include <optional>
#include <unordered_map>
#include <random>

namespace sudoku {

class Cell;
class CleverSolver;

class Reducer {
private:
    std::mt19937 rng;

public:
    Reducer();
    
    std::vector<std::vector<std::optional<int>>> reduce(const std::vector<std::vector<std::optional<int>>>& initialValues);

private:
    std::vector<std::vector<std::optional<int>>> reduce(
        const std::vector<std::vector<std::optional<int>>>& initialValues, 
        const std::vector<Cell>& closeCandidates);
};

} // namespace sudoku
