#include <iostream>
#include <chrono>
#include <map>
#include <algorithm>
#include <sstream>
#include "../solver/Solver.hpp"
#include "../util/SudokuUtils.hpp"
#include "../util/FileUtils.hpp"
#include "DataDirs.hpp"

using namespace just::demo::solver;
using namespace just::demo::util;
using namespace just::demo::main;

void solveSingleComplexSudoku() {
    std::string sudokuStr = ".......9......8.2.7.3.54.....52.................6....88....3..7.9....6..6...8...4";
    auto sudoku = SudokuUtils::fromString1D(sudokuStr);
    
    auto start = std::chrono::steady_clock::now();
    auto solution = Solver(sudoku).solve();
    auto end = std::chrono::steady_clock::now();
    
    std::cout << SudokuUtils::toString2D(solution) << std::endl;
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);
    std::cout << "Time: " << duration.count() << "ms" << std::endl;
}

long countLines(const std::filesystem::path& file) {
    std::string content = FileUtils::readFile(file);
    return std::count(content.begin(), content.end(), '\n') + 1;
}

void solveAllReadySudoku() {
    auto files = FileUtils::streamFiles(DataDirs::READY_DIR);
    std::sort(files.begin(), files.end());
    
    std::map<std::string, long> counts;
    for (const auto& file : files) {
        counts[file.filename().string()] = countLines(file);
    }
    
    long totalCount = 0;
    for (const auto& [_, count] : counts) {
        totalCount += count;
    }
    
    std::cout << "Total count: " << totalCount << std::endl;
    
    long counter = 0;
    auto start = std::chrono::steady_clock::now();
    
    for (const auto& file : files) {
        auto fileStart = std::chrono::steady_clock::now();
        std::string content = FileUtils::readFile(file);
        
        std::istringstream iss(content);
        std::string line;
        long fileCount = 0;
        
        while (std::getline(iss, line)) {
            if (!line.empty()) {
                ++counter;
                if (counter % 1000 == 0) {
                    auto now = std::chrono::steady_clock::now();
                    auto elapsed = std::chrono::duration_cast<std::chrono::seconds>(now - start);
                    std::cout << "progress: " << counter << " / " << elapsed.count() << "s" << std::endl;
                }
                if (counter > 10000) {
                    throw std::runtime_error("stopped");
                }
                
                auto sudoku = SudokuUtils::fromString1D(line);
                Solver(sudoku).solve();
                ++fileCount;
            }
        }
        
        auto fileEnd = std::chrono::steady_clock::now();
        auto fileDuration = std::chrono::duration_cast<std::chrono::milliseconds>(fileEnd - fileStart);
        std::cout << file.filename().string() << ": " << fileDuration.count() << "ms / " 
                 << (static_cast<double>(fileDuration.count()) / fileCount) << "ms per puzzle" << std::endl;
    }
    
    auto end = std::chrono::steady_clock::now();
    auto totalDuration = std::chrono::duration_cast<std::chrono::seconds>(end - start);
    std::cout << "Total time: " << totalDuration.count() << "s" << std::endl;
}

int main() {
    solveSingleComplexSudoku();
    // solveAllReadySudoku(); // Uncomment to run all puzzles
    return 0;
}
