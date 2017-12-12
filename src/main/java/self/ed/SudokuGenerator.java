package self.ed;

import org.apache.commons.lang3.tuple.Pair;
import self.ed.exception.CountLimitException;
import self.ed.exception.MultipleSolutionsException;
import self.ed.exception.NoSolutionException;

import java.util.*;

import static java.util.Collections.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static self.ed.SudokuUtils.copy;
import static self.ed.SudokuUtils.countOpen;

public class SudokuGenerator {
    public static final int OPEN_LIMIT = 29;
    private int size;
    private int blockSize;

    public SudokuGenerator(int size) {
        this.size = size;
        this.blockSize = (int) Math.sqrt(size);
    }

    public Integer[][] generate() {
        Integer[][] initialValues = new Integer[size][size];
        return generate(initialValues);
    }

    public Integer[][] minimize(Integer[][] initialValues) {
        List<Cell> open = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (initialValues[row][col] != null) {
                    open.add(new Cell(row, col, 0, emptySet()));
                }
            }
        }

        return minimize(initialValues, open);
    }

    private Integer[][] minimize(Integer[][] initialValues, Collection<Cell> closeCandidates) {
        Map<Cell, Integer[][]> candidates = new HashMap<>();
        closeCandidates.forEach(cell -> {
            Integer[][] nextGuess = copy(initialValues);
            nextGuess[cell.getRow()][cell.getCol()] = null;
            try {
                new SudokuSolver(nextGuess).solve();
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
                    return minimize(candidates.get(cell), nextCloseCandidates);
                })
                .map(matrix -> Pair.of(matrix, countOpen(matrix)))
                .min(comparing(Pair::getValue))
                .map(Pair::getKey)
                .orElse(initialValues);
    }

    private Integer[][] generate(Integer[][] initialValues) {
        if (countOpen(initialValues) > OPEN_LIMIT) {
            throw new CountLimitException();
        }

        try {
            new SudokuSolver(initialValues).solve();
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
                List<Integer> values = rangeClosed(1, size).boxed().collect(toList());
                open.stream().filter(cell::related).map(Cell::getValue).forEach(values::remove);
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
}
