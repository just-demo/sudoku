#include "utils.hpp"
#include <sstream>
#include <algorithm>
#include <filesystem>
#include <fstream>
#include <chrono>
#include <iomanip>

namespace sudoku {

std::string Utils::asString(const std::vector<std::vector<std::optional<int>>>& matrix) {
    std::ostringstream oss;
    for (size_t i = 0; i < matrix.size(); ++i) {
        for (size_t j = 0; j < matrix[i].size(); ++j) {
            if (j > 0) oss << " ";
            if (matrix[i][j].has_value()) {
                oss << matrix[i][j].value();
            } else {
                oss << EMPTY_VALUE;
            }
        }
        if (i < matrix.size() - 1) oss << "\n";
    }
    return oss.str();
}

std::string Utils::toString1D(const std::vector<std::vector<std::optional<int>>>& matrix) {
    std::ostringstream oss;
    for (const auto& row : matrix) {
        for (const auto& cell : row) {
            if (cell.has_value()) {
                oss << cell.value();
            } else {
                oss << EMPTY_VALUE;
            }
        }
    }
    return oss.str();
}

std::string Utils::toString2D(const std::vector<std::vector<std::optional<int>>>& matrix) {
    return asString(matrix);
}

std::vector<std::vector<std::optional<int>>> Utils::parseSimpleString(const std::string& flat) {
    int size = static_cast<int>(std::sqrt(flat.length()));
    std::vector<std::vector<std::optional<int>>> matrix(size, std::vector<std::optional<int>>(size));
    
    for (int row = 0; row < size; ++row) {
        for (int col = 0; col < size; ++col) {
            char value = flat[row * size + col];
            if (value == '.' || value == '0') {
                matrix[row][col] = std::nullopt;
            } else {
                matrix[row][col] = value - '0';
            }
        }
    }
    return matrix;
}

std::vector<std::vector<std::optional<int>>> Utils::fromString1D(const std::string& flat) {
    return parseSimpleString(flat);
}

std::vector<std::vector<std::optional<int>>> Utils::fromString2D(const std::string& file) {
    std::istringstream iss(file);
    std::string line;
    std::vector<std::vector<std::optional<int>>> matrix;
    
    while (std::getline(iss, line)) {
        if (!line.empty()) {
            matrix.push_back(parseLine2D(line));
        }
    }
    
    return matrix;
}

long Utils::countOpen(const std::vector<std::vector<std::optional<int>>>& matrix) {
    long count = 0;
    for (const auto& row : matrix) {
        for (const auto& cell : row) {
            if (cell.has_value()) {
                count++;
            }
        }
    }
    return count;
}

std::vector<std::vector<std::optional<int>>> Utils::copy(const std::vector<std::vector<std::optional<int>>>& source) {
    return source; // std::vector has copy constructor
}

std::string Utils::getCurrentTime() {
    auto now = std::chrono::system_clock::now();
    auto time_t = std::chrono::system_clock::to_time_t(now);
    auto tm = *std::localtime(&time_t);
    
    std::ostringstream oss;
    oss << std::put_time(&tm, "%Y%m%d-%H%M%S");
    return oss.str();
}

std::string Utils::join(const std::string& delimiter, const std::vector<std::string>& items) {
    std::ostringstream oss;
    for (size_t i = 0; i < items.size(); ++i) {
        if (i > 0) oss << delimiter;
        oss << items[i];
    }
    return oss.str();
}

std::string Utils::readFile(const std::filesystem::path& file) {
    std::ifstream ifs(file);
    if (!ifs.is_open()) {
        throw std::runtime_error("Cannot open file: " + file.string());
    }
    
    std::ostringstream oss;
    oss << ifs.rdbuf();
    return oss.str();
}

void Utils::writeFile(const std::filesystem::path& file, const std::string& data) {
    std::filesystem::create_directories(file.parent_path());
    std::ofstream ofs(file);
    if (!ofs.is_open()) {
        throw std::runtime_error("Cannot create file: " + file.string());
    }
    ofs << data;
}

void Utils::appendFile(const std::filesystem::path& file, const std::string& data) {
    std::filesystem::create_directories(file.parent_path());
    std::ofstream ofs(file, std::ios::app);
    if (!ofs.is_open()) {
        throw std::runtime_error("Cannot append to file: " + file.string());
    }
    ofs << data;
}

std::vector<std::filesystem::path> Utils::listFiles(const std::filesystem::path& dir) {
    std::vector<std::filesystem::path> files;
    if (std::filesystem::is_directory(dir)) {
        for (const auto& entry : std::filesystem::recursive_directory_iterator(dir)) {
            if (std::filesystem::is_regular_file(entry)) {
                files.push_back(entry.path());
            }
        }
    }
    return files;
}

std::vector<std::optional<int>> Utils::parseLine2D(const std::string& line) {
    std::istringstream iss(line);
    std::string cell;
    std::vector<std::optional<int>> result;
    
    while (iss >> cell) {
        result.push_back(parseCell(cell));
    }
    
    return result;
}

std::optional<int> Utils::parseCell(const std::string& cell) {
    if (cell == EMPTY_VALUE) {
        return std::nullopt;
    }
    return std::stoi(cell);
}

} // namespace sudoku
