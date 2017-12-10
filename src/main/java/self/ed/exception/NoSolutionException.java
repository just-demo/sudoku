package self.ed.exception;

public class NoSolutionException extends RuntimeException {
    public NoSolutionException() {
        super("No solution found!");
    }
}
