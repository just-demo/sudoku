package self.ed.generator;

import static java.util.Collections.shuffle;
import static java.util.Map.Entry.comparingByValue;

import static self.ed.util.SudokuUtils.copy;
import static self.ed.util.SudokuUtils.countOpen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Value;
import self.ed.exception.MultipleSolutionsException;
import self.ed.solver.CleverSolver;

public class Reducer {

  public int[][] reduce(int[][] initialValues) {
    int size = initialValues.length;
    List<Cell> open = new ArrayList<>();
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (initialValues[row][col] != 0) {
          open.add(new Cell(row, col));
        }
      }
    }

    return reduce(initialValues, open);
  }

  private int[][] reduce(int[][] initialValues, Collection<Cell> closeCandidates) {
    Map<Cell, int[][]> candidates = new HashMap<>();
    closeCandidates.forEach(cell -> {
      int[][] nextGuess = copy(initialValues);
      nextGuess[cell.getRow()][cell.getCol()] = 0;
      try {
        new CleverSolver(nextGuess).solve();
        candidates.put(cell, nextGuess);
      } catch (MultipleSolutionsException e) {
        // no-op
      }
    });

    List<Cell> nextCloseCandidates = new ArrayList<>(candidates.keySet());
    shuffle(nextCloseCandidates);
    return new ArrayList<>(nextCloseCandidates).stream()
        .map(cell -> {
          nextCloseCandidates.remove(cell);
          return reduce(candidates.get(cell), nextCloseCandidates);
        })
        .map(matrix -> Pair.of(matrix, countOpen(matrix)))
        .min(comparingByValue())
        .map(Pair::getKey)
        .orElse(initialValues);
  }

  @Value
  private static class Cell {

    int row;
    int col;
  }
}
