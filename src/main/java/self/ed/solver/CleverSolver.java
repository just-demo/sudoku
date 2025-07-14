package self.ed.solver;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.rangeClosed;

import static self.ed.visitor.Visitor.notifyGuessed;
import static self.ed.visitor.Visitor.notifyGuessing;
import static self.ed.visitor.Visitor.notifyInitial;
import static self.ed.visitor.Visitor.notifyOpeningCell;
import static self.ed.visitor.Visitor.notifyOpeningValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import self.ed.exception.MultipleSolutionsException;
import self.ed.exception.NoSolutionException;
import self.ed.exception.TimeLimitException;
import self.ed.visitor.Visitor;

public class CleverSolver {

  private final int size;
  private final List<Cell> allCells = new ArrayList<>();
  private final Visitor[] visitors;

  private final List<Cell> pendingCells = new ArrayList<>();
  private final List<Value> pendingValues = new ArrayList<>();

  public CleverSolver(Integer[][] initialValues, Visitor... visitors) {
    this.visitors = visitors;
    size = initialValues.length;
    int blockSize = (int) Math.sqrt(size);
    Map<Integer, Value> valueMap = rangeClosed(1, size)
        .boxed()
        .collect(toMap(Function.identity(), Value::new));

    Map<Cell, Value> openCells = new HashMap<>();
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        int block = blockSize * (row / blockSize) + col / blockSize;
        Cell cell = new Cell(row, col, block);
        allCells.add(cell);
        Integer value = initialValues[row][col];
        if (value != null) {
          openCells.put(cell, valueMap.get(value));
        }
      }
    }

    pendingCells.addAll(allCells);
    pendingValues.addAll(valueMap.values());
    pendingCells.forEach(cell -> cell.addCandidates(pendingValues));
    pendingValues.forEach(value -> value.addCandidates(pendingCells));
    notifyInitial(visitors, openCells.size());
    openCells.forEach(Cell::open);
  }

  public Integer[][] solve() {
    if (Thread.interrupted()) {
      throw new TimeLimitException();
    }

//        if (countOpenDistinct(result) < result.length - 1) { //TODO: filter Value.isEmpty() ???
//            throw new MultipleSolutionsException();
//        }

    while (!pendingCells.isEmpty()) {
      try {
        openNext();
      } catch (CannotOpenWithoutGuessingException e) {
        return solveWithGuess(e.getCell(), e.getValue());
      }
    }

    return copyState();
  }

  private void openNext() throws CannotOpenWithoutGuessingException {
    if (pendingValues.isEmpty()) {
      throw new NoSolutionException();
    }

    Cell cell = pendingCells.stream().min(comparing(Cell::countCandidates)).get();
    if (cell.countCandidates() == 1) {
      notifyOpeningCell(visitors);
      cell.open(cell.getCandidate());
      return;
    }

    Value value = pendingValues.stream().min(comparing(Value::countCandidates)).get();
    if (value.countCandidates() == 1) {
      notifyOpeningValue(visitors);
      value.getCandidate().open(value);
      return;
    }

    if (cell.countCandidates() == 0 || value.countCandidates() == 0) {
      throw new NoSolutionException();
    }

    throw new CannotOpenWithoutGuessingException(cell, value);
  }

  private Integer[][] solveWithGuess(Cell cell, Value value) {
    Collection<Cell> guessCells;
    Collection<Value> guessValues;
    if (cell.countCandidates() <= value.countCandidates()) {
      guessCells = singletonList(cell);
      guessValues = cell.getCandidates();
    } else {
      guessCells = value.getCandidates();
      guessValues = singletonList(value);
    }

    List<Integer[][]> solutions = new ArrayList<>();
    notifyGuessing(visitors, guessCells.size() * guessValues.size());
    // System.out.println("Guessing out of " + guessCells.size() * guessValues.size());
    for (Cell guessCell : guessCells) {
      for (Value guessValue : guessValues) {
        Integer[][] nextGuess = copyState();
        nextGuess[guessCell.getRow()][guessCell.getCol()] = guessValue.getValue();
        try {
          solutions.add(new CleverSolver(nextGuess, visitors).solve());
          if (solutions.size() > 1) {
            throw new MultipleSolutionsException();
          }
        } catch (NoSolutionException e) {
          // Our guess did not work, let's try another one
        }
      }
    }

    if (solutions.isEmpty()) {
      throw new NoSolutionException();
    }

    notifyGuessed(visitors);
    return solutions.getFirst();
  }

  private Integer[][] copyState() {
    Integer[][] state = new Integer[size][size];
    allCells.forEach(cell -> state[cell.getRow()][cell.getCol()] = cell.getValue());
    return state;
  }

  @lombok.Value
  @EqualsAndHashCode(callSuper = true)
  private class CannotOpenWithoutGuessingException extends RuntimeException {

    Cell cell;
    Value value;
  }

  @Getter
  @RequiredArgsConstructor
  private class Cell {

    private final int row;
    private final int col;
    private final int block;
    private Value value;
    private Collection<Value> candidates;

    void addCandidates(Collection<Value> candidates) {
      this.candidates = new ArrayList<>(candidates);
    }

    void open(Value value) {
      this.value = value;
      candidates.forEach(candidate -> candidate.removeCandidate(this));
      candidates.clear();
      value.open(this);
      pendingCells.remove(this);
      pendingCells.stream()
          .filter(this::isRelated)
          .forEach(pendingCell -> pendingCell.removeCandidate(value));
    }

    boolean isRelated(Cell cell) {
      return row == cell.row || col == cell.col || block == cell.block;
    }

    void removeCandidate(Value value) {
      value.removeCandidate(this);
      candidates.remove(value);
    }

    int countCandidates() {
      return candidates.size();
    }

    Value getCandidate() {
      return candidates.iterator().next();
    }

    Integer getValue() {
      return ofNullable(value).map(Value::getValue).orElse(null);
    }
  }

  @RequiredArgsConstructor
  private class Value {

    @Getter
    private final Integer value;
    private final Collection<Cell> cells = new ArrayList<>();
    private final Collection<Collection<Cell>> candidates = new ArrayList<>();

    void addCandidates(Collection<Cell> candidates) {
      this.candidates.addAll(candidates.stream().collect(groupingBy(Cell::getRow)).values());
      this.candidates.addAll(candidates.stream().collect(groupingBy(Cell::getCol)).values());
      this.candidates.addAll(candidates.stream().collect(groupingBy(Cell::getBlock)).values());
    }

    void removeCandidate(Cell cell) {
      candidates.forEach(candidateGroup -> candidateGroup.remove(cell));
      candidates.removeIf(Collection::isEmpty);
    }

    void open(Cell cell) {
      removeCandidate(cell);
      cells.add(cell);
      if (cells.size() == size) {
        pendingValues.remove(this);
      }
    }

    int countCandidates() {
      return candidates.stream()
          .mapToInt(Collection::size)
          .min()
          .orElse(0);
    }

    Cell getCandidate() {
      return getCandidates().iterator().next();
    }

    Collection<Cell> getCandidates() {
      return candidates.stream()
          .min(comparing(Collection::size))
          .orElse(emptySet());
    }
  }
}
