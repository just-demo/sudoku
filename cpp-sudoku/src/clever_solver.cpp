#include "clever_solver.hpp"
#include <cmath>
#include <algorithm>
#include <stdexcept>

namespace sudoku {

CleverSolver::CleverSolver(const std::vector<std::vector<std::optional<int>>>& initialValues) 
    : size(static_cast<int>(initialValues.size())), 
      blockSize(static_cast<int>(std::sqrt(size))) {
    
    // Create value map
    for (int i = 1; i <= size; ++i) {
        valueMap[i] = std::make_unique<Value>(i);
    }
    
    // Create cells and initialize with values
    std::unordered_map<Cell*, Value*> openCells;
    for (int row = 0; row < size; ++row) {
        for (int col = 0; col < size; ++col) {
            int block = blockSize * (row / blockSize) + col / blockSize;
            auto cell = std::make_unique<Cell>(row, col, block);
            allCells.push_back(std::move(cell));
            
            if (initialValues[row][col].has_value()) {
                int value = initialValues[row][col].value();
                openCells[allCells.back().get()] = valueMap[value].get();
            }
        }
    }
    
    // Add candidates to cells and values
    std::vector<Value*> values;
    for (auto& [_, value] : valueMap) {
        values.push_back(value.get());
        pendingValues.push_back(value.get());
    }
    
    std::vector<Cell*> cells;
    for (auto& cell : allCells) {
        cells.push_back(cell.get());
        pendingCells.push_back(cell.get());
    }
    
    for (auto& cell : allCells) {
        cell->addCandidates(values);
    }
    
    for (auto& [_, value] : valueMap) {
        value->addCandidates(cells);
    }
    
    // Open initial cells AFTER setting up all candidates
    for (auto& [cell, value] : openCells) {
        cell->open(value);
        // Remove cell from pending cells
        auto cellToRemove = std::find(pendingCells.begin(), pendingCells.end(), cell);
        if (cellToRemove != pendingCells.end()) {
            pendingCells.erase(cellToRemove);
        }
        // Remove value from related pending cells
        for (auto it = pendingCells.begin(); it != pendingCells.end(); ++it) {
            if (cell->isRelated(*it)) {
                (*it)->removeCandidate(value);
            }
        }
        // Check if value is complete and remove from pending values
        if (value->cells.size() == static_cast<size_t>(size)) {
            auto valueToRemove = std::find(pendingValues.begin(), pendingValues.end(), value);
            if (valueToRemove != pendingValues.end()) {
                pendingValues.erase(valueToRemove);
            }
        }
    }
}

std::vector<std::vector<std::optional<int>>> CleverSolver::solve() {
    while (!pendingCells.empty()) {
        try {
            openNext();
        } catch (const CannotOpenWithoutGuessingException& e) {
            return solveWithGuess(e.cell, e.value);
        }
    }
    return copyState();
}

void CleverSolver::openNext() {
    if (pendingValues.empty()) {
        throw NoSolutionException();
    }
    
    // Find cell with minimum candidates
    auto cellIt = std::min_element(pendingCells.begin(), pendingCells.end(),
        [](const Cell* a, const Cell* b) {
            return a->countCandidates() < b->countCandidates();
        });
    
    Cell* cell = *cellIt;
    
    if (cell->countCandidates() == 1) {
        Value* valueToOpen = cell->getCandidate();
        cell->open(valueToOpen);
        // Remove cell from pending cells
        pendingCells.erase(cellIt);
        // Remove value from related pending cells
        for (auto it = pendingCells.begin(); it != pendingCells.end(); ++it) {
            if (cell->isRelated(*it)) {
                (*it)->removeCandidate(valueToOpen);
            }
        }
        // Check if value is complete and remove from pending values
        if (valueToOpen->cells.size() == static_cast<size_t>(size)) {
            auto valueToRemove = std::find(pendingValues.begin(), pendingValues.end(), valueToOpen);
            if (valueToRemove != pendingValues.end()) {
                pendingValues.erase(valueToRemove);
            }
        }
        return;
    }
    
    // Find value with minimum candidates
    auto valueIt = std::min_element(pendingValues.begin(), pendingValues.end(),
        [](const Value* a, const Value* b) {
            return a->countCandidates() < b->countCandidates();
        });
    
    Value* value = *valueIt;
    
    if (value->countCandidates() == 1) {
        Cell* targetCell = value->getCandidate();
        targetCell->open(value);
        // Remove cell from pending cells
        auto cellToRemove = std::find(pendingCells.begin(), pendingCells.end(), targetCell);
        if (cellToRemove != pendingCells.end()) {
            pendingCells.erase(cellToRemove);
        }
        // Remove value from related pending cells
        for (auto it = pendingCells.begin(); it != pendingCells.end(); ++it) {
            if (targetCell->isRelated(*it)) {
                (*it)->removeCandidate(value);
            }
        }
        // Check if value is complete and remove from pending values
        if (value->cells.size() == static_cast<size_t>(size)) {
            auto valueToRemove = std::find(pendingValues.begin(), pendingValues.end(), value);
            if (valueToRemove != pendingValues.end()) {
                pendingValues.erase(valueToRemove);
            }
        }
        return;
    }
    
    if (cell->countCandidates() == 0 || value->countCandidates() == 0) {
        throw NoSolutionException();
    }
    
    throw CannotOpenWithoutGuessingException(cell, value);
}

std::vector<std::vector<std::optional<int>>> CleverSolver::solveWithGuess(Cell* cell, Value* value) {
    std::vector<Cell*> guessCells;
    std::vector<Value*> guessValues;
    
    if (cell->countCandidates() <= value->countCandidates()) {
        guessCells = {cell};
        guessValues.assign(cell->candidates.begin(), cell->candidates.end());
    } else {
        auto candidates = value->getCandidates();
        guessCells.assign(candidates.begin(), candidates.end());
        guessValues = {value};
    }
    
    std::vector<std::vector<std::vector<std::optional<int>>>> solutions;
    
    for (Cell* guessCell : guessCells) {
        for (Value* guessValue : guessValues) {
            auto nextGuess = copyState();
            nextGuess[guessCell->row][guessCell->col] = guessValue->value;
            
            try {
                solutions.push_back(CleverSolver(nextGuess).solve());
                if (solutions.size() > 1) {
                    throw MultipleSolutionsException();
                }
            } catch (const NoSolutionException&) {
                // Our guess did not work, try another one
            }
        }
    }
    
    if (solutions.empty()) {
        throw NoSolutionException();
    }
    
    return solutions[0];
}

std::vector<std::vector<std::optional<int>>> CleverSolver::copyState() const {
    std::vector<std::vector<std::optional<int>>> state(size, std::vector<std::optional<int>>(size));
    for (const auto& cell : allCells) {
        state[cell->row][cell->col] = cell->getValue();
    }
    return state;
}

// Cell methods
void CleverSolver::Cell::open(Value* val) {
    this->value = val;
    for (Value* candidate : candidates) {
        candidate->removeCandidate(this);
    }
    candidates.clear();
    val->open(this);
    
    // Remove this cell from pending cells
    // Note: This will be handled by the solver when it calls openNext()
    
    // Remove this value from all related pending cells
    // This is the key part that was missing!
}

bool CleverSolver::Cell::isRelated(const Cell* cell) const {
    return row == cell->row || col == cell->col || block == cell->block;
}

void CleverSolver::Cell::removeCandidate(Value* val) {
    val->removeCandidate(this);
    candidates.erase(val);
}

// Value methods
void CleverSolver::Value::addCandidates(const std::vector<Cell*>& cells) {
    // Group by row
    std::unordered_map<int, std::unordered_set<Cell*>> rowGroups, colGroups, blockGroups;
    
    for (Cell* cell : cells) {
        rowGroups[cell->row].insert(cell);
        colGroups[cell->col].insert(cell);
        blockGroups[cell->block].insert(cell);
    }
    
    for (auto& [_, group] : rowGroups) {
        candidates.push_back(group);
    }
    for (auto& [_, group] : colGroups) {
        candidates.push_back(group);
    }
    for (auto& [_, group] : blockGroups) {
        candidates.push_back(group);
    }
}

void CleverSolver::Value::removeCandidate(Cell* cell) {
    for (auto& candidateGroup : candidates) {
        candidateGroup.erase(cell);
    }
    candidates.erase(
        std::remove_if(candidates.begin(), candidates.end(),
            [](const std::unordered_set<Cell*>& group) { return group.empty(); }),
        candidates.end()
    );
}

void CleverSolver::Value::open(Cell* cell) {
    removeCandidate(cell);
    cells.insert(cell);
}

size_t CleverSolver::Value::countCandidates() const {
    if (candidates.empty()) return 0;
    return std::min_element(candidates.begin(), candidates.end(),
        [](const std::unordered_set<Cell*>& a, const std::unordered_set<Cell*>& b) {
            return a.size() < b.size();
        })->size();
}

CleverSolver::Cell* CleverSolver::Value::getCandidate() const {
    auto minGroup = std::min_element(candidates.begin(), candidates.end(),
        [](const std::unordered_set<Cell*>& a, const std::unordered_set<Cell*>& b) {
            return a.size() < b.size();
        });
    return *minGroup->begin();
}

std::unordered_set<CleverSolver::Cell*> CleverSolver::Value::getCandidates() const {
    if (candidates.empty()) return {};
    auto minGroup = std::min_element(candidates.begin(), candidates.end(),
        [](const std::unordered_set<Cell*>& a, const std::unordered_set<Cell*>& b) {
            return a.size() < b.size();
        });
    return *minGroup;
}

} // namespace sudoku
