#include "SudokuUtils.hpp"
#include <cmath>
#include <iostream>

namespace just::demo::util {

std::string SudokuUtils::toString1D(const std::vector<std::vector<int>>& matrix) {
    std::string result;
    for (const auto& row : matrix) {
        for (int cell : row) {
            result += cellToString(cell);
        }
    }
    return result;
}

std::string SudokuUtils::toString2D(const std::vector<std::vector<int>>& matrix) {
    std::string result;
    for (size_t i = 0; i < matrix.size(); ++i) {
        for (size_t j = 0; j < matrix[i].size(); ++j) {
            result += cellToString(matrix[i][j]);
            if (j < matrix[i].size() - 1) {
                result += " ";
            }
        }
        if (i < matrix.size() - 1) {
            result += "\n";
        }
    }
    return result;
}

std::vector<std::vector<int>> SudokuUtils::fromString1D(const std::string& flat) {
    std::vector<std::string> values;
    for (char c : flat) {
        values.emplace_back(1, c);
    }
    
    int size = static_cast<int>(std::sqrt(values.size()));
    std::vector<std::vector<int>> matrix(size, std::vector<int>(size));
    
    for (int row = 0; row < size; row++) {
        for (int col = 0; col < size; col++) {
            std::string value = values[row * size + col];
            matrix[row][col] = cellFromString(value);
        }
    }
    return matrix;
}

std::vector<std::vector<int>> SudokuUtils::fromString2D(const std::string& file) {
    std::istringstream iss(file);
    std::string line;
    std::vector<std::vector<int>> matrix;
    
    while (std::getline(iss, line)) {
        if (!line.empty()) {
            matrix.push_back(parseLine2D(line));
        }
    }
    return matrix;
}

long SudokuUtils::countOpen(const std::vector<std::vector<int>>& matrix) {
    long count = 0;
    for (const auto& row : matrix) {
        count += std::count_if(row.begin(), row.end(), [](int v) { return v != 0; });
    }
    return count;
}

std::vector<std::vector<int>> SudokuUtils::copy(const std::vector<std::vector<int>>& source) {
    std::vector<std::vector<int>> result;
    result.reserve(source.size());
    for (const auto& row : source) {
        result.push_back(row);
    }
    return result;
}

std::vector<int> SudokuUtils::parseLine2D(const std::string& line) {
    std::istringstream iss(line);
    std::string token;
    std::vector<int> result;
    
    while (iss >> token) {
        result.push_back(cellFromString(token));
    }
    return result;
}

int SudokuUtils::cellFromString(const std::string& cell) {
    return (cell == EMPTY_AS_STRING) ? 0 : std::stoi(cell);
}

std::string SudokuUtils::cellToString(int cell) {
    return (cell == 0) ? EMPTY_AS_STRING : std::to_string(cell);
}

} // namespace just::demo::util
