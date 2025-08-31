#include "Solver.hpp"
#include <algorithm>
#include <cmath>
#include <stdexcept>

namespace just::demo::solver {

// Cell implementation
Solver::Cell::Cell(int row, int col, int block) 
    : row_(row), col_(col), block_(block), value_(nullptr) {}

void Solver::Cell::addCandidates(const std::vector<std::shared_ptr<Value>>& candidates) {
    candidates_ = candidates;
}

void Solver::Cell::open(std::shared_ptr<Value> value) {
    value_ = value;
    for (auto& candidate : candidates_) {
        if (candidate) {
            candidate->removeCandidate(std::make_shared<Cell>(*this));
        }
    }
    candidates_.clear();
    value->open(std::make_shared<Cell>(*this));
}

bool Solver::Cell::isRelated(const std::shared_ptr<Cell>& cell) const {
    return row_ == cell->row_ || col_ == cell->col_ || block_ == cell->block_;
}

void Solver::Cell::removeCandidate(std::shared_ptr<Value> value) {
    value->removeCandidate(std::make_shared<Cell>(*this));
    candidates_.erase(
        std::remove(candidates_.begin(), candidates_.end(), value),
        candidates_.end()
    );
}

std::shared_ptr<Solver::Value> Solver::Cell::getCandidate() const {
    return candidates_.empty() ? nullptr : candidates_.front();
}

std::vector<std::shared_ptr<Solver::Value>> Solver::Cell::getCandidates() const {
    return candidates_;
}

// Value implementation
Solver::Value::Value(int value) : value_(value) {}

void Solver::Value::addCandidates(const std::vector<std::shared_ptr<Cell>>& candidates) {
    // Group by row
    std::unordered_map<int, std::vector<std::shared_ptr<Cell>>> rowGroups;
    for (const auto& cell : candidates) {
        rowGroups[cell->getRow()].push_back(cell);
    }
    for (const auto& [_, group] : rowGroups) {
        candidates_.push_back(group);
    }
    
    // Group by column
    std::unordered_map<int, std::vector<std::shared_ptr<Cell>>> colGroups;
    for (const auto& cell : candidates) {
        colGroups[cell->getCol()].push_back(cell);
    }
    for (const auto& [_, group] : colGroups) {
        candidates_.push_back(group);
    }
    
    // Group by block
    std::unordered_map<int, std::vector<std::shared_ptr<Cell>>> blockGroups;
    for (const auto& cell : candidates) {
        blockGroups[cell->getBlock()].push_back(cell);
    }
    for (const auto& [_, group] : blockGroups) {
        candidates_.push_back(group);
    }
}

void Solver::Value::removeCandidate(std::shared_ptr<Cell> cell) {
    for (auto& group : candidates_) {
        group.erase(
            std::remove(group.begin(), group.end(), cell),
            group.end()
        );
    }
    candidates_.erase(
        std::remove_if(candidates_.begin(), candidates_.end(),
            [](const std::vector<std::shared_ptr<Cell>>& group) { return group.empty(); }),
        candidates_.end()
    );
}



void Solver::Value::open(std::shared_ptr<Cell> cell) {
    removeCandidate(cell);
    cells_.push_back(cell);
}

int Solver::Value::countCandidates() const {
    if (candidates_.empty()) return 0;
    return std::min_element(candidates_.begin(), candidates_.end(),
        [](const auto& a, const auto& b) { return a.size() < b.size(); })->size();
}

std::shared_ptr<Solver::Cell> Solver::Value::getCandidate() const {
    auto minGroup = std::min_element(candidates_.begin(), candidates_.end(),
        [](const auto& a, const auto& b) { return a.size() < b.size(); });
    return minGroup->empty() ? nullptr : minGroup->front();
}

std::vector<std::shared_ptr<Solver::Cell>> Solver::Value::getCandidates() const {
    auto minGroup = std::min_element(candidates_.begin(), candidates_.end(),
        [](const auto& a, const auto& b) { return a.size() < b.size(); });
    return minGroup != candidates_.end() ? *minGroup : std::vector<std::shared_ptr<Cell>>{};
}

bool Solver::Value::isComplete(int size) const {
    return cells_.size() == size;
}

// Solver implementation
Solver::Solver(const std::vector<std::vector<int>>& initialValues) {
    size_ = static_cast<int>(initialValues.size());
    blockSize_ = static_cast<int>(std::sqrt(size_));
    
    // Create value map
    std::unordered_map<int, std::shared_ptr<Value>> valueMap;
    for (int i = 1; i <= size_; ++i) {
        valueMap[i] = std::make_shared<Value>(i);
    }
    
    // Create cells and initialize
    std::unordered_map<std::shared_ptr<Cell>, std::shared_ptr<Value>> openCells;
    for (int row = 0; row < size_; ++row) {
        for (int col = 0; col < size_; ++col) {
            int block = blockSize_ * (row / blockSize_) + col / blockSize_;
            auto cell = std::make_shared<Cell>(row, col, block);
            allCells_.push_back(cell);
            
            int value = initialValues[row][col];
            if (value != 0) {
                openCells[cell] = valueMap[value];
            }
        }
    }
    
    // Add all cells and values to pending lists
    for (const auto& cell : allCells_) {
        pendingCells_.push_back(cell);
    }
    for (const auto& [_, value] : valueMap) {
        pendingValues_.push_back(value);
    }
    
    // Initialize candidates
    std::vector<std::shared_ptr<Value>> allValues;
    for (const auto& [_, value] : valueMap) {
        allValues.push_back(value);
    }
    for (const auto& cell : allCells_) {
        cell->addCandidates(allValues);
    }
    
    std::vector<std::shared_ptr<Cell>> allCells;
    for (const auto& cell : allCells_) {
        allCells.push_back(cell);
    }
    for (const auto& value : allValues) {
        value->addCandidates(allCells);
    }
    
    // Open initial cells
    for (const auto& [cell, value] : openCells) {
        cell->open(value);
        pendingCells_.remove(cell);
        
        // Remove related cells' candidates
        for (auto& pendingCell : pendingCells_) {
            if (cell->isRelated(pendingCell)) {
                pendingCell->removeCandidate(value);
            }
        }
        
        // Remove value from pending values if complete
        if (value->isComplete(size_)) {
            pendingValues_.remove(value);
        }
    }
}

std::vector<std::vector<int>> Solver::solve() {
    while (!pendingCells_.empty()) {
        try {
            openNext();
        } catch (const CannotOpenWithoutGuessingException& e) {
            return solveWithGuess(e.cell, e.value);
        }
    }
    return copyState();
}

void Solver::openNext() {
    if (pendingValues_.empty()) {
        throw just::demo::exception::NoSolutionException();
    }
    
    // Find cell with minimum candidates
    auto minCell = std::min_element(pendingCells_.begin(), pendingCells_.end(),
        [](const auto& a, const auto& b) { return a->countCandidates() < b->countCandidates(); });
    
    if ((*minCell)->countCandidates() == 1) {
        auto cell = *minCell;
        auto value = cell->getCandidate();
        cell->open(value);
        pendingCells_.remove(cell);
        
        // Remove related cells' candidates
        for (auto& pendingCell : pendingCells_) {
            if (cell->isRelated(pendingCell)) {
                pendingCell->removeCandidate(value);
            }
        }
        
        // Remove value from pending values if complete
        if (value->isComplete(size_)) {
            pendingValues_.remove(value);
        }
        return;
    }
    
    // Find value with minimum candidates
    auto minValue = std::min_element(pendingValues_.begin(), pendingValues_.end(),
        [](const auto& a, const auto& b) { return a->countCandidates() < b->countCandidates(); });
    
    if ((*minValue)->countCandidates() == 1) {
        auto value = *minValue;
        auto cell = value->getCandidate();
        cell->open(value);
        pendingCells_.remove(cell);
        
        // Remove related cells' candidates
        for (auto& pendingCell : pendingCells_) {
            if (cell->isRelated(pendingCell)) {
                pendingCell->removeCandidate(value);
            }
        }
        
        // Remove value from pending values if complete
        if (value->isComplete(size_)) {
            pendingValues_.remove(value);
        }
        return;
    }
    
    if ((*minCell)->countCandidates() == 0 || (*minValue)->countCandidates() == 0) {
        throw just::demo::exception::NoSolutionException();
    }
    
    throw CannotOpenWithoutGuessingException(*minCell, *minValue);
}

std::vector<std::vector<int>> Solver::solveWithGuess(std::shared_ptr<Cell> cell, std::shared_ptr<Value> value) {
    std::vector<std::shared_ptr<Cell>> guessCells;
    std::vector<std::shared_ptr<Value>> guessValues;
    
    if (cell->countCandidates() <= value->countCandidates()) {
        guessCells = {cell};
        guessValues = cell->getCandidates();
    } else {
        guessCells = value->getCandidates();
        guessValues = {value};
    }
    
    std::vector<std::vector<std::vector<int>>> solutions;
    
    for (const auto& guessCell : guessCells) {
        for (const auto& guessValue : guessValues) {
            auto nextGuess = copyState();
            nextGuess[guessCell->getRow()][guessCell->getCol()] = guessValue->getValue();
            
            try {
                solutions.push_back(Solver(nextGuess).solve());
                if (solutions.size() > 1) {
                    throw just::demo::exception::MultipleSolutionsException();
                }
            } catch (const just::demo::exception::NoSolutionException&) {
                // Our guess did not work, let's try another one
            }
        }
    }
    
    if (solutions.empty()) {
        throw just::demo::exception::NoSolutionException();
    }
    
    return solutions.front();
}

std::vector<std::vector<int>> Solver::copyState() const {
    std::vector<std::vector<int>> state(size_, std::vector<int>(size_));
    for (const auto& cell : allCells_) {
        state[cell->getRow()][cell->getCol()] = cell->getValue();
    }
    return state;
}

} // namespace just::demo::solver
