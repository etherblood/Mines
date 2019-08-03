package mines.bots.mcts_new;

import java.security.SecureRandom;
import mines.ConstraintGenerator;
import mines.bots.Bot;
import mines.bots.ConstrainedRandomBot;
import mines.state.MineConstraints;
import mines.state.MinesStateReadable;

public class SmallMctsBot implements Bot {

    private final ConstraintGenerator constraintGenerator = new ConstraintGenerator();
    private final SmallMctsAgent agent = new SmallMctsAgent(new ConstrainedRandomBot(constraintGenerator, new SecureRandom()));
    
    @Override
    public int findMove(MinesStateReadable state) {
        return agent.search(new MineConstraints(constraintGenerator.generateConstraints(state)));
    }

}
