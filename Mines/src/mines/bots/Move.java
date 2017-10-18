package mines.bots;

/**
 *
 * @author Philipp
 */
public class Move {

    public int square;
    public boolean mine;

    public Move() {
    }

    public Move(int square, boolean mine) {
        this.square = square;
        this.mine = mine;
    }

    public int pack() {
        return mine ? 64 | square : square;
    }

    public Move unpack(int packed) {
        square = packed & 63;
        mine = packed > 63;
        return this;
    }
}
