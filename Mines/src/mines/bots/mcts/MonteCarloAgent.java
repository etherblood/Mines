package mines.bots.mcts;

import mines.bots.Move;
import mines.bots.Bot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mines.SmallMinesState;
import mines.Util;

/**
 *
 * @author Philipp
 */
public class MonteCarloAgent {

    private final Random rng;
    private final SmallMinesState currentState;
    private final UctScore uct = new UctScore();
    private final VisitScore visit = new VisitScore();
    private final Bot playoutBot;
    private final List<Move> bestMoves = new ArrayList<>(64);

    public MonteCarloAgent(SmallMinesState currentState, Bot playoutBot, Random rng) {
        this.currentState = currentState;
        this.playoutBot = playoutBot;
        this.rng = rng;
    }

    public void iteration(SmallMinesState state, MctsNode node) {
        assert !state.isGameOver();
        currentState.copyFrom(state);
        List<Move> path = new ArrayList<>(64);
        MctsNode currentNode = select(node, path);
        tryExpand(currentNode, path);
        float result = playout();
        propagateResult(node, path, result);
    }

    private float playout() {
        while (!currentState.isGameOver()) {
            Move move = playoutBot.findMove(currentState);
            currentState.discoverSquare(move.square, move.mine);
        }
        return currentState.playerWon() ? 1 : 0;
    }

    private void propagateResult(MctsNode startNode, List<Move> path, float result) {
        MctsNode currentNode = startNode;
        for (int i = 0; i < path.size(); i++) {
            Move move = path.get(i);
            currentNode.increaseScores(result);
            currentNode.increaseVisits(1);
            currentNode = currentNode.getChild(move.pack());
        }
        currentNode.increaseScores(result);
        currentNode.increaseVisits(1);
    }

    private MctsNode select(MctsNode startNode, List<Move> path) {
        MctsNode currentNode = startNode;
        while (currentNode.isInitialized() && !currentState.isGameOver()) {
            Move move = selectChild(currentNode, uct);
            path.add(move);
            currentNode = gotoChild(currentNode, move);
        }
        return currentNode;
    }

    private MctsNode gotoChild(MctsNode currentNode, Move move) {
        currentState.discoverSquare(move.square, move.mine);
        return currentNode.getChild(move.pack());
    }

    private void tryExpand(MctsNode currentNode, List<Move> path) {
        if (!currentNode.isInitialized()) {
            currentNode.initChilds(128);
            if (!currentState.isGameOver()) {
                Move move = playoutBot.findMove(currentState);
                path.add(move);
                gotoChild(currentNode, move);
            }
        }
    }

    private Move selectChild(MctsNode node, ScoringFunction score) {
        long hidden = ~currentState.getVisible();
        long tmp = hidden;
        float childsTotal = 0;
        while (tmp != 0) {
            int square = Long.numberOfTrailingZeros(tmp);
            for (int packed = square; packed < 128; packed += 64) {
                childsTotal += node.getChild(packed).score();
            }
            tmp ^= Util.toFlag(square);
        }

        float bestScore = Float.NEGATIVE_INFINITY;
        tmp = hidden;
        while (tmp != 0) {
            int square = Long.numberOfTrailingZeros(tmp);
            for (int i = 0; i < 2; i++) {
                boolean mine = i == 1;

                Move move = new Move(square, mine);
                MctsNode child = node.getChild(move.pack());
                float visitScore = child.visitScore();
                float playerScore = child.score();
                float childScore = score.score(childsTotal, visitScore, playerScore);
                if (childScore >= bestScore) {
                    if (childScore > bestScore) {
                        bestMoves.clear();
                        bestScore = childScore;
                    }
                    bestMoves.add(move);
                }
            }

            tmp ^= Util.toFlag(square);
        }
        assert bestScore != Float.NEGATIVE_INFINITY;
        assert !bestMoves.isEmpty();
        Move selectedMove = bestMoves.get(bestMoves.size() == 1 ? 0 : rng.nextInt(bestMoves.size()));
        assert (hidden & Util.toFlag(selectedMove.square)) != 0;
        return selectedMove;
    }

    public Move bestChild(SmallMinesState state, MctsNode node) {
        currentState.copyFrom(state);
        return selectChild(node, visit);
    }

    public float simulationStrength(MctsNode node) {
        return node.visitScore();
    }

    public float simulationConfidence(MctsNode node, Move move) {
        MctsNode c = node.getChild(move.pack());
        return c.visitScore() / node.visitScore();
    }

    Random getRng() {
        return rng;
    }
}
