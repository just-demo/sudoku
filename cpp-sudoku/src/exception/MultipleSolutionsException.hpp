#pragma once

#include <stdexcept>
#include <string>

namespace just::demo::exception {

class MultipleSolutionsException : public std::runtime_error {
public:
    MultipleSolutionsException() : std::runtime_error("Multiple solutions found") {}
    explicit MultipleSolutionsException(const std::string& message) : std::runtime_error(message) {}
};

} // namespace just::demo::exception
