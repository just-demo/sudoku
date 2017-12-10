package self.ed;

import java.util.HashSet;
import java.util.Set;

public class Cell {
    private int row;
    private int col;
    private int block;
    private Set<Integer> values;

    public Cell(int row, int col, int block, Set<Integer> values) {
        this.row = row;
        this.col = col;
        this.block = block;
        this.values = new HashSet<>(values);
    }

    public boolean related(Cell cell) {
        return row == cell.row || col == cell.col || block == cell.block;
    }

    public void removeValue(Integer value) {
        values.remove(value);
    }

    public int getSize() {
        return values.size();
    }

    public Set<Integer> getValues() {
        return values;
    }

    public Integer getValue() {
        return values.iterator().next();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
