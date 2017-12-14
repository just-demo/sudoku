package self.ed.visitor;

public interface Visitor {
    void opened();
    void guessing(int number);
    void guessed();
}