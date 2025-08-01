package self.ed.generator;

import static java.util.Collections.emptySet;
import static java.util.Collections.shuffle;
import static java.util.Collections.singleton;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;

import static self.ed.util.Utils.copy;
import static self.ed.util.Utils.countOpen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import self.ed.exception.ComplexityLimitException;
import self.ed.exception.MultipleSolutionsException;
import self.ed.exception.NoSolutionException;
import self.ed.solver.Cell;
import self.ed.solver.CleverSolver;

public class Generator {

  private final int size;
  private final int blockSize;
  private final Set<Integer> values;

  public Generator(int size) {
    this.size = size;
    this.blockSize = (int) Math.sqrt(size);
    this.values = rangeClosed(1, size).boxed().collect(toSet());
  }

  public Integer[][] generate() {
    return generate(size * size);
  }

  public Integer[][] generate(int complexityLowerLimit) {
    return generate(new Integer[size][size], complexityLowerLimit);
  }

  public Integer[][] reduce(Integer[][] initialValues) {
    List<Cell> open = new ArrayList<>();
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (initialValues[row][col] != null) {
          open.add(new Cell(row, col, 0, emptySet()));
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
        .min(comparing(Pair::getValue))
        .map(Pair::getKey)
        .orElse(initialValues);
  }

  private Integer[][] generate(Integer[][] initialValues, int complexityLowerLimit) {
    if (countOpen(initialValues) > complexityLowerLimit) {
      throw new ComplexityLimitException();
    }

    try {
      new CleverSolver(initialValues).solve();
      return initialValues;
    } catch (MultipleSolutionsException e) {
      List<Cell> open = new ArrayList<>();
      List<Cell> pending = new ArrayList<>();
      for (int row = 0; row < size; row++) {
        for (int col = 0; col < size; col++) {
          Integer value = initialValues[row][col];
          int block = blockSize * (row / blockSize) + col / blockSize;
          if (value != null) {
            open.add(new Cell(row, col, block, singleton(value)));
          } else {
            pending.add(new Cell(row, col, block, emptySet()));
          }
        }
      }

      shuffle(pending);
      for (Cell cell : pending) {
        Integer[][] nextGuess = copy(initialValues);
        List<Integer> values = new ArrayList<>(this.values);
        open.stream().filter(cell::isRelated).map(Cell::getCandidate).forEach(values::remove);
        shuffle(values);
        for (Integer value : values) {
          nextGuess[cell.getRow()][cell.getCol()] = value;
          try {
            return generate(nextGuess, complexityLowerLimit);
          } catch (NoSolutionException e2) {
            // Our guess did not work, let's try another one
          }
        }
      }
      throw new NoSolutionException();
    }
  }
}
