package self.ed.solver;

import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;

import static self.ed.util.SudokuUtils.copy;
import static self.ed.visitor.Visitor.notifyGuessed;
import static self.ed.visitor.Visitor.notifyGuessing;
import static self.ed.visitor.Visitor.notifyInitial;
import static self.ed.visitor.Visitor.notifyOpeningCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import self.ed.exception.MultipleSolutionsException;
import self.ed.exception.NoSolutionException;
import self.ed.exception.TimeLimitException;
import self.ed.visitor.Visitor;

public class SimpleSolver {

  private final int[][] solution;
  private final List<Cell> pendingCells = new ArrayList<>();
  private final Visitor[] visitors;

  public SimpleSolver(int[][] initialValues, Visitor... visitors) {
    this.visitors = visitors;
    int size = initialValues.length;
    int blockSize = (int) Math.sqrt(size);
    Set<Integer> values = rangeClosed(1, size).boxed().collect(toSet());

    solution = new int[size][size];
    Map<Cell, Integer> openCells = new HashMap<>();
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        int block = blockSize * (row / blockSize) + col / blockSize;
        int value = initialValues[row][col];
        if (value != 0) {
          openCells.put(new Cell(row, col, block, emptySet()), value);
        } else {
          pendingCells.add(new Cell(row, col, block, values));
        }
      }
    }

    notifyInitial(visitors, openCells.size());
    openCells.forEach(this::open);
  }

  public int[][] solve() {
    if (Thread.interrupted()) {
      throw new TimeLimitException();
    }

    while (!pendingCells.isEmpty()) {
      Cell cell = pendingCells.stream().min(comparing(Cell::countCandidates)).get();
      if (cell.countCandidates() == 1) {
        notifyOpeningCell(visitors);
        open(cell, cell.getCandidate());
      } else if (cell.countCandidates() > 1) {
        notifyGuessing(visitors, cell.countCandidates());
//                System.out.println("Guessing out of " + cell.countCandidates());
        List<int[][]> solutions = new ArrayList<>();
        for (Integer value : cell.getCandidates()) {
          int[][] nextGuess = copy(solution);
          nextGuess[cell.getRow()][cell.getCol()] = value;
          try {
            solutions.add(new SimpleSolver(nextGuess, visitors).solve());
            if (solutions.size() > 1) {
              throw new MultipleSolutionsException();
            }
          } catch (NoSolutionException e) {
            // Our guess did not work, let's try another one
          }
        }
        if (solutions.isEmpty()) {
          throw new NoSolutionException();
        }
        notifyGuessed(visitors);
        return solutions.getFirst();
      } else {
        throw new NoSolutionException();
      }
    }

    return solution;
  }

  private void open(Cell cell, Integer value) {
    solution[cell.getRow()][cell.getCol()] = value;
    pendingCells.remove(cell);
    pendingCells.stream().filter(cell::isRelated).forEach(pend -> pend.removeCandidate(value));
  }

  @Getter
  private static class Cell {

    private final int row;
    private final int col;
    private final int block;
    private final Set<Integer> candidates;

    public Cell(int row, int col, int block, Set<Integer> candidates) {
      this.row = row;
      this.col = col;
      this.block = block;
      this.candidates = new HashSet<>(candidates);
    }

    public boolean isRelated(Cell cell) {
      return row == cell.row || col == cell.col || block == cell.block;
    }

    public void removeCandidate(Integer value) {
      candidates.remove(value);
    }

    public int countCandidates() {
      return candidates.size();
    }

    public Integer getCandidate() {
      return candidates.iterator().next();
    }
  }
}
