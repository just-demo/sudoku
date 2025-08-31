package just.demo.generator;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;

import static just.demo.util.SudokuUtils.copy;
import static just.demo.util.SudokuUtils.countOpen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import just.demo.exception.ComplexityLimitException;
import just.demo.exception.MultipleSolutionsException;
import just.demo.exception.NoSolutionException;
import just.demo.solver.Solver;
import lombok.Value;

public class Generator {

  private final int size;
  private final int complexityLowerLimit;
  private final int blockSize;
  private final Set<Integer> values;

  public Generator(int size, int complexityLowerLimit) {
    this.size = size;
    this.complexityLowerLimit = complexityLowerLimit;
    this.blockSize = (int) Math.sqrt(size);
    this.values = rangeClosed(1, size).boxed().collect(toSet());
  }

  public int[][] generate() {
    return generate(new int[size][size]);
  }

  private int[][] generate(int[][] initialValues) {
    if (countOpen(initialValues) > complexityLowerLimit) {
      throw new ComplexityLimitException();
    }

    try {
      new Solver(initialValues).solve();
      return initialValues;
    } catch (MultipleSolutionsException e) {
      List<Cell> open = new ArrayList<>();
      List<Cell> pending = new ArrayList<>();
      for (int row = 0; row < size; row++) {
        for (int col = 0; col < size; col++) {
          int value = initialValues[row][col];
          int block = blockSize * (row / blockSize) + col / blockSize;
          Cell cell = new Cell(row, col, block, value);
          if (value != 0) {
            open.add(cell);
          } else {
            pending.add(cell);
          }
        }
      }

      shuffle(pending);
      for (Cell cell : pending) {
        int[][] nextGuess = copy(initialValues);
        List<Integer> values = new ArrayList<>(this.values);
        open.stream().filter(cell::isRelated).map(Cell::getValue).forEach(values::remove);
        shuffle(values);
        for (Integer value : values) {
          nextGuess[cell.getRow()][cell.getCol()] = value;
          try {
            return generate(nextGuess);
          } catch (NoSolutionException e2) {
            // Our guess did not work, let's try another one
          }
        }
      }
      throw new NoSolutionException();
    }
  }

  @Value
  private static class Cell {

    int row;
    int col;
    int block;
    int value;

    public boolean isRelated(Cell cell) {
      return row == cell.row || col == cell.col || block == cell.block;
    }
  }
}
