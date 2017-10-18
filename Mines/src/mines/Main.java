package mines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import mines.bots.CombinationsBot;
import mines.bots.Move;
import mines.bots.mcts.MctsBot;

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
        long mines = Util.randomBits(rng, 0, ~0, 16);
        SmallMinesState state = new SmallMinesState(mines);
        state.discoverSquare(Util.randomBit(rng, ~mines), false);

        CombinationsBot combinationsBot = new CombinationsBot(new Simulator(rng), rng, 1000);
        MctsBot bot = new MctsBot(rng, 1000, combinationsBot);

        System.out.println(PRINTER.getFullStateString(state.getMines(), state.getVisible()));
        System.out.println();
        System.out.println();
        System.out.println(PRINTER.getVisibleStateString(state.getMines(), state.getVisible()));
        while (!state.isGameOver()) {
            Move move = bot.findMove(state);
            int bestSquare = move.square;
            boolean isMine = move.mine;
            System.out.println("clicking: " + bestSquare + ", isMine: " + isMine);
            state.discoverSquare(bestSquare, isMine);
            if (state.playerLost()) {
                System.out.println("game over");
                break;
            }
            correctMoves++;
            if (state.playerWon()) {
                System.out.println("game won");
                break;
            }
            System.out.println(PRINTER.getVisibleStateString(state.getMines(), state.getVisible()));
        }
        System.out.println(PRINTER.getFullStateString(state.getMines(), state.getVisible()));

//        for (int i = 0; i < 1000; i++) {
//            playGame(rng);
//        }
    }

    private static void playGame(Random rng) {
        Simulator simulator = new Simulator(rng);
        long mines = Util.randomBits(rng, 0, ~0, 16);
        SmallMinesState state = new SmallMinesState(mines);
        state.discoverSquare(Util.randomBit(rng, ~mines), false);

        int[] mineCountResult = new int[64];
        int iterationCount = 10000;

        System.out.println(PRINTER.getFullStateString(state.getMines(), state.getVisible()));
        System.out.println();
        System.out.println();
        System.out.println(PRINTER.getVisibleStateString(state.getMines(), state.getVisible()));
        outer:
        while (true) {
            Arrays.fill(mineCountResult, 0);
            simulator.simulate(state.getMines(), state.getVisible(), iterationCount, mineCountResult);

            List<Integer> bestSquares = new ArrayList<>();
            int bestScore = -1;
            long hidden = ~state.getVisible();
            while (hidden != 0) {
                int square = Long.numberOfTrailingZeros(hidden);
                int score = mineCountResult[square];
                score = Math.max(score, iterationCount - score);
                if (score >= bestScore) {
                    if (score > bestScore) {
                        bestSquares.clear();
                    }
                    bestSquares.add(square);
                    bestScore = score;
                }
                hidden ^= Util.toFlag(square);
            }

            do {
                int bestSquare = bestSquares.remove(rng.nextInt(bestSquares.size()));
                boolean isMine = mineCountResult[bestSquare] >= iterationCount / 2;
                int chance = bestScore * 100 / iterationCount;
                chanceSum += chance;
                moves++;
                System.out.println("clicking: " + bestSquare + ", isMine: " + isMine + ", chance: " + chance + "%");
                state.discoverSquare(bestSquare, isMine);
                if (state.playerLost()) {
                    System.out.println("game over");
                    break outer;
                }
                correctMoves++;
                if (state.playerWon()) {
                    System.out.println("game won");
                    break outer;
                }
            } while (!bestSquares.isEmpty() && bestScore == iterationCount);
            System.out.println(PRINTER.getVisibleStateString(state.getMines(), state.getVisible()));
        }
        System.out.println(PRINTER.getFullStateString(state.getMines(), state.getVisible()));
        System.out.println("moves: " + moves + ", chanceSum: " + chanceSum + "%, correctMoves: " + correctMoves);
    }

}
