#pragma once

#include <stdexcept>
#include <string>

namespace sudoku {

class MultipleSolutionsException : public std::runtime_error {
public:
    MultipleSolutionsException() : std::runtime_error("Multiple solutions found") {}
};

class NoSolutionException : public std::runtime_error {
public:
    NoSolutionException() : std::runtime_error("No solution exists") {}
};

class TimeLimitException : public std::runtime_error {
public:
    TimeLimitException() : std::runtime_error("Time limit exceeded") {}
};

class ComplexityLimitException : public std::runtime_error {
public:
    ComplexityLimitException() : std::runtime_error("Complexity limit exceeded") {}
};

} // namespace sudoku
