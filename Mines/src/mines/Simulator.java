package mines;

import java.util.ArrayList;
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
        List<Constraint> constraints = createConstraints(visible, mines);
        optimizeConstraints(constraints);
        calls = fails = 0;
        for (int i = 0; i < simulationCount; i++) {
            long combination = generateCombinationFast(mines, visible, new ArrayList<>(constraints));
            while (combination != 0) {
                int square = Long.numberOfTrailingZeros(combination);
                mineCountResult[square]++;
                combination ^= Util.toFlag(square);
            }
        }
        System.out.println("calls: " + calls + ", fails: " + fails);
    }

    private List<Constraint> createConstraints(long visible, long mines) {
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

    private long calls, fails;

    private long generateCombination(long mines, long visible, List<Constraint> constraints) {
        calls++;
        int mineCount = Long.bitCount(mines);
        long min = mines & visible;
        long max = mines | ~visible;
        outer:
        while (true) {
            long candidate = Util.selectRandomBits(rng, min, max, mineCount);
            for (Constraint constraint : constraints) {
                if (!constraint.isValid(candidate)) {
                    fails++;
                    continue outer;
                }
            }
            return candidate;
        }
    }

    private long generateCombinationFast(long mines, long visible, List<Constraint> constraints) {
        calls++;
        long min = mines & visible;
        long max = mines | ~visible;
        return Util.selectConstrainedRandomBits(rng, min, max, constraints);
    }
}
