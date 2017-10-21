package mines;

/**
 *
 * @author Philipp
 */
public class Constraint {

    private final long squares;
    private final int mineCount;

    public Constraint(long squares, int mineCount) {
        assert Long.bitCount(squares) >= mineCount;
        this.mineCount = mineCount;
        this.squares = squares;
    }

    public boolean isValid(long mines) {
        return Long.bitCount(squares & mines) == mineCount;
    }

    public long getSquares() {
        return squares;
    }

    public int getMineCount() {
        return mineCount;
    }

    @Override
    public int hashCode() {
        return 5 * Long.hashCode(squares) + Integer.hashCode(mineCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Constraint)) {
            return false;
        }
        Constraint other = (Constraint) obj;
        return squares == other.squares && mineCount == other.mineCount;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{squares=" + Long.toHexString(squares) + ", mineCount=" + mineCount + '}';
    }
}
