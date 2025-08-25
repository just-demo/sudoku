#pragma once

#include <vector>
#include <string>
#include <sstream>
#include <cmath>
#include <optional>
#include <filesystem>
#include <fstream>
#include <algorithm>
#include <chrono>
#include <iomanip>
#include <sstream>

namespace sudoku {

class Utils {
public:
    static constexpr const char* EMPTY_VALUE = ".";
    
    // Matrix conversion functions
    static std::string asString(const std::vector<std::vector<std::optional<int>>>& matrix);
    static std::string toString1D(const std::vector<std::vector<std::optional<int>>>& matrix);
    static std::string toString2D(const std::vector<std::vector<std::optional<int>>>& matrix);
    
    // Parsing functions
    static std::vector<std::vector<std::optional<int>>> parseSimpleString(const std::string& flat);
    static std::vector<std::vector<std::optional<int>>> fromString1D(const std::string& flat);
    static std::vector<std::vector<std::optional<int>>> fromString2D(const std::string& file);
    
    // Utility functions
    static long countOpen(const std::vector<std::vector<std::optional<int>>>& matrix);
    static std::vector<std::vector<std::optional<int>>> copy(const std::vector<std::vector<std::optional<int>>>& source);
    static std::string getCurrentTime();
    static std::string join(const std::string& delimiter, const std::vector<std::string>& items);
    
    // File utility functions
    static std::string readFile(const std::filesystem::path& file);
    static void writeFile(const std::filesystem::path& file, const std::string& data);
    static void appendFile(const std::filesystem::path& file, const std::string& data);
    static std::vector<std::filesystem::path> listFiles(const std::filesystem::path& dir);
    
private:
    static std::vector<std::optional<int>> parseLine2D(const std::string& line);
    static std::optional<int> parseCell(const std::string& cell);
};

} // namespace sudoku
