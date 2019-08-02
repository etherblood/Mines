package mines.bots.mcts;

import mines.state.MineConstraints;

/**
 *
 * @author Philipp
 */
public class ConstraintEdge {
    int square;
    float visits;
    MineConstraints childConstraints;
}
