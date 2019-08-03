package mines.state;

/**
 *
 * @author Philipp
 */
public interface MinesState extends MinesStateReadable {

    void reveal(int square);

    void bulkReveal(long squares);
}
