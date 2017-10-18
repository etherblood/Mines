package mines.bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import mines.Simulator;
import mines.SmallMinesState;
import mines.Util;

/**
 *
 * @author Philipp
 */
public class CombinationsBot implements Bot {

    private final Simulator simulator;
    private final Random rng;
    private final SmallMinesState state = new SmallMinesState();
    private final int iterations;
    private final int[] mineCounts = new int[64];

    public CombinationsBot(Simulator simulator, Random rng, int iterations) {
        this.simulator = simulator;
        this.rng = rng;
        this.iterations = iterations;
    }

    @Override
    public Move findMove(SmallMinesState sourceState) {
        reset();
        for (int i = 0; i < iterations; i++) {
            simulator.applyRandomCombination(sourceState, state);
            addStats(state.getMines());
        }
        return bestMove(~sourceState.getVisible());
    }

    private void addStats(long mines) {
        while (mines != 0) {
            int square = Long.numberOfTrailingZeros(mines);
            mineCounts[square]++;
            mines ^= Util.toFlag(square);
        }
    }

    private Move bestMove(long hidden) {
        List<Integer> bestSquares = new ArrayList<>();
        int bestScore = -1;
        while (hidden != 0) {
            int square = Long.numberOfTrailingZeros(hidden);
            int score = mineCounts[square];
            score = Math.max(score, iterations - score);
            if (score >= bestScore) {
                if (score > bestScore) {
                    bestSquares.clear();
                }
                bestSquares.add(square);
                bestScore = score;
            }
            hidden ^= Util.toFlag(square);
        }
        int square = bestSquares.get(rng.nextInt(bestSquares.size()));
        return new Move(square, 2 * mineCounts[square] > iterations);
    }
    
    private void reset() {
        Arrays.fill(mineCounts, 0);
    }
}
