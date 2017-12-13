package self.ed;

import self.ed.exception.MultipleSolutionsException;
import self.ed.exception.NoSolutionException;
import self.ed.exception.TimeLimitException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;
import static self.ed.SudokuUtils.copy;

public class SudokuSolver {
    private Integer[][] currentState;
    private List<Cell> pending = new ArrayList<>();

    public SudokuSolver(Integer[][] initialValues) {
        int size = initialValues.length;
        int blockSize = (int) Math.sqrt(size);
        Set<Integer> values = rangeClosed(1, size).boxed().collect(toSet());

        currentState = new Integer[size][size];
        List<Cell> open = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int block = blockSize * (row / blockSize) + col / blockSize;
                Integer value = initialValues[row][col];
                if (value != null) {
                    open.add(new Cell(row, col, block, singleton(value)));
                } else {
                    pending.add(new Cell(row, col, block, values));
                }
            }
        }

        open.forEach(this::open);
    }

    public Integer[][] solve() {
        if (Thread.interrupted()) {
            throw new TimeLimitException();
        }

        while (!pending.isEmpty()) {
            Cell cell = pending.get(0);
            if (cell.countCandidates() != 1) {
                pending.sort(comparing(Cell::countCandidates));
                cell = pending.get(0);
            }
            if (cell.countCandidates() == 1) {
                open(cell);
            } else if (cell.countCandidates() > 1) {
                System.out.println("Guessing out of " + cell.countCandidates());
                List<Integer[][]> solutions = new ArrayList<>();
                for (Integer value : cell.getCandidates()) {
                    Integer[][] nextGuess = copy(currentState);
                    nextGuess[cell.getRow()][cell.getCol()] = value;
                    try {
                        solutions.add(new SudokuSolver(nextGuess).solve());
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
                return solutions.iterator().next();
            } else {
                throw new NoSolutionException();
            }
        }

        return currentState;
    }

    private void open(Cell cell) {
        Integer value = cell.getValue();
        currentState[cell.getRow()][cell.getCol()] = value;
        pending.remove(cell);
        pending.stream().filter(cell::isRelated).forEach(pend -> pend.removeValue(value));
    }
}
