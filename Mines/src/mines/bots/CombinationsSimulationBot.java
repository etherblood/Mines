package mines.bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import mines.Simulator;
import mines.Util;
import mines.state.MinesStateReadable;

/**
 *
 * @author Philipp
 */
public class CombinationsSimulationBot implements Bot {

    private final Simulator simulator;
    private final Random rng;
    private final int iterations;
    private final int[] mineCounts = new int[64];

    public CombinationsSimulationBot(Simulator simulator, Random rng, int iterations) {
        this.simulator = simulator;
        this.rng = rng;
        this.iterations = iterations;
    }

    @Override
    public int findMove(MinesStateReadable sourceState) {
        reset();
        for (int i = 0; i < iterations; i++) {
            long mines = simulator.randomCombination(sourceState);
            addStats(mines);
        }
        return bestMove(~sourceState.getRevealed());
    }

    private void addStats(long mines) {
        while (mines != 0) {
            int square = Long.numberOfTrailingZeros(mines);
            mineCounts[square]++;
            mines ^= Util.toFlag(square);
        }
    }

    private int bestMove(long hidden) {
        List<Integer> bestSquares = new ArrayList<>();
        int bestScore = Integer.MAX_VALUE;
        while (hidden != 0) {
            int square = Long.numberOfTrailingZeros(hidden);
            int score = mineCounts[square];
            if (score <= bestScore) {
                if (score < bestScore) {
                    bestScore = score;
                    bestSquares.clear();
                }
                bestSquares.add(square);
            }
            hidden ^= Util.toFlag(square);
        }
        return bestSquares.get(rng.nextInt(bestSquares.size()));
    }

    private void reset() {
        Arrays.fill(mineCounts, 0);
    }
}
