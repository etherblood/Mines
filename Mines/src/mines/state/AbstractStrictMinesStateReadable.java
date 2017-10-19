package mines.state;

import mines.Util;

/**
 *
 * @author Philipp
 */
public abstract class AbstractStrictMinesStateReadable<S extends MinesStateReadable> implements MinesStateReadable {

    protected final S state;

    public AbstractStrictMinesStateReadable(S state) {
        this.state = state;
    }

    @Override
    public long getRevealed() {
        return state.getRevealed();
    }

    @Override
    public boolean isWon() {
        return state.isWon();
    }

    @Override
    public boolean isLost() {
        return state.isLost();
    }

    @Override
    public int countNeighborMines(int square) {
        if ((state.getRevealed() & Util.toFlag(square)) == 0) {
            throw new IllegalArgumentException("tried to get neighbor minecount of hidden square");
        }
        return state.countNeighborMines(square);
    }

    @Override
    public int countTotalMines() {
        return state.countTotalMines();
    }

}
