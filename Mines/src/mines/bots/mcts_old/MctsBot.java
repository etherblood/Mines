package mines.bots.mcts_old;

import java.util.List;
import java.util.Random;
import mines.Constraint;
import mines.ConstraintGenerator;
import mines.IntList;
import mines.Util;
import mines.bots.Bot;
import mines.state.FastMinesState;
import mines.state.MinesStateReadable;

/**
 *
 * @author Philipp
 */
public class MctsBot implements Bot {

    private final ConstraintGenerator constraintGenerator;
    private final Random rng;
    private final MonteCarloAgent agent;
    private final int iterations, innerIterations;

    public MctsBot(ConstraintGenerator constraintGenerator, Random rng, int iterations, int innerIterations, Bot playoutBot) {
        this.agent = new MonteCarloAgent(playoutBot, rng);
        this.rng = rng;
        this.iterations = iterations;
        this.innerIterations = innerIterations;
        this.constraintGenerator = constraintGenerator;
    }

    @Override
    public int findMove(MinesStateReadable sourceState) {
        agent.reset();
        List<Constraint> constraints = constraintGenerator.generateConstraints(sourceState);
        float[] scores = new float[64];
        for (int i = 0; i < iterations; i++) {
            MctsNode root = new MctsNode();
            long mines = Util.constrainedRandomBits(rng, constraints);
            for (int j = 0; j < innerIterations; j++) {
                agent.iteration(new FastMinesState(mines, sourceState.getRevealed()), root);
            }
            long iterator = ~sourceState.getRevealed();
            while (iterator != 0) {
                int square = Long.numberOfTrailingZeros(iterator);
                MctsNode child = root.getChild(square);
                scores[square] += child == null ? 0 : child.visitScore();
                iterator ^= Util.toFlag(square);
            }
        }

        IntList bestSquares = new IntList(64);
        float bestScore = Float.NEGATIVE_INFINITY;
        long iterator = ~sourceState.getRevealed();
        while (iterator != 0) {
            int square = Long.numberOfTrailingZeros(iterator);
            float score = scores[square];
            if (score >= bestScore) {
                if (score > bestScore) {
                    bestScore = score;
                    bestSquares.clear();
                }
                bestSquares.push(square);
            }
            iterator ^= Util.toFlag(square);
        }
        int bestMove = bestSquares.get(rng.nextInt(bestSquares.size()));
        return bestMove;
    }
}
