package mines.bots.mcts_new;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import mines.ConstraintGenerator;
import mines.IntList;
import mines.Util;
import mines.bots.Bot;
import mines.bots.SecureMover;
import mines.bots.mcts_old.ScoringFunction;
import mines.bots.mcts_old.UctScore;
import mines.bots.mcts_old.VisitScore;
import mines.state.FastMinesState;
import mines.state.MineConstraints;

/**
 *
 * @author Philipp
 */
public class SmallMctsAgent {

    private final SecureMover secureMover = new SecureMover(new ConstraintGenerator());
    private final Random random = new SecureRandom();
    private final UctScore uct = new UctScore();
    private final VisitScore visit = new VisitScore();
    private final Bot playoutBot;

    public SmallMctsAgent(Bot playoutBot) {
        this.playoutBot = playoutBot;
    }

    public int search(MineConstraints constraints) {
        float[] result = new float[Long.SIZE];
        int stateSamples = 1000;
        for (int stateSample = 0; stateSample < stateSamples; stateSample++) {
            long mines = Util.constrainedRandomBits(random, constraints.getConstraints());
            long revealed = constraints.getSecured();
            FastMinesState state = new FastMinesState(mines, revealed);
            secureMover.applySecureMoves(state);
            assert !state.isGameOver();

            SmallNode root = new SmallNode(0);
            int stateIterations = 1000;
            for (int iteration = 0; iteration < stateIterations; iteration++) {
                iteration(root, new FastMinesState(state));
            }
            //apply result
            Iterator<SmallNode> it = root.childIterator();
            while (it.hasNext()) {
                SmallNode next = it.next();
                result[next.getMove()] += next.getVisits();
            }
        }
        
        System.out.println(Arrays.toString(result));
        float max = Float.NEGATIVE_INFINITY;
        int bestMove = 0;
        for (int move = 0; move < result.length; move++) {
            float score = result[move];
            if(score > max) {
                max = score;
                bestMove = move;
            }
        }
        System.out.println("best move: " + bestMove);
        return bestMove;
    }

    private void iteration(SmallNode root, FastMinesState state) {
        assert !state.isGameOver();
        SmallNode current = root;
        IntList path = new IntList(Long.bitCount(~state.getRevealed()));
        int lastMove;
        //select
        do {
            MineConstraints constraints = new MineConstraints(secureMover.getConstraintGenerator().generateConstraints(state));
            long moveCandidates = ~(constraints.getForcedBombs() | state.getRevealed());
            lastMove = selectMove(current, uct, moveCandidates);
            path.push(lastMove);
            state.reveal(lastMove);
            SecureMover.applySecureMoves(state, constraints);
            SmallNode child = current.getChild(lastMove);
            if (child == null) {
                //expand
                child = new SmallNode(lastMove);
                current.appendChild(child);
                break;
            }
            current = child;
        } while (!state.isGameOver());
        //playout
        while (!state.isGameOver()) {
            int move = playoutBot.findMove(state);
            state.reveal(move);
            secureMover.applySecureMoves(state);
        }
        //propagate
        float score = state.isWon() ? 1 : 0;
        current = root;
        current.applyScore(score);
        for (int i = 0; i < path.size(); i++) {
            int move = path.get(i);
            current = current.getChild(move);
            current.applyScore(score);
        }
    }

    private int selectMove(SmallNode node, ScoringFunction scoring, long availableMoves) {
        IntList bestMoves = new IntList(Long.bitCount(availableMoves));
        float bestScore = Float.NEGATIVE_INFINITY;
        long remainingMoves = availableMoves;
        while (remainingMoves != 0) {
            int square = Long.numberOfTrailingZeros(remainingMoves);
            SmallNode child = node.getChild(square);
            float visitScore = child == null ? 0 : child.getVisits();
            float childWins = child == null ? 0 : child.getScore();
            float childScore = scoring.score(node.getVisits(), visitScore, childWins);
            if (childScore >= bestScore) {
                if (childScore > bestScore) {
                    bestMoves.clear();
                    bestScore = childScore;
                }
                bestMoves.push(square);
            }

            remainingMoves ^= Util.toFlag(square);
        }
        assert bestScore != Float.NEGATIVE_INFINITY;
        assert !bestMoves.isEmpty();
        int selectedMove = bestMoves.get(random.nextInt(bestMoves.size()));
        assert (availableMoves & Util.toFlag(selectedMove)) != 0;
        return selectedMove;
    }

}
