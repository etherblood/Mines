package mines.bots.mcts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mines.state.MineConstraints;

/**
 *
 * @author Philipp
 */
public class MctsNode {

    final MineConstraints constraints;
    float wins, visits;
    Map<Integer, List<ConstraintEdge>> childs = new HashMap<>();

    public MctsNode(MineConstraints constraints) {
        this.constraints = constraints;
    }

    public float winrate() {
        return visits == 0 ? 0 : wins / visits;
    }

    public void addVisitWin(float winScore) {
        visits++;
        wins += winScore;
    }

    public List<ConstraintEdge> getChilds(int square) {
        return childs.computeIfAbsent(square, s -> new ArrayList<>());
    }

    public void addVisit(int square, MineConstraints childConstraints) {
        getOrCreateChild(square, childConstraints).visits++;
    }

    public ConstraintEdge getOrCreateChild(int square, MineConstraints childConstraints) {
        List<ConstraintEdge> list = getChilds(square);
        for (ConstraintEdge child : list) {
            if (child.childConstraints.equals(childConstraints)) {
                return child;
            }
        }
        ConstraintEdge child = new ConstraintEdge();
        child.square = square;
        child.childConstraints = childConstraints;
        return child;
    }

}
