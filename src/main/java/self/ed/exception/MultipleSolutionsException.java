package self.ed.exception;

public class MultipleSolutionsException extends RuntimeException {
    public MultipleSolutionsException() {
        super("Multiple solutions found!");
    }
}
