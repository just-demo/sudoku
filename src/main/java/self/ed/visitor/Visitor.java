package self.ed.visitor;

public interface Visitor {
    void initial(int number);

    void opening();

    void guessing(int number);

    void guessed();

    static void notifyInitial(Visitor[] visitors, int number) {
        for (Visitor visitor : visitors) {
            visitor.initial(number);
        }
    }

    static void notifyOpening(Visitor[] visitors) {
        for (Visitor visitor : visitors) {
            visitor.opening();
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