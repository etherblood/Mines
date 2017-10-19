package mines.state;

import mines.Util;

/**
 *
 * @author Philipp
 */
public class FastMinesState implements MinesState {

    private final long mines;
    private long revealed;

    public FastMinesState(long mines) {
        this(mines, 0);
    }

    public FastMinesState(long mines, long revealed) {
        this.mines = mines;
        this.revealed = revealed;
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
        assert (revealed & Util.toFlag(square)) == 0;
        revealed |= Util.toFlag(square);
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
