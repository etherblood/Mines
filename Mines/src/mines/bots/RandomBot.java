package mines.bots;

import java.util.Random;
import mines.Util;
import mines.state.MinesStateReadable;

public class RandomBot implements Bot {

    private final Random rng;

    public RandomBot(Random rng) {
        this.rng = rng;
    }

    @Override
    public int findMove(MinesStateReadable state) {
        long hidden = ~state.getRevealed();
        return Util.randomBit(rng, hidden);
    }

}
