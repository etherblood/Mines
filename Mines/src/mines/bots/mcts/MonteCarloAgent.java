package mines.bots.mcts;

import java.util.HashMap;
import java.util.Map;
import mines.bots.Bot;
import java.util.Random;
import mines.IntList;
import mines.Util;
import mines.bots.SecureMover;
import mines.state.MinesState;
import mines.state.MinesStateReadable;

/**
 *
 * @author Philipp
 */
public class MonteCarloAgent {

    private final SecureMover secureMover = new SecureMover();
    private final Random rng;
    private final UctScore uct = new UctScore();
    private final VisitScore visit = new VisitScore();
    private final Bot playoutBot;
    private final IntList bestMoves = new IntList(64);

    public MonteCarloAgent(Bot playoutBot, Random rng) {
        this.playoutBot = playoutBot;
        this.rng = rng;
    }

    public void iteration(MinesState state, MctsNode node) {
        assert !state.isOver();
        IntList path = new IntList(64);
        MctsNode currentNode = select(state, node, path);
        tryExpand(state, currentNode, path);
        float result = playout(state);
        playouts++;
        playoutResults += result;
        propagateResult(node, path, result);
    }

    public static long playouts = 0, playoutResults = 0;

    private float playout(MinesState state) {
        while (!state.isOver()) {
            secureMover.applySecureMoves(state);
            if (state.isOver()) {
                break;
            }
            int move = playoutBot.findMove(state);
            state.reveal(move);
        }
        return state.isWon() ? 1 : 0;
    }

    private void propagateResult(MctsNode startNode, IntList path, float result) {
        MctsNode currentNode = startNode;
        for (int i = 0; i < path.size(); i++) {
            int move = path.get(i);
            currentNode.increaseScores(result);
            currentNode.increaseVisits(1);
            currentNode = currentNode.getChild(move);
        }
        currentNode.increaseScores(result);
        currentNode.increaseVisits(1);
    }

    private MctsNode select(MinesState state, MctsNode startNode, IntList path) {
        MctsNode currentNode = startNode;
        while (currentNode.isInitialized() && !state.isOver()) {
            int move = selectChild(currentNode, uct, state);
            path.push(move);
            currentNode = gotoChild(state, currentNode, move);
        }
        return currentNode;
    }

    private MctsNode gotoChild(MinesState state, MctsNode currentNode, int move) {
        state.reveal(move);
        return currentNode.getChild(move);
    }

    private void tryExpand(MinesState state, MctsNode currentNode, IntList path) {
        if (!currentNode.isInitialized()) {
            currentNode.initChilds(64);
            if (!state.isOver()) {
                int move = playoutBot.findMove(state);
                path.push(move);
                gotoChild(state, currentNode, move);
            }
        }
    }

    private int selectChild(MctsNode node, ScoringFunction score, MinesStateReadable state) {
        long hidden = ~state.getRevealed();
        long tmp = hidden;
        float childsTotal = 0;
        while (tmp != 0) {
            int square = Long.numberOfTrailingZeros(tmp);
            childsTotal += node.getChild(square).score();
            tmp ^= Util.toFlag(square);
        }

        float bestScore = Float.NEGATIVE_INFINITY;
        tmp = hidden;
        while (tmp != 0) {
            int square = Long.numberOfTrailingZeros(tmp);
            MctsNode child = node.getChild(square);
            float visitScore = child.visitScore();
            float playerScore = child.score();
            float childScore = score.score(childsTotal, visitScore, playerScore);
            if (childScore >= bestScore) {
                if (childScore > bestScore) {
                    bestMoves.clear();
                    bestScore = childScore;
                }
                bestMoves.push(square);
            }

            tmp ^= Util.toFlag(square);
        }
        assert bestScore != Float.NEGATIVE_INFINITY;
        assert !bestMoves.isEmpty();
        int selectedMove = bestMoves.get(bestMoves.size() == 1 ? 0 : rng.nextInt(bestMoves.size()));
        assert (hidden & Util.toFlag(selectedMove)) != 0;
        return selectedMove;
    }

    public int bestChild(MinesStateReadable state, MctsNode node) {
        return selectChild(node, visit, state);
    }

    public float simulationStrength(MctsNode node) {
        return node.visitScore();
    }

    public float simulationConfidence(MctsNode node, int move) {
        MctsNode child = node.getChild(move);
        return child.visitScore() / node.visitScore();
    }

    public float simulationWinrate(MctsNode node, int move) {
        MctsNode child = node.getChild(move);
        return child.score() / child.visitScore();
    }

    Random getRng() {
        return rng;
    }
}
