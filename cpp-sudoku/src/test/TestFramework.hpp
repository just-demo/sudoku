#pragma once

#include <iostream>
#include <string>
#include <vector>
#include <functional>

namespace test {

class TestFramework {
public:
    static void assertEqual(const std::string& expected, const std::string& actual, const std::string& testName) {
        if (expected == actual) {
            std::cout << "✓ " << testName << " PASSED" << std::endl;
        } else {
            std::cout << "✗ " << testName << " FAILED" << std::endl;
            std::cout << "  Expected: " << expected << std::endl;
            std::cout << "  Actual:   " << actual << std::endl;
        }
    }
    
    static void assertEqual(long expected, long actual, const std::string& testName) {
        if (expected == actual) {
            std::cout << "✓ " << testName << " PASSED" << std::endl;
        } else {
            std::cout << "✗ " << testName << " FAILED" << std::endl;
            std::cout << "  Expected: " << expected << std::endl;
            std::cout << "  Actual:   " << actual << std::endl;
        }
    }
    
    static void runTest(const std::string& testName, std::function<void()> testFunc) {
        try {
            testFunc();
            std::cout << "✓ " << testName << " PASSED" << std::endl;
        } catch (const std::exception& e) {
            std::cout << "✗ " << testName << " FAILED with exception: " << e.what() << std::endl;
        }
    }
};

} // namespace test
