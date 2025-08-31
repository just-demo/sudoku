#include "FileUtils.hpp"
#include <fstream>
#include <sstream>
#include <iostream>

namespace just::demo::util {

std::string FileUtils::readFile(const std::filesystem::path& file) {
    std::ifstream ifs(file);
    if (!ifs.is_open()) {
        throw std::runtime_error("Cannot open file: " + file.string());
    }
    std::stringstream buffer;
    buffer << ifs.rdbuf();
    return buffer.str();
}

std::string FileUtils::readFile(const std::string& file) {
    return readFile(std::filesystem::path(file));
}

void FileUtils::writeFile(const std::filesystem::path& file, const std::string& data) {
    std::filesystem::create_directories(file.parent_path());
    std::ofstream ofs(file);
    if (!ofs.is_open()) {
        throw std::runtime_error("Cannot create file: " + file.string());
    }
    ofs << data;
}

void FileUtils::writeFile(const std::string& file, const std::string& data) {
    writeFile(std::filesystem::path(file), data);
}

void FileUtils::writeFile(const std::filesystem::path& file, const std::vector<uint8_t>& bytes) {
    std::filesystem::create_directories(file.parent_path());
    std::ofstream ofs(file, std::ios::binary);
    if (!ofs.is_open()) {
        throw std::runtime_error("Cannot create file: " + file.string());
    }
    ofs.write(reinterpret_cast<const char*>(bytes.data()), bytes.size());
}

void FileUtils::appendFile(const std::filesystem::path& file, const std::string& data) {
    std::filesystem::create_directories(file.parent_path());
    std::ofstream ofs(file, std::ios::app);
    if (!ofs.is_open()) {
        throw std::runtime_error("Cannot append to file: " + file.string());
    }
    ofs << data;
}

void FileUtils::appendFile(const std::string& file, const std::string& data) {
    appendFile(std::filesystem::path(file), data);
}

std::vector<std::filesystem::path> FileUtils::streamFiles(const std::filesystem::path& dir) {
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

} // namespace just::demo::util
