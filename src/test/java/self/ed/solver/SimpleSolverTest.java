package self.ed.solver;


import static org.junit.jupiter.api.Assertions.assertEquals;

import static self.ed.util.Utils.asString;
import static self.ed.util.Utils.parseFile;
import static self.ed.util.Utils.readFile;

import org.junit.jupiter.api.Test;

public class SimpleSolverTest {

  @Test
  public void testSolveSimple() {
    Integer[][] input = parseFile(readFile("input-simple.txt"));
    Integer[][] output = parseFile(readFile("output-simple.txt"));
    SimpleSolver resolver = new SimpleSolver(input);
    assertEquals(asString(output), asString(resolver.solve()));
  }

  @Test
  public void testSolveComplex() {
    Integer[][] input = parseFile(readFile("input-complex.txt"));
    Integer[][] output = parseFile(readFile("output-complex.txt"));
    SimpleSolver resolver = new SimpleSolver(input);
    assertEquals(asString(output), asString(resolver.solve()));
  }

  @Test
  public void testSolveMostComplex() {
    Integer[][] input = parseFile(readFile("input-most-complex.txt"));
    Integer[][] output = parseFile(readFile("output-most-complex.txt"));
    SimpleSolver resolver = new SimpleSolver(input);
    assertEquals(asString(output), asString(resolver.solve()));
  }
}