package mines.bots.mcts;

import java.util.Arrays;
import java.util.Random;
import mines.Constraint;
import mines.ConstraintGenerator;
import mines.Util;
import mines.bots.ConstrainedRandomBot;
import mines.state.FastMinesState;
import mines.state.MineConstraints;
import mines.state.MinesState;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Philipp
 */
public class MctsAgentTest {

    public MctsAgentTest() {
    }

    @Test
    public void testIteration() {
        Random random = new Random(7);
        MctsAgent agent = new MctsAgent(new ConstrainedRandomBot(new ConstraintGenerator(), random));
        long mines = Util.randomBits(random, 16);
        long revealed = Util.toFlag(Util.randomBit(random, ~mines));
        MinesState state = new FastMinesState(mines);
        MineConstraints startConstraints = new MineConstraints(Arrays.asList(new Constraint(~0, 16)));
        agent.iteration(startConstraints);
    }
    
    

}