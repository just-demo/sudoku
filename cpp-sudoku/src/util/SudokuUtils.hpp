#pragma once

#include <vector>
#include <string>
#include <sstream>
#include <algorithm>
#include <numeric>

namespace just::demo::util {

class SudokuUtils {
public:
    static std::string toString1D(const std::vector<std::vector<int>>& matrix);
    static std::string toString2D(const std::vector<std::vector<int>>& matrix);
    static std::vector<std::vector<int>> fromString1D(const std::string& flat);
    static std::vector<std::vector<int>> fromString2D(const std::string& file);
    static long countOpen(const std::vector<std::vector<int>>& matrix);
    static std::vector<std::vector<int>> copy(const std::vector<std::vector<int>>& source);

private:
    static constexpr const char* EMPTY_AS_STRING = ".";
    static std::vector<int> parseLine2D(const std::string& line);
    static int cellFromString(const std::string& cell);
    static std::string cellToString(int cell);
};

} // namespace just::demo::util
