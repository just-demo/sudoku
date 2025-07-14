package self.ed.visitor;

import static java.util.Comparator.comparing;

import static self.ed.util.Utils.join;

import java.util.Comparator;

import lombok.Getter;

@Getter
public class StatisticsCaptor implements Visitor {

  public static Comparator<StatisticsCaptor> COMPLEXITY_COMPARATOR = comparing(StatisticsCaptor::getOpenings)
      .thenComparing(StatisticsCaptor::getMaxGuesses)
      .thenComparing(StatisticsCaptor::getMinGuesses)
      .thenComparing(StatisticsCaptor::getInitial);

  private int initial = -1;
  private int minGuesses;
  private int maxGuesses;
  private int cellOpenings;
  private int valueOpenings;

  @Override
  public void initial(int number) {
    if (initial == -1) {
      initial = number;
    }
  }

  @Override
  public void openingCell() {
    cellOpenings++;
  }

  @Override
  public void openingValue() {
    valueOpenings++;
  }

  @Override
  public void guessing(int number) {
    maxGuesses += number;
  }

  @Override
  public void guessed() {
    minGuesses++;
  }

  public int getOpenings() {
    return cellOpenings + valueOpenings;
  }

  @Override
  public String toString() {
    return join(" | ", minGuesses, maxGuesses, valueOpenings, cellOpenings, initial);
  }
}
