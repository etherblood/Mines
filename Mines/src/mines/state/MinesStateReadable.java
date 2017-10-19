package mines.state;

/**
 *
 * @author Philipp
 */
public interface MinesStateReadable {

    long getRevealed();

    boolean isWon();

    boolean isLost();

    default boolean isOver() {
        return isWon() || isLost();
    }

    int countNeighborMines(int square);

    int countTotalMines();
}
