#pragma once

#include <vector>
#include <unordered_map>
#include "../solver/Solver.hpp"
#include "../exception/MultipleSolutionsException.hpp"

namespace just::demo::generator {

struct Cell {
    int row;
    int col;
    
    Cell(int r, int c) : row(r), col(c) {}
    
    bool operator==(const Cell& other) const {
        return row == other.row && col == other.col;
    }
};

class Reducer {
public:
    std::vector<std::vector<int>> reduce(const std::vector<std::vector<int>>& initialValues);

private:
    std::vector<std::vector<int>> reduce(const std::vector<std::vector<int>>& initialValues, 
                                        const std::vector<Cell>& closeCandidates);
};

} // namespace just::demo::generator

// Hash function for Cell
namespace std {
    template<>
    struct hash<just::demo::generator::Cell> {
        size_t operator()(const just::demo::generator::Cell& cell) const {
            return hash<int>()(cell.row) ^ (hash<int>()(cell.col) << 1);
        }
    };
}
