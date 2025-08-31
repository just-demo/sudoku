#pragma once

#include <vector>
#include <list>
#include <unordered_map>
#include <unordered_set>
#include <memory>
#include "../exception/MultipleSolutionsException.hpp"
#include "../exception/NoSolutionException.hpp"
#include "../exception/TimeLimitException.hpp"

namespace just::demo::solver {

class Solver {
public:
    explicit Solver(const std::vector<std::vector<int>>& initialValues);
    std::vector<std::vector<int>> solve();

private:
    class Cell;
    class Value;
    
    struct CannotOpenWithoutGuessingException : public std::runtime_error {
        std::shared_ptr<Cell> cell;
        std::shared_ptr<Value> value;
        
        CannotOpenWithoutGuessingException(std::shared_ptr<Cell> c, std::shared_ptr<Value> v)
            : std::runtime_error("Cannot open without guessing"), cell(std::move(c)), value(std::move(v)) {}
    };

    class Cell {
    public:
        Cell(int row, int col, int block);
        
        int getRow() const { return row_; }
        int getCol() const { return col_; }
        int getBlock() const { return block_; }
        int getValue() const { return value_ ? value_->getValue() : 0; }
        
        void addCandidates(const std::vector<std::shared_ptr<Value>>& candidates);
        void open(std::shared_ptr<Value> value);
        bool isRelated(const std::shared_ptr<Cell>& cell) const;
        void removeCandidate(std::shared_ptr<Value> value);
        int countCandidates() const { return candidates_.size(); }
        std::shared_ptr<Value> getCandidate() const;
        std::vector<std::shared_ptr<Value>> getCandidates() const;
        
    private:
        int row_;
        int col_;
        int block_;
        std::shared_ptr<Value> value_;
        std::vector<std::shared_ptr<Value>> candidates_;
    };

    class Value {
    public:
        explicit Value(int value);
        
        int getValue() const { return value_; }
        void addCandidates(const std::vector<std::shared_ptr<Cell>>& candidates);
        void removeCandidate(std::shared_ptr<Cell> cell);
        void open(std::shared_ptr<Cell> cell);
        int countCandidates() const;
        std::shared_ptr<Cell> getCandidate() const;
        std::vector<std::shared_ptr<Cell>> getCandidates() const;
        bool isComplete(int size) const;
        
    private:
        int value_;
        std::vector<std::shared_ptr<Cell>> cells_;
        std::vector<std::vector<std::shared_ptr<Cell>>> candidates_;
    };

    void openNext();
    std::vector<std::vector<int>> solveWithGuess(std::shared_ptr<Cell> cell, std::shared_ptr<Value> value);
    std::vector<std::vector<int>> copyState() const;

    int size_;
    int blockSize_;
    std::vector<std::shared_ptr<Cell>> allCells_;
    std::list<std::shared_ptr<Cell>> pendingCells_;
    std::list<std::shared_ptr<Value>> pendingValues_;
};

} // namespace just::demo::solver
