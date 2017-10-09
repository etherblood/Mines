package mines;

/**
 *
 * @author Philipp
 */
public class SmallMinesState {

    private final long mines;
    private long visible;

    public SmallMinesState(long mines) {
        this.mines = mines;
    }

    public boolean testSquare(int square, boolean isMine) {
        long flag = Util.toFlag(square);
        assert (visible & flag) == 0;
        visible |= flag;
        return ((mines & flag) != 0) == isMine;
    }

    public long getMines() {
        return mines;
    }

    public long getVisible() {
        return visible;
    }
}
