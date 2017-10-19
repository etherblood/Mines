package mines.bots.mcts;

import java.util.Random;
import mines.Simulator;
import mines.bots.Bot;
import mines.bots.CombinationsSimulationBot;
import mines.state.FastMinesState;
import mines.state.MinesStateReadable;

/**
 *
 * @author Philipp
 */
public class MctsBot implements Bot {

    private final Simulator simulator;
    private final MonteCarloAgent agent;
    private final int iterations;

    public MctsBot(Simulator simulator, Random rng, int iterations, CombinationsSimulationBot playoutBot) {
        this.agent = new MonteCarloAgent(playoutBot, rng);
        this.iterations = iterations;
        this.simulator = simulator;
    }

    @Override
    public int findMove(MinesStateReadable sourceState) {
        MctsNode root = new MctsNode();
        for (int i = 0; i < iterations; i++) {
            long mines = simulator.randomCombination(sourceState);
            agent.iteration(new FastMinesState(mines, sourceState.getRevealed()), root);
        }
        int bestMove = agent.bestChild(sourceState, root);
        System.out.println("confidence: " + agent.simulationConfidence(root, bestMove));
        return bestMove;
    }
}
