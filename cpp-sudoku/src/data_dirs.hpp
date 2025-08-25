#pragma once

#include <filesystem>

namespace sudoku {

class DataDirs {
public:
    static const std::filesystem::path DATA_DIR;
    static const std::filesystem::path READY_DIR;
    static const std::filesystem::path REDUCER_FAILED_DIR;
    static const std::filesystem::path REDUCER_FIXED_DIR;
};

} // namespace sudoku
