#pragma once

#include <vector>
#include <list>
#include <unordered_map>
#include <unordered_set>
#include <algorithm>
#include <optional>
#include <memory>
#include "exceptions.hpp"

namespace sudoku {

class CleverSolver {
private:
    struct Cell;
    struct Value;
    
    struct CannotOpenWithoutGuessingException : public std::runtime_error {
        Cell* cell;
        Value* value;
        
        CannotOpenWithoutGuessingException(Cell* c, Value* v) 
            : std::runtime_error("Cannot open without guessing"), cell(c), value(v) {}
    };
    
    struct Cell {
        int row, col, block;
        Value* value = nullptr;
        std::unordered_set<Value*> candidates;
        
        Cell(int r, int c, int b) : row(r), col(c), block(b) {}
        
        void addCandidates(const std::vector<Value*>& candidates) {
            this->candidates.insert(candidates.begin(), candidates.end());
        }
        
        void open(Value* val);
        bool isRelated(const Cell* cell) const;
        void removeCandidate(Value* val);
        size_t countCandidates() const { return candidates.size(); }
        Value* getCandidate() const { return *candidates.begin(); }
        std::optional<int> getValue() const { 
            return value ? std::optional<int>(value->value) : std::nullopt; 
        }
    };
    
    struct Value {
        int value;
        std::unordered_set<Cell*> cells;
        std::vector<std::unordered_set<Cell*>> candidates;
        
        Value(int v) : value(v) {}
        
        void addCandidates(const std::vector<Cell*>& cells);
        void removeCandidate(Cell* cell);
        void open(Cell* cell);
        size_t countCandidates() const;
        Cell* getCandidate() const;
        std::unordered_set<Cell*> getCandidates() const;
    };
    
    int size;
    int blockSize;
    std::vector<std::unique_ptr<Cell>> allCells;
    std::list<Cell*> pendingCells;
    std::list<Value*> pendingValues;
    std::unordered_map<int, std::unique_ptr<Value>> valueMap;
    
    void openNext();
    std::vector<std::vector<std::optional<int>>> solveWithGuess(Cell* cell, Value* value);
    std::vector<std::vector<std::optional<int>>> copyState() const;
    
public:
    explicit CleverSolver(const std::vector<std::vector<std::optional<int>>>& initialValues);
    std::vector<std::vector<std::optional<int>>> solve();
};

} // namespace sudoku
