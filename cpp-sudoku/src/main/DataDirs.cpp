#include "DataDirs.hpp"

namespace just::demo::main {

const std::filesystem::path DataDirs::DATA_DIR = std::filesystem::path("data");
const std::filesystem::path DataDirs::READY_DIR = DATA_DIR / "ready";
const std::filesystem::path DataDirs::REDUCER_FAILED_DIR = DATA_DIR / "reducer-failed";
const std::filesystem::path DataDirs::REDUCER_FIXED_DIR = DATA_DIR / "reducer-fixed";

} // namespace just::demo::main
