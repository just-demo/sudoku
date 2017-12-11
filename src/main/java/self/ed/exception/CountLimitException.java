package self.ed.exception;

public class CountLimitException extends RuntimeException {
    public CountLimitException() {
        super("To many open!");
    }
}
