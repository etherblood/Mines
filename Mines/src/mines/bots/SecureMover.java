package mines.bots;

import mines.Constraint;
import mines.ConstraintGenerator;
import mines.Util;
import mines.state.MineConstraints;
import mines.state.MinesState;

/**
 *
 * @author Philipp
 */
public class SecureMover {

    private final ConstraintGenerator constraintGenerator;

    public SecureMover(ConstraintGenerator constraintGenerator) {
        this.constraintGenerator = constraintGenerator;
    }

    public ConstraintGenerator getConstraintGenerator() {
        return constraintGenerator;
    }

    public void applySecureMoves(MinesState state) {
        applySecureMoves(state, new MineConstraints(constraintGenerator.generateConstraints(state)));
    }

    public static void applySecureMoves(MinesState state, MineConstraints constraints) {
        if (state.isGameOver()) {
            return;
        }

        long secureMoves;
        do {
            secureMoves = 0;
            for (Constraint constraint : constraints.getConstraints()) {
                if (constraint.getMineCount() == 0) {
                    secureMoves |= constraint.getSquares();
                }
            }
            secureMoves &= ~state.getRevealed();
            makeMoves(state, constraints, secureMoves);
        } while (secureMoves != 0);
    }

    private static void makeMoves(MinesState state, MineConstraints constraints, long moves) {
        state.bulkReveal(moves);
        assert !state.isLost();
        while (moves != 0) {
            int move = Long.numberOfTrailingZeros(moves);
            constraints.addConstraint(new Constraint(Util.neighbors(move) & ~state.getRevealed(), state.countNeighborMines(move)));
            moves ^= Util.toFlag(move);
        }
    }
}
