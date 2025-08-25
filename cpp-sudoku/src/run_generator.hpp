#pragma once

#include <chrono>
#include <map>
#include <atomic>

namespace sudoku {

class RunGenerator {
public:
    static void generateMany();

private:
    static constexpr std::chrono::seconds GENERATOR_TIMEOUT{3};
    static constexpr std::chrono::minutes REDUCER_TIMEOUT{1};
};

} // namespace sudoku
