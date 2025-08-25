#pragma once

#include <chrono>

namespace sudoku {

class RunReducer {
public:
    static void reduceFailedByGeneratorTimeout();
    static void copyReducedToReady();
};

} // namespace sudoku
