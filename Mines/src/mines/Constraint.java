package mines;

/**
 *
 * @author Philipp
 */
public class Constraint {

    private final long mask;
    private final int count;

    public Constraint(long mask, int count) {
        this.count = count;
        this.mask = mask;
    }

    public boolean isValid(long bits) {
        return Long.bitCount(mask & bits) == count;
    }

    public long getMask() {
        return mask;
    }

    public int getCount() {
        return count;
    }
}
