#pragma once

#include <unordered_set>
#include <optional>

namespace sudoku {

class Cell {
private:
    int row;
    int col;
    int block;
    std::unordered_set<int> candidates;

public:
    Cell(int row, int col, int block, const std::unordered_set<int>& candidates = {});
    
    int getRow() const { return row; }
    int getCol() const { return col; }
    int getBlock() const { return block; }
    const std::unordered_set<int>& getCandidates() const { return candidates; }
    
    bool isRelated(const Cell& cell) const;
    std::optional<int> getCandidate() const;
    
    // For use in containers
    bool operator==(const Cell& other) const {
        return row == other.row && col == other.col && block == other.block;
    }
};

} // namespace sudoku

// Hash function for Cell
namespace std {
    template<>
    struct hash<sudoku::Cell> {
        size_t operator()(const sudoku::Cell& cell) const {
            return hash<int>()(cell.getRow()) ^ 
                   (hash<int>()(cell.getCol()) << 1) ^ 
                   (hash<int>()(cell.getBlock()) << 2);
        }
    };
}
