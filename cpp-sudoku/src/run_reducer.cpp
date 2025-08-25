#include "run_reducer.hpp"
#include "reducer.hpp"
#include "utils.hpp"
#include "data_dirs.hpp"
#include <iostream>
#include <chrono>
#include <atomic>

namespace sudoku {

void RunReducer::reduceFailedByGeneratorTimeout() {
    Reducer reducer;
    std::atomic<long> minimizedCount{0};
    std::vector<std::filesystem::path> files = Utils::listFiles(DataDirs::REDUCER_FAILED_DIR);
    
    for (const auto& file : files) {
        std::cout << file.filename().string() << std::endl;
        std::string content = Utils::readFile(file);
        auto input = Utils::fromString2D(content);
        
        auto startTime = std::chrono::steady_clock::now();
        auto output = reducer.reduce(input);
        auto endTime = std::chrono::steady_clock::now();
        auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(endTime - startTime);
        
        std::cout << "Time: " << duration.count() / 1000.0 << "s" << std::endl;
        
        long inputCount = Utils::countOpen(input);
        long outputCount = Utils::countOpen(output);
        
        std::string filename = file.filename().string();
        size_t dashPos = filename.find('-');
        std::string outFile = std::to_string(outputCount) + "-" + filename.substr(dashPos + 1);
        
        Utils::writeFile(DataDirs::REDUCER_FIXED_DIR / outFile, Utils::toString2D(output));
        
        if (outputCount != inputCount) {
            minimizedCount++;
            std::cout << "Minimized " << filename << ":" << inputCount << " => " << outputCount << std::endl;
        }
        
        std::filesystem::remove(file);
    }
    
    std::cout << "Minimized " << minimizedCount.load() << " of " << files.size() << std::endl;
}

void RunReducer::copyReducedToReady() {
    std::vector<std::filesystem::path> files = Utils::listFiles(DataDirs::REDUCER_FIXED_DIR);
    
    for (const auto& file : files) {
        std::cout << file.filename().string() << std::endl;
        std::string content = Utils::readFile(file);
        auto result = Utils::fromString2D(content);
        
        long openCount = Utils::countOpen(result);
        std::filesystem::path readyFile = DataDirs::READY_DIR / (std::to_string(openCount) + ".txt");
        Utils::appendFile(readyFile, Utils::toString1D(result) + "\n");
        
        std::filesystem::remove(file);
    }
}

} // namespace sudoku
