#include "cell.hpp"

namespace sudoku {

Cell::Cell(int row, int col, int block, const std::unordered_set<int>& candidates)
    : row(row), col(col), block(block), candidates(candidates) {}

bool Cell::isRelated(const Cell& cell) const {
    return row == cell.row || col == cell.col || block == cell.block;
}

std::optional<int> Cell::getCandidate() const {
    if (candidates.empty()) {
        return std::nullopt;
    }
    return *candidates.begin();
}

} // namespace sudoku
