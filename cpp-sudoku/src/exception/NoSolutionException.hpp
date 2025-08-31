#pragma once

#include <stdexcept>
#include <string>

namespace just::demo::exception {

class NoSolutionException : public std::runtime_error {
public:
    NoSolutionException() : std::runtime_error("No solution found") {}
    explicit NoSolutionException(const std::string& message) : std::runtime_error(message) {}
};

} // namespace just::demo::exception
