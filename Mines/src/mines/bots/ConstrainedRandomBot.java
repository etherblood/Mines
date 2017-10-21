package mines.bots;

import java.util.List;
import java.util.Random;
import mines.Constraint;
import mines.ConstraintGenerator;
import mines.Util;
import mines.state.MinesStateReadable;

public class ConstrainedRandomBot implements Bot {

    private final ConstraintGenerator constraintGenerator;
    private final Random rng;

    public ConstrainedRandomBot(ConstraintGenerator constraintGenerator, Random rng) {
        this.constraintGenerator = constraintGenerator;
        this.rng = rng;
    }

    @Override
    public int findMove(MinesStateReadable state) {
        List<Constraint> constraints = constraintGenerator.generateConstraints(state);
        long lowerBound = 0;
        long upperBound = ~state.getRevealed();
        long mines = Util.constrainedRandomBits(rng, lowerBound, upperBound, constraints);
        long moves = ~mines & ~state.getRevealed();
        return Util.randomBit(rng, moves);
    }

}
