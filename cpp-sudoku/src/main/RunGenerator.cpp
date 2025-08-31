#include <iostream>
#include <chrono>
#include <thread>
#include <future>
#include <map>
#include <algorithm>
#include <iomanip>
#include <sstream>
#include "../generator/Generator.hpp"
#include "../generator/Reducer.hpp"
#include "../util/SudokuUtils.hpp"
#include "../util/FileUtils.hpp"
#include "DataDirs.hpp"
#include "../exception/ComplexityLimitException.hpp"

using namespace just::demo::generator;
using namespace just::demo::util;
using namespace just::demo::main;

std::string getCurrentTime() {
    auto now = std::chrono::system_clock::now();
    auto time_t = std::chrono::system_clock::to_time_t(now);
    auto tm = *std::localtime(&time_t);
    
    std::ostringstream oss;
    oss << std::put_time(&tm, "%Y%m%d-%H%M%S");
    return oss.str();
}

void generateMany() {
    auto start = std::chrono::steady_clock::now();
    auto reducerFailedDir = DataDirs::REDUCER_FAILED_DIR / getCurrentTime();
    
    Generator generator(9, 31);
    Reducer reducer;
    
    long totalCounter = 0;
    long successCounter = 0;
    long openMin = std::numeric_limits<long>::max();
    
    std::map<long, long> counts;
    
    for (int i = 0; i < 10; ++i) {
        ++totalCounter;
        std::cout << "Generating " << totalCounter << std::endl;
        
        try {
            auto generateFuture = std::async(std::launch::async, [&generator]() {
                return generator.generate();
            });
            
            auto result = generateFuture.get();
            long openCount = SudokuUtils::countOpen(result);
            
            std::cout << "Minimizing " << totalCounter << std::endl;
            
            try {
                auto minimizeFuture = std::async(std::launch::async, [&reducer, result]() {
                    return reducer.reduce(result);
                });
                
                auto minimizedResult = minimizeFuture.get();
                long newOpenCount = SudokuUtils::countOpen(minimizedResult);
                
                if (newOpenCount != openCount) {
                    std::cout << "Minimized: " << openCount << " => " << newOpenCount << std::endl;
                    openCount = newOpenCount;
                    result = minimizedResult;
                }
                
                openMin = std::min(openMin, openCount);
                ++successCounter;
                long successPercentage = 100 * successCounter / totalCounter;
                
                std::cout << "Generated: " << openCount << "/" << openMin << " - " 
                         << successCounter << "/" << successPercentage << "%" << std::endl;
                std::cout << SudokuUtils::toString2D(result) << std::endl;
                
                auto readyFile = DataDirs::READY_DIR / (std::to_string(openCount) + ".txt");
                FileUtils::appendFile(readyFile, SudokuUtils::toString1D(result) + "\n");
                
                counts[openCount]++;
                
            } catch (const std::exception& e) {
                std::cout << "Failed to reduce: " << openCount << std::endl;
                auto failedFile = reducerFailedDir / (std::to_string(openCount) + "-" + getCurrentTime() + "-" + std::to_string(totalCounter) + ".txt");
                FileUtils::writeFile(failedFile, SudokuUtils::toString2D(result));
                counts[300]++;
            }
            
        } catch (const just::demo::exception::ComplexityLimitException&) {
            std::cout << "Failed to generate (complexity limit)" << std::endl;
            counts[200]++;
        } catch (const std::exception& e) {
            std::cout << "Failed to generate: " << e.what() << std::endl;
            counts[100]++;
        }
        
        std::cout << "------------------" << std::endl;
    }
    
    std::cout << "Counts: ";
    for (const auto& [key, value] : counts) {
        std::cout << key << ":" << value << " ";
    }
    std::cout << std::endl;
    
    auto end = std::chrono::steady_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::seconds>(end - start);
    std::cout << "Time: " << duration.count() << "s" << std::endl;
}

int main() {
    generateMany();
    return 0;
}
