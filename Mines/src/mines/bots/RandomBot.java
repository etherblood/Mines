package mines.bots;

import java.util.Random;
import mines.SmallMinesState;
import mines.Util;

public class RandomBot implements Bot {

    private final Random rng;

    public RandomBot(Random rng) {
        this.rng = rng;
    }

    @Override
    public Move findMove(SmallMinesState state) {
        long hidden = ~state.getVisible();
        long mines = state.getMines();
        return new Move(Util.randomBit(rng, hidden), rng.nextInt(Long.bitCount(hidden)) < Long.bitCount(hidden & mines));
    }

}
