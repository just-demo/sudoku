#pragma once

#include <string>
#include <vector>
#include <filesystem>

namespace just::demo::util {

class FileUtils {
public:
    static std::string readFile(const std::filesystem::path& file);
    static std::string readFile(const std::string& file);
    static void writeFile(const std::filesystem::path& file, const std::string& data);
    static void writeFile(const std::string& file, const std::string& data);
    static void writeFile(const std::filesystem::path& file, const std::vector<uint8_t>& bytes);
    static void appendFile(const std::filesystem::path& file, const std::string& data);
    static void appendFile(const std::string& file, const std::string& data);
    static std::vector<std::filesystem::path> streamFiles(const std::filesystem::path& dir);
};

} // namespace just::demo::util
