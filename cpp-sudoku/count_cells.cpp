#include <iostream>
#include <string>

int main() {
    std::string puzzle1 = R"(
. . . 8 7 . . . .
. . . . . . . 5 9
3 . . 1 . . . . .
. . . . 4 . 2 1 .
5 8 . 7 . . . . .
6 . . . . . . . 4
. 2 . . 5 . 8 . .
. . . . 3 4 . 9 .
. . . . . . . 3 .
)";
    
    std::string puzzle2 = R"(
. 5 4 . 1 3 . . .
6 . . . . . . . 2
. . . . . 5 . 7 .
. 8 . 2 . . 7 . .
4 . . . . . . . .
. . 6 . . . 9 . 1
. . 2 . . . 6 . .
. . 1 . 4 . . 8 .
. . . . . 8 5 . .
)";
    
    std::string puzzle3 = R"(
2 . 9 . . . 3 . .
. . . . 2 4 6 . .
1 . . . 7 . . 5 .
. 1 . . . . . . .
3 4 . . . . . 7 .
. 5 . . . . 9 4 8
4 . . . . 8 . . .
. . . 6 . . . . .
. 2 . 3 5 . . 6 .
)";
    
    int count1 = 0, count2 = 0, count3 = 0;
    
    for (char c : puzzle1) {
        if (c >= '1' && c <= '9') count1++;
    }
    for (char c : puzzle2) {
        if (c >= '1' && c <= '9') count2++;
    }
    for (char c : puzzle3) {
        if (c >= '1' && c <= '9') count3++;
    }
    
    std::cout << "Puzzle 1 (Already Minimal): " << count1 << " filled cells" << std::endl;
    std::cout << "Puzzle 2 (Minus One): " << count2 << " filled cells" << std::endl;
    std::cout << "Puzzle 3 (Minus Two): " << count3 << " filled cells" << std::endl;
    
    return 0;
}
