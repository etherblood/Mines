package mines.bots;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import mines.Constraint;
import mines.ConstraintGenerator;
import mines.IntList;
import mines.Util;
import mines.state.MinesStateReadable;

/**
 *
 * @author Philipp
 */
public class CombinationsSimulationBot implements Bot {

    private final ConstraintGenerator constraintGenerator;
    private final Random rng;
    private final int iterations;
    private final float[] mineCounts = new float[64];

    public CombinationsSimulationBot(ConstraintGenerator constraintGenerator, Random rng, int iterations) {
        this.constraintGenerator = constraintGenerator;
        this.rng = rng;
        this.iterations = iterations;
    }

    @Override
    public int findMove(MinesStateReadable sourceState) {
        simulateMineCounts(sourceState);
        return bestMove(~sourceState.getRevealed());
    }

    public float[] simulateMineCounts(MinesStateReadable sourceState) {
        reset();
        List<Constraint> constraints = constraintGenerator.generateConstraints(sourceState);
        for (int i = 0; i < iterations; i++) {
            long mines = Util.constrainedRandomBits(rng, 0, ~sourceState.getRevealed(), constraints);
            addStats(mines);
        }
        return mineCounts;
    }

    private void addStats(long mines) {
        while (mines != 0) {
            int square = Long.numberOfTrailingZeros(mines);
            mineCounts[square]++;
            mines ^= Util.toFlag(square);
        }
    }

    private int bestMove(long hidden) {
        IntList bestSquares = new IntList(64);
        float lowestScore = Float.POSITIVE_INFINITY;
        while (hidden != 0) {
            int square = Long.numberOfTrailingZeros(hidden);
            float score = mineCounts[square];
            if (score <= lowestScore) {
                if (score < lowestScore) {
                    lowestScore = score;
                    bestSquares.clear();
                }
                bestSquares.push(square);
            }
            hidden ^= Util.toFlag(square);
        }
        return bestSquares.get(rng.nextInt(bestSquares.size()));
    }

    private void reset() {
        Arrays.fill(mineCounts, 0);
    }
}
