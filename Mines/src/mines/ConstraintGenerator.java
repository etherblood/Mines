package mines;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import mines.state.MinesStateReadable;

/**
 *
 * @author Philipp
 */
public class ConstraintGenerator {

    public List<Constraint> generateConstraints(MinesStateReadable state) {
        List<Constraint> constraints = createConstraints(state);
        optimizeConstraints(constraints);
        return constraints;
    }

    private List<Constraint> createConstraints(MinesStateReadable state) {
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(new Constraint(~0, state.countTotalMines()));
        long tmp = state.getRevealed();
        while (tmp != 0) {
            int square = Long.numberOfTrailingZeros(tmp);
            long hiddenNeighborhood = Util.neighbors(square) & ~state.getRevealed();
            if (hiddenNeighborhood != 0) {
                int hiddenNeighborMineCount = state.countNeighborMines(square);
                constraints.add(new Constraint(hiddenNeighborhood, hiddenNeighborMineCount));
            }
            tmp ^= Util.toFlag(square);
        }
        return constraints;
    }

    private void optimizeConstraints(List<Constraint> constraints) {
        for (int i = 0; i + 1 < constraints.size(); i++) {
            Constraint a = constraints.get(i);
            if (filter(a, constraints)) {
                constraints.remove(i);
                i--;
                continue;
            }
            for (int j = i + 1; j < constraints.size(); j++) {
                Constraint b = constraints.get(j);
                if (filter(b, constraints)) {
                    constraints.remove(j);
                    i = - 1;
                    break;
                }
                if (merge(a, b, constraints)) {
                    constraints.remove(j);
                    constraints.remove(i);
                    i = - 1;
                    break;
                }
            }
        }
        Comparator<Constraint> maskSizeComparator = Comparator.comparingInt(c -> Long.bitCount(c.getMask()));
        constraints.sort(maskSizeComparator.reversed());
    }

    private boolean filter(Constraint c, List<Constraint> result) {
        int maskCount = Long.bitCount(c.getMask());
        if (maskCount > 1) {
            if (c.getCount() == maskCount) {
                createConstraints(c.getMask(), 1, result);
                return true;
            } else if (c.getCount() == 0) {
                createConstraints(c.getMask(), 0, result);
                return true;
            }
        }
        return c.getMask() == 0;
    }

    private void createConstraints(long iterator, int minesPerSquare, List<Constraint> result) {
        while (iterator != 0) {
            int square = Long.numberOfTrailingZeros(iterator);
            long flag = Util.toFlag(square);
            result.add(new Constraint(flag, minesPerSquare));
            iterator ^= flag;
        }
    }

    private boolean merge(Constraint a, Constraint b, List<Constraint> result) {
        long sharedSquares = a.getMask() & b.getMask();
        if (sharedSquares == 0) {
            return false;
        }

        if (sharedSquares == a.getMask()) {//a is subset of b
            Constraint n = new Constraint(b.getMask() ^ a.getMask(), b.getCount() - a.getCount());
            result.add(n);
            result.add(a);
            return true;
        }
        if (sharedSquares == b.getMask()) {//b is subset of a
            Constraint n = new Constraint(a.getMask() ^ b.getMask(), a.getCount() - b.getCount());
            result.add(n);
            result.add(b);
            return true;
        }

        return false;
    }
}
