package mines.bots.mcts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import mines.Constraint;
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
public class MctsAgent {

    private final Map<MineConstraints, MctsNode> nodeMap = new HashMap<>();
    private final SecureMover secureMover = new SecureMover(null);
    private final Random random = new Random();
    private final UctScore uct = new UctScore();
    private final VisitScore visit = new VisitScore();
    private final Bot playoutBot;

    public MctsAgent(Bot playoutBot) {
        this.playoutBot = playoutBot;
    }

    public void iteration(MineConstraints constraints) {
        List<ConstraintEdge> path = new ArrayList<>();
        MctsNode startNode = nodeMap.get(constraints);
        //select
        MctsNode node = startNode;
        MineConstraints currentConstraints = new MineConstraints(constraints);
        while (node != null) {
            long hiddenSquares = ~currentConstraints.getRevealed();
            int square = selectChild(node, uct, hiddenSquares);
            long mines = Util.constrainedRandomBits(random, currentConstraints.getConstraints());
            long squareFlag = Util.toFlag(square);
            hiddenSquares &= ~squareFlag;
            if ((mines & squareFlag) != 0) {
                propagate(startNode, 0, path);
                return;
            }
            if (hiddenSquares == 0) {
                propagate(startNode, 1, path);
                return;
            }
            long hiddenNeighbors = Util.neighbors(square) & hiddenSquares;
            currentConstraints.addConstraint(new Constraint(hiddenNeighbors, Long.bitCount(mines & hiddenNeighbors)));
            currentConstraints.addConstraint(new Constraint(squareFlag, 0));
            MineConstraints childConstraints = new MineConstraints(currentConstraints);
            ConstraintEdge child = node.getOrCreateChild(square, childConstraints);
            path.add(child);
            node = nodeMap.get(childConstraints);
            if (child.visits < node.visits) {
                float result = node.winrate();
                propagate(startNode, result, path);
                return;
            }
        }
        //select end

        //expand
//        if(node == null) {
        nodeMap.computeIfAbsent(currentConstraints, MctsNode::new);
//        }
        //expand end
        //playout
        long mines = Util.constrainedRandomBits(random, currentConstraints.getConstraints());
        long revealed = constraints.getRevealed();
        FastMinesState minesState = new FastMinesState(mines, revealed);
        while (!minesState.isGameOver()) {
            int square = playoutBot.findMove(minesState);
            minesState.reveal(square);
            secureMover.applySecureMoves(minesState, new MineConstraints(new ConstraintGenerator().generateConstraints(minesState)));
        }
        //playout end
        //propagate
        float result = minesState.isWon() ? 1 : 0;
        propagate(startNode, result, path);
        //propagate end
    }

    private void propagate(MctsNode startNode, float result, List<ConstraintEdge> path) {
        startNode.addVisitWin(result);
        for (ConstraintEdge edge : path) {
            edge.visits++;
            nodeMap.get(edge.childConstraints).addVisitWin(result);
        }
    }

    private int selectChild(MctsNode node, ScoringFunction score, long availableMoves) {
        long tmp = availableMoves;
        float childsTotalWins = 0;
        while (tmp != 0) {
            int square = Long.numberOfTrailingZeros(tmp);
            childsTotalWins += getChildsWins(node, square);
            tmp ^= Util.toFlag(square);
        }

        IntList bestMoves = new IntList(64);
        float bestScore = Float.NEGATIVE_INFINITY;
        tmp = availableMoves;
        while (tmp != 0) {
            int square = Long.numberOfTrailingZeros(tmp);
            float visitScore = getChildsVisits(node, square);
            float playerScore = getChildsWins(node, square);
            float childScore = score.score(childsTotalWins, visitScore, playerScore);
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
        int selectedMove = bestMoves.get(random.nextInt(bestMoves.size()));
        assert (availableMoves & Util.toFlag(selectedMove)) != 0;
        return selectedMove;
    }

    private float getChildsWins(MctsNode node, int move) {
        float wins = 0;
        for (ConstraintEdge child : node.getChilds(move)) {
            MctsNode childNode = nodeMap.get(child.childConstraints);
            if (childNode != null) {
                wins += child.visits * childNode.winrate();
            }
        }
        return wins;
    }

    private float getChildsVisits(MctsNode node, int move) {
        float visits = 0;
        for (ConstraintEdge child : node.getChilds(move)) {
            visits += child.visits;
        }
        return visits;
    }
}
