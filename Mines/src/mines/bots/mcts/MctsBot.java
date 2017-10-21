package mines.bots.mcts;

import java.util.List;
import java.util.Random;
import mines.Constraint;
import mines.ConstraintGenerator;
import mines.Util;
import mines.bots.Bot;
import mines.bots.CombinationsSimulationBot;
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
    private final int iterations;

    public MctsBot(ConstraintGenerator constraintGenerator, Random rng, int iterations, Bot playoutBot) {
        this.agent = new MonteCarloAgent(playoutBot, rng);
        this.rng = rng;
        this.iterations = iterations;
        this.constraintGenerator = constraintGenerator;
    }

    @Override
    public int findMove(MinesStateReadable sourceState) {
        List<Constraint> constraints = constraintGenerator.generateConstraints(sourceState);
        MctsNode root = new MctsNode();
        for (int i = 0; i < iterations; i++) {
            long mines = Util.constrainedRandomBits(rng, constraints);
            agent.iteration(new FastMinesState(mines, sourceState.getRevealed()), root);
        }
        int bestMove = agent.bestChild(sourceState, root);
        System.out.println("expected winrate: " + agent.simulationWinrate(root, bestMove) + ", confidence: " + agent.simulationConfidence(root, bestMove));
        return bestMove;
    }
}
