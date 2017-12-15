package self.ed.visitor;

import java.util.Comparator;

import static java.util.Comparator.comparing;

public class Statistics implements Visitor {
    public static Comparator<Statistics> COMPLEXITY_COMPARATOR = comparing(Statistics::getMinGuesses)
            .thenComparing(Statistics::getMaxGuesses)
            .thenComparing(Statistics::getOpenings)
            .thenComparing(Statistics::getInitial);

    private int initial = -1;
    private int minGuesses;
    private int maxGuesses;
    private int openings;

    @Override
    public void initial(int number) {
        if (initial == -1) {
            initial = number;
        }
    }

    @Override
    public void opening() {
        openings++;
    }

    @Override
    public void guessing(int number) {
        maxGuesses += number;
    }

    @Override
    public void guessed() {
        minGuesses++;
    }

    public int getInitial() {
        return initial;
    }

    public int getOpenings() {
        return openings;
    }

    public int getMaxGuesses() {
        return maxGuesses;
    }

    public int getMinGuesses() {
        return minGuesses;
    }

    @Override
    public String toString() {
        return minGuesses + " | " + maxGuesses + " | " + openings + " | " + initial;
    }
}
