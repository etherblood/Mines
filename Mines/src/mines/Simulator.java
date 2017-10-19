package mines;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mines.state.MinesStateReadable;

/**
 *
 * @author Philipp
 */
public class Simulator {

    private final ConstraintGenerator constraintGenerator;
    private final Random rng;

    public Simulator(ConstraintGenerator constraintGenerator, Random rng) {
        this.constraintGenerator = constraintGenerator;
        this.rng = rng;
    }

    public void simulate(MinesStateReadable state, int simulationCount, int[] mineCountResult) {
        List<Constraint> constraints = constraintGenerator.generateConstraints(state);
        for (int i = 0; i < simulationCount; i++) {
            long combination = generateCombination(state, new ArrayList<>(constraints));
            while (combination != 0) {
                int square = Long.numberOfTrailingZeros(combination);
                mineCountResult[square]++;
                combination ^= Util.toFlag(square);
            }
        }
    }
    
    public long randomCombination(MinesStateReadable source) {
        List<Constraint> constraints = constraintGenerator.generateConstraints(source);
        return generateCombination(source, constraints);
    }

    private long generateCombination(MinesStateReadable state, List<Constraint> constraints) {
        return Util.constrainedRandomBits(rng, 0, ~state.getRevealed(), constraints);
    }
}
