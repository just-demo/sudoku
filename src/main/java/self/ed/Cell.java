package self.ed;

import java.util.HashSet;
import java.util.Set;

public class Cell {
    private int row;
    private int col;
    private int block;
    private Set<Integer> candidates;

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

    public Set<Integer> getCandidates() {
        return candidates;
    }

    public Integer getCandidate() {
        return candidates.iterator().next();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
