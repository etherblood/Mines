package mines.state;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import mines.Constraint;
import mines.ConstraintGenerator;
import mines.Util;

/**
 *
 * @author Philipp
 */
public class MineConstraints {

    private final List<Constraint> constraints;

    public MineConstraints(MineConstraints constraints) {
        this(constraints.constraints);
    }

    public MineConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }
    
    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
        for (int i = constraints.size() - 1; i < constraints.size(); i++) {
            Constraint a = constraints.get(i);
            if (ConstraintGenerator.filter(a, constraints)) {
                constraints.remove(i);
                i--;
                continue;
            }
            for (int j = 0; j < i; j++) {
                Constraint b = constraints.get(j);
                if (ConstraintGenerator.merge(a, b, constraints)) {
                    constraints.remove(i);
                    constraints.remove(j);
                    i -= 2;
                    break;
                }
            }
        }
        Comparator<Constraint> maskSizeComparator = Comparator.comparingInt(c -> Long.bitCount(c.getSquares()));
        constraints.sort(maskSizeComparator.reversed().thenComparingLong(Constraint::getSquares));
    }

    public int countNeighborMines(int square) {
        long neighborhood = Util.neighbors(square);
        int count = 0;
        for (Constraint constraint : constraints) {
            if ((constraint.getSquares() & neighborhood) == constraint.getSquares()) {
                neighborhood &= ~constraint.getSquares();
                count += constraint.getMineCount();
            }
        }
        assert neighborhood == 0;
        return count;
    }

    public long getSecured() {
        long result = 0;
        for (Constraint constraint : constraints) {
            if (constraint.getMineCount() == 0) {
                result |= constraint.getSquares();
            }
        }
        return result;
    }
    
    public long getForcedBombs() {
        long result = 0;
        for (Constraint constraint : constraints) {
            if (constraint.getMineCount() == Long.bitCount(constraint.getSquares())) {
                result |= constraint.getSquares();
            }
        }
        return result;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    @Override
    public int hashCode() {
        return constraints.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MineConstraints)) {
            return false;
        }
        MineConstraints other = (MineConstraints) obj;
        return other.constraints.equals(constraints);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{constraints=" + Arrays.toString(constraints.toArray()) + '}';
    }
}
