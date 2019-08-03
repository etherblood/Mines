package mines.state;

import mines.Util;

/**
 *
 * @author Philipp
 */
public class FastMinesState implements MinesState {

    final long mines;
    long revealed;

    public FastMinesState(long mines) {
        this(mines, 0);
    }

    public FastMinesState(FastMinesState state) {
        this(state.mines, state.revealed);
    }

    public FastMinesState(long mines, long revealed) {
        this.mines = mines;
        this.revealed = revealed;
    }

    @Override
    public long getMines() {
        assert isGameOver();
        return mines;
    }

    @Override
    public long getRevealed() {
        return revealed;
    }

    @Override
    public boolean isWon() {
        return (revealed ^ mines) == ~0;
    }

    @Override
    public boolean isLost() {
        return (revealed & mines) != 0;
    }

    @Override
    public void reveal(int square) {
        bulkReveal(Util.toFlag(square));
    }

    @Override
    public void bulkReveal(long squares) {
        assert (revealed & squares) == 0;
        revealed |= squares;
    }

    @Override
    public int countNeighborMines(int square) {
        assert (revealed & Util.toFlag(square)) != 0;
        return Long.bitCount(Util.neighbors(square) & mines);
    }

    @Override
    public int countTotalMines() {
        return Long.bitCount(mines);
    }

}
