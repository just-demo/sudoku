CXX = g++
CXXFLAGS = -std=c++20 -Wall -Wextra -O2
SRCDIR = src
BUILDDIR = build

# Source files
SOURCES = $(SRCDIR)/main.cpp \
          $(SRCDIR)/clever_solver.cpp \
          $(SRCDIR)/cell.cpp \
          $(SRCDIR)/utils.cpp \
          $(SRCDIR)/generator.cpp \
          $(SRCDIR)/reducer.cpp \
          $(SRCDIR)/data_dirs.cpp \
          $(SRCDIR)/run_generator.cpp \
          $(SRCDIR)/run_reducer.cpp

TEST_CLEVER_SOURCES = $(SRCDIR)/test_clever_solver.cpp \
                      $(SRCDIR)/clever_solver.cpp \
                      $(SRCDIR)/utils.cpp

TEST_REDUCER_SOURCES = $(SRCDIR)/test_reducer.cpp \
                       $(SRCDIR)/reducer.cpp \
                       $(SRCDIR)/clever_solver.cpp \
                       $(SRCDIR)/cell.cpp \
                       $(SRCDIR)/utils.cpp

# Object files
OBJECTS = $(SOURCES:$(SRCDIR)/%.cpp=$(BUILDDIR)/%.o)
TEST_CLEVER_OBJECTS = $(TEST_CLEVER_SOURCES:$(SRCDIR)/%.cpp=$(BUILDDIR)/%.o)
TEST_REDUCER_OBJECTS = $(TEST_REDUCER_SOURCES:$(SRCDIR)/%.cpp=$(BUILDDIR)/%.o)

# Targets
TARGET = sudoku
TEST_CLEVER_TARGET = test_clever_solver
TEST_REDUCER_TARGET = test_reducer

.PHONY: all clean test

all: $(TARGET)

$(TARGET): $(OBJECTS)
	$(CXX) $(OBJECTS) -o $(TARGET)

$(TEST_CLEVER_TARGET): $(TEST_CLEVER_OBJECTS)
	$(CXX) $(TEST_CLEVER_OBJECTS) -o $(TEST_CLEVER_TARGET)

$(TEST_REDUCER_TARGET): $(TEST_REDUCER_OBJECTS)
	$(CXX) $(TEST_REDUCER_OBJECTS) -o $(TEST_REDUCER_TARGET)

$(BUILDDIR)/%.o: $(SRCDIR)/%.cpp
	@mkdir -p $(BUILDDIR)
	$(CXX) $(CXXFLAGS) -c $< -o $@

test: $(TEST_CLEVER_TARGET) $(TEST_REDUCER_TARGET)
	./$(TEST_CLEVER_TARGET)
	./$(TEST_REDUCER_TARGET)

clean:
	rm -rf $(BUILDDIR) $(TARGET) $(TEST_CLEVER_TARGET) $(TEST_REDUCER_TARGET)
