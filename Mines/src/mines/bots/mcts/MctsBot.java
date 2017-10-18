package mines.bots.mcts;

import java.util.Random;
import mines.Simulator;
import mines.SmallMinesState;
import mines.bots.Bot;
import mines.bots.CombinationsBot;
import mines.bots.Move;

/**
 *
 * @author Philipp
 */
public class MctsBot implements Bot {

    private final Simulator simulator;
    private final MonteCarloAgent agent;
    private final SmallMinesState state = new SmallMinesState();
    private final int iterations;

    public MctsBot(Random rng, int iterations, CombinationsBot playoutBot) {
        this.agent = new MonteCarloAgent(new SmallMinesState(), playoutBot, rng);
        this.iterations = iterations;
        this.simulator = new Simulator(rng);
    }

    @Override
    public Move findMove(SmallMinesState sourceState) {
        MctsNode root = new MctsNode();
        for (int i = 0; i < iterations; i++) {
            simulator.applyRandomCombination(sourceState, state);
            agent.iteration(state, root);
        }
        Move bestMove = agent.bestChild(sourceState, root);
        System.out.println("confidence: " + agent.simulationConfidence(root, bestMove));
        return bestMove;
    }
}
