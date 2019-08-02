package mines;

import mines.state.MinesPrinter;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Random;
import mines.bots.Bot;
import mines.bots.CombinationsSimulationBot;
import mines.bots.ConstrainedRandomBot;
import mines.bots.SecureMover;
import mines.bots.mcts_old.MctsBot;
import mines.bots.mcts_old.MonteCarloAgent;
import mines.state.FastMinesState;
import mines.state.MineConstraints;

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
        int totalMineCount = 10;
//        long seed = System.currentTimeMillis();
//        System.out.println("seed: " + seed);
        Random rng = new SecureRandom();//new Random(seed);
        ConstraintGenerator constraintGenerator = new ConstraintGenerator();

        Bot playoutBot = new CombinationsSimulationBot(constraintGenerator, rng, 10000000);
        playoutBot = new ConstrainedRandomBot(constraintGenerator, rng);
        Bot bot = new MctsBot(constraintGenerator, rng, 10000, 100, playoutBot);

//        bot = playoutBot;//new ConstrainedRandomBot(new ConstraintGenerator(), rng);
        boolean verbose = false;
        long wins = 0, losses = 0;
        for (int i = 0; i < 10; i++) {
//            long mines = Util.randomBits(rng, totalMineCount);
            long mines = Util.randomBits(rng, 0, ~1, totalMineCount);
            FastMinesState state = new FastMinesState(mines);
//            state.reveal(Util.randomBit(rng, ~mines));
            state.reveal(0);
            playBotGame(state, bot, verbose);
            if (state.isWon()) {
                wins++;
                System.out.println("win");
                System.out.println(PRINTER.getFullStateString(state));
                System.out.println();
//                System.out.println(PRINTER.constraintStateString(new MineConstraints(new ConstraintGenerator().generateConstraints(state))));
//                System.out.println();
            } else {
                assert state.isLost();
                losses++;
                System.out.println("loss");
                System.out.println(PRINTER.getFullStateString(state));
                System.out.println();
                FastMinesState previousState = new FastMinesState(state.getMines(), state.getRevealed() & ~state.getMines());
                System.out.println(PRINTER.constraintStateString(new MineConstraints(new ConstraintGenerator().generateConstraints(previousState))));
                System.out.println();
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
        new SecureMover(new ConstraintGenerator()).applySecureMoves(state);
        while (!state.isGameOver()) {
            int move = bot.findMove(state);
            int bestSquare = move;
            if (verbose) {
                System.out.println("revealing: " + bestSquare + " (" + Util.x(bestSquare) + "," + Util.y(bestSquare) + ")");
            }
            state.reveal(bestSquare);
            if(!state.isGameOver()) {
                new SecureMover(new ConstraintGenerator()).applySecureMoves(state);
            }
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
