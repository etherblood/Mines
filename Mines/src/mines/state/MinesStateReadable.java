package mines.state;

/**
 *
 * @author Philipp
 */
public interface MinesStateReadable {

    long getRevealed();

    boolean isWon();

    boolean isLost();

    default boolean isGameOver() {
        return isWon() || isLost();
    }

    int countNeighborMines(int square);

    int countTotalMines();
}
