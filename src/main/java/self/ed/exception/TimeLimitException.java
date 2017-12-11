package self.ed.exception;

public class TimeLimitException extends RuntimeException {
    public TimeLimitException() {
        super("Took too much time!");
    }
}
