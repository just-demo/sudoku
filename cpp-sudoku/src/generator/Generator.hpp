#pragma once

#include <vector>
#include <unordered_set>
#include <memory>
#include "../solver/Solver.hpp"
#include "../exception/ComplexityLimitException.hpp"
#include "../exception/MultipleSolutionsException.hpp"
#include "../exception/NoSolutionException.hpp"

namespace just::demo::generator {

class Generator {
public:
    Generator(int size, int complexityLowerLimit);
    std::vector<std::vector<int>> generate();

private:
    struct Cell {
        int row;
        int col;
        int block;
        int value;
        
        Cell(int r, int c, int b, int v) : row(r), col(c), block(b), value(v) {}
        
        bool isRelated(const Cell& cell) const {
            return row == cell.row || col == cell.col || block == cell.block;
        }
    };
    
    std::vector<std::vector<int>> generate(const std::vector<std::vector<int>>& initialValues);

    int size_;
    int complexityLowerLimit_;
    int blockSize_;
    std::unordered_set<int> values_;
};

} // namespace just::demo::generator
