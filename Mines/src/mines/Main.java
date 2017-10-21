package mines;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Random;
import mines.bots.Bot;
import mines.bots.CombinationsSimulationBot;
import mines.bots.ConstrainedRandomBot;
import mines.bots.SecureMover;
import mines.bots.mcts.MctsBot;
import mines.bots.mcts.MonteCarloAgent;
import mines.state.FastMinesState;

/**
 *
 * @author Philipp
 */
public class Main {

    private static final MinesPrinter PRINTER = new MinesPrinter();

    static long chanceSum, moves, correctMoves;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int totalMineCount = 16;
//        long seed = System.currentTimeMillis();
//        System.out.println("seed: " + seed);
        Random rng = new SecureRandom();//new Random(seed);
        ConstraintGenerator constraintGenerator = new ConstraintGenerator();

        Bot playoutBot = new CombinationsSimulationBot(constraintGenerator, rng, 10);
        playoutBot = new ConstrainedRandomBot(constraintGenerator, rng);
        Bot bot = new MctsBot(constraintGenerator, rng, 100000, playoutBot);

//        bot = new ConstrainedRandomBot(new ConstraintGenerator(), rng);
        boolean verbose = false;
        long wins = 0, losses = 0;
        for (int i = 0; i < 10; i++) {
            long mines = Util.randomBits(rng, totalMineCount);
            FastMinesState state = new FastMinesState(mines);
            state.reveal(Util.randomBit(rng, ~mines));
            playBotGame(state, bot, verbose);
            if (state.isWon()) {
                wins++;
                System.out.println("win");
                System.out.println(PRINTER.getFullStateString(state));
            } else {
                assert state.isLost();
                losses++;
                System.out.println("loss");
                System.out.println(PRINTER.getFullStateString(state));
            }
        }
        System.out.println("wins: " + wins);
        System.out.println("losses: " + losses);
        System.out.println("winrate: " + new DecimalFormat("#.##").format(100d * wins / (losses + wins)) + "%");
        System.out.println("rng calls: " + Util.rngCalls);
        System.out.println("playout wins: " + MonteCarloAgent.playoutResults + "/" + MonteCarloAgent.playouts + " (" + new DecimalFormat("#.##").format(100d * MonteCarloAgent.playoutResults / MonteCarloAgent.playouts) + "%)");
    }

    private static void playBotGame(FastMinesState state, Bot bot, boolean verbose) {
        if (verbose) {
            System.out.println(PRINTER.getFullStateString(state));
            System.out.println();
            System.out.println();
            System.out.println(PRINTER.getVisibleStateString(state));
        }
        new SecureMover().applySecureMoves(state);
        while (!state.isOver()) {
            int move = bot.findMove(state);
            int bestSquare = move;
            if (verbose) {
                System.out.println("clicking: " + bestSquare);
            }
            state.reveal(bestSquare);
            new SecureMover().applySecureMoves(state);
            if (state.isLost()) {
                if (verbose) {
                    System.out.println("game over");
                }
                break;
            }
            correctMoves++;
            if (state.isWon()) {
                if (verbose) {
                    System.out.println("game won");
                }
                break;
            }
            if (verbose) {
                System.out.println(PRINTER.getVisibleStateString(state));
            }
        }
        if (verbose) {
            System.out.println(PRINTER.getFullStateString(state));
        }
    }

}
