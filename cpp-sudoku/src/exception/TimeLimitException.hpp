#pragma once

#include <stdexcept>
#include <string>

namespace just::demo::exception {

class TimeLimitException : public std::runtime_error {
public:
    TimeLimitException() : std::runtime_error("Time limit exceeded") {}
    explicit TimeLimitException(const std::string& message) : std::runtime_error(message) {}
};

} // namespace just::demo::exception
