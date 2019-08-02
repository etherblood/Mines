package mines.bots.mcts;

import mines.Constraint;
import mines.Util;
import mines.state.MineConstraints;
import mines.state.MinesState;

/**
 *
 * @author Philipp
 */
public class MctsAgentState {

    final MinesState state;
    final MineConstraints constraints;

    public MctsAgentState(MinesState state, MineConstraints constraints) {
        this.state = state;
        this.constraints = constraints;
    }
    
    public void reveal(int square) {
        state.reveal(square);
        constraints.addConstraint(new Constraint(Util.neighbors(square), state.countNeighborMines(square)));
    }
}
