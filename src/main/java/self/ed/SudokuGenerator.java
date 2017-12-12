package self.ed;

import self.ed.exception.CountLimitException;
import self.ed.exception.MultipleSolutionsException;
import self.ed.exception.NoSolutionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
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
//        List<Integer> indexes = IntStream.rangeClosed(1, size * size).boxed().collect(toList());
//        List<Integer> values = IntStream.rangeClosed(1, size).boxed().collect(toList());
//        Collections.shuffle(indexes);
//        Collections.shuffle(values);
//        for (int i = 0; i < size - 1; i++) {
//            int index = indexes.get(i);
//            int value = values.get(i);
//            initialValues[index / size][index % size] = value;
//        }
        return generate(initialValues);
    }

//    private int counter;

    public Integer[][] minimize(Integer[][] initialValues) {
        List<Cell> opened = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (initialValues[row][col] != null) {
                    opened.add(new Cell(row, col, 0, emptySet()));
                }
            }
        }

        Collections.shuffle(opened);
        return opened.stream()
                .map(cell -> {
                    Integer[][] nextGuess = copy(initialValues);
                    nextGuess[cell.getRow()][cell.getCol()] = null;
                    try {
                        new SudokuSolver(nextGuess).solve();
                        return minimize(nextGuess);
                    } catch (NoSolutionException | MultipleSolutionsException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .min(comparing(values -> (int) SudokuUtils.countOpen(values)))
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
            List<Cell> opened = new ArrayList<>();
            List<Cell> pending = new ArrayList<>();
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    Integer value = initialValues[row][col];
                    int block = blockSize * (row / blockSize) + col / blockSize;
                    if (value != null) {
                        opened.add(new Cell(row, col, block, singleton(value)));
                    } else {
                        pending.add(new Cell(row, col, block, emptySet()));
                    }
                }
            }

            Collections.shuffle(pending);
            for (Cell cell : pending) {
                Integer[][] nextGuess = copy(initialValues);
                List<Integer> values = rangeClosed(1, size).boxed().collect(toList());
                opened.stream().filter(cell::related).map(Cell::getValue).forEach(values::remove);
                Collections.shuffle(values);
                for (Integer value : values) {
                    nextGuess[cell.getRow()][cell.getCol()] = value;
                    try {
//                        System.out.println("Try: " + ++counter);  // 69, 32/29, 57/43, 48/40
//                        System.out.println(asString(nextGuess)); ////////////////
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
