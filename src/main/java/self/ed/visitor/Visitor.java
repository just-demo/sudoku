package self.ed.visitor;

public interface Visitor {

  void initial(int number);

  void openingCell();

  void openingValue();

  void guessing(int number);

  void guessed();

  static void notifyInitial(Visitor[] visitors, int number) {
    for (Visitor visitor : visitors) {
      visitor.initial(number);
    }
  }

  static void notifyOpeningCell(Visitor[] visitors) {
    for (Visitor visitor : visitors) {
      visitor.openingCell();
    }
  }

  static void notifyOpeningValue(Visitor[] visitors) {
    for (Visitor visitor : visitors) {
      visitor.openingValue();
    }
  }

  static void notifyGuessing(Visitor[] visitors, int number) {
    for (Visitor visitor : visitors) {
      visitor.guessing(number);
    }
  }

  static void notifyGuessed(Visitor[] visitors) {
    for (Visitor visitor : visitors) {
      visitor.guessed();
    }
  }
}