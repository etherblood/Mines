package mines;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Philipp
 */
public class Simulator {

    private final Random rng;

    public Simulator(Random rng) {
        this.rng = rng;
    }

    public void simulate(long mines, long visible, int simulationCount, int[] mineCountResult) {
        List<Constraint> constraints = createConstraints(mines, visible);
        optimizeConstraints(constraints);
        for (int i = 0; i < simulationCount; i++) {
            long combination = generateCombination(mines, visible, new ArrayList<>(constraints));
            while (combination != 0) {
                int square = Long.numberOfTrailingZeros(combination);
                mineCountResult[square]++;
                combination ^= Util.toFlag(square);
            }
        }
    }
    
    public void applyRandomCombination(SmallMinesState source, SmallMinesState dest) {
        List<Constraint> constraints = createConstraints(source.getMines(), source.getVisible());
        optimizeConstraints(constraints);
        long mines = generateCombination(source.getMines(), source.getVisible(), constraints);
        dest.setMines(mines);
        dest.setVisible(source.getVisible());
    }

    private List<Constraint> createConstraints(long mines, long visible) {
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(new Constraint(~0, Long.bitCount(mines)));
        long tmp = visible & ~mines;
        while (tmp != 0) {
            int square = Long.numberOfTrailingZeros(tmp);
            long neighborhood = Util.neighborhood(square);
            long hiddenNeighborhood = neighborhood & ~visible;
            if (hiddenNeighborhood != 0) {
                int hiddenNeighborMineCount = Long.bitCount(hiddenNeighborhood & mines);
                constraints.add(new Constraint(hiddenNeighborhood, hiddenNeighborMineCount));
            }
            tmp ^= Util.toFlag(square);
        }
        return constraints;
    }

    private void optimizeConstraints(List<Constraint> constraints) {
        for (int i = 0; i + 1 < constraints.size(); i++) {
            Constraint a = constraints.get(i);
            if (filter(a)) {
                constraints.remove(i);
                i--;
                continue;
            }
            for (int j = i + 1; j < constraints.size(); j++) {
                Constraint b = constraints.get(j);
                if (filter(b)) {
                    constraints.remove(j);
                    j--;
                    continue;
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

    private boolean filter(Constraint c) {
        return c.getMask() == 0;
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

    private long generateCombination(long mines, long visible, List<Constraint> constraints) {
        long lowerBound = mines & visible;
        long upperBound = mines | ~visible;
        return Util.constrainedRandomBits(rng, lowerBound, upperBound, constraints);
    }
}
