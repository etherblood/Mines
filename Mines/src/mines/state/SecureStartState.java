package mines.state;

import java.util.Random;
import mines.Util;

/**
 *
 * @author Philipp
 */
public class SecureStartState implements MinesState {

    private final Random random;
    private final int mineCount;
    private MinesState state = null;

    public SecureStartState(Random random, int mineCount) {
        this.random = random;
        this.mineCount = mineCount;
    }

    @Override
    public void reveal(int square) {
        if (state == null) {
            long revealed = Util.toFlag(square);
            state = new FastMinesState(Util.randomBits(random, revealed, ~0, mineCount), revealed);
        } else {
            state.reveal(square);
        }
    }

    @Override
    public long getRevealed() {
        return state == null ? 0 : state.getRevealed();
    }

    @Override
    public boolean isWon() {
        return state != null && state.isWon();
    }

    @Override
    public boolean isLost() {
        return state != null && state.isLost();
    }

    @Override
    public int countNeighborMines(int square) {
        return state.countNeighborMines(square);
    }

    @Override
    public int countTotalMines() {
        return mineCount;
    }
}
