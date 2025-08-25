#include "run_generator.hpp"
#include "generator.hpp"
#include "reducer.hpp"
#include "utils.hpp"
#include "data_dirs.hpp"
#include "exceptions.hpp"
#include <iostream>
#include <thread>
#include <future>
#include <chrono>
#include <map>
#include <algorithm>

namespace sudoku {

void RunGenerator::generateMany() {
    auto start = std::chrono::steady_clock::now();
    std::filesystem::path reducerFailedDir = DataDirs::REDUCER_FAILED_DIR / Utils::getCurrentTime();
    std::map<long, long> counts;
    
    Generator generator(9, 31);
    Reducer reducer;

    std::atomic<long> totalCounter{0};
    std::atomic<long> successCounter{0};
    std::atomic<long> openMin{LONG_MAX};
    
    for (int i = 0; i < 10; ++i) {
        std::cout << "Generating " << ++totalCounter << std::endl;
        
        auto generateFuture = std::async(std::launch::async, [&generator]() {
            return generator.generate();
        });
        
        try {
            auto result = generateFuture.wait_for(GENERATOR_TIMEOUT);
            if (result == std::future_status::timeout) {
                std::cout << "Failed to generate (timeout)" << std::endl;
                continue;
            }
            
            auto sudoku = generateFuture.get();
            long openCount = Utils::countOpen(sudoku);
            
            // Only try to reduce if the puzzle has a reasonable number of clues (not 81)
            if (openCount < 81) {
                std::cout << "Minimizing " << totalCounter.load() << std::endl;
                auto minimizeFuture = std::async(std::launch::async, [&reducer, sudoku]() {
                    return reducer.reduce(sudoku);
                });
                
                try {
                    result = minimizeFuture.wait_for(REDUCER_TIMEOUT);
                    if (result == std::future_status::timeout) {
                        std::cout << "Failed to reduce: " << openCount << std::endl;
                        std::filesystem::path failedFile = reducerFailedDir / 
                            (std::to_string(openCount) + "-" + Utils::getCurrentTime() + "-" + std::to_string(totalCounter.load()) + ".txt");
                        Utils::writeFile(failedFile, Utils::toString2D(sudoku));
                        counts[300]++;
                        continue;
                    }
                    
                    auto minimizedSudoku = minimizeFuture.get();
                    long newOpenCount = Utils::countOpen(minimizedSudoku);
                    if (newOpenCount != openCount) {
                        std::cout << "Minimized: " << openCount << " => " << newOpenCount << std::endl;
                        openCount = newOpenCount;
                        sudoku = minimizedSudoku;
                    }
                } catch (const std::exception& e) {
                    std::cout << "Failed to reduce: " << openCount << std::endl;
                    std::filesystem::path failedFile = reducerFailedDir / 
                        (std::to_string(openCount) + "-" + Utils::getCurrentTime() + "-" + std::to_string(totalCounter.load()) + ".txt");
                    Utils::writeFile(failedFile, Utils::toString2D(sudoku));
                    counts[300]++;
                    continue;
                }
            } else {
                std::cout << "Generated complete puzzle (81 clues), skipping reduction" << std::endl;
            }
            
            long newMin = openCount;
            long oldMin = openMin.load();
            while (newMin < oldMin && !openMin.compare_exchange_weak(oldMin, newMin)) {
                // Retry until we successfully update the minimum
            }
            
            successCounter++;
            long successPercentage = 100 * successCounter.load() / totalCounter.load();
            std::cout << "Generated: " << openCount << "/" << openMin.load() << " - " 
                      << successCounter.load() << "/" << successPercentage << "%" << std::endl;
            std::cout << Utils::toString2D(sudoku) << std::endl;
            
            std::filesystem::path readyFile = DataDirs::READY_DIR / (std::to_string(openCount) + ".txt");
            Utils::appendFile(readyFile, Utils::toString1D(sudoku) + "\n");
            std::cout << "------------------" << std::endl;
            
            if (openCount < 100) {
                counts[openCount]++;
            }
            
        } catch (const ComplexityLimitException& e) {
            std::cout << "Failed to generate (complexity limit)" << std::endl;
            counts[200]++;
        } catch (const std::exception& e) {
            std::cout << "Failed to generate" << std::endl;
            counts[100]++;
        }
    }

    std::cout << "Counts: ";
    for (const auto& [count, frequency] : counts) {
        std::cout << count << ":" << frequency << " ";
    }
    std::cout << std::endl;
    
    auto end = std::chrono::steady_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);
    std::cout << "Time: " << duration.count() / 1000.0 << std::endl;
}

} // namespace sudoku
