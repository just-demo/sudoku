package self.ed.visitor;

public interface Visitor {
    void initial(int number);
    void opening();
    void guessing(int number);
    void guessed();
}