package self.ed.exception;

public class ComplexityLimitException extends RuntimeException {
    public ComplexityLimitException() {
        super("To many open!");
    }
}
