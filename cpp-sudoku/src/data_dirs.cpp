#include "data_dirs.hpp"

namespace sudoku {

const std::filesystem::path DataDirs::DATA_DIR = "data";
const std::filesystem::path DataDirs::READY_DIR = DATA_DIR / "ready";
const std::filesystem::path DataDirs::REDUCER_FAILED_DIR = DATA_DIR / "reducer-failed";
const std::filesystem::path DataDirs::REDUCER_FIXED_DIR = DATA_DIR / "reducer-fixed";

} // namespace sudoku
