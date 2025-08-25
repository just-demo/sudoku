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
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import self.ed.exception.MultipleSolutionsException;
import self.ed.solver.Cell;
import self.ed.solver.CleverSolver;

public class Reducer {

  public Integer[][] reduce(Integer[][] initialValues) {
    int size = initialValues.length;
    List<Cell> open = new ArrayList<>();
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (initialValues[row][col] != null) {
          open.add(new Cell(row, col, 0, Set.of()));
        }
      }
    }

    return reduce(initialValues, open);
  }

  private Integer[][] reduce(Integer[][] initialValues, Collection<Cell> closeCandidates) {
    Map<Cell, Integer[][]> candidates = new HashMap<>();
    closeCandidates.forEach(cell -> {
      Integer[][] nextGuess = copy(initialValues);
      nextGuess[cell.getRow()][cell.getCol()] = null;
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
}
