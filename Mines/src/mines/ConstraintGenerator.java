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
        assert validateConstraints(state, constraints);
        optimizeConstraints(constraints);
        assert validateConstraints(state, constraints);
        return constraints;
    }

    private boolean validateConstraints(MinesStateReadable state, List<Constraint> constraints) {
        long constrainedSquares = 0;
        for (Constraint constraint : constraints) {
            constrainedSquares |= constraint.getSquares();
        }
        assert constrainedSquares == ~0;
        return true;
    }

    private List<Constraint> createConstraints(MinesStateReadable state) {
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(new Constraint(~state.getRevealed(), state.countTotalMines()));
        constraints.add(new Constraint(state.getRevealed(), 0));
        long iterator = state.getRevealed();
        while (iterator != 0) {
            int square = Long.numberOfTrailingZeros(iterator);
            long hiddenNeighborhood = Util.neighbors(square) & ~state.getRevealed();
            if (hiddenNeighborhood != 0) {
                int hiddenNeighborMineCount = state.countNeighborMines(square);
                constraints.add(new Constraint(hiddenNeighborhood, hiddenNeighborMineCount));
            }
            iterator ^= Util.toFlag(square);
        }
        return constraints;
    }

    private List<Constraint> createSimpleConstraints(MinesStateReadable state) {
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(new Constraint(~0, state.countTotalMines()));
        constraints.add(new Constraint(state.getRevealed(), 0));
        long iterator = state.getRevealed();
        while (iterator != 0) {
            int square = Long.numberOfTrailingZeros(iterator);
            constraints.add(new Constraint(Util.neighbors(square), state.countNeighborMines(square)));
            iterator ^= Util.toFlag(square);
        }
        return constraints;
    }

    private void optimizeConstraints(List<Constraint> constraints) {
        for (int i = 0; i < constraints.size(); i++) {
            Constraint a = constraints.get(i);
            if (filter(a, constraints)) {
                constraints.remove(i);
                i--;
                continue;
            }
            for (int j = 0; j < i; j++) {
                Constraint b = constraints.get(j);
                if (merge(a, b, constraints)) {
                    constraints.remove(i);
                    constraints.remove(j);
                    i -= 2;
                    break;
                }
            }
        }
        Comparator<Constraint> maskSizeComparator = Comparator.comparingInt(c -> Long.bitCount(c.getSquares()));
        constraints.sort(maskSizeComparator.reversed());
    }

    private boolean filter(Constraint c, List<Constraint> result) {
        int maskCount = Long.bitCount(c.getSquares());
        assert 0 <= c.getMineCount() && c.getMineCount() <= maskCount;
        if (maskCount > 1) {
            if (c.getMineCount() == maskCount) {
                createConstraints(c.getSquares(), 1, result);
                return true;
            } else if (c.getMineCount() == 0) {
                createConstraints(c.getSquares(), 0, result);
                return true;
            }
        }
        return c.getSquares() == 0;
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
        long sharedSquares = a.getSquares() & b.getSquares();
        if (sharedSquares == 0) {
            return false;
        }

        if (mergeEqual(a, b, result)) {
            return true;
        }

        if (mergeSubset(a, b, result)) {
            return true;
        }
        if (mergeSubset(b, a, result)) {
            return true;
        }

        if (mergeForced(a, b, result)) {
            return true;
        }
        if (mergeForced(b, a, result)) {
            return true;
        }

        return false;
    }

    private boolean mergeEqual(Constraint a, Constraint b, List<Constraint> result) {
        if (a.getSquares() == b.getSquares() && a.getMineCount() == b.getMineCount()) {
            result.add(a);
            return true;
        }
        return false;
    }

    private boolean mergeSubset(Constraint a, Constraint b, List<Constraint> result) {
        long sharedSquares = a.getSquares() & b.getSquares();
        if (sharedSquares == a.getSquares()) {
            //a is subset of b
            result.add(new Constraint(b.getSquares() ^ a.getSquares(), b.getMineCount() - a.getMineCount()));
            result.add(a);
            return true;
        }
        return false;
    }

    private boolean mergeForced(Constraint a, Constraint b, List<Constraint> result) {
        long sharedSquares = a.getSquares() & b.getSquares();

        long aOnlySquares = a.getSquares() ^ sharedSquares;
        int aOnlySquareCount = Long.bitCount(aOnlySquares);

        if (aOnlySquareCount == a.getMineCount() - b.getMineCount()) {
            int sharedMineCount = a.getMineCount() - aOnlySquareCount;
            result.add(new Constraint(aOnlySquares, aOnlySquareCount));
            result.add(new Constraint(sharedSquares, sharedMineCount));
            result.add(new Constraint(b.getSquares() ^ sharedSquares, b.getMineCount() - sharedMineCount));
            return true;
        }
        if (aOnlySquareCount == Long.bitCount(a.getSquares()) - Long.bitCount(b.getSquares()) + b.getMineCount() - a.getMineCount()) {
            result.add(new Constraint(aOnlySquares, 0));
            result.add(new Constraint(sharedSquares, a.getMineCount()));
            result.add(new Constraint(b.getSquares() ^ sharedSquares, b.getMineCount() - a.getMineCount()));
            return true;
        }
        return false;
    }
}
