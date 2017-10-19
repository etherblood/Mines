package mines;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import mines.bots.Bot;
import mines.bots.CombinationsSimulationBot;
import mines.bots.SecureMover;
import mines.bots.mcts.MctsBot;
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
        long seed = System.currentTimeMillis();
        System.out.println("seed: " + seed);
        Random rng = new Random(seed);

        CombinationsSimulationBot combinationsBot = new CombinationsSimulationBot(new Simulator(new ConstraintGenerator(), rng), rng, 1000);
        Bot bot = new MctsBot(new Simulator(new ConstraintGenerator(), rng), rng, 1000, combinationsBot);

//        bot = combinationsBot;//new ConstrainedRandomBot(new ConstraintGenerator(), rng);
        boolean verbose = true;
        long wins = 0, losses = 0;
        for (int i = 0; i < 1; i++) {
            long mines = Util.randomBits(rng, 0, ~0, 16);
            FastMinesState state = new FastMinesState(mines);
            state.reveal(Util.randomBit(rng, ~mines));
            playBotGame(state, bot, verbose);
            if (state.isWon()) {
                wins++;
            } else {
                assert state.isLost();
                losses++;
            }
        }
        System.out.println("wins: " + wins);
        System.out.println("losses: " + losses);
        System.out.println("winrate: " + new DecimalFormat("#.##").format(100d * wins / (losses + wins)) + "%");
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

    private static void playGame(Random rng) {
        Simulator simulator = new Simulator(new ConstraintGenerator(), rng);
        long mines = Util.randomBits(rng, 0, ~0, 16);
        FastMinesState state = new FastMinesState(mines);
        state.reveal(Util.randomBit(rng, ~mines));

        int[] mineCountResult = new int[64];
        int iterationCount = 10000;

        System.out.println(PRINTER.getFullStateString(state));
        System.out.println();
        System.out.println();
        System.out.println(PRINTER.getVisibleStateString(state));
        outer:
        while (true) {
            Arrays.fill(mineCountResult, 0);
            simulator.simulate(state, iterationCount, mineCountResult);

            List<Integer> bestSquares = new ArrayList<>();
            int bestScore = Integer.MAX_VALUE;
            long hidden = ~state.getRevealed();
            while (hidden != 0) {
                int square = Long.numberOfTrailingZeros(hidden);
                int score = mineCountResult[square];
                if (score <= bestScore) {
                    if (score < bestScore) {
                        bestSquares.clear();
                        bestScore = score;
                    }
                    bestSquares.add(square);
                }
                hidden ^= Util.toFlag(square);
            }

            do {
                int bestSquare = bestSquares.remove(rng.nextInt(bestSquares.size()));
                int chance = bestScore * 100 / iterationCount;
                chanceSum += chance;
                moves++;
                System.out.println("clicking: " + bestSquare + ", chance: " + chance + "%");
                state.reveal(bestSquare);
                if (state.isLost()) {
                    System.out.println("game over");
                    break outer;
                }
                correctMoves++;
                if (state.isWon()) {
                    System.out.println("game won");
                    break outer;
                }
            } while (!bestSquares.isEmpty() && bestScore == iterationCount);
            System.out.println(PRINTER.getVisibleStateString(state));
        }
        System.out.println(PRINTER.getFullStateString(state));
        System.out.println("moves: " + moves + ", chanceSum: " + chanceSum + "%, correctMoves: " + correctMoves);
    }

}
