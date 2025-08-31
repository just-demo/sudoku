#pragma once

#include <stdexcept>
#include <string>

namespace just::demo::exception {

class ComplexityLimitException : public std::runtime_error {
public:
    ComplexityLimitException() : std::runtime_error("Complexity limit exceeded") {}
    explicit ComplexityLimitException(const std::string& message) : std::runtime_error(message) {}
};

} // namespace just::demo::exception
