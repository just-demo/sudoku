package self.ed.visitor;

import java.util.Comparator;

import static java.util.Comparator.comparing;

public class StatisticsCaptor implements Visitor {
    public static Comparator<StatisticsCaptor> COMPLEXITY_COMPARATOR = comparing(StatisticsCaptor::getGuessMin)
            .thenComparing(StatisticsCaptor::getGuessMax)
            .thenComparing(StatisticsCaptor::getOpened);

    private int guessMin;
    private int guessMax;
    private int opened;

    @Override
    public void opened() {
        opened++;
    }

    @Override
    public void guessing(int number) {
        guessMax += number;
    }

    @Override
    public void guessed() {
        guessMin++;
    }

    public int getOpened() {
        return opened;
    }

    public int getGuessMax() {
        return guessMax;
    }

    public int getGuessMin() {
        return guessMin;
    }

    @Override
    public String toString() {
        return guessMin + " / " + guessMax + " / " + opened;
    }
}
