package self.ed.solver;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public class Cell {

  private final int row;
  private final int col;
  private final int block;
  private final Set<Integer> candidates;

  public Cell(int row, int col, int block, Set<Integer> candidates) {
    this.row = row;
    this.col = col;
    this.block = block;
    this.candidates = new HashSet<>(candidates);
  }

  public boolean isRelated(Cell cell) {
    return row == cell.row || col == cell.col || block == cell.block;
  }

  public void removeCandidate(Integer value) {
    candidates.remove(value);
  }

  public int countCandidates() {
    return candidates.size();
  }

  public Integer getCandidate() {
    return candidates.iterator().next();
  }
}
