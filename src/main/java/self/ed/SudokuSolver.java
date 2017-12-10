package self.ed;

import self.ed.exception.MultipleSolutionsException;
import self.ed.exception.NoSolutionException;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;
import static self.ed.SudokuUtils.copy;

public class SudokuSolver {
    private Integer[][] result;
    private List<Cell> pending = new ArrayList<>();

    public SudokuSolver(Integer[][] initialValues) {
        int size = initialValues.length;
        int blockSize = (int) Math.sqrt(size);
        Set<Integer> defaultValues = rangeClosed(1, size).boxed().collect(toSet());

        result = new Integer[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int block = blockSize * (row / blockSize) + col / blockSize;
                Set<Integer> values = ofNullable(initialValues[row][col])
                        .map(Collections::singleton)
                        .orElse(defaultValues);
                pending.add(new Cell(row, col, block, values));
            }
        }
    }

    public Integer[][] solve() {
        if (Thread.interrupted()) {
            throw new RuntimeException("Interrupted!"); //=============
        }
        Cell cell;
        while ((cell = getNextPending()) != null) {
            if (cell.getSize() == 1) {
                open(cell);
            } else if (cell.getSize() > 1) {
//                System.out.println("Guessing out of " + cell.getSize());
//                System.out.println(asString(result));
                List<Integer[][]> solutions = new ArrayList<>();
                for (Integer value : cell.getValues()) {
                    Integer[][] nextGuess = copy(result);
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

        return result;
    }

    private Cell getNextPending() {
        Cell next = null;
        if (!pending.isEmpty()) {
            next = pending.get(0);
            if (next.getSize() != 1) {
                pending.sort(comparing(Cell::getSize));
                next = pending.get(0);
            }
        }
        return next;
    }

    private void open(Cell cell) {
        Integer value = cell.getValue();
        result[cell.getRow()][cell.getCol()] = value;
        pending.remove(cell);
        pending.stream().filter(cell::related).forEach(pend -> pend.removeValue(value));
    }
}
